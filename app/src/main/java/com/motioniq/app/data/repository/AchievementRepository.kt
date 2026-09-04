package com.motioniq.app.data.repository

import com.motioniq.app.data.local.db.dao.AchievementDao
import com.motioniq.app.data.local.db.entity.AchievementEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementRepository @Inject constructor(
    private val achievementDao: AchievementDao
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    val achievements: Flow<List<AchievementEntity>> = achievementDao.getAllAchievements().onStart {
        initDefaultAchievementsIfEmpty()
    }

    val unlockedCount: Flow<Int> = achievementDao.getUnlockedCount()
    val totalXp: Flow<Int?> = achievementDao.getTotalXpEarned()

    private fun initDefaultAchievementsIfEmpty() {
        scope.launch {
            val existing = achievementDao.getAllAchievements().first()
            if (existing.isEmpty()) {
                val defaults = listOf(
                    AchievementEntity("ach_first_session", "Initial Vector", "Complete your first recorded workout session", "MILESTONE", 100, 0f, false),
                    AchievementEntity("ach_century_sprint", "Century Sprint", "Log an active cadence session exceeding 10,000 steps", "CADENCE", 250, 0f, false),
                    AchievementEntity("ach_spatial_5k", "Spatial 5K", "Cover 5.0 kilometers of verified GPS movement", "DISTANCE", 200, 0f, false),
                    AchievementEntity("ach_spatial_10k", "Sub-Orbital 10K", "Cover 10.0 continuous kilometers in an activity", "DISTANCE", 500, 0f, false),
                    AchievementEntity("ach_kinetic_streak_3", "Kinetic Velocity", "Maintain activity consistency for 3 consecutive days", "STREAK", 150, 0f, false),
                    AchievementEntity("ach_kinetic_streak_7", "Orbital Resonance", "Maintain activity consistency for 7 consecutive days", "STREAK", 350, 0f, false),
                    AchievementEntity("ach_biomechanical_purity", "Biomechanical Purity", "Complete a session with verified 100% hardware sensor telemetry", "PRECISION", 300, 0f, false)
                )
                achievementDao.upsertAchievements(defaults)
            }
        }
    }

    suspend fun evaluateAchievements(totalSteps: Long, totalDistanceKm: Double, sessionCount: Int) {
        val all = achievementDao.getAllAchievements().first()
        for (ach in all) {
            if (ach.isUnlocked) continue
            var progress = 0f
            var unlocked = false

            when (ach.id) {
                "ach_first_session" -> {
                    progress = (sessionCount.toFloat() / 1f).coerceIn(0f, 1f)
                    unlocked = sessionCount >= 1
                }
                "ach_century_sprint" -> {
                    progress = (totalSteps.toFloat() / 10000f).coerceIn(0f, 1f)
                    unlocked = totalSteps >= 10000
                }
                "ach_spatial_5k" -> {
                    progress = (totalDistanceKm.toFloat() / 5f).coerceIn(0f, 1f)
                    unlocked = totalDistanceKm >= 5.0
                }
                "ach_spatial_10k" -> {
                    progress = (totalDistanceKm.toFloat() / 10f).coerceIn(0f, 1f)
                    unlocked = totalDistanceKm >= 10.0
                }
            }

            if (unlocked || progress != ach.progress) {
                achievementDao.upsertAchievement(
                    ach.copy(
                        progress = progress,
                        isUnlocked = unlocked,
                        unlockedAtMillis = if (unlocked) System.currentTimeMillis() else ach.unlockedAtMillis
                    )
                )
            }
        }
    }

    suspend fun clearAll() {
        achievementDao.deleteAllAchievements()
    }
}
