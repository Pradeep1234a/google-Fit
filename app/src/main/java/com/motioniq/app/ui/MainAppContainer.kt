package com.motioniq.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.motioniq.app.ui.viewmodel.*
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
fun MainAppContainer(
    homeViewModel: HomeViewModel = hiltViewModel(),
    activityViewModel: ActivityViewModel = hiltViewModel(),
    exploreViewModel: ExploreViewModel = hiltViewModel(),
    statsViewModel: StatsViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    goalsViewModel: GoalsViewModel = hiltViewModel(),
    achievementsViewModel: AchievementsViewModel = hiltViewModel(),
    healthSyncViewModel: HealthSyncViewModel = hiltViewModel(),
    insightsViewModel: InsightsViewModel = hiltViewModel()
) {
    val profile by profileViewModel.profile.collectAsStateWithLifecycle()
    val todaySummary by homeViewModel.todaySummary.collectAsStateWithLifecycle()
    val activities by statsViewModel.activities.collectAsStateWithLifecycle()
    val workoutState by activityViewModel.workoutState.collectAsStateWithLifecycle()
    val activeActivityType by activityViewModel.activeActivityType.collectAsStateWithLifecycle()
    val durationSeconds by activityViewModel.durationSeconds.collectAsStateWithLifecycle()
    val distanceMeters by activityViewModel.distanceMeters.collectAsStateWithLifecycle()
    val steps by activityViewModel.steps.collectAsStateWithLifecycle()
    val calories by activityViewModel.calories.collectAsStateWithLifecycle()
    val speedKmh by activityViewModel.speedKmh.collectAsStateWithLifecycle()
    val paceMinPerKm by activityViewModel.paceMinPerKm.collectAsStateWithLifecycle()
    val routePoints by activityViewModel.routePoints.collectAsStateWithLifecycle()
    val completedActivity by activityViewModel.completedActivity.collectAsStateWithLifecycle()
    val activeSource by homeViewModel.activeSource.collectAsStateWithLifecycle()
    val nearbyParks by exploreViewModel.nearbyPlaces.collectAsStateWithLifecycle()
    val isHealthSyncEnabled by healthSyncViewModel.isSyncEnabled.collectAsStateWithLifecycle()

    var showSplash by remember { mutableStateOf(true) }
    var currentTab by remember { mutableStateOf(AppTab.HOME) }
    var isSelectingActivity by remember { mutableStateOf(false) }
    var selectedPastActivity by remember { mutableStateOf<MovementActivity?>(null) }
    var secondaryScreen by remember { mutableStateOf(SecondaryScreen.NONE) }

    // Splash Screen Auto-dismiss (1200ms)
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
                profileViewModel.completeOnboarding(updatedProfile)
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
            onPauseClick = { activityViewModel.pauseWorkout() },
            onResumeClick = { activityViewModel.resumeWorkout() },
            onStopClick = { activityViewModel.stopWorkout() }
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
                    activityViewModel.saveWorkout(activeSummary)
                }
                selectedPastActivity = null
            },
            onDiscardClick = {
                activityViewModel.discardWorkout()
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
                activityViewModel.startWorkout(type)
            },
            onBackClick = { isSelectingActivity = false }
        )
        return
    }

    // 5. Secondary Screens
    if (secondaryScreen != SecondaryScreen.NONE) {
        BackHandler { secondaryScreen = SecondaryScreen.NONE }

        when (secondaryScreen) {
            SecondaryScreen.GOALS -> {
                GoalsScreen(
                    profile = profile,
                    currentSteps = todaySummary.steps,
                    currentDistanceKm = todaySummary.distanceMeters / 1000.0,
                    currentActiveMinutes = todaySummary.activeMinutes.toLong(),
                    onBackClick = { secondaryScreen = SecondaryScreen.NONE },
                    onSaveGoals = { newSteps, newDist, newMin ->
                        profileViewModel.updateProfile(
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
                        profileViewModel.resetAllData()
                        secondaryScreen = SecondaryScreen.NONE
                    }
                )
            }
            SecondaryScreen.HEALTH_SYNC -> {
                HealthSyncScreen(
                    isAvailable = healthSyncViewModel.healthConnectBridge.isAvailable,
                    isSyncEnabled = isHealthSyncEnabled,
                    onToggleSync = { enabled ->
                        healthSyncViewModel.toggleSync(enabled)
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
                        profileViewModel.updateProfile(updated)
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
                        parks = nearbyParks,
                        isGpsActive = exploreViewModel.isGpsActive,
                        onStartRouteClick = { type ->
                            activityViewModel.startWorkout(type)
                        }
                    )
                }
                AppTab.ANALYTICS -> {
                    StatsScreen(
                        activities = activities,
                        weeklySteps = statsViewModel.getWeeklySteps(),
                        weeklyTotal = statsViewModel.getWeeklyTotalSteps(),
                        averageDaily = statsViewModel.getAverageDailySteps(),
                        bestDay = statsViewModel.getBestDay(),
                        onActivityClick = { activity -> selectedPastActivity = activity },
                        onViewAllActivities = { secondaryScreen = SecondaryScreen.HISTORY }
                    )
                }
                AppTab.SETTINGS -> {
                    ProfileScreen(
                        profile = profile,
                        isHealthConnectSyncEnabled = isHealthSyncEnabled,
                        onNavigateToPersonalInfo = { secondaryScreen = SecondaryScreen.PROFILE_SETUP },
                        onNavigateToGoals = { secondaryScreen = SecondaryScreen.GOALS },
                        onNavigateToAchievements = { secondaryScreen = SecondaryScreen.ACHIEVEMENTS },
                        onNavigateToInsights = { secondaryScreen = SecondaryScreen.INSIGHTS },
                        onNavigateToHealthConnect = { secondaryScreen = SecondaryScreen.HEALTH_SYNC },
                        onNavigateToSettings = { secondaryScreen = SecondaryScreen.SETTINGS },
                        onNavigateToNotifications = { secondaryScreen = SecondaryScreen.NOTIFICATIONS },
                        onNavigateToHelp = { secondaryScreen = SecondaryScreen.HELP },
                        onResetData = { profileViewModel.resetAllData() }
                    )
                }
            }
        }
    }
}
