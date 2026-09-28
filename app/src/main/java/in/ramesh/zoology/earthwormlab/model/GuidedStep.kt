package `in`.ramesh.zoology.earthwormlab.model

/**
 * One step of a guided-dissection procedure for a system.
 * Ported from ContentRepository.GuidedAction (Java-native branches) /
 * "guidedModules" in earthworm_content_v138.json — see
 * reference/guided-steps.json for the mechanically-generated source data.
 *
 * The Java-native branches' guidedScientificAudit already corrected three
 * tool-mapping errors (see provenance.guidedScientificAudit in
 * scientific-content.json) before this model was written; [tool] values
 * here should be treated as already-corrected, not re-derived from the
 * raw hybrid HTML guided-step definitions.
 */
data class GuidedStep(
    val id: String,
    val sequence: Int,
    val targetSystem: EarthwormSystem,
    val targetStructureId: String?,
    val tool: GuidedTool,
    val instructionKey: String,
)

enum class GuidedTool {
    IDENTIFY,
    PROBE,
    OBSERVE,
    INSPECT,
}

/** Runtime completion state, kept separate from the static [GuidedStep] content. */
data class GuidedStepState(
    val stepId: String,
    val isCompleted: Boolean = false,
)
