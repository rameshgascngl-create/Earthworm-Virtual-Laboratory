package `in`.ramesh.zoology.earthwormlab.ui.privacy

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import `in`.ramesh.zoology.earthwormlab.R

/**
 * Store-review-safe privacy destination.
 *
 * The complete policy is bundled in res/raw and is therefore readable without
 * a browser, network connection or INTERNET permission. A public web copy is
 * offered only as an optional secondary action.
 */
@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val policy = remember(context) {
        context.resources.openRawResource(R.raw.privacy_policy)
            .bufferedReader(Charsets.UTF_8)
            .use { it.readText() }
    }
    val publicUrl = stringResource(R.string.privacy_policy_url)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.privacy_policy_action),
                style = MaterialTheme.typography.headlineSmall,
            )
        }
        item {
            Text(
                text = stringResource(R.string.privacy_policy_local_note),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        item {
            Text(
                text = policy,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = { openPublicPrivacyPolicy(context, publicUrl) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.privacy_policy_public_action))
                }
                Button(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.nav_back))
                }
            }
        }
    }
}

private fun openPublicPrivacyPolicy(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        addCategory(Intent.CATEGORY_BROWSABLE)
        if (context !is Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching { context.startActivity(intent) }
}
