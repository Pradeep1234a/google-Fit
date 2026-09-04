package com.motioniq.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val type: String, // "STEPS", "DISTANCE", "ACTIVE_MINUTES", "CALORIES"
    val targetValue: Double,
    val currentValue: Double,
    val unit: String,
    val period: String, // "DAILY", "WEEKLY"
    val isCompleted: Boolean = false,
    val updatedDate: String // "yyyy-MM-dd"
)
