package com.motioniq.app.core

import android.content.Context
import android.content.SharedPreferences
import com.motioniq.app.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class MotionRepository(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("motioniq_preferences", Context.MODE_PRIVATE)

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    val stepTracker = StepTracker(context)

    // User Profile & Goals
    private val _userProfile = MutableStateFlow(loadUserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Today's Aggregate Summary
    private val _todaySummary = MutableStateFlow(loadTodaySummary())
    val todaySummary: StateFlow<DailySummary> = _todaySummary.asStateFlow()

    // Activity History
    private val _activities = MutableStateFlow(loadInitialActivities())
    val activities: StateFlow<List<MovementActivity>> = _activities.asStateFlow()

    // Active Workout Tracking State
    private val _workoutState = MutableStateFlow(WorkoutState.IDLE)
    val workoutState: StateFlow<WorkoutState> = _workoutState.asStateFlow()

    private val _activeActivityType = MutableStateFlow(ActivityType.WALKING)
    val activeActivityType: StateFlow<ActivityType> = _activeActivityType.asStateFlow()

    private val _activeDurationSeconds = MutableStateFlow(0L)
    val activeDurationSeconds: StateFlow<Long> = _activeDurationSeconds.asStateFlow()

    private val _activeDistanceMeters = MutableStateFlow(0.0)
    val activeDistanceMeters: StateFlow<Double> = _activeDistanceMeters.asStateFlow()

    private val _activeSteps = MutableStateFlow(0L)
    val activeSteps: StateFlow<Long> = _activeSteps.asStateFlow()

    private val _activeCalories = MutableStateFlow(0)
    val activeCalories: StateFlow<Int> = _activeCalories.asStateFlow()

    private val _activeSpeedKmh = MutableStateFlow(0.0)
    val activeSpeedKmh: StateFlow<Double> = _activeSpeedKmh.asStateFlow()

    private val _activePaceMinPerKm = MutableStateFlow(0.0)
    val activePaceMinPerKm: StateFlow<Double> = _activePaceMinPerKm.asStateFlow()

    private val _activeRoutePoints = MutableStateFlow<List<RoutePoint>>(emptyList())
    val activeRoutePoints: StateFlow<List<RoutePoint>> = _activeRoutePoints.asStateFlow()

    private val _completedActivity = MutableStateFlow<MovementActivity?>(null)
    val completedActivity: StateFlow<MovementActivity?> = _completedActivity.asStateFlow()

    // Explore Nearby Parks Dataset
    val nearbyParks = listOf(
        ParkPlace(
            id = "park_1",
            name = "Cubbon Green Park",
            type = "Park & Botanical Trail",
            distanceKm = 0.8,
            etaMinutes = 11,
            latitude = 12.9763,
            longitude = 77.5929,
            difficulty = "Easy",
            greenSpace = "High",
            traffic = "Zero",
            description = "Paved leafy walkway with ancient shade trees, gentle inclines, and quiet pedestrian-only paths."
        ),
        ParkPlace(
            id = "park_2",
            name = "Lakeside Fitness Trail",
            type = "Running Loop",
            distanceKm = 2.4,
            etaMinutes = 29,
            latitude = 12.9815,
            longitude = 77.6045,
            difficulty = "Moderate",
            greenSpace = "High",
            traffic = "Low",
            description = "3.2 km scenic lakeside dirt & gravel track designed for marathon training and interval running."
        ),
        ParkPlace(
            id = "park_3",
            name = "Highland Hill Park",
            type = "Elevation & Hiking",
            distanceKm = 4.6,
            etaMinutes = 55,
            latitude = 12.9650,
            longitude = 77.5810,
            difficulty = "Challenging",
            greenSpace = "Medium",
            traffic = "Low",
            description = "Ascending route featuring 120m elevation gain, stone steps, and panoramic city views."
        ),
        ParkPlace(
            id = "park_4",
            name = "Metro Sports Complex",
            type = "Athletic Arena",
            distanceKm = 1.6,
            etaMinutes = 20,
            latitude = 12.9880,
            longitude = 77.5990,
            difficulty = "Easy",
            greenSpace = "Low",
            traffic = "Medium",
            description = "Olympic-standard synthetic 400m track, outdoor calisthenics gym, and open basketball courts."
        ),
        ParkPlace(
            id = "park_5",
            name = "Riverbank Nature Path",
            type = "Scenic Walkway",
            distanceKm = 7.2,
            etaMinutes = 88,
            latitude = 12.9520,
            longitude = 77.6150,
            difficulty = "Moderate",
            greenSpace = "High",
            traffic = "Zero",
            description = "Extended continuous waterside greenway connecting multiple municipal gardens."
        )
    )

    private var workoutJob: Job? = null
    private var workoutStartTime = 0L

    init {
        stepTracker.startTracking()
    }

    fun startWorkout(type: ActivityType) {
        _activeActivityType.value = type
        _workoutState.value = WorkoutState.ACTIVE
        _activeDurationSeconds.value = 0L
        _activeDistanceMeters.value = 0.0
        _activeSteps.value = 0L
        _activeCalories.value = 0
        _activeSpeedKmh.value = 0.0
        _activePaceMinPerKm.value = 0.0
        _activeRoutePoints.value = emptyList()
        _completedActivity.value = null
        workoutStartTime = System.currentTimeMillis()

        // Base coordinate around downtown
        var currentLat = 12.9716
        var currentLng = 77.5946

        val initialPoint = RoutePoint(currentLat, currentLng, 920.0, 0f, workoutStartTime)
        _activeRoutePoints.value = listOf(initialPoint)

        workoutJob?.cancel()
        workoutJob = scope.launch {
            while (_workoutState.value == WorkoutState.ACTIVE || _workoutState.value == WorkoutState.PAUSED) {
                delay(1000L)
                if (_workoutState.value == WorkoutState.ACTIVE) {
                    _activeDurationSeconds.value += 1
                    val duration = _activeDurationSeconds.value

                    // Increment realistic movement
                    val speedFactor = when (type) {
                        ActivityType.RUNNING -> 2.8 // ~10 km/h
                        ActivityType.CYCLING -> 5.5 // ~20 km/h
                        else -> 1.35 // ~5 km/h
                    }
                    val deltaDist = speedFactor + (Math.random() - 0.5) * 0.4
                    _activeDistanceMeters.value += deltaDist

                    val stepIncrement = when (type) {
                        ActivityType.RUNNING -> if (duration % 2 == 0L) 3L else 2L
                        ActivityType.WALKING -> if (duration % 2 == 0L) 2L else 1L
                        else -> 0L
                    }
                    _activeSteps.value += stepIncrement

                    // Update live GPS coordinates (curved path)
                    if (type.isOutdoorGps && duration % 3 == 0L) {
                        currentLat += (Math.random() - 0.48) * 0.00018
                        currentLng += (Math.random() - 0.46) * 0.00022
                        val newPoint = RoutePoint(
                            latitude = currentLat,
                            longitude = currentLng,
                            altitudeMeters = 920.0 + (duration % 15),
                            speedMps = speedFactor.toFloat(),
                            timestampMillis = System.currentTimeMillis()
                        )
                        _activeRoutePoints.value = _activeRoutePoints.value + newPoint
                    }

                    val currentSpeed = GpsCalculator.calculateSpeedKmh(_activeDistanceMeters.value, duration)
                    _activeSpeedKmh.value = currentSpeed
                    _activePaceMinPerKm.value = GpsCalculator.calculatePaceMinPerKm(_activeDistanceMeters.value, duration)

                    _activeCalories.value = CalorieCalculator.calculate(
                        activityType = type,
                        weightKg = _userProfile.value.weightKg,
                        durationSeconds = duration,
                        speedKmh = currentSpeed
                    )
                }
            }
        }
    }

    fun pauseWorkout() {
        if (_workoutState.value == WorkoutState.ACTIVE) {
            _workoutState.value = WorkoutState.PAUSED
        }
    }

    fun resumeWorkout() {
        if (_workoutState.value == WorkoutState.PAUSED) {
            _workoutState.value = WorkoutState.ACTIVE
        }
    }

    fun stopWorkout(): MovementActivity {
        workoutJob?.cancel()
        _workoutState.value = WorkoutState.COMPLETED

        val endTime = System.currentTimeMillis()
        val duration = _activeDurationSeconds.value
        val dist = _activeDistanceMeters.value
        val steps = _activeSteps.value
        val cals = _activeCalories.value
        val type = _activeActivityType.value
        val speed = GpsCalculator.calculateSpeedKmh(dist, duration)
        val pace = GpsCalculator.calculatePaceMinPerKm(dist, duration)

        val activity = MovementActivity(
            type = type,
            startTimeMillis = workoutStartTime,
            endTimeMillis = endTime,
            durationSeconds = duration,
            steps = steps,
            distanceMeters = dist,
            caloriesKcal = cals,
            avgSpeedKmh = speed,
            avgPaceMinPerKm = pace,
            startPlaceName = "Central Park Trailhead",
            endPlaceName = "East Promenade",
            confidenceLevel = ConfidenceLevel.HIGH,
            stepSource = if (stepTracker.hasHardwareSensor) StepSource.HARDWARE_SENSOR else StepSource.ESTIMATED,
            routePoints = _activeRoutePoints.value
        )
        _completedActivity.value = activity
        return activity
    }

    fun saveWorkout(activity: MovementActivity) {
        val updatedList = listOf(activity) + _activities.value
        _activities.value = updatedList

        // Update Today's Aggregate Summary
        val currentSummary = _todaySummary.value
        val newSummary = currentSummary.copy(
            steps = currentSummary.steps + activity.steps,
            distanceMeters = currentSummary.distanceMeters + activity.distanceMeters,
            caloriesKcal = currentSummary.caloriesKcal + activity.caloriesKcal,
            activeMinutes = currentSummary.activeMinutes + (activity.durationSeconds / 60).toInt().coerceAtLeast(1),
            activityCount = currentSummary.activityCount + 1
        )
        _todaySummary.value = newSummary
        saveTodaySummary(newSummary)

        _workoutState.value = WorkoutState.IDLE
        _completedActivity.value = null
    }

    fun discardWorkout() {
        _workoutState.value = WorkoutState.IDLE
        _completedActivity.value = null
    }

    fun updateUserProfile(profile: UserProfile) {
        _userProfile.value = profile
        prefs.edit()
            .putString("user_name", profile.name)
            .putFloat("user_weight", profile.weightKg.toFloat())
            .putFloat("user_height", profile.heightCm.toFloat())
            .putInt("user_age", profile.age)
            .putString("user_gender", profile.gender)
            .putInt("user_step_goal", profile.dailyStepGoal)
            .putFloat("user_dist_goal", profile.dailyDistanceGoalKm.toFloat())
            .putInt("user_active_goal", profile.dailyActiveMinutesGoal)
            .putBoolean("user_onboarded", profile.isOnboarded)
            .apply()
    }

    fun completeOnboarding(profile: UserProfile) {
        updateUserProfile(profile.copy(isOnboarded = true))
    }

    fun resetData() {
        prefs.edit().clear().apply()
        _todaySummary.value = DailySummary(getCurrentDateString())
        _activities.value = emptyList()
        _userProfile.value = UserProfile(isOnboarded = true)
    }

    private fun loadUserProfile(): UserProfile {
        return UserProfile(
            name = prefs.getString("user_name", "Alex Rivera") ?: "Alex Rivera",
            weightKg = prefs.getFloat("user_weight", 68.5f).toDouble(),
            heightCm = prefs.getFloat("user_height", 176.0f).toDouble(),
            age = prefs.getInt("user_age", 26),
            gender = prefs.getString("user_gender", "Not Specified") ?: "Not Specified",
            dailyStepGoal = prefs.getInt("user_step_goal", 10000),
            dailyDistanceGoalKm = prefs.getFloat("user_dist_goal", 6.0f).toDouble(),
            dailyActiveMinutesGoal = prefs.getInt("user_active_goal", 60),
            isOnboarded = prefs.getBoolean("user_onboarded", true)
        )
    }

    private fun loadTodaySummary(): DailySummary {
        val today = getCurrentDateString()
        val savedDate = prefs.getString("today_date", today) ?: today
        if (savedDate != today) {
            return DailySummary(today, steps = 4250, distanceMeters = 3120.0, caloriesKcal = 184, activeMinutes = 38, activityCount = 1)
        }
        return DailySummary(
            date = today,
            steps = prefs.getLong("today_steps", 8426L),
            distanceMeters = prefs.getFloat("today_distance", 6200.0f).toDouble(),
            caloriesKcal = prefs.getInt("today_calories", 342),
            activeMinutes = prefs.getInt("today_active_min", 72),
            activityCount = prefs.getInt("today_activity_count", 3)
        )
    }

    private fun saveTodaySummary(summary: DailySummary) {
        prefs.edit()
            .putString("today_date", summary.date)
            .putLong("today_steps", summary.steps)
            .putFloat("today_distance", summary.distanceMeters.toFloat())
            .putInt("today_calories", summary.caloriesKcal)
            .putInt("today_active_min", summary.activeMinutes)
            .putInt("today_activity_count", summary.activityCount)
            .apply()
    }

    private fun loadInitialActivities(): List<MovementActivity> {
        val now = System.currentTimeMillis()
        val dummyRoute = listOf(
            RoutePoint(12.9716, 77.5946),
            RoutePoint(12.9725, 77.5958),
            RoutePoint(12.9738, 77.5971),
            RoutePoint(12.9752, 77.5985),
            RoutePoint(12.9765, 77.6001),
            RoutePoint(12.9778, 77.6015)
        )

        return listOf(
            MovementActivity(
                type = ActivityType.WALKING,
                startTimeMillis = now - 7200000,
                endTimeMillis = now - 3600000,
                durationSeconds = 3501,
                steps = 4120,
                distanceMeters = 3100.0,
                caloriesKcal = 168,
                avgSpeedKmh = 4.8,
                avgPaceMinPerKm = 12.5,
                startPlaceName = "Home",
                endPlaceName = "Cubbon Park",
                confidenceLevel = ConfidenceLevel.HIGH,
                stepSource = StepSource.HARDWARE_SENSOR,
                routePoints = dummyRoute
            ),
            MovementActivity(
                type = ActivityType.RUNNING,
                startTimeMillis = now - 18000000,
                endTimeMillis = now - 16200000,
                durationSeconds = 1694,
                steps = 3450,
                distanceMeters = 2400.0,
                caloriesKcal = 142,
                avgSpeedKmh = 8.5,
                avgPaceMinPerKm = 7.05,
                startPlaceName = "Stadium Gate A",
                endPlaceName = "Lake Perimeter",
                confidenceLevel = ConfidenceLevel.HIGH,
                stepSource = StepSource.HARDWARE_SENSOR,
                routePoints = dummyRoute.reversed()
            ),
            MovementActivity(
                type = ActivityType.WALKING,
                startTimeMillis = now - 28800000,
                endTimeMillis = now - 28200000,
                durationSeconds = 600,
                steps = 856,
                distanceMeters = 700.0,
                caloriesKcal = 32,
                avgSpeedKmh = 4.2,
                avgPaceMinPerKm = 14.2,
                startPlaceName = "Coffee House",
                endPlaceName = "Metro Station",
                confidenceLevel = ConfidenceLevel.MEDIUM,
                stepSource = StepSource.HARDWARE_SENSOR,
                routePoints = dummyRoute.take(3)
            )
        )
    }

    private fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
