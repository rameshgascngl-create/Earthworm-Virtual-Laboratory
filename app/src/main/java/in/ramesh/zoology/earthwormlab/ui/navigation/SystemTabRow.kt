package `in`.ramesh.zoology.earthwormlab.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import `in`.ramesh.zoology.earthwormlab.model.EarthwormSystem

/**
 * Responsive anatomical-system navigation.
 *
 * Phones use one full-width selector so every system is immediately reachable
 * without clipped tab labels. Wider screens retain the original tab model.
 */
@Composable
fun SystemTabRow(
    selected: EarthwormSystem,
    onSystemSelected: (EarthwormSystem) -> Unit,
    labelFor: @Composable (EarthwormSystem) -> String,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        if (maxWidth < 600.dp) {
            CompactSystemSelector(
                selected = selected,
                onSystemSelected = onSystemSelected,
                labelFor = labelFor,
            )
        } else {
            WideSystemTabs(
                selected = selected,
                onSystemSelected = onSystemSelected,
                labelFor = labelFor,
            )
        }
    }
}

@Composable
private fun CompactSystemSelector(
    selected: EarthwormSystem,
    onSystemSelected: (EarthwormSystem) -> Unit,
    labelFor: @Composable (EarthwormSystem) -> String,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = labelFor(selected)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = selectedLabel,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.widthIn(min = 280.dp, max = 520.dp),
        ) {
            EarthwormSystem.TAB_ORDER.forEach { system ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = labelFor(system),
                            maxLines = 2,
                        )
                    },
                    onClick = {
                        expanded = false
                        onSystemSelected(system)
                    },
                )
            }
        }
    }
}

@Composable
private fun WideSystemTabs(
    selected: EarthwormSystem,
    onSystemSelected: (EarthwormSystem) -> Unit,
    labelFor: @Composable (EarthwormSystem) -> String,
) {
    val selectedIndex = EarthwormSystem.TAB_ORDER.indexOf(selected).coerceAtLeast(0)
    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
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
