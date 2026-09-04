package com.motioniq.app.core.step

import com.motioniq.app.model.UserProfile
import kotlin.math.roundToInt

/**
 * Estimates distance and calories from step counts.
 *
 * These are ESTIMATES, not precise measurements. Actual values depend
 * on stride length, terrain, speed, individual biomechanics, etc.
 *
 * Stride estimation: approximately 0.415 * heightCm (walking).
 * Running stride is about 20% longer.
 *
 * Calorie estimation uses the standard MET formula:
 *   kcal = MET * weight_kg * duration_hours
 * Walking MET ≈ 3.5, Running MET ≈ 8.0
 * Simplified: ~0.04 kcal per step (walking, 70kg person)
 */
object DistanceEstimator {

    /**
     * Estimate distance in meters from step count and user profile.
     *
     * @param steps     Number of steps
     * @param heightCm  User height in centimeters (used for stride estimation)
     * @param isRunning Whether the user is running (longer stride)
     * @return Estimated distance in meters (rounded to nearest meter)
     */
    fun estimateDistanceMeters(steps: Long, heightCm: Double, isRunning: Boolean = false): Double {
        if (steps <= 0) return 0.0
        val safeHeight = heightCm.coerceIn(120.0, 220.0)
        // Walking stride ≈ 0.415 * height (cm) → meters
        // Running stride ≈ 20% longer
        val strideMeters = if (isRunning) {
            safeHeight * 0.415 * 1.2 / 100.0
        } else {
            safeHeight * 0.415 / 100.0
        }
        return steps * strideMeters
    }

    /**
     * Estimate distance in kilometers with sensible rounding (1 decimal place).
     */
    fun estimateDistanceKm(steps: Long, heightCm: Double, isRunning: Boolean = false): Double {
        val meters = estimateDistanceMeters(steps, heightCm, isRunning)
        return (meters / 100.0).roundToInt() / 10.0 // rounds to 0.1 km
    }

    /**
     * Estimate calories burned from steps.
     *
     * Uses a simplified formula:
     *   Walking: ~0.04 kcal/step * (weight/70) scaling
     *   Running: ~0.08 kcal/step * (weight/70) scaling
     *
     * This is an approximation. Real calorie burn depends on speed,
     * terrain, fitness level, metabolic rate, and many other factors.
     */
    fun estimateCalories(steps: Long, weightKg: Double, isRunning: Boolean = false): Int {
        if (steps <= 0) return 0
        val safeWeight = weightKg.coerceIn(30.0, 250.0)
        val weightFactor = safeWeight / 70.0
        val kcalPerStep = if (isRunning) 0.08 else 0.04
        return (steps * kcalPerStep * weightFactor).roundToInt().coerceAtLeast(1)
    }

    /**
     * Format distance for display.
     * < 1km → meters (e.g., "~820 m")
     * >= 1km → km with 1 decimal (e.g., "~3.2 km")
     */
    fun formatDistance(steps: Long, heightCm: Double): String {
        val meters = estimateDistanceMeters(steps, heightCm)
        return if (meters < 1000) {
            "~${meters.roundToInt()} m"
        } else {
            "~${"%.1f".format(meters / 1000.0)} km"
        }
    }
}
