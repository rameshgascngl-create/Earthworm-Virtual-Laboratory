package `in`.ramesh.zoology.earthwormlab.ui.app

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.ramesh.zoology.earthwormlab.data.ProgressRepository
import `in`.ramesh.zoology.earthwormlab.model.EarthwormSystem
import `in`.ramesh.zoology.earthwormlab.preferences.AppLanguage
import `in`.ramesh.zoology.earthwormlab.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class EarthwormUiState(
    val selectedSystem: EarthwormSystem = EarthwormSystem.EXTERNAL,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val isLoading: Boolean = true,
)

private const val KEY_SELECTED_SYSTEM = "selected_system"

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
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val selectedSystem = savedStateHandle.getStateFlow(
        KEY_SELECTED_SYSTEM,
        EarthwormSystem.EXTERNAL.dataKey,
    )

    private val isLoading = MutableStateFlow(true)

    val uiState: StateFlow<EarthwormUiState> = combine(
        selectedSystem,
        userPreferencesRepository.language,
        isLoading,
    ) { systemKey, language, loading ->
        EarthwormUiState(
            selectedSystem = EarthwormSystem.fromDataKey(systemKey) ?: EarthwormSystem.EXTERNAL,
            language = language,
            isLoading = loading,
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
                savedStateHandle[KEY_SELECTED_SYSTEM] = restored.dataKey
            }
            isLoading.value = false
        }
    }

    fun onSystemSelected(system: EarthwormSystem) {
        savedStateHandle[KEY_SELECTED_SYSTEM] = system.dataKey
        viewModelScope.launch { progressRepository.setLastSystem(system) }
    }

    fun onLanguageSelected(language: AppLanguage) {
        // AppCompatDelegate persists this itself and survives process death;
        // DataStore is still the source of truth for our own UI (e.g. showing
        // which option is checked) since AppCompatDelegate has no public
        // synchronous getter that's safe to read before the framework applies it.
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language.localeTag))
        viewModelScope.launch { userPreferencesRepository.setLanguage(language) }
    }
}
