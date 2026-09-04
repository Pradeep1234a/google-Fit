package com.motioniq.app.data.repository

import android.content.Context
import com.motioniq.app.core.GpsCalculator
import com.motioniq.app.core.location.LocationTracker
import com.motioniq.app.core.step.DistanceEstimator
import com.motioniq.app.core.step.StepCountingEngine
import com.motioniq.app.core.step.StepForegroundService
import com.motioniq.app.core.step.StepSourceType
import com.motioniq.app.data.local.db.dao.ActivitySessionDao
import com.motioniq.app.data.local.db.dao.RoutePointDao
import com.motioniq.app.data.local.db.entity.ActivitySessionEntity
import com.motioniq.app.data.local.db.entity.RoutePointEntity
import com.motioniq.app.model.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivitySessionRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sessionDao: ActivitySessionDao,
    private val routePointDao: RoutePointDao,
    val stepEngine: StepCountingEngine,
    val locationTracker: LocationTracker,
    val healthConnect: com.motioniq.app.core.health.HealthConnectBridge,
    private val userProfileRepository: UserProfileRepository
) {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Active Workout State
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

    private var workoutJob: Job? = null
    private var workoutStartTime = 0L
    private var workoutStartSteps = 0L
    private var isUsingRealGps = false

    // Persistent activities flow from Room
    val allSessions: Flow<List<MovementActivity>> = sessionDao.getAllSessions().map { entities ->
        entities.map { it.toDomain() }
    }

    fun getRecentSessions(limit: Int): Flow<List<MovementActivity>> {
        return sessionDao.getRecentSessions(limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getSessionWithRoute(id: String): MovementActivity? {
        val entity = sessionDao.getSessionById(id) ?: return null
        val routeEntities = routePointDao.getPointsForSession(id)
        return entity.toDomain(routeEntities.map { it.toDomain() })
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
        workoutStartSteps = stepEngine.todaySteps.value

        StepForegroundService.start(
            context,
            "MOTIONIQ — ${type.displayName}",
            "Recording biomechanical telemetry..."
        )

        // Real GPS tracking initialization
        isUsingRealGps = false
        if (type.isOutdoorGps && locationTracker.hasPermission()) {
            val lastLoc = locationTracker.getLastKnownLocation()
            if (lastLoc != null) {
                val initialPoint = RoutePoint(
                    latitude = lastLoc.latitude,
                    longitude = lastLoc.longitude,
                    altitudeMeters = if (lastLoc.hasAltitude()) lastLoc.altitude else null,
                    speedMps = if (lastLoc.hasSpeed()) lastLoc.speed else null,
                    timestampMillis = workoutStartTime
                )
                _activeRoutePoints.value = listOf(initialPoint)
            }

            val trackingStarted = locationTracker.startTracking { newPoint ->
                if (_workoutState.value == WorkoutState.ACTIVE) {
                    _activeRoutePoints.value = _activeRoutePoints.value + newPoint
                    isUsingRealGps = true
                }
            }
            isUsingRealGps = trackingStarted
        }

        workoutJob?.cancel()
        workoutJob = scope.launch {
            var currentProfile = UserProfile()
            userProfileRepository.userProfile.collectLatest { profile ->
                currentProfile = profile
            }
        }

        workoutJob = scope.launch {
            while (_workoutState.value == WorkoutState.ACTIVE || _workoutState.value == WorkoutState.PAUSED) {
                delay(1000L)
                if (_workoutState.value == WorkoutState.ACTIVE) {
                    _activeDurationSeconds.value += 1
                    val duration = _activeDurationSeconds.value

                    // Real step delta since session started
                    val currentEngineSteps = stepEngine.todaySteps.value
                    val workoutSteps = (currentEngineSteps - workoutStartSteps).coerceAtLeast(0L)
                    _activeSteps.value = workoutSteps

                    val userProfile = userProfileRepository.userProfile.first()
                    val isRunning = type == ActivityType.RUNNING

                    // Real distance fusion: GPS distance if >= 2 points available, else step-based estimation
                    val gpsDistance = if (_activeRoutePoints.value.size >= 2) {
                        GpsCalculator.calculateRouteDistanceMeters(_activeRoutePoints.value)
                    } else 0.0

                    val stepDistance = DistanceEstimator.estimateDistanceMeters(workoutSteps, userProfile.heightCm, isRunning)
                    val finalDistance = if (isUsingRealGps && gpsDistance > 5.0) gpsDistance else stepDistance
                    _activeDistanceMeters.value = finalDistance

                    // Speed and Pace from real distance and duration
                    val currentSpeed = GpsCalculator.calculateSpeedKmh(finalDistance, duration)
                    _activeSpeedKmh.value = currentSpeed
                    _activePaceMinPerKm.value = GpsCalculator.calculatePaceMinPerKm(finalDistance, duration)

                    // Calories from real MET and steps
                    _activeCalories.value = DistanceEstimator.estimateCalories(workoutSteps, userProfile.weightKg, isRunning)

                    // Live notification update every 3s
                    if (duration % 3 == 0L) {
                        val distKmStr = String.format(Locale.US, "%.2f km", finalDistance / 1000.0)
                        val durStr = GpsCalculator.formatDuration(duration)
                        StepForegroundService.update(
                            context,
                            "MOTIONIQ — ${type.displayName}",
                            "$durStr | $distKmStr | $workoutSteps steps"
                        )
                    }
                }
            }
        }
    }

    fun pauseWorkout() {
        if (_workoutState.value == WorkoutState.ACTIVE) {
            _workoutState.value = WorkoutState.PAUSED
            val durStr = GpsCalculator.formatDuration(_activeDurationSeconds.value)
            val distKmStr = String.format(Locale.US, "%.2f km", _activeDistanceMeters.value / 1000.0)
            StepForegroundService.update(
                context,
                "MOTIONIQ — Paused",
                "$durStr | $distKmStr (Paused)"
            )
        }
    }

    fun resumeWorkout() {
        if (_workoutState.value == WorkoutState.PAUSED) {
            _workoutState.value = WorkoutState.ACTIVE
            val durStr = GpsCalculator.formatDuration(_activeDurationSeconds.value)
            val distKmStr = String.format(Locale.US, "%.2f km", _activeDistanceMeters.value / 1000.0)
            StepForegroundService.update(
                context,
                "MOTIONIQ — ${_activeActivityType.value.displayName}",
                "$durStr | $distKmStr | ${_activeSteps.value} steps"
            )
        }
    }

    fun stopWorkout(): MovementActivity {
        workoutJob?.cancel()
        locationTracker.stopTracking()
        StepForegroundService.stop(context)
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

        val startName = if (isUsingRealGps && _activeRoutePoints.value.isNotEmpty()) "GPS Origin" else "Session Start"
        val endName = if (isUsingRealGps && _activeRoutePoints.value.isNotEmpty()) "GPS Finish" else "Session Finish"

        val activity = MovementActivity(
            id = UUID.randomUUID().toString(),
            type = type,
            startTimeMillis = workoutStartTime,
            endTimeMillis = endTime,
            durationSeconds = duration,
            steps = steps,
            distanceMeters = dist,
            caloriesKcal = cals,
            avgSpeedKmh = speed,
            avgPaceMinPerKm = pace,
            startPlaceName = startName,
            endPlaceName = endName,
            confidenceLevel = confidence,
            stepSource = stepSource,
            routePoints = _activeRoutePoints.value
        )
        _completedActivity.value = activity
        return activity
    }

    suspend fun saveWorkout(activity: MovementActivity) {
        val entity = ActivitySessionEntity.fromDomain(activity)
        sessionDao.insertSession(entity)

        if (activity.routePoints.isNotEmpty()) {
            val pointEntities = activity.routePoints.map { point ->
                RoutePointEntity.fromDomain(activity.id, point)
            }
            routePointDao.insertPoints(pointEntities)
        }

        // Synchronize with Android Health Connect
        scope.launch(Dispatchers.IO) {
            try {
                healthConnect.syncCompletedActivity(activity)
            } catch (_: Exception) {}
        }

        _workoutState.value = WorkoutState.IDLE
        _completedActivity.value = null
        StepForegroundService.stop(context)
    }

    fun discardWorkout() {
        locationTracker.stopTracking()
        StepForegroundService.stop(context)
        _workoutState.value = WorkoutState.IDLE
        _completedActivity.value = null
    }

    suspend fun deleteSession(id: String) {
        routePointDao.deletePointsForSession(id)
        sessionDao.deleteSessionById(id)
    }

    suspend fun clearAll() {
        routePointDao.deleteAllPoints()
        sessionDao.deleteAllSessions()
    }
}
