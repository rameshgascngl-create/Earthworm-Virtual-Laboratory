package `in`.ramesh.zoology.earthwormlab.ui.placeholder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import `in`.ramesh.zoology.earthwormlab.R
import `in`.ramesh.zoology.earthwormlab.model.EarthwormSystem

/**
 * Deliberately inert: no hybrid HTML embedded, no fabricated hotspot
 * count, no borrowed screenshot. This is what "honest placeholder" means
 * per the migration brief — a system a student taps into should never
 * look finished when it isn't (migration brief §6 / Phase-1 parity
 * report note: "Do not label intentionally deferred features as
 * regressions" cuts both ways — don't let them read as complete, either).
 */
@Composable
fun SystemPlaceholderScreen(system: EarthwormSystem, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.placeholder_title, systemDisplayName(system)),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = stringResource(R.string.placeholder_body),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun systemDisplayName(system: EarthwormSystem): String {
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
