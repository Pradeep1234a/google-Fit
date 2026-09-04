package com.motioniq.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motioniq.app.data.repository.ActivitySessionRepository
import com.motioniq.app.model.ActivityType
import com.motioniq.app.model.MovementActivity
import com.motioniq.app.model.RoutePoint
import com.motioniq.app.model.WorkoutState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val activitySessionRepository: ActivitySessionRepository
) : ViewModel() {

    val workoutState: StateFlow<WorkoutState> = activitySessionRepository.workoutState
    val activeActivityType: StateFlow<ActivityType> = activitySessionRepository.activeActivityType
    val durationSeconds: StateFlow<Long> = activitySessionRepository.activeDurationSeconds
    val distanceMeters: StateFlow<Double> = activitySessionRepository.activeDistanceMeters
    val steps: StateFlow<Long> = activitySessionRepository.activeSteps
    val calories: StateFlow<Int> = activitySessionRepository.activeCalories
    val speedKmh: StateFlow<Double> = activitySessionRepository.activeSpeedKmh
    val paceMinPerKm: StateFlow<Double> = activitySessionRepository.activePaceMinPerKm
    val routePoints: StateFlow<List<RoutePoint>> = activitySessionRepository.activeRoutePoints
    val completedActivity: StateFlow<MovementActivity?> = activitySessionRepository.completedActivity

    fun startWorkout(type: ActivityType) {
        activitySessionRepository.startWorkout(type)
    }

    fun pauseWorkout() {
        activitySessionRepository.pauseWorkout()
    }

    fun resumeWorkout() {
        activitySessionRepository.resumeWorkout()
    }

    fun stopWorkout(): MovementActivity {
        return activitySessionRepository.stopWorkout()
    }

    fun saveWorkout(activity: MovementActivity) {
        viewModelScope.launch {
            activitySessionRepository.saveWorkout(activity)
        }
    }

    fun discardWorkout() {
        activitySessionRepository.discardWorkout()
    }
}
