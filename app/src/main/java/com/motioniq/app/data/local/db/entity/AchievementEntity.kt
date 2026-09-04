package com.motioniq.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val category: String, // "DISTANCE", "STREAK", "CADENCE", "SPEED"
    val xpValue: Int = 100,
    val progress: Float = 0f, // 0.0 to 1.0
    val isUnlocked: Boolean = false,
    val unlockedAtMillis: Long? = null
)
