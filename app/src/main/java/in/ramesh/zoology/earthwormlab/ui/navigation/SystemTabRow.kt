package `in`.ramesh.zoology.earthwormlab.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import `in`.ramesh.zoology.earthwormlab.model.EarthwormSystem

/**
 * Phase 1 requirement: "single authoritative system-selection state",
 * "selected item automatically brought into view" (ScrollableTabRow does
 * this natively when selectedTabIndex changes), suitable touch targets
 * (Tab's default min height already satisfies 48dp), TalkBack semantics
 * (Role.Tab + selected state, both supplied by Tab itself — no extra
 * semantics block needed beyond what's shown for the bilingual label).
 */
@Composable
fun SystemTabRow(
    selected: EarthwormSystem,
    onSystemSelected: (EarthwormSystem) -> Unit,
    labelFor: @Composable (EarthwormSystem) -> String,
    modifier: Modifier = Modifier,
) {
    val selectedIndex = EarthwormSystem.TAB_ORDER.indexOf(selected).coerceAtLeast(0)
    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier,
        edgePadding = 12.dp,
    ) {
        EarthwormSystem.TAB_ORDER.forEach { system ->
            Tab(
                selected = system == selected,
                onClick = { onSystemSelected(system) },
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .semantics { role = Role.Tab },
                text = { Text(labelFor(system)) },
            )
        }
    }
}
