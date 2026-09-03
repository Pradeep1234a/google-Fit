package com.motioniq.app.core

import com.motioniq.app.model.ActivityType
import kotlin.math.roundToInt

object CalorieCalculator {
    /**
     * Standard Metabolic Equivalent of Task (MET) formula:
     * Calories (kcal) = MET * Weight (kg) * Duration (hours)
     */
    fun calculate(
        activityType: ActivityType,
        weightKg: Double,
        durationSeconds: Long,
        speedKmh: Double = 0.0
    ): Int {
        val durationHours = durationSeconds / 3600.0
        val effectiveWeight = if (weightKg in 30.0..250.0) weightKg else 70.0

        val met = when (activityType) {
            ActivityType.WALKING -> {
                when {
                    speedKmh > 6.0 -> 4.5
                    speedKmh > 4.5 -> 3.8
                    else -> 3.0
                }
            }
            ActivityType.RUNNING -> {
                when {
                    speedKmh > 12.0 -> 12.5
                    speedKmh > 9.5 -> 10.0
                    else -> 8.5
                }
            }
            ActivityType.CYCLING -> {
                when {
                    speedKmh > 20.0 -> 9.0
                    speedKmh > 15.0 -> 7.5
                    else -> 6.0
                }
            }
            ActivityType.SPORTS -> 7.0
            ActivityType.JUMP -> 8.0
            ActivityType.SWIMMING -> 6.5
        }

        val totalKcal = met * effectiveWeight * durationHours
        return totalKcal.roundToInt().coerceAtLeast(1)
    }
}
