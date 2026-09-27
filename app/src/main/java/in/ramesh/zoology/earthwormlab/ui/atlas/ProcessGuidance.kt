package `in`.ramesh.zoology.earthwormlab.ui.atlas

import `in`.ramesh.zoology.earthwormlab.model.EarthwormSystem

/**
 * Phase-3B2 process sequences.
 *
 * These are teaching sequences, not literal particle tracks. Every target is an
 * existing, already-reviewed structure ID from earthworm_content_v138.json.
 * Narrative content is read from that frozen dataset at runtime.
 */
internal enum class BiologicalProcess {
    CUTANEOUS_RESPIRATION,
    BLOOD_CIRCULATION_OVERVIEW,
}

internal data class ProcessDefinition(
    val process: BiologicalProcess,
    val system: EarthwormSystem,
    val targets: List<String>,
)

internal object ProcessGuidance {
    private val definitions = mapOf(
        EarthwormSystem.RESPIRATORY to ProcessDefinition(
            process = BiologicalProcess.CUTANEOUS_RESPIRATION,
            system = EarthwormSystem.RESPIRATORY,
            targets = listOf(
                "mucus-film",
                "moist-epidermis",
                "cutaneous-exchange",
                "cutaneous-capillaries",
            ),
        ),
        EarthwormSystem.CIRCULATORY to ProcessDefinition(
            process = BiologicalProcess.BLOOD_CIRCULATION_OVERVIEW,
            system = EarthwormSystem.CIRCULATORY,
            targets = listOf(
                "dorsal-vessel",
                "lateral-hearts",
                "ventral-vessel",
                "capillary-networks",
                "subneural-vessel",
                "lateral-oesophageal-vessels",
                "supra-oesophageal-vessel",
            ),
        ),
    )

    fun forSystem(system: EarthwormSystem): ProcessDefinition? = definitions[system]
}
