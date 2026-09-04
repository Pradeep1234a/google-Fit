package com.motioniq.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motioniq.app.data.repository.ActivitySessionRepository
import com.motioniq.app.data.repository.DailyStatsRepository
import com.motioniq.app.data.repository.StatisticsRepository
import com.motioniq.app.model.DailySummary
import com.motioniq.app.model.MovementActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val statisticsRepository: StatisticsRepository,
    private val dailyStatsRepository: DailyStatsRepository,
    private val activitySessionRepository: ActivitySessionRepository
) : ViewModel() {

    val todaySummary: StateFlow<DailySummary> = dailyStatsRepository.todaySummary

    val activities: StateFlow<List<MovementActivity>> = activitySessionRepository.allSessions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    fun getWeeklySteps(): List<Pair<String, Long>> = dailyStatsRepository.getWeeklySteps()

    fun getWeeklyTotalSteps(): Long = dailyStatsRepository.getWeeklyTotalSteps()

    fun getAverageDailySteps(): Long = dailyStatsRepository.getAverageDailySteps()

    fun getBestDay(): Pair<String, Long>? = dailyStatsRepository.getBestDay()
}
