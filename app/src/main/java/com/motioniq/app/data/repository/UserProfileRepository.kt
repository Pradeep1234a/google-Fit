package com.motioniq.app.data.repository

import com.motioniq.app.data.local.datastore.UserPreferencesDataStore
import com.motioniq.app.model.UserProfile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepository @Inject constructor(
    private val dataStore: UserPreferencesDataStore
) {
    val userProfile: Flow<UserProfile> = dataStore.userProfileFlow

    suspend fun updateUserProfile(profile: UserProfile) {
        dataStore.updateUserProfile(profile)
    }

    suspend fun completeOnboarding(profile: UserProfile) {
        dataStore.updateUserProfile(profile.copy(isOnboarded = true))
    }

    suspend fun clearAll() {
        dataStore.clearAll()
    }
}
