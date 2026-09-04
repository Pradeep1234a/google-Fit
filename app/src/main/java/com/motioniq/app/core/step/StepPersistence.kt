package com.motioniq.app.core.step

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*

/**
 * Persists step counting state across app restarts and reboots.
 *
 * Stores:
 * - Hardware counter baseline for the current day
 * - Last known raw hardware counter value
 * - Daily step totals (keyed by date string yyyy-MM-dd)
 * - The active data source
 * - Last update timestamp
 * - Step detector session accumulator
 *
 * All writes use apply() for async persistence.
 */
class StepPersistence(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("motioniq_step_engine", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CURRENT_DATE = "current_date"
        private const val KEY_HW_COUNTER_BASELINE = "hw_counter_baseline"
        private const val KEY_HW_COUNTER_LAST_RAW = "hw_counter_last_raw"
        private const val KEY_DAILY_STEPS = "daily_steps"
        private const val KEY_ACTIVE_SOURCE = "active_source"
        private const val KEY_LAST_UPDATE_MILLIS = "last_update_millis"
        private const val KEY_DETECTOR_SESSION_STEPS = "detector_session_steps"
        private const val KEY_SW_SESSION_STEPS = "sw_session_steps"
        private const val KEY_BOOT_COUNT_AT_BASELINE = "boot_count_at_baseline"
        private const val HISTORY_PREFIX = "history_"
        private const val MAX_HISTORY_DAYS = 90
    }

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        timeZone = TimeZone.getDefault()
    }

    fun getTodayDateString(): String = dateFormat.format(Date())

    // ── Current Date ──
    var currentDate: String
        get() = prefs.getString(KEY_CURRENT_DATE, getTodayDateString()) ?: getTodayDateString()
        set(value) = prefs.edit().putString(KEY_CURRENT_DATE, value).apply()

    // ── Hardware counter baseline (the raw counter value at the start of the day) ──
    var hwCounterBaseline: Long
        get() = prefs.getLong(KEY_HW_COUNTER_BASELINE, -1L)
        set(value) = prefs.edit().putLong(KEY_HW_COUNTER_BASELINE, value).apply()

    // ── Last observed raw hardware counter ──
    var hwCounterLastRaw: Long
        get() = prefs.getLong(KEY_HW_COUNTER_LAST_RAW, -1L)
        set(value) = prefs.edit().putLong(KEY_HW_COUNTER_LAST_RAW, value).apply()

    // ── Today's accumulated daily steps (the value displayed to user) ──
    var dailySteps: Long
        get() = prefs.getLong(KEY_DAILY_STEPS, 0L)
        set(value) = prefs.edit().putLong(KEY_DAILY_STEPS, value.coerceAtLeast(0L)).apply()

    // ── Detector-mode session steps (for TYPE_STEP_DETECTOR source) ──
    var detectorSessionSteps: Long
        get() = prefs.getLong(KEY_DETECTOR_SESSION_STEPS, 0L)
        set(value) = prefs.edit().putLong(KEY_DETECTOR_SESSION_STEPS, value).apply()

    // ── Software pedometer session steps ──
    var softwareSessionSteps: Long
        get() = prefs.getLong(KEY_SW_SESSION_STEPS, 0L)
        set(value) = prefs.edit().putLong(KEY_SW_SESSION_STEPS, value).apply()

    // ── Active data source ──
    var activeSource: StepSourceType
        get() {
            val name = prefs.getString(KEY_ACTIVE_SOURCE, StepSourceType.NONE.name)
            return try { StepSourceType.valueOf(name ?: StepSourceType.NONE.name) } catch (_: Exception) { StepSourceType.NONE }
        }
        set(value) = prefs.edit().putString(KEY_ACTIVE_SOURCE, value.name).apply()

    // ── Last update timestamp ──
    var lastUpdateMillis: Long
        get() = prefs.getLong(KEY_LAST_UPDATE_MILLIS, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_UPDATE_MILLIS, value).apply()

    // ── Boot count tracking for reboot detection ──
    var bootCountAtBaseline: Int
        get() = prefs.getInt(KEY_BOOT_COUNT_AT_BASELINE, -1)
        set(value) = prefs.edit().putInt(KEY_BOOT_COUNT_AT_BASELINE, value).apply()

    // ── Historical daily records ──
    fun saveHistoricalDay(date: String, steps: Long) {
        prefs.edit().putLong("$HISTORY_PREFIX$date", steps.coerceAtLeast(0L)).apply()
    }

    fun getHistoricalDay(date: String): Long {
        return prefs.getLong("$HISTORY_PREFIX$date", 0L)
    }

    /**
     * Returns historical daily step data for the last [days] days
     * (not including today). Ordered most-recent first.
     */
    fun getRecentHistory(days: Int = 30): List<Pair<String, Long>> {
        val result = mutableListOf<Pair<String, Long>>()
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1) // start from yesterday
        repeat(days.coerceAtMost(MAX_HISTORY_DAYS)) {
            val d = dateFormat.format(cal.time)
            val s = getHistoricalDay(d)
            if (s > 0) {
                result.add(d to s)
            }
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        return result
    }

    /**
     * Called at midnight rollover: archives the current daily steps
     * under the old date and resets for the new day.
     */
    fun rolloverDay(oldDate: String, stepsForOldDay: Long, newDate: String) {
        saveHistoricalDay(oldDate, stepsForOldDay)
        prefs.edit()
            .putString(KEY_CURRENT_DATE, newDate)
            .putLong(KEY_DAILY_STEPS, 0L)
            .putLong(KEY_HW_COUNTER_BASELINE, -1L) // will be recaptured on next sensor event
            .putLong(KEY_DETECTOR_SESSION_STEPS, 0L)
            .putLong(KEY_SW_SESSION_STEPS, 0L)
            .apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
