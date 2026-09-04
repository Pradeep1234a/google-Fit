package com.motioniq.app.data.local.db.dao

import androidx.room.*
import com.motioniq.app.data.local.db.entity.ActivitySessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivitySessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ActivitySessionEntity)

    @Query("SELECT * FROM activity_sessions ORDER BY startTimeMillis DESC")
    fun getAllSessions(): Flow<List<ActivitySessionEntity>>

    @Query("SELECT * FROM activity_sessions ORDER BY startTimeMillis DESC LIMIT :limit")
    fun getRecentSessions(limit: Int): Flow<List<ActivitySessionEntity>>

    @Query("SELECT * FROM activity_sessions WHERE id = :id LIMIT 1")
    suspend fun getSessionById(id: String): ActivitySessionEntity?

    @Query("DELETE FROM activity_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: String)

    @Query("DELETE FROM activity_sessions")
    suspend fun deleteAllSessions()

    @Query("SELECT SUM(distanceMeters) FROM activity_sessions")
    fun getTotalDistanceMeters(): Flow<Double?>

    @Query("SELECT SUM(steps) FROM activity_sessions")
    fun getTotalSteps(): Flow<Long?>

    @Query("SELECT COUNT(*) FROM activity_sessions")
    fun getTotalSessionsCount(): Flow<Int>
}
