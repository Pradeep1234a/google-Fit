package com.motioniq.app.di

import android.content.Context
import androidx.room.Room
import com.motioniq.app.data.local.db.MotionIQDatabase
import com.motioniq.app.data.local.db.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MotionIQDatabase {
        return Room.databaseBuilder(
            context,
            MotionIQDatabase::class.java,
            "motioniq.db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideActivitySessionDao(database: MotionIQDatabase): ActivitySessionDao {
        return database.activitySessionDao()
    }

    @Provides
    fun provideRoutePointDao(database: MotionIQDatabase): RoutePointDao {
        return database.routePointDao()
    }

    @Provides
    fun provideDailyStatsDao(database: MotionIQDatabase): DailyStatsDao {
        return database.dailyStatsDao()
    }

    @Provides
    fun provideGoalDao(database: MotionIQDatabase): GoalDao {
        return database.goalDao()
    }

    @Provides
    fun provideAchievementDao(database: MotionIQDatabase): AchievementDao {
        return database.achievementDao()
    }
}
