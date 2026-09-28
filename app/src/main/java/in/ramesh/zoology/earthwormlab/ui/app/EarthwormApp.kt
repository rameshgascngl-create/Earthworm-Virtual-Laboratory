package `in`.ramesh.zoology.earthwormlab.ui.app

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import `in`.ramesh.zoology.earthwormlab.R
import `in`.ramesh.zoology.earthwormlab.data.ScientificContentRepository
import `in`.ramesh.zoology.earthwormlab.model.EarthwormSystem
import `in`.ramesh.zoology.earthwormlab.preferences.AppLanguage
import `in`.ramesh.zoology.earthwormlab.ui.atlas.AtlasScreen
import `in`.ramesh.zoology.earthwormlab.ui.navigation.SystemTabRow
import `in`.ramesh.zoology.earthwormlab.ui.preparation.PreparationScreen
import `in`.ramesh.zoology.earthwormlab.ui.privacy.PrivacyPolicyScreen

/**
 * Native application shell. Phase 2 replaces the eight system placeholders
 * with [AtlasScreen] while keeping PREPARATION as the dissection-readiness
 * screen. Scientific content remains data-driven through
 * [ScientificContentRepository].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EarthwormApp(viewModel: EarthwormViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appContext = LocalContext.current.applicationContext
    val scientificContentRepository = remember(appContext) {
        ScientificContentRepository(appContext)
    }
    var showPrivacyPolicy by rememberSaveable { mutableStateOf(false) }

    BackHandler(
        enabled = showPrivacyPolicy || uiState.canNavigateBack,
    ) {
        if (showPrivacyPolicy) {
            showPrivacyPolicy = false
        } else {
            viewModel.navigateBack()
        }
    }

    EarthwormTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            if (showPrivacyPolicy) {
                                stringResource(R.string.privacy_screen_title)
                            } else {
                                stringResource(R.string.app_name)
                            },
                        )
                    },
                    navigationIcon = {
                        when {
                            showPrivacyPolicy -> {
                                TextButton(onClick = { showPrivacyPolicy = false }) {
                                    Text(stringResource(R.string.privacy_back_short))
                                }
                            }
                            uiState.selectedSystem != EarthwormSystem.PREPARATION -> {
                                TextButton(onClick = viewModel::navigateHome) {
                                    Text(stringResource(R.string.nav_home))
                                }
                            }
                        }
                    },
                    actions = {
                        TextButton(onClick = {
                            val next = if (uiState.language == AppLanguage.ENGLISH) AppLanguage.TAMIL else AppLanguage.ENGLISH
                            viewModel.onLanguageSelected(next)
                        }) {
                            // Deliberately hard-coded "EN"/"TA", not stringResource: this
                            // is a language *selector*, so its own label must stay legible
                            // to someone currently in the *other* language — translating it
                            // would defeat the control (a Tamil speaker in English mode
                            // needs to read "TA" in Roman letters to find the switch).
                            Text(if (uiState.language == AppLanguage.ENGLISH) "தமிழ்" else "English")
                        }
                    },
                )
            },
        ) { innerPadding ->
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                if (showPrivacyPolicy) {
                    PrivacyPolicyScreen(
                        onBack = { showPrivacyPolicy = false },
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    SystemTabRow(
                        selected = uiState.selectedSystem,
                        onSystemSelected = viewModel::onSystemSelected,
                        labelFor = { system -> systemLabel(system) },
                    )
                    if (uiState.isLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.padding(24.dp))
                        }
                    } else {
                        when (uiState.selectedSystem) {
                            EarthwormSystem.PREPARATION -> PreparationScreen(
                                onPrivacyPolicy = { showPrivacyPolicy = true },
                            )
                            else -> AtlasScreen(
                                system = uiState.selectedSystem,
                                language = uiState.language,
                                repository = scientificContentRepository,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EarthwormTheme(content: @Composable () -> Unit) {
    MaterialTheme(content = content)
}

/**
 * Resolves through the normal res/values(-ta)/strings.xml mechanism. The
 * in-app English/Tamil toggle (ViewModel.onLanguageSelected) does not
 * maintain a second, parallel string-lookup path — it calls
 * AppCompatDelegate.setApplicationLocales(), Android's own supported API
 * for a per-app language override independent of the device's system
 * language (see EarthwormViewModel), so ordinary stringResource() calls
 * automatically follow it. This is what migration brief §14 means by
 * "appropriate Android locale APIs" rather than a bespoke translation map.
 */
@Composable
private fun systemLabel(system: EarthwormSystem): String {
    val resId = when (system) {
        EarthwormSystem.PREPARATION -> R.string.system_preparation
        EarthwormSystem.EXTERNAL -> R.string.system_external
        EarthwormSystem.DIGESTIVE -> R.string.system_digestive
        EarthwormSystem.CIRCULATORY -> R.string.system_circulatory
        EarthwormSystem.RESPIRATORY -> R.string.system_respiratory
        EarthwormSystem.EXCRETORY -> R.string.system_excretory
        EarthwormSystem.REPRODUCTIVE -> R.string.system_reproductive
        EarthwormSystem.NERVOUS -> R.string.system_nervous
        EarthwormSystem.TRANSVERSE_SECTION -> R.string.system_transverse_section
    }
    return stringResource(resId)
}
