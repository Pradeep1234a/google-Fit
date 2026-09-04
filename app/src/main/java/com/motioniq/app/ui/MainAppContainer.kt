package com.motioniq.app.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.motioniq.app.core.MotionRepository
import com.motioniq.app.model.ActivityType
import com.motioniq.app.model.MovementActivity
import com.motioniq.app.model.WorkoutState
import com.motioniq.app.ui.activity.ActivitySelectionScreen
import com.motioniq.app.ui.activity.ActivitySummaryScreen
import com.motioniq.app.ui.activity.LiveActivityScreen
import com.motioniq.app.ui.components.AppTab
import com.motioniq.app.ui.components.MotionIQBottomBar
import com.motioniq.app.ui.explore.ExploreScreen
import com.motioniq.app.ui.home.HomeScreen
import com.motioniq.app.ui.onboarding.OnboardingScreen
import com.motioniq.app.ui.profile.ProfileScreen
import com.motioniq.app.ui.stats.StatsScreen

@Composable
fun MainAppContainer(repository: MotionRepository) {
    val profile by repository.userProfile.collectAsStateWithLifecycle()
    val todaySummary by repository.todaySummary.collectAsStateWithLifecycle()
    val activities by repository.activities.collectAsStateWithLifecycle()
    val workoutState by repository.workoutState.collectAsStateWithLifecycle()
    val activeActivityType by repository.activeActivityType.collectAsStateWithLifecycle()
    val durationSeconds by repository.activeDurationSeconds.collectAsStateWithLifecycle()
    val distanceMeters by repository.activeDistanceMeters.collectAsStateWithLifecycle()
    val steps by repository.activeSteps.collectAsStateWithLifecycle()
    val calories by repository.activeCalories.collectAsStateWithLifecycle()
    val speedKmh by repository.activeSpeedKmh.collectAsStateWithLifecycle()
    val paceMinPerKm by repository.activePaceMinPerKm.collectAsStateWithLifecycle()
    val routePoints by repository.activeRoutePoints.collectAsStateWithLifecycle()
    val completedActivity by repository.completedActivity.collectAsStateWithLifecycle()
    val activeSource by repository.stepEngine.activeSource.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(AppTab.HOME) }
    var isSelectingActivity by remember { mutableStateOf(false) }
    var selectedPastActivity by remember { mutableStateOf<MovementActivity?>(null) }

    // 1. Onboarding Flow
    if (!profile.isOnboarded) {
        OnboardingScreen(
            initialProfile = profile,
            onCompleteOnboarding = { updatedProfile ->
                repository.completeOnboarding(updatedProfile)
            }
        )
        return
    }

    // 2. Full-Screen Live Workout
    if (workoutState == WorkoutState.ACTIVE || workoutState == WorkoutState.PAUSED) {
        LiveActivityScreen(
            activityType = activeActivityType,
            state = workoutState,
            durationSeconds = durationSeconds,
            distanceMeters = distanceMeters,
            steps = steps,
            calories = calories,
            speedKmh = speedKmh,
            paceMinPerKm = paceMinPerKm,
            routePoints = routePoints,
            onPauseClick = { repository.pauseWorkout() },
            onResumeClick = { repository.resumeWorkout() },
            onStopClick = { repository.stopWorkout() }
        )
        return
    }

    // 3. Activity Summary Screen (Post-Workout or Detail View)
    val activeSummary = completedActivity ?: selectedPastActivity
    if (activeSummary != null) {
        ActivitySummaryScreen(
            activity = activeSummary,
            onSaveClick = {
                if (completedActivity != null) {
                    repository.saveWorkout(activeSummary)
                }
                selectedPastActivity = null
            },
            onDiscardClick = {
                repository.discardWorkout()
                selectedPastActivity = null
            }
        )
        return
    }

    // 4. Activity Selection Screen
    if (isSelectingActivity) {
        ActivitySelectionScreen(
            onSelectActivity = { type ->
                isSelectingActivity = false
                repository.startWorkout(type)
            },
            onBackClick = { isSelectingActivity = false }
        )
        return
    }

    // 5. Main Scaffold with Bottom Navigation
    Scaffold(
        bottomBar = {
            MotionIQBottomBar(
                currentTab = currentTab,
                onTabSelected = { tab ->
                    if (tab == AppTab.ACTIVITY) {
                        isSelectingActivity = true
                    } else {
                        currentTab = tab
                    }
                }
            )
        }
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.HOME -> {
                    HomeScreen(
                        summary = todaySummary,
                        profile = profile,
                        recentActivities = activities,
                        activeSource = activeSource,
                        onStartActivityClick = { isSelectingActivity = true },
                        onExploreClick = { currentTab = AppTab.EXPLORE },
                        onActivityClick = { activity -> selectedPastActivity = activity }
                    )
                }
                AppTab.EXPLORE -> {
                    ExploreScreen(
                        parks = repository.getDynamicNearbyParks(),
                        isGpsActive = repository.locationTracker.hasPermission(),
                        onStartRouteClick = { type ->
                            repository.startWorkout(type)
                        }
                    )
                }
                AppTab.ACTIVITY -> {
                    // Clicking tab opens activity selector directly
                    isSelectingActivity = true
                }
                AppTab.STATS -> {
                    StatsScreen(
                        activities = activities,
                        weeklySteps = repository.getWeeklySteps(),
                        weeklyTotal = repository.getWeeklyTotalSteps(),
                        averageDaily = repository.getAverageDailySteps(),
                        bestDay = repository.getBestDay()
                    )
                }
                AppTab.PROFILE -> {
                    ProfileScreen(
                        profile = profile,
                        hasHardwareStepSensor = repository.hasHardwareStepSensor,
                        diagnostics = repository.getDiagnostics(),
                        isHealthConnectAvailable = repository.healthConnect.isAvailable,
                        isHealthConnectSyncEnabled = repository.healthConnect.isSyncEnabled,
                        onToggleHealthConnectSync = { enabled ->
                            repository.healthConnect.isSyncEnabled = enabled
                        },
                        onUpdateProfile = { updated -> repository.updateUserProfile(updated) },
                        onResetData = { repository.resetData() }
                    )
                }
            }
        }
    }
}

