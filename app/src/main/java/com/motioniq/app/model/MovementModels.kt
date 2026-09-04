package com.motioniq.app.model

enum class ConfidenceLevel {
    HIGH, MEDIUM, LOW
}

enum class StepSource {
    HARDWARE_SENSOR,
    HARDWARE_STEP_DETECTOR,
    SOFTWARE_PEDOMETER,
    HEALTH_CONNECT,
    ESTIMATED
}

data class RoutePoint(
    val latitude: Double,
    val longitude: Double,
    val altitudeMeters: Double? = null,
    val speedMps: Float? = null,
    val timestampMillis: Long = System.currentTimeMillis()
)

data class MovementActivity(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: ActivityType = ActivityType.WALKING,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val durationSeconds: Long,
    val steps: Long,
    val distanceMeters: Double,
    val caloriesKcal: Int,
    val avgSpeedKmh: Double,
    val avgPaceMinPerKm: Double,
    val startPlaceName: String = "Start Point",
    val endPlaceName: String = "End Point",
    val confidenceLevel: ConfidenceLevel = ConfidenceLevel.HIGH,
    val stepSource: StepSource = StepSource.HARDWARE_SENSOR,
    val routePoints: List<RoutePoint> = emptyList()
)

data class DailySummary(
    val date: String,
    val steps: Long = 0,
    val distanceMeters: Double = 0.0,
    val caloriesKcal: Int = 0,
    val activeMinutes: Int = 0,
    val activityCount: Int = 0
)

data class UserProfile(
    val name: String = "Runner",
    val weightKg: Double = 70.0,
    val heightCm: Double = 175.0,
    val age: Int = 28,
    val gender: String = "Prefer not to say",
    val dailyStepGoal: Int = 10000,
    val dailyDistanceGoalKm: Double = 5.0,
    val dailyActiveMinutesGoal: Int = 60,
    val isOnboarded: Boolean = true
)

data class ParkPlace(
    val id: String,
    val name: String,
    val type: String,
    val distanceKm: Double,
    val etaMinutes: Int,
    val latitude: Double,
    val longitude: Double,
    val difficulty: String,
    val greenSpace: String,
    val traffic: String,
    val description: String
)

enum class WorkoutState {
    IDLE,
    ACTIVE,
    PAUSED,
    COMPLETED
}
