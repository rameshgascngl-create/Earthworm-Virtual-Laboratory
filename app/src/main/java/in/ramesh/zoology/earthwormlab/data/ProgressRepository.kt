package `in`.ramesh.zoology.earthwormlab.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import `in`.ramesh.zoology.earthwormlab.model.EarthwormSystem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.progressDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "earthworm_native_progress_v2", // same store name as ProgressStore.java (SharedPreferences) —
    // deliberately kept identical only as a human-readable label; DataStore Preferences uses its own
    // file format, so this is a fresh store, not an automatic migration of the Java SharedPreferences file.
    // A one-time SharedPreferences→DataStore migration (androidx.datastore SharedPreferencesMigration)
    // is straightforward to add later if preserving existing installs' progress matters; Phase 1 does not
    // implement it, since there is no installed base on the Compose architecture yet.
)

/**
 * Kotlin/DataStore equivalent of ProgressStore.java. Same four fields,
 * same semantics (verified against the Java source):
 *   last_system      String  → EarthwormSystem, default PREPARATION
 *   visited          Set<String> of structure ids
 *   assessment_score Int, default 0
 * Plain key-value shape — Room is not warranted here (per migration
 * brief §6: do not introduce Room unless data genuinely needs relational
 * storage; it doesn't).
 */
class ProgressRepository(private val context: Context) {

    private object Keys {
        val LAST_SYSTEM = stringPreferencesKey("last_system")
        val VISITED = stringSetPreferencesKey("visited")
        val ASSESSMENT_SCORE = intPreferencesKey("assessment_score")
    }

    val lastSystem: Flow<EarthwormSystem> = context.progressDataStore.data.map { prefs ->
        prefs[Keys.LAST_SYSTEM]?.let(EarthwormSystem::fromDataKey) ?: EarthwormSystem.PREPARATION
    }

    val visitedStructureIds: Flow<Set<String>> = context.progressDataStore.data.map { prefs ->
        prefs[Keys.VISITED] ?: emptySet()
    }

    val assessmentScore: Flow<Int> = context.progressDataStore.data.map { prefs ->
        prefs[Keys.ASSESSMENT_SCORE] ?: 0
    }

    suspend fun setLastSystem(system: EarthwormSystem) {
        context.progressDataStore.edit { it[Keys.LAST_SYSTEM] = system.dataKey }
    }

    suspend fun markVisited(structureId: String) {
        context.progressDataStore.edit { prefs ->
            val current = prefs[Keys.VISITED] ?: emptySet()
            prefs[Keys.VISITED] = current + structureId
        }
    }

    suspend fun saveScore(score: Int) {
        context.progressDataStore.edit { it[Keys.ASSESSMENT_SCORE] = score }
    }
}
