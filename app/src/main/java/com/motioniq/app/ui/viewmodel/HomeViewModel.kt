package com.motioniq.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motioniq.app.core.step.StepSourceType
import com.motioniq.app.data.repository.ActivitySessionRepository
import com.motioniq.app.data.repository.DailyStatsRepository
import com.motioniq.app.data.repository.UserProfileRepository
import com.motioniq.app.model.DailySummary
import com.motioniq.app.model.MovementActivity
import com.motioniq.app.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dailyStatsRepository: DailyStatsRepository,
    private val userProfileRepository: UserProfileRepository,
    private val activitySessionRepository: ActivitySessionRepository
) : ViewModel() {

    val todaySummary: StateFlow<DailySummary> = dailyStatsRepository.todaySummary

    val profile: StateFlow<UserProfile> = userProfileRepository.userProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = UserProfile()
    )

    val recentActivities: StateFlow<List<MovementActivity>> = activitySessionRepository.getRecentSessions(5).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    val activeSource: StateFlow<StepSourceType> = activitySessionRepository.stepEngine.activeSource
}
