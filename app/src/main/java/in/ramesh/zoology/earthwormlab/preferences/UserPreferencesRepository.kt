package `in`.ramesh.zoology.earthwormlab.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "earthworm_native_preferences_v2",
)

enum class AppLanguage(val localeTag: String) { ENGLISH("en"), TAMIL("ta") }

/**
 * Deliberately separate from [in.ramesh.zoology.earthwormlab.data.ProgressRepository]:
 * this is app-level *preference* (language, one-time privacy acknowledgement),
 * not per-session learning *progress*. Splitting them means a future
 * "reset my progress" action does not accidentally also reset the
 * person's language choice, and vice versa.
 */
class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val LANGUAGE = stringPreferencesKey("language")
        val PRIVACY_ACKNOWLEDGED = booleanPreferencesKey("privacy_acknowledged")
    }

    val language: Flow<AppLanguage> = context.userPreferencesDataStore.data.map { prefs ->
        when (prefs[Keys.LANGUAGE]) {
            AppLanguage.TAMIL.localeTag -> AppLanguage.TAMIL
            else -> AppLanguage.ENGLISH
        }
    }

    val isPrivacyAcknowledged: Flow<Boolean> = context.userPreferencesDataStore.data.map { prefs ->
        prefs[Keys.PRIVACY_ACKNOWLEDGED] ?: false
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.userPreferencesDataStore.edit { it[Keys.LANGUAGE] = language.localeTag }
    }

    suspend fun setPrivacyAcknowledged(acknowledged: Boolean) {
        context.userPreferencesDataStore.edit { it[Keys.PRIVACY_ACKNOWLEDGED] = acknowledged }
    }
}
