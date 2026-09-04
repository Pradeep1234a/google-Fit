package com.motioniq.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motioniq.app.data.repository.ActivitySessionRepository
import com.motioniq.app.data.repository.DailyStatsRepository
import com.motioniq.app.data.repository.GoalRepository
import com.motioniq.app.data.repository.UserProfileRepository
import com.motioniq.app.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val activitySessionRepository: ActivitySessionRepository,
    private val dailyStatsRepository: DailyStatsRepository,
    private val goalRepository: GoalRepository
) : ViewModel() {

    val profile: StateFlow<UserProfile> = userProfileRepository.userProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = UserProfile()
    )

    val hasHardwareSensor: Boolean
        get() = activitySessionRepository.stepEngine.capabilities.hasStepCounter ||
                activitySessionRepository.stepEngine.capabilities.hasStepDetector

    fun getDiagnostics(): Map<String, Any> = dailyStatsRepository.getDiagnostics()

    fun updateProfile(profile: UserProfile) {
        viewModelScope.launch {
            userProfileRepository.updateUserProfile(profile)
        }
    }

    fun completeOnboarding(profile: UserProfile) {
        viewModelScope.launch {
            userProfileRepository.completeOnboarding(profile)
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            userProfileRepository.clearAll()
            activitySessionRepository.clearAll()
            dailyStatsRepository.clearAll()
            goalRepository.clearAll()
        }
    }
}
