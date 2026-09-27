package `in`.ramesh.zoology.earthwormlab.ui.atlas

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import `in`.ramesh.zoology.earthwormlab.preferences.AppLanguage
import java.util.Locale

internal enum class NarrationState {
    INITIALIZING,
    READY,
    SPEAKING,
    UNAVAILABLE,
    ERROR,
}

/**
 * Phase-3A1 native narration controller.
 *
 * Guarantees:
 * - only locally installed/offline TTS voices are selected;
 * - the previous utterance is flushed when a new structure is selected;
 * - speech stops when the host goes to the background;
 * - unavailable language data is exposed to the UI instead of failing silently;
 * - scientific display text is never rewritten; only the speech copy is normalized.
 */
internal class StructureNarrator(context: Context) {
    private val mainHandler = Handler(Looper.getMainLooper())
    private var engine: TextToSpeech? = null
    private var ready = false
    private var pending: PendingSpeech? = null

    var state by mutableStateOf(NarrationState.INITIALIZING)
        private set

    private data class PendingSpeech(
        val text: String,
        val language: AppLanguage,
    )

    init {
        engine = TextToSpeech(context.applicationContext) { status ->
            ready = status == TextToSpeech.SUCCESS
            if (!ready) {
                pending = null
                updateState(NarrationState.UNAVAILABLE)
                return@TextToSpeech
            }

            engine?.setOnUtteranceProgressListener(
                object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        updateState(NarrationState.SPEAKING)
                    }

                    override fun onDone(utteranceId: String?) {
                        updateState(NarrationState.READY)
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        updateState(NarrationState.ERROR)
                    }

                    override fun onError(utteranceId: String?, errorCode: Int) {
                        updateState(NarrationState.ERROR)
                    }

                    override fun onStop(utteranceId: String?, interrupted: Boolean) {
                        updateState(NarrationState.READY)
                    }
                },
            )

            updateState(NarrationState.READY)
            pending?.let { request ->
                pending = null
                speakNow(request.text, request.language)
            }
        }
    }

    fun speak(text: String, language: AppLanguage) {
        val normalized = ScientificSpeechNormalizer.normalize(text.trim(), language)
        if (normalized.isBlank()) return

        if (!ready) {
            pending = PendingSpeech(normalized, language)
            updateState(NarrationState.INITIALIZING)
            return
        }
        speakNow(normalized, language)
    }

    private fun speakNow(text: String, language: AppLanguage) {
        val tts = engine ?: run {
            updateState(NarrationState.UNAVAILABLE)
            return
        }

        val targetLocale = when (language) {
            AppLanguage.TAMIL -> Locale.forLanguageTag("ta-IN")
            AppLanguage.ENGLISH -> Locale.forLanguageTag("en-IN")
        }

        val availableVoices = tts.voices.orEmpty()
        val localVoices = availableVoices
            .filter { voice ->
                !voice.isNetworkConnectionRequired &&
                    voice.locale.language.equals(targetLocale.language, ignoreCase = true)
            }

        val selectedVoice =
            localVoices.firstOrNull { voice ->
                voice.locale.country.equals(targetLocale.country, ignoreCase = true)
            } ?: localVoices.firstOrNull()

        if (selectedVoice == null) {
            pending = null
            tts.stop()
            updateState(NarrationState.UNAVAILABLE)
            return
        }

        tts.voice = selectedVoice
        tts.setSpeechRate(0.92f)

        val result = tts.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "earthworm-structure-" + System.nanoTime(),
        )
        if (result == TextToSpeech.ERROR) {
            updateState(NarrationState.ERROR)
        }
    }

    fun stop() {
        pending = null
        engine?.stop()
        if (ready && state != NarrationState.UNAVAILABLE) {
            updateState(NarrationState.READY)
        }
    }

    fun shutdown() {
        pending = null
        ready = false
        engine?.stop()
        engine?.shutdown()
        engine = null
        updateState(NarrationState.UNAVAILABLE)
    }

    private fun updateState(newState: NarrationState) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            state = newState
        } else {
            mainHandler.post { state = newState }
        }
    }
}

@Composable
internal fun rememberStructureNarrator(): StructureNarrator {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val narrator = remember(context.applicationContext) {
        StructureNarrator(context.applicationContext)
    }

    DisposableEffect(narrator, lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                narrator.stop()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            narrator.shutdown()
        }
    }
    return narrator
}

/**
 * Converts notation that is visually appropriate in a Zoology atlas into
 * phrases that TTS engines can speak reliably. The underlying scientific
 * strings are never changed.
 */
internal object ScientificSpeechNormalizer {
    private val romanRange = Regex("""\b([IVXLCDM]+)\s*[–—-]\s*([IVXLCDM]+)\b""")
    private val romanSingle = Regex("""\b[IVXLCDM]+\b""")
    private val intersegment = Regex("""\b(\d{1,2})\s*/\s*(\d{1,2})\b""")
    private val numericRange = Regex("""\b(\d{1,2})\s*[–—-]\s*(\d{1,2})\b""")

    fun normalize(text: String, language: AppLanguage): String {
        if (text.isBlank()) return text

        var output = text
            .replace(Regex("""\bM\.\s+posthuma\b"""), "Metaphire posthuma")

        output = romanRange.replace(output) { match ->
            rangePhrase(
                romanToInt(match.groupValues[1]),
                romanToInt(match.groupValues[2]),
                language,
            )
        }

        output = intersegment.replace(output) { match ->
            intersegmentPhrase(
                match.groupValues[1].toInt(),
                match.groupValues[2].toInt(),
                language,
            )
        }

        output = numericRange.replace(output) { match ->
            rangePhrase(
                match.groupValues[1].toInt(),
                match.groupValues[2].toInt(),
                language,
            )
        }

        output = romanSingle.replace(output) { match ->
            val number = romanToInt(match.value)
            numberPhrase(number, language)
        }

        return output
            .replace(Regex("""\s+"""), " ")
            .trim()
    }

    private fun rangePhrase(start: Int, end: Int, language: AppLanguage): String =
        when (language) {
            AppLanguage.ENGLISH ->
                "${numberPhrase(start, language)} to ${numberPhrase(end, language)}"
            AppLanguage.TAMIL ->
                "${numberPhrase(start, language)} முதல் ${numberPhrase(end, language)} வரை"
        }

    private fun intersegmentPhrase(first: Int, second: Int, language: AppLanguage): String =
        when (language) {
            AppLanguage.ENGLISH ->
                "between segments ${numberPhrase(first, language)} and ${numberPhrase(second, language)}"
            AppLanguage.TAMIL ->
                "கண்டங்கள் ${numberPhrase(first, language)} மற்றும் ${numberPhrase(second, language)} இடையில்"
        }

    private fun numberPhrase(number: Int, language: AppLanguage): String =
        when (language) {
            AppLanguage.ENGLISH -> englishNumber(number)
            AppLanguage.TAMIL -> tamilNumber(number)
        }

    private fun englishNumber(number: Int): String {
        val underTwenty = listOf(
            "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
            "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",
            "seventeen", "eighteen", "nineteen",
        )
        if (number in 0..19) return underTwenty[number]

        val tens = mapOf(
            20 to "twenty", 30 to "thirty", 40 to "forty", 50 to "fifty",
            60 to "sixty", 70 to "seventy", 80 to "eighty", 90 to "ninety",
        )
        if (number in 20..99) {
            val base = (number / 10) * 10
            val remainder = number % 10
            return if (remainder == 0) {
                tens.getValue(base)
            } else {
                "${tens.getValue(base)} ${underTwenty[remainder]}"
            }
        }
        return number.toString()
    }

    private fun tamilNumber(number: Int): String {
        val fixed = mapOf(
            0 to "பூஜ்ஜியம்",
            1 to "ஒன்று",
            2 to "இரண்டு",
            3 to "மூன்று",
            4 to "நான்கு",
            5 to "ஐந்து",
            6 to "ஆறு",
            7 to "ஏழு",
            8 to "எட்டு",
            9 to "ஒன்பது",
            10 to "பத்து",
            11 to "பதினொன்று",
            12 to "பன்னிரண்டு",
            13 to "பதின்மூன்று",
            14 to "பதினான்கு",
            15 to "பதினைந்து",
            16 to "பதினாறு",
            17 to "பதினேழு",
            18 to "பதினெட்டு",
            19 to "பத்தொன்பது",
            20 to "இருபது",
            30 to "முப்பது",
            40 to "நாற்பது",
            50 to "ஐம்பது",
            60 to "அறுபது",
            70 to "எழுபது",
            80 to "எண்பது",
            90 to "தொண்ணூறு",
        )
        fixed[number]?.let { return it }

        if (number in 21..99) {
            val base = (number / 10) * 10
            val remainder = number % 10
            val stem = when (base) {
                20 -> "இருபத்து"
                30 -> "முப்பத்து"
                40 -> "நாற்பத்து"
                50 -> "ஐம்பத்து"
                60 -> "அறுபத்து"
                70 -> "எழுபத்து"
                80 -> "எண்பத்து"
                90 -> "தொண்ணூற்று"
                else -> return number.toString()
            }
            return "$stem ${fixed[remainder] ?: remainder.toString()}"
        }

        return number.toString()
    }

    private fun romanToInt(value: String): Int {
        val values = mapOf(
            'I' to 1,
            'V' to 5,
            'X' to 10,
            'L' to 50,
            'C' to 100,
            'D' to 500,
            'M' to 1000,
        )
        var total = 0
        var previous = 0
        value.uppercase().reversed().forEach { char ->
            val current = values[char] ?: return@forEach
            if (current < previous) {
                total -= current
            } else {
                total += current
                previous = current
            }
        }
        return total
    }
}
