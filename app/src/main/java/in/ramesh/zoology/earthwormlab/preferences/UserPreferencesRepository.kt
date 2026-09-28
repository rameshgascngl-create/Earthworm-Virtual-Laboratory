package `in`.ramesh.zoology.earthwormlab.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "earthworm_native_preferences_v2",
)

enum class AppLanguage(val localeTag: String) { ENGLISH("en"), TAMIL("ta") }

/**
 * Deliberately separate from [in.ramesh.zoology.earthwormlab.data.ProgressRepository]:
 * this is app-level preference state that is independent of per-session learning
 * progress. Per-app locale persistence is intentionally not stored here: AppCompat
 * is the sole locale persistence authority (see AndroidManifest autoStoreLocales).
 */
class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val PRIVACY_ACKNOWLEDGED = booleanPreferencesKey("privacy_acknowledged")
    }

    val isPrivacyAcknowledged: Flow<Boolean> = context.userPreferencesDataStore.data.map { prefs ->
        prefs[Keys.PRIVACY_ACKNOWLEDGED] ?: false
    }

    suspend fun setPrivacyAcknowledged(acknowledged: Boolean) {
        context.userPreferencesDataStore.edit { it[Keys.PRIVACY_ACKNOWLEDGED] = acknowledged }
    }
}
