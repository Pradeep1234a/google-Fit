package com.motioniq.app.core

import android.content.Context
import android.content.SharedPreferences
import com.motioniq.app.core.step.DistanceEstimator
import com.motioniq.app.core.step.StepCountingEngine
import com.motioniq.app.core.step.StepSourceType
import com.motioniq.app.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import java.text.SimpleDateFormat
import java.util.*

/**
 * Central repository managing movement data, workout tracking, and user profile.
 *
 * Integrates the production [StepCountingEngine] for real sensor-based step
 * counting. The engine handles all hardware/software sensor selection,
 * daily baselines, persistence, reboot recovery, and midnight rollover.
 *
 * This repository:
 * - Exposes todaySummary that automatically updates from real sensor data
 * - Manages workout sessions with real step counting during active workouts
 * - Persists user profile and goals
 * - Provides distance/calorie estimates based on real step counts
 */
class MotionRepository(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("motioniq_preferences", Context.MODE_PRIVATE)

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // --- Production Step Engine ---
    val stepEngine = StepCountingEngine(context)

    // User Profile & Goals
    private val _userProfile = MutableStateFlow(loadUserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Today's Aggregate Summary (driven by real sensor data)
    private val _todaySummary = MutableStateFlow(DailySummary(getCurrentDateString()))
    val todaySummary: StateFlow<DailySummary> = _todaySummary.asStateFlow()

    // Activity History (completed workout sessions)
    private val _activities = MutableStateFlow<List<MovementActivity>>(emptyList())
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
    private var workoutStartSteps = 0L // engine step count at workout start

    /** Backward compatibility for UI profile sensor check */
    val hasHardwareStepSensor: Boolean
        get() = stepEngine.capabilities.hasStepCounter || stepEngine.capabilities.hasStepDetector

    init {
        // Start the production step engine
        stepEngine.start()

        // Observe real step count and update today's summary
        scope.launch {
            stepEngine.todaySteps.collect { realSteps ->
                updateTodaySummaryFromEngine(realSteps)
            }
        }
    }

    private fun updateTodaySummaryFromEngine(steps: Long) {
        val profile = _userProfile.value
        val distanceMeters = DistanceEstimator.estimateDistanceMeters(steps, profile.heightCm)
        val calories = DistanceEstimator.estimateCalories(steps, profile.weightKg)
        val activeMinutes = estimateActiveMinutes(steps)

        _todaySummary.value = DailySummary(
            date = getCurrentDateString(),
            steps = steps,
            distanceMeters = distanceMeters,
            caloriesKcal = calories,
            activeMinutes = activeMinutes,
            activityCount = _activities.value.size
        )
    }

    /**
     * Estimate active minutes from step count.
     * Assumes average walking cadence of ~100 steps/min.
     */
    private fun estimateActiveMinutes(steps: Long): Int {
        if (steps <= 0) return 0
        return (steps / 100).toInt().coerceAtLeast(1)
    }

    // ── Workout Tracking ──

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
        workoutStartSteps = stepEngine.todaySteps.value

        // Starting position
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

                    // Get real steps from engine (delta since workout started)
                    val currentEngineSteps = stepEngine.todaySteps.value
                    val workoutSteps = (currentEngineSteps - workoutStartSteps).coerceAtLeast(0L)
                    _activeSteps.value = workoutSteps

                    // Calculate distance from real steps
                    val profile = _userProfile.value
                    val isRunning = type == ActivityType.RUNNING
                    val distance = DistanceEstimator.estimateDistanceMeters(workoutSteps, profile.heightCm, isRunning)
                    _activeDistanceMeters.value = distance

                    // Calculate speed and pace from real distance and time
                    val currentSpeed = GpsCalculator.calculateSpeedKmh(distance, duration)
                    _activeSpeedKmh.value = currentSpeed
                    _activePaceMinPerKm.value = GpsCalculator.calculatePaceMinPerKm(distance, duration)

                    // Calculate calories from real steps
                    _activeCalories.value = DistanceEstimator.estimateCalories(workoutSteps, profile.weightKg, isRunning)

                    // Update GPS coordinates for route canvas (simulated path based on distance)
                    if (type.isOutdoorGps && duration % 3 == 0L && workoutSteps > 0) {
                        val bearing = (duration % 360).toDouble() * Math.PI / 180.0
                        val stepDistMeters = distance / workoutSteps.coerceAtLeast(1L)
                        currentLat += Math.cos(bearing) * stepDistMeters * 0.000009
                        currentLng += Math.sin(bearing) * stepDistMeters * 0.000009
                        val newPoint = RoutePoint(
                            latitude = currentLat,
                            longitude = currentLng,
                            altitudeMeters = 920.0 + (duration % 15),
                            speedMps = (currentSpeed / 3.6).toFloat(),
                            timestampMillis = System.currentTimeMillis()
                        )
                        _activeRoutePoints.value = _activeRoutePoints.value + newPoint
                    }
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

        val stepSource = when (stepEngine.activeSource.value) {
            StepSourceType.HARDWARE_STEP_COUNTER -> StepSource.HARDWARE_SENSOR
            StepSourceType.HARDWARE_STEP_DETECTOR -> StepSource.HARDWARE_STEP_DETECTOR
            StepSourceType.SOFTWARE_ACCELEROMETER -> StepSource.SOFTWARE_PEDOMETER
            StepSourceType.NONE -> StepSource.ESTIMATED
        }

        val confidence = when (stepEngine.activeSource.value) {
            StepSourceType.HARDWARE_STEP_COUNTER -> ConfidenceLevel.HIGH
            StepSourceType.HARDWARE_STEP_DETECTOR -> ConfidenceLevel.HIGH
            StepSourceType.SOFTWARE_ACCELEROMETER -> ConfidenceLevel.MEDIUM
            StepSourceType.NONE -> ConfidenceLevel.LOW
        }

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
            startPlaceName = "Start Point",
            endPlaceName = "End Point",
            confidenceLevel = confidence,
            stepSource = stepSource,
            routePoints = _activeRoutePoints.value
        )
        _completedActivity.value = activity
        return activity
    }

    fun saveWorkout(activity: MovementActivity) {
        val updatedList = listOf(activity) + _activities.value
        _activities.value = updatedList
        _workoutState.value = WorkoutState.IDLE
        _completedActivity.value = null
        // Summary is already driven by stepEngine.todaySteps
    }

    fun discardWorkout() {
        _workoutState.value = WorkoutState.IDLE
        _completedActivity.value = null
    }

    // ── User Profile Management ──

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
        // Recalculate today's summary with new profile data
        updateTodaySummaryFromEngine(stepEngine.todaySteps.value)
    }

    fun completeOnboarding(profile: UserProfile) {
        updateUserProfile(profile.copy(isOnboarded = true))
    }

    fun resetData() {
        prefs.edit().clear().apply()
        stepEngine.persistence.clearAll()
        _todaySummary.value = DailySummary(getCurrentDateString())
        _activities.value = emptyList()
        _userProfile.value = UserProfile(isOnboarded = true)
    }

    private fun loadUserProfile(): UserProfile {
        return UserProfile(
            name = prefs.getString("user_name", "") ?: "",
            weightKg = prefs.getFloat("user_weight", 70.0f).toDouble(),
            heightCm = prefs.getFloat("user_height", 175.0f).toDouble(),
            age = prefs.getInt("user_age", 28),
            gender = prefs.getString("user_gender", "Not Specified") ?: "Not Specified",
            dailyStepGoal = prefs.getInt("user_step_goal", 10000),
            dailyDistanceGoalKm = prefs.getFloat("user_dist_goal", 5.0f).toDouble(),
            dailyActiveMinutesGoal = prefs.getInt("user_active_goal", 60),
            isOnboarded = prefs.getBoolean("user_onboarded", false)
        )
    }

    private fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
