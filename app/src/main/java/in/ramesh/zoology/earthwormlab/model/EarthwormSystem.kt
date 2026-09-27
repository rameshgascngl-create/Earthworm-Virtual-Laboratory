package `in`.ramesh.zoology.earthwormlab.model

/**
 * Stable, non-translated identifiers for the nine navigable systems.
 * [dataKey] matches the JSON key used in reference/scientific-content.json
 * and in app/src/main/res/raw/earthworm_content_v138.json ("systems" map),
 * so import code can round-trip without a second lookup table.
 *
 * Do not use translated text as an ID (see migration brief, model spec §11).
 */
enum class EarthwormSystem(val dataKey: String) {
    PREPARATION("setup"),
    EXTERNAL("external"),
    DIGESTIVE("digestive"),
    CIRCULATORY("circulatory"),
    RESPIRATORY("respiratory"),
    EXCRETORY("excretory"),
    REPRODUCTIVE("reproductive"),
    NERVOUS("nervous"),
    TRANSVERSE_SECTION("crosssection");

    companion object {
        /** Canonical tab order — matches ContentRepository.SYSTEM_ORDER in the
         * Java-native branches and the system:"…" ordering in the frozen
         * hybrid reference. Keep this list, not enum declaration order,
         * as the source of truth for UI ordering. */
        val TAB_ORDER: List<EarthwormSystem> = listOf(
            PREPARATION, EXTERNAL, DIGESTIVE, CIRCULATORY, RESPIRATORY,
            EXCRETORY, REPRODUCTIVE, NERVOUS, TRANSVERSE_SECTION,
        )

        fun fromDataKey(key: String): EarthwormSystem? =
            entries.firstOrNull { it.dataKey == key }
    }
}
