package com.motioniq.app.data.local.db.dao

import androidx.room.*
import com.motioniq.app.data.local.db.entity.AchievementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAchievement(achievement: AchievementEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAchievements(achievements: List<AchievementEntity>)

    @Query("SELECT * FROM achievements ORDER BY isUnlocked DESC, xpValue DESC")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE isUnlocked = 1 ORDER BY unlockedAtMillis DESC")
    fun getUnlockedAchievements(): Flow<List<AchievementEntity>>

    @Query("SELECT COUNT(*) FROM achievements WHERE isUnlocked = 1")
    fun getUnlockedCount(): Flow<Int>

    @Query("SELECT SUM(xpValue) FROM achievements WHERE isUnlocked = 1")
    fun getTotalXpEarned(): Flow<Int?>

    @Query("SELECT * FROM achievements WHERE id = :id LIMIT 1")
    suspend fun getAchievementById(id: String): AchievementEntity?

    @Query("DELETE FROM achievements")
    suspend fun deleteAllAchievements()
}
