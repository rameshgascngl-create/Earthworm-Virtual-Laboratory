package `in`.ramesh.zoology.earthwormlab.ui.privacy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import `in`.ramesh.zoology.earthwormlab.R

/**
 * Native, offline privacy-policy screen.
 *
 * The store-facing public policy remains hosted at [R.string.privacy_policy_url],
 * but the in-app policy is intentionally rendered natively so reading it never
 * depends on a browser, network availability, browser task behaviour, or an
 * external app. This screen contains no scientific/anatomical content.
 */
@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            TextButton(onClick = onBack) {
                Text(stringResource(R.string.privacy_back))
            }
        }

        item {
            Text(
                text = stringResource(R.string.privacy_screen_title),
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = stringResource(R.string.privacy_effective_date),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        item {
            PrivacySection(
                title = stringResource(R.string.privacy_overview_title),
                body = stringResource(R.string.privacy_overview_body),
            )
        }

        item {
            PrivacySection(
                title = stringResource(R.string.privacy_information_collected_title),
                body = stringResource(R.string.privacy_information_collected_body),
            )
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = stringResource(R.string.privacy_stored_title),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = stringResource(R.string.privacy_stored_intro),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text("• " + stringResource(R.string.privacy_stored_language))
                    Text("• " + stringResource(R.string.privacy_stored_last_structure))
                    Text("• " + stringResource(R.string.privacy_stored_assessment))
                    Text("• " + stringResource(R.string.privacy_stored_acknowledgement))
                    Text(
                        text = stringResource(R.string.privacy_stored_closing),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        item {
            PrivacySection(
                title = stringResource(R.string.privacy_speech_title),
                body = stringResource(R.string.privacy_speech_body),
            )
        }

        item {
            PrivacySection(
                title = stringResource(R.string.privacy_external_links_title),
                body = stringResource(R.string.privacy_external_links_body),
            )
        }

        item {
            PrivacySection(
                title = stringResource(R.string.privacy_ads_title),
                body = stringResource(R.string.privacy_ads_body),
            )
        }

        item {
            PrivacySection(
                title = stringResource(R.string.privacy_children_title),
                body = stringResource(R.string.privacy_children_body),
            )
        }

        item {
            PrivacySection(
                title = stringResource(R.string.privacy_security_title),
                body = stringResource(R.string.privacy_security_body),
            )
        }

        item {
            PrivacySection(
                title = stringResource(R.string.privacy_changes_title),
                body = stringResource(R.string.privacy_changes_body),
            )
        }

        item {
            PrivacySection(
                title = stringResource(R.string.privacy_contact_title),
                body = stringResource(R.string.privacy_contact_body),
            )
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = stringResource(R.string.privacy_public_url_title),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = stringResource(R.string.privacy_policy_url),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = stringResource(R.string.privacy_public_url_note),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

@Composable
private fun PrivacySection(
    title: String,
    body: String,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
