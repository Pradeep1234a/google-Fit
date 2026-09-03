package com.motioniq.app.core

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.motioniq.app.model.ConfidenceLevel
import com.motioniq.app.model.StepSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StepTracker(context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val stepCounterSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private val stepDetectorSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

    private val _stepCount = MutableStateFlow(0L)
    val stepCount: StateFlow<Long> = _stepCount.asStateFlow()

    private val _stepSource = MutableStateFlow(StepSource.HARDWARE_SENSOR)
    val stepSource: StateFlow<StepSource> = _stepSource.asStateFlow()

    private val _confidence = MutableStateFlow(ConfidenceLevel.HIGH)
    val confidence: StateFlow<ConfidenceLevel> = _confidence.asStateFlow()

    val hasHardwareSensor: Boolean
        get() = stepCounterSensor != null || stepDetectorSensor != null

    private var initialCounterValue: Long = -1L
    private var isListening = false

    fun startTracking() {
        if (isListening) return
        isListening = true

        if (stepCounterSensor != null) {
            _stepSource.value = StepSource.HARDWARE_SENSOR
            _confidence.value = ConfidenceLevel.HIGH
            sensorManager?.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI)
        } else if (stepDetectorSensor != null) {
            _stepSource.value = StepSource.HARDWARE_SENSOR
            _confidence.value = ConfidenceLevel.HIGH
            sensorManager?.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            _stepSource.value = StepSource.ESTIMATED
            _confidence.value = ConfidenceLevel.MEDIUM
        }
    }

    fun stopTracking() {
        if (!isListening) return
        isListening = false
        sensorManager?.unregisterListener(this)
    }

    fun addManualSteps(steps: Long) {
        _stepCount.value += steps
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        when (event.sensor.type) {
            Sensor.TYPE_STEP_COUNTER -> {
                val totalBootSteps = event.values[0].toLong()
                if (initialCounterValue < 0L) {
                    initialCounterValue = totalBootSteps
                }
                val delta = totalBootSteps - initialCounterValue
                if (delta >= 0) {
                    _stepCount.value = delta
                }
            }
            Sensor.TYPE_STEP_DETECTOR -> {
                if (event.values[0] == 1.0f) {
                    _stepCount.value += 1
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
