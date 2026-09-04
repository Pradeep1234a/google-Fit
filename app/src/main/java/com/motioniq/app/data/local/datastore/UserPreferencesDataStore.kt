package com.motioniq.app.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.motioniq.app.model.UserProfile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "motioniq_user_preferences")

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_WEIGHT_KG = doublePreferencesKey("user_weight_kg")
        val USER_HEIGHT_CM = doublePreferencesKey("user_height_cm")
        val USER_AGE = intPreferencesKey("user_age")
        val USER_GENDER = stringPreferencesKey("user_gender")
        val DAILY_STEP_GOAL = intPreferencesKey("daily_step_goal")
        val DAILY_DISTANCE_GOAL_KM = doublePreferencesKey("daily_distance_goal_km")
        val DAILY_ACTIVE_MINUTES_GOAL = intPreferencesKey("daily_active_minutes_goal")
        val IS_ONBOARDED = booleanPreferencesKey("is_onboarded")

        // App Settings
        val METRIC_UNITS = booleanPreferencesKey("metric_units")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val AUTO_PAUSE_ENABLED = booleanPreferencesKey("auto_pause_enabled")
        val OFFLINE_MODE = booleanPreferencesKey("offline_mode")
        val HEALTH_CONNECT_SYNC = booleanPreferencesKey("health_connect_sync")
    }

    val userProfileFlow: Flow<UserProfile> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserProfile(
                name = preferences[PreferencesKeys.USER_NAME] ?: "Runner",
                weightKg = preferences[PreferencesKeys.USER_WEIGHT_KG] ?: 70.0,
                heightCm = preferences[PreferencesKeys.USER_HEIGHT_CM] ?: 175.0,
                age = preferences[PreferencesKeys.USER_AGE] ?: 28,
                gender = preferences[PreferencesKeys.USER_GENDER] ?: "Prefer not to say",
                dailyStepGoal = preferences[PreferencesKeys.DAILY_STEP_GOAL] ?: 10000,
                dailyDistanceGoalKm = preferences[PreferencesKeys.DAILY_DISTANCE_GOAL_KM] ?: 5.0,
                dailyActiveMinutesGoal = preferences[PreferencesKeys.DAILY_ACTIVE_MINUTES_GOAL] ?: 60,
                isOnboarded = preferences[PreferencesKeys.IS_ONBOARDED] ?: true
            )
        }

    suspend fun updateUserProfile(profile: UserProfile) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = profile.name
            preferences[PreferencesKeys.USER_WEIGHT_KG] = profile.weightKg
            preferences[PreferencesKeys.USER_HEIGHT_CM] = profile.heightCm
            preferences[PreferencesKeys.USER_AGE] = profile.age
            preferences[PreferencesKeys.USER_GENDER] = profile.gender
            preferences[PreferencesKeys.DAILY_STEP_GOAL] = profile.dailyStepGoal
            preferences[PreferencesKeys.DAILY_DISTANCE_GOAL_KM] = profile.dailyDistanceGoalKm
            preferences[PreferencesKeys.DAILY_ACTIVE_MINUTES_GOAL] = profile.dailyActiveMinutesGoal
            preferences[PreferencesKeys.IS_ONBOARDED] = profile.isOnboarded
        }
    }

    suspend fun setOnboarded(isOnboarded: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_ONBOARDED] = isOnboarded
        }
    }

    val isHealthSyncEnabled: Flow<Boolean> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[PreferencesKeys.HEALTH_CONNECT_SYNC] ?: false }

    suspend fun setHealthSyncEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.HEALTH_CONNECT_SYNC] = enabled }
    }

    val isOfflineMode: Flow<Boolean> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[PreferencesKeys.OFFLINE_MODE] ?: false }

    suspend fun setOfflineMode(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.OFFLINE_MODE] = enabled }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
