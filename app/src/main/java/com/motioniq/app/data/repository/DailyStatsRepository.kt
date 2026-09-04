package com.motioniq.app.data.repository

import com.motioniq.app.core.step.DistanceEstimator
import com.motioniq.app.core.step.StepCountingEngine
import com.motioniq.app.data.local.db.dao.ActivitySessionDao
import com.motioniq.app.data.local.db.dao.DailyStatsDao
import com.motioniq.app.data.local.db.entity.DailyStatsEntity
import com.motioniq.app.model.DailySummary
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyStatsRepository @Inject constructor(
    private val stepEngine: StepCountingEngine,
    private val dailyStatsDao: DailyStatsDao,
    private val sessionDao: ActivitySessionDao,
    private val userProfileRepository: UserProfileRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _todaySummary = MutableStateFlow(DailySummary(getCurrentDateString()))
    val todaySummary: StateFlow<DailySummary> = _todaySummary.asStateFlow()

    init {
        scope.launch {
            combine(
                stepEngine.todaySteps,
                userProfileRepository.userProfile,
                sessionDao.getAllSessions()
            ) { realSteps, profile, sessions ->
                val todayStr = getCurrentDateString()
                val todaySessions = sessions.filter {
                    val cal = Calendar.getInstance().apply { timeInMillis = it.startTimeMillis }
                    val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
                    dateStr == todayStr
                }

                val distanceMeters = DistanceEstimator.estimateDistanceMeters(realSteps, profile.heightCm)
                val calories = DistanceEstimator.estimateCalories(realSteps, profile.weightKg)
                val activeMinutes = if (realSteps > 0) (realSteps / 100).toInt().coerceAtLeast(1) else 0

                val summary = DailySummary(
                    date = todayStr,
                    steps = realSteps,
                    distanceMeters = distanceMeters,
                    caloriesKcal = calories,
                    activeMinutes = activeMinutes,
                    activityCount = todaySessions.size
                )
                dailyStatsDao.upsertDailyStats(DailyStatsEntity.fromDomain(summary))
                summary
            }.collect { summary ->
                _todaySummary.value = summary
            }
        }
    }

    fun getWeeklySteps(): List<Pair<String, Long>> = stepEngine.getWeeklyDaysSteps()

    fun getWeeklyTotalSteps(): Long = stepEngine.getWeeklyTotal()

    fun getAverageDailySteps(): Long = stepEngine.getAverageDailySteps()

    fun getBestDay(): Pair<String, Long>? = stepEngine.getBestDay()

    fun getDiagnostics(): Map<String, Any> = stepEngine.getDiagnostics()

    suspend fun clearAll() {
        stepEngine.persistence.clearAll()
        dailyStatsDao.deleteAllStats()
        _todaySummary.value = DailySummary(getCurrentDateString())
    }

    private fun getCurrentDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}
