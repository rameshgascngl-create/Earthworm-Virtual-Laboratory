package `in`.ramesh.zoology.earthwormlab.ui.preparation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import `in`.ramesh.zoology.earthwormlab.R

/**
 * Preparation is specimen/apparatus checklist content, not anatomical
 * atlas content — it has no hotspots or structures in
 * earthworm_content_v138.json (system "setup" has zero entries under
 * "structures", confirmed during the Phase 0 audit), so this screen has
 * nothing to source from ScientificContentRepository yet. The four
 * checklist lines below are UI copy (res/values strings), not scientific
 * content, and are intentionally minimal for Phase 1 — expanding this
 * list is Phase 2 scope, not a Phase 1 gap to silently patch over.
 */
@Composable
fun PreparationScreen(modifier: Modifier = Modifier) {
    val items = listOf(
        R.string.preparation_item_specimen,
        R.string.preparation_item_dissection_tray,
        R.string.preparation_item_instruments,
        R.string.preparation_item_safety,
    )
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = stringResource(R.string.preparation_title),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = stringResource(R.string.preparation_intro),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(items) { resId ->
                Text(text = "• " + stringResource(resId), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
