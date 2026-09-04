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
import com.motioniq.app.core.MotionRepository
import com.motioniq.app.theme.MOTIONIQTheme
import com.motioniq.app.ui.MainAppContainer

class MainActivity : ComponentActivity() {
  private val repository by lazy { MotionRepository(applicationContext) }

  private val permissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
  ) { permissions ->
    val activityRecognitionGranted = permissions[Manifest.permission.ACTIVITY_RECOGNITION] ?: false
    if (activityRecognitionGranted) {
      // Re-initialize step engine with newly granted hardware sensor access
      repository.stepEngine.stop()
      repository.stepEngine.start()
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
          MainAppContainer(repository)
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
