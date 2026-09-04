package com.motioniq.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.motioniq.app.data.local.db.dao.*
import com.motioniq.app.data.local.db.entity.*

@Database(
    entities = [
        ActivitySessionEntity::class,
        RoutePointEntity::class,
        DailyStatsEntity::class,
        GoalEntity::class,
        AchievementEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MotionIQDatabase : RoomDatabase() {
    abstract fun activitySessionDao(): ActivitySessionDao
    abstract fun routePointDao(): RoutePointDao
    abstract fun dailyStatsDao(): DailyStatsDao
    abstract fun goalDao(): GoalDao
    abstract fun achievementDao(): AchievementDao
}
