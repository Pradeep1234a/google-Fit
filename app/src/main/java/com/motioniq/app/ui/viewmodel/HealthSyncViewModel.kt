package com.motioniq.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motioniq.app.core.health.HealthConnectBridge
import com.motioniq.app.data.local.datastore.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HealthSyncViewModel @Inject constructor(
    val healthConnectBridge: HealthConnectBridge,
    private val dataStore: UserPreferencesDataStore
) : ViewModel() {

    val isSyncEnabled: StateFlow<Boolean> = dataStore.isHealthSyncEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = healthConnectBridge.isSyncEnabled
    )

    fun toggleSync(enabled: Boolean) {
        healthConnectBridge.isSyncEnabled = enabled
        viewModelScope.launch {
            dataStore.setHealthSyncEnabled(enabled)
        }
    }
}
