package com.motioniq.app.core.step

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Production step-counting engine that intelligently selects the best available
 * data source and provides reliable daily step counts.
 *
 * Architecture:
 * - Probes device capabilities via [SensorCapabilities]
 * - Selects best source: HW Step Counter > HW Step Detector > SW Accelerometer
 * - Maintains daily baseline for hardware counter (cumulative counter since boot)
 * - Persists state across app restarts, process death, and reboots
 * - Handles midnight rollover (date boundary)
 * - Never double-counts between sources
 * - Never fakes data
 *
 * Source-of-truth strategy:
 * - Only ONE source is active at any time (no fusion/mixing)
 * - Hardware step counter is authoritative when available
 * - If HW counter becomes unavailable, falls back to detector or software
 * - Transition between sources preserves accumulated count for the day
 */
class StepCountingEngine(private val context: Context) : SensorEventListener {

    companion object {
        private const val TAG = "StepCountingEngine"
        // Maximum plausible daily steps. Anything above this suggests counter reset.
        private const val MAX_PLAUSIBLE_DAILY_STEPS = 100_000L
        // Maximum plausible delta between sensor readings (reboot/reset detection)
        private const val MAX_PLAUSIBLE_DELTA = 50_000L
    }

    val capabilities = SensorCapabilities(context)
    val persistence = StepPersistence(context)

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val softwarePedometer = SoftwarePedometer(context)

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // ── Public reactive state ──
    private val _todaySteps = MutableStateFlow(0L)
    val todaySteps: StateFlow<Long> = _todaySteps.asStateFlow()

    private val _activeSource = MutableStateFlow(StepSourceType.NONE)
    val activeSource: StateFlow<StepSourceType> = _activeSource.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private val _lastUpdatedMillis = MutableStateFlow(0L)
    val lastUpdatedMillis: StateFlow<Long> = _lastUpdatedMillis.asStateFlow()

    // ── Internal state ──
    private var hwCounterBaseline: Long = -1L
    private var hwCounterLastRaw: Long = -1L
    private var stepsBeforeSourceSwitch: Long = 0L // steps accumulated under previous source before switching
    private var isListening = false
    private var dateCheckJob: Job? = null

    /**
     * Initialize engine: restore persisted state and start listening.
     */
    fun start() {
        if (isListening) return
        Log.i(TAG, "Starting step engine. Capabilities: ${capabilities.buildDiagnosticReport()}")

        // Restore persisted state
        restoreState()

        // Check for date change since last run
        checkDateRollover()

        // Select and start the best source
        val selectedSource = capabilities.bestStepSource()
        _activeSource.value = selectedSource
        persistence.activeSource = selectedSource

        when (selectedSource) {
            StepSourceType.HARDWARE_STEP_COUNTER -> startHardwareStepCounter()
            StepSourceType.HARDWARE_STEP_DETECTOR -> startHardwareStepDetector()
            StepSourceType.SOFTWARE_ACCELEROMETER -> startSoftwarePedometer()
            StepSourceType.NONE -> {
                Log.w(TAG, "No step sensors available on this device")
            }
        }

        // Start periodic date-change checker (every 30 seconds)
        startDateChecker()

        isListening = true
        _isTracking.value = selectedSource != StepSourceType.NONE
        Log.i(TAG, "Engine started. Source: $selectedSource, Restored daily steps: ${_todaySteps.value}")
    }

    fun stop() {
        if (!isListening) return
        dateCheckJob?.cancel()
        sensorManager?.unregisterListener(this)
        softwarePedometer.stopTracking()
        persistState()
        isListening = false
        _isTracking.value = false
        Log.i(TAG, "Engine stopped. Daily steps: ${_todaySteps.value}")
    }

    // ── Sensor event handling ──
    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        when (event.sensor.type) {
            Sensor.TYPE_STEP_COUNTER -> handleStepCounter(event)
            Sensor.TYPE_STEP_DETECTOR -> handleStepDetector(event)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "Sensor accuracy changed: ${sensor?.name} -> $accuracy")
    }

    // ── TYPE_STEP_COUNTER handling ──
    //
    // The hardware step counter reports a cumulative count since last device reboot.
    // We track a daily baseline and compute: todaySteps = rawCounter - baseline
    //
    // Edge cases handled:
    // - First event after install (baseline = -1, capture it)
    // - App restart (restore baseline from persistence)
    // - Device reboot (counter resets to 0, detect via counter < baseline)
    // - Midnight rollover (archive old day, reset baseline)
    private fun handleStepCounter(event: SensorEvent) {
        val rawCounter = event.values[0].toLong()

        // 1. First-ever event or uninitialized baseline
        if (hwCounterBaseline < 0L) {
            hwCounterBaseline = rawCounter
            persistence.hwCounterBaseline = rawCounter
            hwCounterLastRaw = rawCounter
            persistence.hwCounterLastRaw = rawCounter
            Log.d(TAG, "HW counter baseline captured: $rawCounter")
            return
        }

        // 2. Detect reboot or counter reset:
        //    The counter should be monotonically increasing. If it's significantly
        //    less than our baseline, a reboot has occurred.
        if (rawCounter < hwCounterBaseline) {
            Log.w(TAG, "HW counter reset detected (raw=$rawCounter < baseline=$hwCounterBaseline). " +
                    "Preserving ${_todaySteps.value} steps and recapturing baseline.")
            // Preserve steps counted so far today, recapture new baseline
            stepsBeforeSourceSwitch = _todaySteps.value
            hwCounterBaseline = rawCounter
            persistence.hwCounterBaseline = rawCounter
            hwCounterLastRaw = rawCounter
            persistence.hwCounterLastRaw = rawCounter
            return
        }

        // 3. Normal operation: compute delta from baseline
        val delta = rawCounter - hwCounterBaseline

        // 4. Sanity check: reject impossibly large jumps (data corruption)
        if (delta > MAX_PLAUSIBLE_DAILY_STEPS) {
            Log.w(TAG, "Implausible daily delta: $delta. Possible counter corruption. Recapturing baseline.")
            stepsBeforeSourceSwitch = _todaySteps.value
            hwCounterBaseline = rawCounter
            persistence.hwCounterBaseline = rawCounter
            hwCounterLastRaw = rawCounter
            persistence.hwCounterLastRaw = rawCounter
            return
        }

        // 5. Check for large jumps between readings (> MAX_PLAUSIBLE_DELTA)
        if (hwCounterLastRaw > 0 && rawCounter - hwCounterLastRaw > MAX_PLAUSIBLE_DELTA) {
            Log.w(TAG, "Large inter-reading jump: ${rawCounter - hwCounterLastRaw}. Adjusting baseline.")
            stepsBeforeSourceSwitch = _todaySteps.value
            hwCounterBaseline = rawCounter
            persistence.hwCounterBaseline = rawCounter
        }

        // 6. Update daily steps
        val newDailySteps = stepsBeforeSourceSwitch + delta
        _todaySteps.value = newDailySteps.coerceAtLeast(0L)

        // 7. Persist state
        hwCounterLastRaw = rawCounter
        persistence.hwCounterLastRaw = rawCounter
        persistence.dailySteps = _todaySteps.value
        persistence.lastUpdateMillis = System.currentTimeMillis()
        _lastUpdatedMillis.value = System.currentTimeMillis()
    }

    // ── TYPE_STEP_DETECTOR handling ──
    //
    // Each event represents exactly one step. We simply accumulate.
    private fun handleStepDetector(event: SensorEvent) {
        if (event.values[0] == 1.0f) {
            val newTotal = stepsBeforeSourceSwitch + persistence.detectorSessionSteps + 1
            persistence.detectorSessionSteps = persistence.detectorSessionSteps + 1
            _todaySteps.value = newTotal.coerceAtLeast(0L)
            persistence.dailySteps = _todaySteps.value
            persistence.lastUpdateMillis = System.currentTimeMillis()
            _lastUpdatedMillis.value = System.currentTimeMillis()
        }
    }

    // ── Software pedometer integration ──
    private fun startSoftwarePedometer() {
        softwarePedometer.resetCount()
        softwarePedometer.startTracking()
        // Collect software step changes
        scope.launch {
            softwarePedometer.stepCount.collect { swSteps ->
                val newTotal = stepsBeforeSourceSwitch + persistence.softwareSessionSteps + swSteps
                _todaySteps.value = newTotal.coerceAtLeast(0L)
                persistence.softwareSessionSteps = swSteps
                persistence.dailySteps = _todaySteps.value
                persistence.lastUpdateMillis = System.currentTimeMillis()
                _lastUpdatedMillis.value = System.currentTimeMillis()
            }
        }
        Log.i(TAG, "Software pedometer started as fallback")
    }

    // ── Sensor registration helpers ──
    private fun startHardwareStepCounter() {
        val sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) ?: run {
            Log.e(TAG, "TYPE_STEP_COUNTER sensor disappeared. Falling back.")
            fallbackToNextSource(StepSourceType.HARDWARE_STEP_COUNTER)
            return
        }
        // Use SENSOR_DELAY_NORMAL for battery efficiency. The step counter
        // is inherently low-frequency and batched by hardware.
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        Log.i(TAG, "Hardware step counter registered")
    }

    private fun startHardwareStepDetector() {
        val sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) ?: run {
            Log.e(TAG, "TYPE_STEP_DETECTOR sensor disappeared. Falling back.")
            fallbackToNextSource(StepSourceType.HARDWARE_STEP_DETECTOR)
            return
        }
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        Log.i(TAG, "Hardware step detector registered")
    }

    private fun fallbackToNextSource(failedSource: StepSourceType) {
        stepsBeforeSourceSwitch = _todaySteps.value
        val nextSource = when (failedSource) {
            StepSourceType.HARDWARE_STEP_COUNTER -> {
                if (capabilities.hasStepDetector) StepSourceType.HARDWARE_STEP_DETECTOR
                else if (capabilities.hasAccelerometer) StepSourceType.SOFTWARE_ACCELEROMETER
                else StepSourceType.NONE
            }
            StepSourceType.HARDWARE_STEP_DETECTOR -> {
                if (capabilities.hasAccelerometer) StepSourceType.SOFTWARE_ACCELEROMETER
                else StepSourceType.NONE
            }
            else -> StepSourceType.NONE
        }
        _activeSource.value = nextSource
        persistence.activeSource = nextSource
        when (nextSource) {
            StepSourceType.HARDWARE_STEP_COUNTER -> startHardwareStepCounter()
            StepSourceType.HARDWARE_STEP_DETECTOR -> startHardwareStepDetector()
            StepSourceType.SOFTWARE_ACCELEROMETER -> startSoftwarePedometer()
            StepSourceType.NONE -> Log.e(TAG, "No fallback sensor available")
        }
    }

    // ── State persistence ──
    private fun restoreState() {
        val today = persistence.getTodayDateString()
        val savedDate = persistence.currentDate

        if (savedDate == today) {
            // Same day: restore accumulated steps
            _todaySteps.value = persistence.dailySteps
            hwCounterBaseline = persistence.hwCounterBaseline
            hwCounterLastRaw = persistence.hwCounterLastRaw
            stepsBeforeSourceSwitch = 0L
            // If using hw counter, stepsBeforeSourceSwitch is 0 because
            // baseline + delta already accounts for everything
            Log.d(TAG, "Restored state for $today: ${_todaySteps.value} steps, baseline=$hwCounterBaseline")
        } else {
            // Different day: the app was last active on a previous day
            // Archive old day's data and start fresh
            Log.i(TAG, "Date changed: $savedDate -> $today. Archiving old day.")
            persistence.rolloverDay(savedDate, persistence.dailySteps, today)
            _todaySteps.value = 0L
            hwCounterBaseline = -1L // will be recaptured on first sensor event
            hwCounterLastRaw = -1L
            stepsBeforeSourceSwitch = 0L
        }

        _lastUpdatedMillis.value = persistence.lastUpdateMillis
    }

    private fun persistState() {
        persistence.dailySteps = _todaySteps.value
        if (hwCounterBaseline >= 0) persistence.hwCounterBaseline = hwCounterBaseline
        if (hwCounterLastRaw >= 0) persistence.hwCounterLastRaw = hwCounterLastRaw
        persistence.lastUpdateMillis = System.currentTimeMillis()
    }

    // ── Midnight rollover detection ──
    private fun startDateChecker() {
        dateCheckJob?.cancel()
        dateCheckJob = scope.launch {
            while (isActive) {
                delay(30_000L) // Check every 30 seconds
                checkDateRollover()
            }
        }
    }

    private fun checkDateRollover() {
        val today = persistence.getTodayDateString()
        val savedDate = persistence.currentDate
        if (savedDate != today) {
            Log.i(TAG, "Midnight rollover detected: $savedDate -> $today")
            val oldSteps = _todaySteps.value
            persistence.rolloverDay(savedDate, oldSteps, today)

            // Reset daily tracking
            _todaySteps.value = 0L
            stepsBeforeSourceSwitch = 0L
            hwCounterBaseline = -1L // will be recaptured on next sensor event
            hwCounterLastRaw = -1L

            // Reset session counters for detector/software sources
            softwarePedometer.resetCount()
        }
    }

    // ── Public API for Repositories & Analytics ──

    /** Returns historical daily step data (not including today) */
    fun getRecentHistory(days: Int = 30): List<Pair<String, Long>> {
        return persistence.getRecentHistory(days)
    }

    /** Returns steps for a specific date */
    fun getStepsForDate(date: String): Long {
        return if (date == persistence.getTodayDateString()) _todaySteps.value
        else persistence.getHistoricalDay(date)
    }

    /** Weekly total (last 7 days including today) */
    fun getWeeklyTotal(): Long {
        var total = _todaySteps.value
        val history = persistence.getRecentHistory(6)
        total += history.sumOf { it.second }
        return total
    }

    /** Average daily steps over last N days */
    fun getAverageDailySteps(days: Int = 7): Long {
        val history = persistence.getRecentHistory(days)
        if (history.isEmpty()) return _todaySteps.value
        val total = history.sumOf { it.second } + _todaySteps.value
        return total / (history.size + 1)
    }

    /** Best day in recent history */
    fun getBestDay(): Pair<String, Long>? {
        val history = persistence.getRecentHistory(30)
        val today = persistence.getTodayDateString() to _todaySteps.value
        return (history + today).maxByOrNull { it.second }
    }

    /**
     * Returns step counts for the last 7 days (including today)
     * formatted with short weekday names (e.g. "Mon", "Tue", etc.)
     * for direct use in analytics bar charts.
     */
    fun getWeeklyDaysSteps(): List<Pair<String, Long>> {
        val dayFormat = java.text.SimpleDateFormat("EEE", java.util.Locale.US)
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        val result = mutableListOf<Pair<String, Long>>()
        val cal = java.util.Calendar.getInstance()

        // Generate past 6 days + today in chronological order
        cal.add(java.util.Calendar.DAY_OF_YEAR, -6)
        repeat(6) {
            val dateStr = dateFormat.format(cal.time)
            val dayName = dayFormat.format(cal.time)
            val steps = persistence.getHistoricalDay(dateStr)
            result.add(dayName to steps)
            cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }
        // Today
        val todayName = dayFormat.format(java.util.Date())
        result.add(todayName to _todaySteps.value)
        return result
    }

    /** Returns a diagnostic report for debugging */
    fun getDiagnostics(): Map<String, Any> = buildMap {
        putAll(capabilities.buildDiagnosticReport())
        put("active_source", _activeSource.value.name)
        put("today_steps", _todaySteps.value)
        put("hw_counter_baseline", hwCounterBaseline)
        put("hw_counter_last_raw", hwCounterLastRaw)
        put("steps_before_switch", stepsBeforeSourceSwitch)
        put("persisted_daily_steps", persistence.dailySteps)
        put("persisted_date", persistence.currentDate)
        put("is_tracking", isListening)
        if (capabilities.hasAccelerometer) {
            putAll(softwarePedometer.getDiagnostics())
        }
    }
}
