package com.motioniq.app.core.step

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Software-based step detector using accelerometer data when hardware
 * step sensors are unavailable.
 *
 * Algorithm:
 * 1. Compute gravity-independent acceleration magnitude: |a| = sqrt(ax² + ay² + az²)
 * 2. Apply low-pass exponential smoothing to remove high-frequency noise
 * 3. Detect peaks in the smoothed signal that exceed a dynamic threshold
 * 4. Enforce minimum step interval (250ms for running, 350ms for walking) to
 *    reject vibration, vehicle noise, and hand-shake false positives
 * 5. Validate step cadence continuity: require 3+ consistent peaks before
 *    starting to count ("3-step gate") to avoid isolated false positives
 * 6. Auto-adapt threshold based on recent peak amplitudes
 * 7. Timeout idle detection: if no valid step for 3 seconds, reset gate
 *
 * This is NOT a simple "if acc > threshold" counter. The combination of
 * smoothing, peak detection, interval validation, cadence gating, and
 * adaptive thresholds provides practical walking/running detection.
 */
class SoftwarePedometer(context: Context) : SensorEventListener {

    companion object {
        private const val TAG = "SoftwarePedometer"

        // Physics constants
        private const val GRAVITY = 9.81f

        // Smoothing factor for exponential low-pass filter (0..1, lower = smoother)
        private const val SMOOTHING_ALPHA = 0.15f

        // Minimum magnitude change from gravity to consider as a step candidate
        private const val MIN_PEAK_THRESHOLD = 1.8f

        // Maximum magnitude change (to reject drops, slams, etc.)
        private const val MAX_PEAK_THRESHOLD = 28.0f

        // Minimum time between consecutive steps in nanoseconds
        private const val MIN_STEP_INTERVAL_NS = 250_000_000L   // 250ms (~240 steps/min max = sprinting)

        // Maximum time between steps for cadence continuity
        private const val MAX_STEP_INTERVAL_NS = 2_000_000_000L // 2s (30 steps/min min = very slow walk)

        // Steps needed in gate window before counting begins
        private const val GATE_STEPS_REQUIRED = 3

        // Idle timeout: if no step detected for this long, reset gate
        private const val IDLE_TIMEOUT_NS = 3_000_000_000L // 3s

        // Number of recent peaks to track for adaptive threshold
        private const val ADAPTIVE_WINDOW_SIZE = 8

        // Adaptive threshold factor relative to recent average peak
        private const val ADAPTIVE_THRESHOLD_FACTOR = 0.45f
    }

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val accelerometer: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    val isAvailable: Boolean get() = accelerometer != null

    private val _stepCount = MutableStateFlow(0L)
    val stepCount: StateFlow<Long> = _stepCount.asStateFlow()

    private var isListening = false

    // Signal processing state
    private var smoothedMagnitude = GRAVITY
    private var previousSmoothed = GRAVITY
    private var wasRising = false

    // Timing state
    private var lastStepTimestampNs = 0L
    private var lastSensorTimestampNs = 0L

    // Cadence gate: accumulate candidates before counting
    private var gateCandidateCount = 0
    private var gateOpen = false

    // Adaptive threshold
    private val recentPeakAmplitudes = ArrayDeque<Float>(ADAPTIVE_WINDOW_SIZE)
    private var dynamicThreshold = MIN_PEAK_THRESHOLD

    fun startTracking() {
        if (isListening || accelerometer == null) return
        isListening = true
        resetState()
        // SENSOR_DELAY_GAME ≈ 20ms (50Hz). Good balance between accuracy and battery.
        sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        Log.d(TAG, "Software pedometer started")
    }

    fun stopTracking() {
        if (!isListening) return
        isListening = false
        sensorManager?.unregisterListener(this)
        Log.d(TAG, "Software pedometer stopped. Total steps: ${_stepCount.value}")
    }

    fun resetCount() {
        _stepCount.value = 0L
        resetState()
    }

    private fun resetState() {
        smoothedMagnitude = GRAVITY
        previousSmoothed = GRAVITY
        wasRising = false
        lastStepTimestampNs = 0L
        lastSensorTimestampNs = 0L
        gateCandidateCount = 0
        gateOpen = false
        recentPeakAmplitudes.clear()
        dynamicThreshold = MIN_PEAK_THRESHOLD
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val timestamp = event.timestamp // nanoseconds since boot
        val ax = event.values[0]
        val ay = event.values[1]
        val az = event.values[2]

        // 1. Compute gravity-independent acceleration magnitude
        //    This is orientation-independent: regardless of how the phone is held,
        //    the magnitude captures the total force including gravity (~9.81).
        //    Steps create periodic oscillation around gravity.
        val magnitude = sqrt((ax * ax + ay * ay + az * az).toDouble()).toFloat()

        // 2. Exponential low-pass smoothing
        smoothedMagnitude = SMOOTHING_ALPHA * magnitude + (1f - SMOOTHING_ALPHA) * smoothedMagnitude

        // 3. Peak detection: rising→falling transition in smoothed signal
        val isRising = smoothedMagnitude > previousSmoothed
        val peakDetected = wasRising && !isRising // was going up, now going down = peak

        if (peakDetected) {
            val peakAmplitude = abs(smoothedMagnitude - GRAVITY)

            // 4. Amplitude check (reject noise and extreme events)
            if (peakAmplitude >= dynamicThreshold && peakAmplitude <= MAX_PEAK_THRESHOLD) {

                // 5. Minimum interval check (reject vibration/vehicle)
                val intervalNs = if (lastStepTimestampNs > 0) timestamp - lastStepTimestampNs else Long.MAX_VALUE

                if (intervalNs >= MIN_STEP_INTERVAL_NS) {

                    // 6. Cadence continuity check
                    val withinCadenceWindow = intervalNs <= MAX_STEP_INTERVAL_NS

                    if (!gateOpen) {
                        // Gate is closed: accumulate candidates
                        if (withinCadenceWindow || gateCandidateCount == 0) {
                            gateCandidateCount++
                            if (gateCandidateCount >= GATE_STEPS_REQUIRED) {
                                // Gate opens! Count all gated candidates retroactively
                                gateOpen = true
                                _stepCount.value += GATE_STEPS_REQUIRED
                            }
                        } else {
                            // Cadence broken before gate opened — isolated motion, reset
                            gateCandidateCount = 1 // current peak starts new candidate window
                        }
                    } else {
                        // Gate is open: count step if within cadence window
                        if (withinCadenceWindow) {
                            _stepCount.value += 1
                        } else {
                            // Cadence broken: close gate, start new candidate window
                            gateOpen = false
                            gateCandidateCount = 1
                        }
                    }

                    lastStepTimestampNs = timestamp

                    // 7. Update adaptive threshold from recent peak amplitudes
                    if (recentPeakAmplitudes.size >= ADAPTIVE_WINDOW_SIZE) {
                        recentPeakAmplitudes.removeFirst()
                    }
                    recentPeakAmplitudes.addLast(peakAmplitude)
                    if (recentPeakAmplitudes.size >= 4) {
                        val avgPeak = recentPeakAmplitudes.average().toFloat()
                        dynamicThreshold = (avgPeak * ADAPTIVE_THRESHOLD_FACTOR)
                            .coerceIn(MIN_PEAK_THRESHOLD, MAX_PEAK_THRESHOLD * 0.5f)
                    }
                }
            }
        }

        // 8. Idle detection: if too much time passes with no step, close gate
        if (lastStepTimestampNs > 0 && (timestamp - lastStepTimestampNs) > IDLE_TIMEOUT_NS) {
            if (gateOpen || gateCandidateCount > 0) {
                gateOpen = false
                gateCandidateCount = 0
            }
        }

        previousSmoothed = smoothedMagnitude
        wasRising = isRising
        lastSensorTimestampNs = timestamp
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No action needed for accelerometer accuracy changes
    }
}
