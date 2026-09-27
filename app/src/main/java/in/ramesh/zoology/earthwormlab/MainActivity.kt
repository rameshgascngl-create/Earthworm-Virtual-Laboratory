package `in`.ramesh.zoology.earthwormlab

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import `in`.ramesh.zoology.earthwormlab.ui.app.EarthwormApp
import `in`.ramesh.zoology.earthwormlab.ui.app.EarthwormViewModel
import `in`.ramesh.zoology.earthwormlab.ui.app.EarthwormViewModelFactory

/**
 * Extends AppCompatActivity, not plain ComponentActivity, specifically so
 * AppCompatDelegate.setApplicationLocales() (per-app English/Tamil
 * switching) actually applies on API 24–32 devices, not only API 33+.
 * AppCompatActivity is itself a ComponentActivity subclass, so
 * setContent {} / Compose interop is unaffected.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: EarthwormViewModel = viewModel(
                factory = EarthwormViewModelFactory(this, applicationContext),
            )
            EarthwormApp(viewModel = viewModel)
        }
    }
}
