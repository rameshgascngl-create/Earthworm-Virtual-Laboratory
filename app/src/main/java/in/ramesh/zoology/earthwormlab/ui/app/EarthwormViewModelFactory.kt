package `in`.ramesh.zoology.earthwormlab.ui.app

import android.content.Context
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.savedstate.SavedStateRegistryOwner
import `in`.ramesh.zoology.earthwormlab.data.ProgressRepository

/**
 * Deliberately not a Dagger/Hilt module: the Phase-1 ViewModel has a small dependency surface, no interfaces
 * needed yet, no test doubles required for Phase 1. Introduce DI only when
 * this factory's parameter list actually becomes unwieldy.
 */
class EarthwormViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val appContext: Context,
) : AbstractSavedStateViewModelFactory(owner, null) {

    override fun <T : androidx.lifecycle.ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle,
    ): T {
        @Suppress("UNCHECKED_CAST")
        return EarthwormViewModel(
            savedStateHandle = handle,
            progressRepository = ProgressRepository(appContext),
        ) as T
    }
}
