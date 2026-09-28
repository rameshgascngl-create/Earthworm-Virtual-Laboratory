package `in`.ramesh.zoology.earthwormlab.model

/**
 * One labelled anatomical structure within a system's atlas plate.
 *
 * Content fields are data keys / resource references, not raw display
 * strings — see migration brief §13. Large bilingual scientific prose
 * (location, function, teaching point, common correction) stays in
 * reference/scientific-content.json → data/ScientificContentRepository,
 * not in this model or in strings.xml; short, stable UI chrome only goes
 * to res/values(-ta)/strings.xml.
 *
 * @property id stable, non-translated key (e.g. "intestinal-caeca"),
 *   identical to the "data-structure" id in the frozen hybrid reference
 *   and the key in earthworm_content_v138.json → structures.
 * @property reviewStatus surfaces open scientific-content questions in the
 *   UI/tooling rather than silently shipping an unresolved figure — see
 *   reference/RECONCILIATION-NOTES.md for the one currently open item
 *   (intestinal-caeca segment convention, XXVI vs XXVII).
 */
data class AnatomicalStructure(
    val id: String,
    val system: EarthwormSystem,
    val hotspot: Hotspot,
    val reviewStatus: ReviewStatus = ReviewStatus.ACCEPTED,
    val ttsResourceKey: String? = null,
)

enum class ReviewStatus {
    ACCEPTED,
    SCIENTIFIC_REVIEW_NEEDED,
}
