package com.motioniq.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motioniq.app.data.repository.InsightsRepository
import com.motioniq.app.data.repository.KineticInsights
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val insightsRepository: InsightsRepository
) : ViewModel() {

    val kineticInsights: StateFlow<KineticInsights> = insightsRepository.kineticInsights.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = KineticInsights(94, 98, 88, 92, "Walking", "Calibrated and ready.")
    )
}
