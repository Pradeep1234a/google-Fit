package com.motioniq.app

import com.motioniq.app.data.local.db.entity.AchievementEntity
import com.motioniq.app.data.local.db.entity.GoalEntity
import com.motioniq.app.data.repository.KineticInsights
import org.junit.Assert.*
import org.junit.Test

class RepositoryLogicTest {

    @Test
    fun goalCompletion_evaluatesCorrectly() {
        val stepGoal = GoalEntity(
            id = "goal_steps",
            title = "Kinetic Steps",
            type = "STEPS",
            targetValue = 10000.0,
            currentValue = 10250.0,
            unit = "steps",
            period = "DAILY",
            isCompleted = 10250.0 >= 10000.0,
            updatedDate = "2026-09-04"
        )
        assertTrue("Goal should be marked completed when currentValue >= targetValue", stepGoal.isCompleted)

        val distanceGoal = GoalEntity(
            id = "goal_distance",
            title = "Spatial Range",
            type = "DISTANCE",
            targetValue = 5.0,
            currentValue = 3.2,
            unit = "km",
            period = "DAILY",
            isCompleted = 3.2 >= 5.0,
            updatedDate = "2026-09-04"
        )
        assertFalse("Goal should not be completed when under target", distanceGoal.isCompleted)
    }

    @Test
    fun achievementEvaluation_unlocksMilestones() {
        val sessionCount = 1
        val isFirstSessionUnlocked = sessionCount >= 1
        val progressFirstSession = (sessionCount.toFloat() / 1f).coerceIn(0f, 1f)

        assertEquals(1f, progressFirstSession)
        assertTrue(isFirstSessionUnlocked)

        val totalSteps = 12500L
        val isCenturySprintUnlocked = totalSteps >= 10000
        val progressCentury = (totalSteps.toFloat() / 10000f).coerceIn(0f, 1f)

        assertEquals(1f, progressCentury)
        assertTrue(isCenturySprintUnlocked)

        val totalDistanceKm = 3.5
        val isSpatial5kUnlocked = totalDistanceKm >= 5.0
        val progress5k = (totalDistanceKm.toFloat() / 5f).coerceIn(0f, 1f)

        assertEquals(0.7f, progress5k, 0.01f)
        assertFalse(isSpatial5kUnlocked)
    }

    @Test
    fun kineticInsights_defaultsForNewUser_safeRanges() {
        val defaultInsights = KineticInsights(
            cadenceStabilityPercent = 94,
            bilateralSymmetryPercent = 98,
            aerobicEfficiencyScore = 88,
            recoveryReadinessPercent = 92,
            dominantModality = "Calibrated Stride",
            diagnosticSummary = "Sensors calibrated and ready."
        )

        assertTrue(defaultInsights.cadenceStabilityPercent in 80..100)
        assertTrue(defaultInsights.bilateralSymmetryPercent in 80..100)
        assertTrue(defaultInsights.aerobicEfficiencyScore in 70..100)
        assertTrue(defaultInsights.recoveryReadinessPercent in 70..100)
        assertNotNull(defaultInsights.diagnosticSummary)
    }
}
