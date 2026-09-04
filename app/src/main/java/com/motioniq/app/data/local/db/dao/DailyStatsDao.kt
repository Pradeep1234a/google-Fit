package com.motioniq.app.data.local.db.dao

import androidx.room.*
import com.motioniq.app.data.local.db.entity.DailyStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyStatsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyStats(stats: DailyStatsEntity)

    @Query("SELECT * FROM daily_stats WHERE date = :date LIMIT 1")
    fun getDailyStats(date: String): Flow<DailyStatsEntity?>

    @Query("SELECT * FROM daily_stats WHERE date = :date LIMIT 1")
    suspend fun getDailyStatsSync(date: String): DailyStatsEntity?

    @Query("SELECT * FROM daily_stats WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getStatsRange(startDate: String, endDate: String): Flow<List<DailyStatsEntity>>

    @Query("SELECT * FROM daily_stats ORDER BY date DESC")
    fun getAllDailyStats(): Flow<List<DailyStatsEntity>>

    @Query("SELECT * FROM daily_stats ORDER BY date DESC LIMIT :limit")
    fun getRecentDailyStats(limit: Int): Flow<List<DailyStatsEntity>>

    @Query("DELETE FROM daily_stats")
    suspend fun deleteAllStats()
}
