package com.motioniq.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motioniq.app.data.local.db.entity.GoalEntity
import com.motioniq.app.data.repository.DailyStatsRepository
import com.motioniq.app.data.repository.GoalRepository
import com.motioniq.app.model.DailySummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
    private val dailyStatsRepository: DailyStatsRepository
) : ViewModel() {

    val goals: StateFlow<List<GoalEntity>> = goalRepository.goals.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    val todaySummary: StateFlow<DailySummary> = dailyStatsRepository.todaySummary

    fun updateGoal(goal: GoalEntity) {
        viewModelScope.launch {
            goalRepository.upsertGoal(goal)
        }
    }
}
