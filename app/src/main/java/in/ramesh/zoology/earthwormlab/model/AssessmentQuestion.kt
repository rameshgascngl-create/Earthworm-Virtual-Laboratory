package `in`.ramesh.zoology.earthwormlab.model

/** Immutable content identity — one row of reference/assessments.json. */
data class AssessmentQuestion(
    val id: String,
    val system: EarthwormSystem,
    val promptKey: String,
    val optionKeys: List<String>,
    val correctOptionIndex: Int,
)

/**
 * Mutable per-attempt UI state, kept out of [AssessmentQuestion] so the
 * content model stays a plain, comparable, ViewModel-independent value
 * that reference-data tooling can also consume.
 */
data class AssessmentAttemptState(
    val questionId: String,
    val selectedOptionIndex: Int? = null,
    val isSubmitted: Boolean = false,
) {
    fun isCorrect(question: AssessmentQuestion): Boolean =
        isSubmitted && selectedOptionIndex == question.correctOptionIndex
}
