package com.motioniq.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.motioniq.app.core.location.LocationTracker
import com.motioniq.app.core.step.StepCountingEngine
import com.motioniq.app.theme.MOTIONIQTheme
import com.motioniq.app.ui.MainAppContainer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  @Inject
  lateinit var stepEngine: StepCountingEngine

  @Inject
  lateinit var locationTracker: LocationTracker

  private val permissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
  ) { permissions ->
    val activityRecognitionGranted = permissions[Manifest.permission.ACTIVITY_RECOGNITION] ?: false
    if (activityRecognitionGranted) {
      // Re-initialize step engine with newly granted hardware sensor access
      stepEngine.stop()
      stepEngine.start()
    }
    val locationGranted = (permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false) ||
                          (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false)
    if (locationGranted) {
      locationTracker.getLastKnownLocation()
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    requestRequiredPermissions()

    enableEdgeToEdge()
    setContent {
      MOTIONIQTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          MainAppContainer()
        }
      }
    }
  }

  private fun requestRequiredPermissions() {
    val needed = mutableListOf<String>()

    // Activity Recognition (Android 10+, API 29+)
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
        != PackageManager.PERMISSION_GRANTED) {
      needed.add(Manifest.permission.ACTIVITY_RECOGNITION)
    }

    // Location Tracking (Fine & Coarse)
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      needed.add(Manifest.permission.ACCESS_FINE_LOCATION)
      needed.add(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    // Post Notifications (Android 13+, API 33+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
          != PackageManager.PERMISSION_GRANTED) {
        needed.add(Manifest.permission.POST_NOTIFICATIONS)
      }
    }

    if (needed.isNotEmpty()) {
      permissionLauncher.launch(needed.toTypedArray())
    }
  }
}
