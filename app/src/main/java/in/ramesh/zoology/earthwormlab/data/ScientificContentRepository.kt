package `in`.ramesh.zoology.earthwormlab.data

import android.content.Context
import `in`.ramesh.zoology.earthwormlab.R
import `in`.ramesh.zoology.earthwormlab.model.AnatomicalStructure
import `in`.ramesh.zoology.earthwormlab.model.EarthwormSystem
import `in`.ramesh.zoology.earthwormlab.model.Hotspot
import `in`.ramesh.zoology.earthwormlab.model.ReviewStatus
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Loads app/src/main/res/raw/earthworm_content_v138.json — the same
 * mechanically-extracted dataset already validated 1:1 (all 55 structure
 * IDs, all 9 systems) against the frozen hybrid reference during the
 * Phase 0–3 audit. This class only re-shapes it into typed Kotlin models;
 * it must not re-derive or "improve" any scientific content itself. Any
 * correction belongs in reference/scientific-content.json + a regenerated
 * res/raw copy, with the change recorded in reference/RECONCILIATION-NOTES.md,
 * not here.
 *
 * NOTE ON HOTSPOT GEOMETRY: the currently bundled dataset only carries a
 * single normalized (x, y) marker per structure — inherited from
 * NativeData.java in the Java-native branches, itself a simplification of
 * the original SVG hit-region shapes in the hybrid reference. This
 * repository therefore returns [Hotspot.Point] for every structure today.
 * Restoring true [Hotspot.Rect]/[Hotspot.Polygon] geometry from the
 * original SVG markup is Phase 2 (atlas migration) work, not Phase 1.
 */
class ScientificContentRepository(context: Context) {

    private val root: JsonObject = context.resources
        .openRawResource(R.raw.earthworm_content_v138)
        .bufferedReader(Charsets.UTF_8)
        .use { it.readText() }
        .let { Json.parseToJsonElement(it).jsonObject }

    private val structuresJson: JsonObject = root["structures"]?.jsonObject
        ?: JsonObject(emptyMap())

    /** Segment-numbering caveat surfaced verbatim from provenance metadata —
     * see reference/RECONCILIATION-NOTES.md before treating any single
     * digestive-system segment figure as final. */
    val openScientificCaveats: List<String> by lazy {
        root["provenance"]?.jsonObject
            ?.get("scientificAudit")?.jsonObject
            ?.get("cautions")?.let { c -> (c as? JsonArray)?.map { it.jsonPrimitive.content } }
            ?: emptyList()
    }

    /** [en]/[ta] short label as authored (already reviewed Tamil, per
     * tamilTerminologyAudit in provenance) — used only where the caller
     * genuinely needs the label text itself (e.g. TalkBack content
     * description), not as a substitute for proper string resources for
     * UI chrome. */
    data class StructureContent(
        val id: String,
        val en: String,
        val ta: String,
        val locEn: String,
        val locTa: String,
        val fnEn: String,
        val fnTa: String,
        val sigEn: String,
        val sigTa: String,
        val fixEn: String,
        val fixTa: String,
    )

    fun structuresFor(system: EarthwormSystem): List<AnatomicalStructure> =
        structuresJson.entries
            .filter { (_, value) -> value.jsonObject["system"]?.jsonPrimitive?.content == system.dataKey }
            .mapNotNull { (id, value) -> toStructure(id, system) }

    fun contentFor(id: String): StructureContent? =
        structuresJson[id]?.jsonObject?.let { obj ->
            StructureContent(
                id = id,
                en = obj["en"]?.jsonPrimitive?.content.orEmpty(),
                ta = obj["ta"]?.jsonPrimitive?.content.orEmpty(),
                locEn = obj["locEn"]?.jsonPrimitive?.content.orEmpty(),
                locTa = obj["locTa"]?.jsonPrimitive?.content.orEmpty(),
                fnEn = obj["fnEn"]?.jsonPrimitive?.content.orEmpty(),
                fnTa = obj["fnTa"]?.jsonPrimitive?.content.orEmpty(),
                sigEn = obj["sigEn"]?.jsonPrimitive?.content.orEmpty(),
                sigTa = obj["sigTa"]?.jsonPrimitive?.content.orEmpty(),
                fixEn = obj["fixEn"]?.jsonPrimitive?.content.orEmpty(),
                fixTa = obj["fixTa"]?.jsonPrimitive?.content.orEmpty(),
            )
        }

    private fun toStructure(id: String, system: EarthwormSystem): AnatomicalStructure? {
        val hotspot = StructureGeometry.pointFor(id) ?: return null // no geometry ⇒ not yet portable, skip rather than fabricate a position
        val isCaveatSubject = id == "intestinal-caeca" // see RECONCILIATION-NOTES.md — content itself already resolves this (fixEn/fixTa), flag retained for UI transparency only
        return AnatomicalStructure(
            id = id,
            system = system,
            hotspot = hotspot,
            reviewStatus = if (isCaveatSubject) ReviewStatus.SCIENTIFIC_REVIEW_NEEDED else ReviewStatus.ACCEPTED,
        )
    }
}
