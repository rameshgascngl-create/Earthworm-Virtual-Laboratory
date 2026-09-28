package `in`.ramesh.zoology.earthwormlab.data

import android.content.Context
import `in`.ramesh.zoology.earthwormlab.R
import `in`.ramesh.zoology.earthwormlab.model.AssessmentQuestion
import `in`.ramesh.zoology.earthwormlab.model.EarthwormSystem
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * All 72 questions across 9 systems, verified present in
 * earthworm_content_v138.json (see Phase 0–3 audit: total count 72,
 * matching README's stated question count for v1.3.8). Question *text*
 * is exposed only as a data key (id) here; a future ui/assessment screen
 * resolves it from this repository's raw content, not from strings.xml —
 * per-question bilingual prose is scientific content, not UI chrome.
 */
class AssessmentRepository(context: Context) {

    private val root: JsonObject = context.resources
        .openRawResource(R.raw.earthworm_content_v138)
        .bufferedReader(Charsets.UTF_8)
        .use { it.readText() }
        .let { Json.parseToJsonElement(it).jsonObject }

    private val questionsJson: JsonObject = root["questions"]?.jsonObject ?: JsonObject(emptyMap())

    fun questionsFor(system: EarthwormSystem): List<AssessmentQuestion> {
        val array = questionsJson[system.dataKey]?.jsonArray ?: JsonArray(emptyList())
        return array.mapIndexed { index, element ->
            val obj = element.jsonObject
            AssessmentQuestion(
                id = "${system.dataKey}-$index",
                system = system,
                promptKey = "${system.dataKey}-$index-prompt",
                optionKeys = (obj["oEn"]?.jsonArray ?: JsonArray(emptyList()))
                    .indices.map { "${system.dataKey}-$index-option-$it" },
                correctOptionIndex = obj["a"]?.jsonPrimitive?.int ?: -1,
            )
        }
    }
}
