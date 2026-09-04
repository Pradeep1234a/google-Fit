package com.motioniq.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.motioniq.app.model.DailySummary

@Entity(tableName = "daily_stats")
data class DailyStatsEntity(
    @PrimaryKey
    val date: String, // "yyyy-MM-dd"
    val steps: Long,
    val distanceMeters: Double,
    val caloriesKcal: Int,
    val activeMinutes: Int,
    val activityCount: Int
) {
    fun toDomain(): DailySummary {
        return DailySummary(
            date = date,
            steps = steps,
            distanceMeters = distanceMeters,
            caloriesKcal = caloriesKcal,
            activeMinutes = activeMinutes,
            activityCount = activityCount
        )
    }

    companion object {
        fun fromDomain(summary: DailySummary): DailyStatsEntity {
            return DailyStatsEntity(
                date = summary.date,
                steps = summary.steps,
                distanceMeters = summary.distanceMeters,
                caloriesKcal = summary.caloriesKcal,
                activeMinutes = summary.activeMinutes,
                activityCount = summary.activityCount
            )
        }
    }
}
