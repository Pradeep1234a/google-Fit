package com.motioniq.app.core.step

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build

/**
 * Probes device hardware to determine which motion sensors and health APIs
 * are available. All checks are non-blocking and run on the calling thread.
 */
class SensorCapabilities(context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val packageManager = context.packageManager

    // --- Hardware step sensors ---
    val hasStepCounter: Boolean
        get() = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null

    val hasStepDetector: Boolean
        get() = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null

    // --- Inertial sensors (for software fallback) ---
    val hasAccelerometer: Boolean
        get() = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null

    val hasGyroscope: Boolean
        get() = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null

    val hasSignificantMotion: Boolean
        get() = sensorManager?.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION) != null

    val hasGravitySensor: Boolean
        get() = sensorManager?.getDefaultSensor(Sensor.TYPE_GRAVITY) != null

    val hasLinearAcceleration: Boolean
        get() = sensorManager?.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null

    // --- Health Connect ---
    val isHealthConnectAvailable: Boolean
        get() = try {
            packageManager.getPackageInfo("com.google.android.apps.healthdata", 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }

    /**
     * Select the best available step data source.
     *
     * Priority:
     * 1. TYPE_STEP_COUNTER  (cumulative counter, most battery-efficient, highest accuracy)
     * 2. TYPE_STEP_DETECTOR (event-based, good accuracy, slightly more battery)
     * 3. Accelerometer-based software pedometer (fallback, moderate accuracy)
     * 4. None — device cannot count steps
     */
    fun bestStepSource(): StepSourceType {
        return when {
            hasStepCounter -> StepSourceType.HARDWARE_STEP_COUNTER
            hasStepDetector -> StepSourceType.HARDWARE_STEP_DETECTOR
            hasAccelerometer -> StepSourceType.SOFTWARE_ACCELEROMETER
            else -> StepSourceType.NONE
        }
    }

    fun buildDiagnosticReport(): Map<String, Any> = buildMap {
        put("step_counter", hasStepCounter)
        put("step_detector", hasStepDetector)
        put("accelerometer", hasAccelerometer)
        put("gyroscope", hasGyroscope)
        put("significant_motion", hasSignificantMotion)
        put("gravity_sensor", hasGravitySensor)
        put("linear_acceleration", hasLinearAcceleration)
        put("health_connect_installed", isHealthConnectAvailable)
        put("best_step_source", bestStepSource().name)
        put("device", "${Build.MANUFACTURER} ${Build.MODEL}")
        put("sdk_version", Build.VERSION.SDK_INT)
    }
}

enum class StepSourceType {
    HARDWARE_STEP_COUNTER,
    HARDWARE_STEP_DETECTOR,
    SOFTWARE_ACCELEROMETER,
    NONE
}
