package com.motioniq.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.motioniq.app.core.MotionRepository
import com.motioniq.app.theme.MOTIONIQTheme
import com.motioniq.app.ui.MainAppContainer

class MainActivity : ComponentActivity() {
  private val repository by lazy { MotionRepository(applicationContext) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

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
}
