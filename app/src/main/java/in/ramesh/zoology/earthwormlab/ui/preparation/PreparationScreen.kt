package `in`.ramesh.zoology.earthwormlab.ui.preparation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import `in`.ramesh.zoology.earthwormlab.R
import kotlin.math.PI
import kotlin.math.sin

/**
 * Preparation remains the specimen/apparatus readiness screen.
 *
 * The home presentation added here is intentionally UI-only: it introduces no
 * anatomical claims, hotspots, scientific records, browser payload or network
 * dependency. The native hero graphic is decorative and generated entirely by
 * Compose drawing primitives so the screen stays lightweight and offline.
 */
@Composable
fun PreparationScreen(
    onPrivacyPolicy: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val checklist = listOf(
        R.string.preparation_item_specimen,
        R.string.preparation_item_dissection_tray,
        R.string.preparation_item_instruments,
        R.string.preparation_item_safety,
    )
    val learningHighlights = listOf(
        R.string.home_highlight_anatomy,
        R.string.home_highlight_physiology,
        R.string.home_highlight_bilingual,
    )

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            HomeHeroCard()
        }

        item {
            Text(
                text = stringResource(R.string.home_learning_title),
                style = MaterialTheme.typography.titleMedium,
            )
        }

        items(learningHighlights) { resId ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "• " + stringResource(resId),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                )
            }
        }

        item {
            Text(
                text = stringResource(R.string.preparation_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        item {
            Text(
                text = stringResource(R.string.preparation_intro),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        items(checklist) { resId ->
            Text(
                text = "• " + stringResource(resId),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            )
        }

        item {
            DesignerCreditCard(onPrivacyPolicy = onPrivacyPolicy)
        }
    }
}

@Composable
private fun HomeHeroCard() {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val surface = MaterialTheme.colorScheme.surfaceVariant
    val highlight = MaterialTheme.colorScheme.onPrimary
    val graphicDescription = stringResource(R.string.home_graphic_description)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2.15f)
                    .semantics { contentDescription = graphicDescription },
            ) {
                drawRect(surface)

                val count = 18
                for (i in 0 until count) {
                    val fraction = i.toFloat() / (count - 1).toFloat()
                    val x = size.width * (0.08f + 0.84f * fraction)
                    val wave = sin((fraction * 2f * PI.toFloat()).toDouble()).toFloat()
                    val y = size.height * (0.52f + 0.15f * wave)
                    val taper = 1f - kotlin.math.abs(fraction - 0.5f) * 0.55f
                    val radius = size.height * 0.075f * taper

                    drawCircle(
                        color = if (i % 2 == 0) primary else secondary,
                        radius = radius,
                        center = androidx.compose.ui.geometry.Offset(x, y),
                    )
                    drawCircle(
                        color = highlight.copy(alpha = 0.30f),
                        radius = radius * 0.38f,
                        center = androidx.compose.ui.geometry.Offset(
                            x - radius * 0.22f,
                            y - radius * 0.25f,
                        ),
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = stringResource(R.string.home_species),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    text = stringResource(R.string.home_tagline),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun DesignerCreditCard(
    onPrivacyPolicy: () -> Unit,
) {

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.home_designer_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = stringResource(R.string.home_designer_name),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 6.dp),
            )
            Text(
                text = stringResource(R.string.home_designer_department),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp),
            )
            Text(
                text = stringResource(R.string.home_designer_institution),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 2.dp),
            )

            OutlinedButton(
                onClick = onPrivacyPolicy,
                modifier = Modifier.padding(top = 14.dp),
            ) {
                Text(stringResource(R.string.privacy_policy_action))
            }
            Text(
                text = stringResource(R.string.privacy_policy_store_note),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}

