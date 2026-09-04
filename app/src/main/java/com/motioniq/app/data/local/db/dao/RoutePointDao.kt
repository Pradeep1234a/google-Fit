package com.motioniq.app.data.local.db.dao

import androidx.room.*
import com.motioniq.app.data.local.db.entity.RoutePointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutePointDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoints(points: List<RoutePointEntity>)

    @Query("SELECT * FROM route_points WHERE sessionId = :sessionId ORDER BY timestampMillis ASC")
    suspend fun getPointsForSession(sessionId: String): List<RoutePointEntity>

    @Query("SELECT * FROM route_points WHERE sessionId = :sessionId ORDER BY timestampMillis ASC")
    fun observePointsForSession(sessionId: String): Flow<List<RoutePointEntity>>

    @Query("DELETE FROM route_points WHERE sessionId = :sessionId")
    suspend fun deletePointsForSession(sessionId: String)

    @Query("DELETE FROM route_points")
    suspend fun deleteAllPoints()
}
