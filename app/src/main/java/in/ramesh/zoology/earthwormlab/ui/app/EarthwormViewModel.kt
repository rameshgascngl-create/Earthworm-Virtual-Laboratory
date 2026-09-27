package `in`.ramesh.zoology.earthwormlab.ui.app

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.ramesh.zoology.earthwormlab.data.ProgressRepository
import `in`.ramesh.zoology.earthwormlab.model.EarthwormSystem
import `in`.ramesh.zoology.earthwormlab.preferences.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.util.Locale
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class EarthwormUiState(
    val selectedSystem: EarthwormSystem = EarthwormSystem.PREPARATION,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val isLoading: Boolean = true,
    val canNavigateBack: Boolean = false,
)

private const val KEY_SELECTED_SYSTEM = "selected_system"
private const val KEY_NAVIGATION_HISTORY = "navigation_history"
private const val MAX_NAVIGATION_HISTORY = 32

/**
 * The single authoritative selection state the migration brief's Phase 1
 * requires ("There must be a single authoritative system-selection state.").
 * Selection survives process death via [SavedStateHandle] immediately, and
 * survives app restart via [ProgressRepository.lastSystem] once that Flow
 * emits — SavedStateHandle wins for the current process lifetime, matching
 * ordinary Android expectations for in-session state vs. persisted state.
 */
class EarthwormViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val progressRepository: ProgressRepository,
) : ViewModel() {

    private val selectedSystem = savedStateHandle.getStateFlow(
        KEY_SELECTED_SYSTEM,
        EarthwormSystem.PREPARATION.dataKey,
    )

    private val navigationHistory = savedStateHandle.getStateFlow(
        KEY_NAVIGATION_HISTORY,
        arrayListOf<String>(),
    )
    private val isLoading = MutableStateFlow(true)
    private val language = MutableStateFlow(currentAppLanguage())

    val uiState: StateFlow<EarthwormUiState> = combine(
        selectedSystem,
        language,
        isLoading,
        navigationHistory,
    ) { systemKey, language, loading, history ->
        EarthwormUiState(
            selectedSystem = EarthwormSystem.fromDataKey(systemKey) ?: EarthwormSystem.PREPARATION,
            language = language,
            isLoading = loading,
            canNavigateBack = history.isNotEmpty(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = EarthwormUiState(),
    )

    init {
        // Restore the last system actually visited, only if the caller hasn't
        // already navigated away in this process (SavedStateHandle already set).
        // One-shot restore via first() — deliberately not a live subscription,
        // so a later external change to persisted progress can't yank the
        // person's current on-screen selection out from under them.
        viewModelScope.launch {
            if (savedStateHandle.get<String>(KEY_SELECTED_SYSTEM) == null) {
                val restored = progressRepository.lastSystem.first()
                if (
                    restored != EarthwormSystem.PREPARATION &&
                    navigationHistory.value.isEmpty()
                ) {
                    savedStateHandle[KEY_NAVIGATION_HISTORY] = arrayListOf(
                        EarthwormSystem.PREPARATION.dataKey,
                    )
                }
                savedStateHandle[KEY_SELECTED_SYSTEM] = restored.dataKey
            }
            isLoading.value = false
        }
    }

    fun onSystemSelected(system: EarthwormSystem) {
        val currentKey = selectedSystem.value
        if (currentKey == system.dataKey) return

        val updatedHistory = ArrayList(
            (navigationHistory.value + currentKey).takeLast(MAX_NAVIGATION_HISTORY),
        )
        savedStateHandle[KEY_NAVIGATION_HISTORY] = updatedHistory
        setSelectedSystem(system)
    }

    fun navigateBack() {
        val history = navigationHistory.value
        if (history.isEmpty()) return

        val previousKey = history.last()
        savedStateHandle[KEY_NAVIGATION_HISTORY] = ArrayList(history.dropLast(1))
        setSelectedSystem(
            EarthwormSystem.fromDataKey(previousKey) ?: EarthwormSystem.PREPARATION,
        )
    }

    private fun setSelectedSystem(system: EarthwormSystem) {
        savedStateHandle[KEY_SELECTED_SYSTEM] = system.dataKey
        viewModelScope.launch { progressRepository.setLastSystem(system) }
    }

    fun onLanguageSelected(language: AppLanguage) {
        // AppCompat is the single locale source of truth. On Android 13+ this
        // delegates to the framework locale manager; on Android 12 and lower
        // AppCompat autoStoreLocales persists the selection declared in the manifest.
        this.language.value = language
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language.localeTag))
    }

    private fun currentAppLanguage(): AppLanguage {
        val appLocale = AppCompatDelegate.getApplicationLocales()
            .toLanguageTags()
            .substringBefore(',')
            .substringBefore('-')
            .ifBlank { Locale.getDefault().language }
        return if (appLocale == AppLanguage.TAMIL.localeTag) AppLanguage.TAMIL else AppLanguage.ENGLISH
    }
}
