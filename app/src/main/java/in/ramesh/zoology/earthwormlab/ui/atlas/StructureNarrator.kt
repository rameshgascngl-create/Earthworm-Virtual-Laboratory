package `in`.ramesh.zoology.earthwormlab.ui.atlas

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import `in`.ramesh.zoology.earthwormlab.preferences.AppLanguage
import java.util.Locale

/**
 * Small lifecycle-safe native TTS wrapper for Phase 3A.
 *
 * It never uses network APIs directly. Android's installed TTS engine is used;
 * unsupported/missing language data fails silently rather than speaking in the
 * wrong language. The pending first request is replayed after asynchronous TTS
 * initialisation completes.
 */
internal class StructureNarrator(context: Context) {
    private var engine: TextToSpeech? = null
    private var ready = false
    private var pending: PendingSpeech? = null

    private data class PendingSpeech(
        val text: String,
        val language: AppLanguage,
    )

    init {
        engine = TextToSpeech(context.applicationContext) { status ->
            ready = status == TextToSpeech.SUCCESS
            if (ready) {
                pending?.let { request ->
                    pending = null
                    speakNow(request.text, request.language)
                }
            } else {
                pending = null
            }
        }
    }

    fun speak(text: String, language: AppLanguage) {
        val clean = text.trim()
        if (clean.isBlank()) return

        if (!ready) {
            pending = PendingSpeech(clean, language)
            return
        }
        speakNow(clean, language)
    }

    private fun speakNow(text: String, language: AppLanguage) {
        val tts = engine ?: return
        val locale = when (language) {
            AppLanguage.TAMIL -> Locale.forLanguageTag("ta-IN")
            AppLanguage.ENGLISH -> Locale.forLanguageTag("en-IN")
        }

        val availability = tts.setLanguage(locale)
        if (
            availability == TextToSpeech.LANG_MISSING_DATA ||
            availability == TextToSpeech.LANG_NOT_SUPPORTED
        ) {
            return
        }

        tts.setSpeechRate(0.92f)
        tts.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "earthworm-structure-" + System.nanoTime(),
        )
    }

    fun stop() {
        pending = null
        engine?.stop()
    }

    fun shutdown() {
        pending = null
        ready = false
        engine?.stop()
        engine?.shutdown()
        engine = null
    }
}

@Composable
internal fun rememberStructureNarrator(): StructureNarrator {
    val context = LocalContext.current
    val narrator = remember(context.applicationContext) {
        StructureNarrator(context.applicationContext)
    }
    DisposableEffect(narrator) {
        onDispose { narrator.shutdown() }
    }
    return narrator
}
