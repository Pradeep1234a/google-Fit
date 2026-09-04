import java.util.Properties

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.ksp)
  alias(libs.plugins.hilt)
}

android {
    namespace = "com.motioniq.app"
    compileSdk = 36

    val versionPropsFile = rootProject.file("version.properties")
    val versionProps = Properties().apply {
        if (versionPropsFile.exists()) {
            load(versionPropsFile.inputStream())
        }
    }
    val defaultVersionName = versionProps.getProperty("versionName", "1.0.0")
    val defaultVersionCode = versionProps.getProperty("baseVersionCode", "1").toIntOrNull() ?: 1

    val releaseVersionCode = project.findProperty("versionCode")?.toString()?.toIntOrNull() ?: defaultVersionCode
    val releaseVersionName = project.findProperty("versionName")?.toString() ?: defaultVersionName

    defaultConfig {
        applicationId = "com.motioniq.app"
        minSdk = 29
        targetSdk = 36
        versionCode = releaseVersionCode
        versionName = releaseVersionName

        manifestPlaceholders["MAPS_API_KEY"] = (project.findProperty("MAPS_API_KEY") as? String)
            ?: System.getenv("MAPS_API_KEY")
            ?: "AIzaSy_MOTIONIQ_MAPS_KEY_PLACEHOLDER"
    }

    signingConfigs {
        create("release") {
            val keystoreFilePath = System.getenv("KEYSTORE_FILE")
                ?: project.findProperty("KEYSTORE_FILE")?.toString()
            if (!keystoreFilePath.isNullOrBlank()) {
                val keystoreFile = file(keystoreFilePath)
                if (keystoreFile.exists()) {
                    storeFile = keystoreFile
                    storePassword = System.getenv("KEYSTORE_PASSWORD")
                        ?: project.findProperty("KEYSTORE_PASSWORD")?.toString()
                    keyAlias = System.getenv("KEY_ALIAS")
                        ?: project.findProperty("KEY_ALIAS")?.toString()
                    keyPassword = System.getenv("KEY_PASSWORD")
                        ?: project.findProperty("KEY_PASSWORD")?.toString()
                }
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            val releaseSigning = signingConfigs.getByName("release")
            if (releaseSigning.storeFile != null && releaseSigning.storeFile!!.exists()) {
                signingConfig = releaseSigning
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
      compose = true
      aidl = false
      buildConfig = false
      shaders = false
    }

    packaging {
      resources {
        excludes += "/META-INF/{AL2.0,LGPL2.1}"
      }
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)
  androidTestImplementation(composeBom)

  // Core Android dependencies
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)

  // Arch Components
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.viewmodel.compose)

  // Compose
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)
  implementation("androidx.compose.material:material-icons-extended")
  // Tooling
  debugImplementation(libs.androidx.compose.ui.tooling)
  // Instrumented tests
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  debugImplementation(libs.androidx.compose.ui.test.manifest)

  // Local tests: jUnit, coroutines, Android runner
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)

  // Instrumented tests: jUnit rules and runners
  androidTestImplementation(libs.androidx.test.core)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.androidx.test.espresso.core)

  // Navigation
  implementation(libs.androidx.navigation3.ui)
  implementation(libs.androidx.navigation3.runtime)
  implementation(libs.androidx.lifecycle.viewmodel.navigation3)

  // Dependency Injection: Hilt
  implementation(libs.hilt.android)
  ksp(libs.hilt.compiler)
  implementation(libs.hilt.navigation.compose)

  // Local Persistence: Room
  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.room.ktx)
  ksp(libs.androidx.room.compiler)

  // Preferences: DataStore
  implementation(libs.androidx.datastore.preferences)

  // Health Connect SDK
  implementation(libs.androidx.health.connect)

  // Google Play Services & Maps
  implementation(libs.play.services.location)
  implementation(libs.play.services.maps)
  implementation(libs.maps.compose)
}
