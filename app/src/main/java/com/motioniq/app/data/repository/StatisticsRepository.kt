package com.motioniq.app.data.repository

import com.motioniq.app.data.local.db.dao.ActivitySessionDao
import com.motioniq.app.data.local.db.dao.DailyStatsDao
import com.motioniq.app.model.DailySummary
import com.motioniq.app.model.MovementActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatisticsRepository @Inject constructor(
    private val sessionDao: ActivitySessionDao,
    private val dailyStatsDao: DailyStatsDao
) {
    val totalDistanceMeters: Flow<Double> = sessionDao.getTotalDistanceMeters().map { it ?: 0.0 }
    val totalSteps: Flow<Long> = sessionDao.getTotalSteps().map { it ?: 0L }
    val totalSessionsCount: Flow<Int> = sessionDao.getTotalSessionsCount()

    val recentSessions: Flow<List<MovementActivity>> = sessionDao.getRecentSessions(10).map { list ->
        list.map { it.toDomain() }
    }

    val allDailySummaries: Flow<List<DailySummary>> = dailyStatsDao.getAllDailyStats().map { list ->
        list.map { it.toDomain() }
    }

    fun getRecentDailySummaries(limit: Int): Flow<List<DailySummary>> {
        return dailyStatsDao.getRecentDailyStats(limit).map { list ->
            list.map { it.toDomain() }
        }
    }
}
