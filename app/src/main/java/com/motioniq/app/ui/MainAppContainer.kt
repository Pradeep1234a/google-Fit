package com.motioniq.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.motioniq.app.core.MotionRepository
import com.motioniq.app.model.MovementActivity
import com.motioniq.app.model.WorkoutState
import com.motioniq.app.ui.activity.ActivitySelectionScreen
import com.motioniq.app.ui.activity.ActivitySummaryScreen
import com.motioniq.app.ui.activity.HistoryScreen
import com.motioniq.app.ui.activity.LiveActivityScreen
import com.motioniq.app.ui.components.AppTab
import com.motioniq.app.ui.components.MotionIQBottomBar
import com.motioniq.app.ui.explore.ExploreScreen
import com.motioniq.app.ui.home.HomeScreen
import com.motioniq.app.ui.onboarding.OnboardingScreen
import com.motioniq.app.ui.profile.ProfileScreen
import com.motioniq.app.ui.secondary.*
import com.motioniq.app.ui.stats.StatsScreen
import kotlinx.coroutines.delay

enum class SecondaryScreen {
    NONE,
    GOALS,
    ACHIEVEMENTS,
    INSIGHTS,
    SETTINGS,
    HEALTH_SYNC,
    NOTIFICATIONS,
    HELP,
    PROFILE_SETUP,
    HISTORY
}

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

    var showSplash by remember { mutableStateOf(true) }
    var currentTab by remember { mutableStateOf(AppTab.HOME) }
    var isSelectingActivity by remember { mutableStateOf(false) }
    var selectedPastActivity by remember { mutableStateOf<MovementActivity?>(null) }
    var secondaryScreen by remember { mutableStateOf(SecondaryScreen.NONE) }

    // Splash Screen Auto-dismiss
    LaunchedEffect(Unit) {
        delay(1200)
        showSplash = false
    }

    if (showSplash) {
        SplashScreen()
        return
    }

    // 1. Onboarding Flow (02_Onboarding, 03_Permissions, 04_ProfileSetup)
    if (!profile.isOnboarded) {
        OnboardingScreen(
            initialProfile = profile,
            onCompleteOnboarding = { updatedProfile ->
                repository.completeOnboarding(updatedProfile)
            }
        )
        return
    }

    // 2. Full-Screen Live Workout (07_ActiveTracking.png)
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

    // 3. Activity Summary Screen (08_ActivitySummary.png)
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

    // 4. Activity Selection Screen (06_ActivitySelect.png)
    if (isSelectingActivity) {
        BackHandler { isSelectingActivity = false }
        ActivitySelectionScreen(
            onSelectActivity = { type ->
                isSelectingActivity = false
                repository.startWorkout(type)
            },
            onBackClick = { isSelectingActivity = false }
        )
        return
    }

    // 5. Secondary Screens (04, 11, 12, 13, 16, 17, 18, 19)
    if (secondaryScreen != SecondaryScreen.NONE) {
        BackHandler { secondaryScreen = SecondaryScreen.NONE }

        when (secondaryScreen) {
            SecondaryScreen.GOALS -> {
                GoalsScreen(
                    profile = profile,
                    currentSteps = todaySummary.steps.toLong(),
                    currentDistanceKm = todaySummary.distanceMeters / 1000.0,
                    currentActiveMinutes = todaySummary.activeMinutes.toLong(),
                    onBackClick = { secondaryScreen = SecondaryScreen.NONE },
                    onSaveGoals = { newSteps, newDist, newMin ->
                        repository.updateUserProfile(
                            profile.copy(
                                dailyStepGoal = newSteps,
                                dailyDistanceGoalKm = newDist,
                                dailyActiveMinutesGoal = newMin
                            )
                        )
                        secondaryScreen = SecondaryScreen.NONE
                    }
                )
            }
            SecondaryScreen.ACHIEVEMENTS -> {
                AchievementsScreen(
                    onBackClick = { secondaryScreen = SecondaryScreen.NONE }
                )
            }
            SecondaryScreen.INSIGHTS -> {
                InsightsScreen(
                    onBackClick = { secondaryScreen = SecondaryScreen.NONE }
                )
            }
            SecondaryScreen.SETTINGS -> {
                SettingsScreen(
                    onBackClick = { secondaryScreen = SecondaryScreen.NONE },
                    onNotificationsClick = { secondaryScreen = SecondaryScreen.NOTIFICATIONS },
                    onResetDataClick = {
                        repository.resetData()
                        secondaryScreen = SecondaryScreen.NONE
                    }
                )
            }
            SecondaryScreen.HEALTH_SYNC -> {
                HealthSyncScreen(
                    isAvailable = repository.healthConnect.isAvailable,
                    isSyncEnabled = repository.healthConnect.isSyncEnabled,
                    onToggleSync = { enabled ->
                        repository.healthConnect.isSyncEnabled = enabled
                    },
                    onBackClick = { secondaryScreen = SecondaryScreen.NONE }
                )
            }
            SecondaryScreen.NOTIFICATIONS -> {
                NotificationsScreen(
                    onBackClick = { secondaryScreen = SecondaryScreen.NONE }
                )
            }
            SecondaryScreen.HELP -> {
                HelpScreen(
                    onBackClick = { secondaryScreen = SecondaryScreen.NONE }
                )
            }
            SecondaryScreen.PROFILE_SETUP -> {
                ProfileSetupScreen(
                    initialProfile = profile,
                    onSaveProfile = { updated ->
                        repository.updateUserProfile(updated)
                        secondaryScreen = SecondaryScreen.NONE
                    },
                    onBackClick = { secondaryScreen = SecondaryScreen.NONE }
                )
            }
            SecondaryScreen.HISTORY -> {
                HistoryScreen(
                    activities = activities,
                    onActivityClick = { activity ->
                        selectedPastActivity = activity
                        secondaryScreen = SecondaryScreen.NONE
                    },
                    onStartNewActivity = {
                        secondaryScreen = SecondaryScreen.NONE
                        isSelectingActivity = true
                    },
                    onBackClick = { secondaryScreen = SecondaryScreen.NONE }
                )
            }
            SecondaryScreen.NONE -> {}
        }
        return
    }

    // 6. Main Scaffold with 4-Tab Bottom Navigation (Stitch Design System)
    Scaffold(
        bottomBar = {
            MotionIQBottomBar(
                currentTab = currentTab,
                onTabSelected = { tab ->
                    currentTab = tab
                }
            )
        }
    ) { innerPadding ->
        Box(
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
                        onNotificationClick = { secondaryScreen = SecondaryScreen.NOTIFICATIONS },
                        onGoalClick = { secondaryScreen = SecondaryScreen.GOALS },
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
                AppTab.ANALYTICS -> {
                    StatsScreen(
                        activities = activities,
                        weeklySteps = repository.getWeeklySteps(),
                        weeklyTotal = repository.getWeeklyTotalSteps(),
                        averageDaily = repository.getAverageDailySteps(),
                        bestDay = repository.getBestDay(),
                        onActivityClick = { activity -> selectedPastActivity = activity },
                        onViewAllActivities = { secondaryScreen = SecondaryScreen.HISTORY }
                    )
                }
                AppTab.SETTINGS -> {
                    ProfileScreen(
                        profile = profile,
                        isHealthConnectSyncEnabled = repository.healthConnect.isSyncEnabled,
                        onNavigateToPersonalInfo = { secondaryScreen = SecondaryScreen.PROFILE_SETUP },
                        onNavigateToGoals = { secondaryScreen = SecondaryScreen.GOALS },
                        onNavigateToAchievements = { secondaryScreen = SecondaryScreen.ACHIEVEMENTS },
                        onNavigateToInsights = { secondaryScreen = SecondaryScreen.INSIGHTS },
                        onNavigateToHealthConnect = { secondaryScreen = SecondaryScreen.HEALTH_SYNC },
                        onNavigateToSettings = { secondaryScreen = SecondaryScreen.SETTINGS },
                        onNavigateToNotifications = { secondaryScreen = SecondaryScreen.NOTIFICATIONS },
                        onNavigateToHelp = { secondaryScreen = SecondaryScreen.HELP },
                        onResetData = { repository.resetData() }
                    )
                }
            }
        }
    }
}
