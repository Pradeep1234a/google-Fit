package com.motioniq.app.di

import android.content.Context
import com.motioniq.app.core.health.HealthConnectBridge
import com.motioniq.app.core.location.LocationTracker
import com.motioniq.app.core.step.StepCountingEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EngineModule {

    @Provides
    @Singleton
    fun provideStepCountingEngine(@ApplicationContext context: Context): StepCountingEngine {
        val engine = StepCountingEngine(context)
        engine.start()
        return engine
    }

    @Provides
    @Singleton
    fun provideLocationTracker(@ApplicationContext context: Context): LocationTracker {
        return LocationTracker(context)
    }

    @Provides
    @Singleton
    fun provideHealthConnectBridge(@ApplicationContext context: Context): HealthConnectBridge {
        return HealthConnectBridge(context)
    }
}
