package com.motioniq.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.motioniq.app.model.ActivityType
import com.motioniq.app.model.ConfidenceLevel
import com.motioniq.app.model.MovementActivity
import com.motioniq.app.model.RoutePoint
import com.motioniq.app.model.StepSource

@Entity(tableName = "activity_sessions")
data class ActivitySessionEntity(
    @PrimaryKey
    val id: String,
    val type: String,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val durationSeconds: Long,
    val steps: Long,
    val distanceMeters: Double,
    val caloriesKcal: Int,
    val avgSpeedKmh: Double,
    val avgPaceMinPerKm: Double,
    val startPlaceName: String,
    val endPlaceName: String,
    val confidenceLevel: String,
    val stepSource: String
) {
    fun toDomain(routePoints: List<RoutePoint> = emptyList()): MovementActivity {
        return MovementActivity(
            id = id,
            type = ActivityType.fromString(type),
            startTimeMillis = startTimeMillis,
            endTimeMillis = endTimeMillis,
            durationSeconds = durationSeconds,
            steps = steps,
            distanceMeters = distanceMeters,
            caloriesKcal = caloriesKcal,
            avgSpeedKmh = avgSpeedKmh,
            avgPaceMinPerKm = avgPaceMinPerKm,
            startPlaceName = startPlaceName,
            endPlaceName = endPlaceName,
            confidenceLevel = try { ConfidenceLevel.valueOf(confidenceLevel) } catch (_: Exception) { ConfidenceLevel.HIGH },
            stepSource = try { StepSource.valueOf(stepSource) } catch (_: Exception) { StepSource.HARDWARE_SENSOR },
            routePoints = routePoints
        )
    }

    companion object {
        fun fromDomain(activity: MovementActivity): ActivitySessionEntity {
            return ActivitySessionEntity(
                id = activity.id,
                type = activity.type.name,
                startTimeMillis = activity.startTimeMillis,
                endTimeMillis = activity.endTimeMillis,
                durationSeconds = activity.durationSeconds,
                steps = activity.steps,
                distanceMeters = activity.distanceMeters,
                caloriesKcal = activity.caloriesKcal,
                avgSpeedKmh = activity.avgSpeedKmh,
                avgPaceMinPerKm = activity.avgPaceMinPerKm,
                startPlaceName = activity.startPlaceName,
                endPlaceName = activity.endPlaceName,
                confidenceLevel = activity.confidenceLevel.name,
                stepSource = activity.stepSource.name
            )
        }
    }
}
