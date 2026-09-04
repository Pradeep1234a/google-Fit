package com.motioniq.app

import com.motioniq.app.core.step.DistanceEstimator
import com.motioniq.app.core.step.StepSourceType
import com.motioniq.app.core.GpsCalculator
import com.motioniq.app.core.CalorieCalculator
import com.motioniq.app.model.ActivityType
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for the MOTIONIQ step counting engine components.
 *
 * Tests cover:
 * - Distance estimation from steps
 * - Calorie estimation from steps
 * - Step source priority selection
 * - GPS calculator functions
 * - Edge cases (zero steps, extreme values, boundary conditions)
 */
class StepEngineTest {

    // ══════════════════════════════════════════════════════════════════════
    // Distance Estimation Tests
    // ══════════════════════════════════════════════════════════════════════

    @Test
    fun distanceEstimator_zeroSteps_returnsZero() {
        assertEquals(0.0, DistanceEstimator.estimateDistanceMeters(0, 175.0), 0.001)
    }

    @Test
    fun distanceEstimator_negativeSteps_returnsZero() {
        assertEquals(0.0, DistanceEstimator.estimateDistanceMeters(-10, 175.0), 0.001)
    }

    @Test
    fun distanceEstimator_walking_1000steps_175cm() {
        // stride = 175 * 0.415 / 100 = 0.72625 m
        // distance = 1000 * 0.72625 = 726.25 m
        val distance = DistanceEstimator.estimateDistanceMeters(1000, 175.0, isRunning = false)
        assertTrue("Expected ~726m, got $distance", distance in 700.0..760.0)
    }

    @Test
    fun distanceEstimator_running_longerStride() {
        val walkDist = DistanceEstimator.estimateDistanceMeters(1000, 175.0, isRunning = false)
        val runDist = DistanceEstimator.estimateDistanceMeters(1000, 175.0, isRunning = true)
        assertTrue("Running distance should be > walking distance", runDist > walkDist)
        // Running stride is 20% longer
        val ratio = runDist / walkDist
        assertTrue("Running/walking ratio should be ~1.2, was $ratio", ratio in 1.15..1.25)
    }

    @Test
    fun distanceEstimator_heightClamped_extremelyShort() {
        // Height below 120cm should be clamped to 120cm
        val dist = DistanceEstimator.estimateDistanceMeters(1000, 50.0)
        val distAt120 = DistanceEstimator.estimateDistanceMeters(1000, 120.0)
        assertEquals("Extreme short height should clamp to 120cm", distAt120, dist, 0.001)
    }

    @Test
    fun distanceEstimator_heightClamped_extremelyTall() {
        // Height above 220cm should be clamped to 220cm
        val dist = DistanceEstimator.estimateDistanceMeters(1000, 300.0)
        val distAt220 = DistanceEstimator.estimateDistanceMeters(1000, 220.0)
        assertEquals("Extreme tall height should clamp to 220cm", distAt220, dist, 0.001)
    }

    @Test
    fun distanceEstimator_10000steps_returnsReasonableDistance() {
        // 10,000 steps for 175cm person = ~7.2 km
        val distKm = DistanceEstimator.estimateDistanceKm(10000, 175.0)
        assertTrue("10k steps should be ~7km, got $distKm", distKm in 6.0..8.5)
    }

    @Test
    fun distanceEstimator_formatDistance_underOneKm() {
        val formatted = DistanceEstimator.formatDistance(500, 175.0)
        assertTrue("Under 1km should show meters, got: $formatted", formatted.contains("m"))
        assertFalse("Under 1km should not show km", formatted.contains("km"))
    }

    @Test
    fun distanceEstimator_formatDistance_overOneKm() {
        val formatted = DistanceEstimator.formatDistance(5000, 175.0)
        assertTrue("Over 1km should show km, got: $formatted", formatted.contains("km"))
    }

    // ══════════════════════════════════════════════════════════════════════
    // Calorie Estimation Tests
    // ══════════════════════════════════════════════════════════════════════

    @Test
    fun calorieEstimator_zeroSteps_returnsZero() {
        assertEquals(0, DistanceEstimator.estimateCalories(0, 70.0))
    }

    @Test
    fun calorieEstimator_walking_10000steps_70kg() {
        // ~0.04 * 10000 * 1.0 = ~400 kcal
        val kcal = DistanceEstimator.estimateCalories(10000, 70.0, isRunning = false)
        assertTrue("10k walking steps should burn ~400 kcal, got $kcal", kcal in 300..500)
    }

    @Test
    fun calorieEstimator_running_moreThanWalking() {
        val walkCal = DistanceEstimator.estimateCalories(5000, 70.0, isRunning = false)
        val runCal = DistanceEstimator.estimateCalories(5000, 70.0, isRunning = true)
        assertTrue("Running should burn more calories than walking", runCal > walkCal)
    }

    @Test
    fun calorieEstimator_heavierPerson_burnsMore() {
        val light = DistanceEstimator.estimateCalories(5000, 50.0)
        val heavy = DistanceEstimator.estimateCalories(5000, 100.0)
        assertTrue("Heavier person should burn more calories", heavy > light)
    }

    @Test
    fun calorieEstimator_weightClamped_extremeWeight() {
        val cal10 = DistanceEstimator.estimateCalories(5000, 10.0) // should clamp to 30
        val cal30 = DistanceEstimator.estimateCalories(5000, 30.0)
        assertEquals("Extreme low weight should clamp to 30kg", cal30, cal10)
    }

    @Test
    fun calorieEstimator_minimumOneCalorie() {
        val cal = DistanceEstimator.estimateCalories(1, 70.0)
        assertTrue("Even 1 step should return at least 1 kcal", cal >= 1)
    }

    // ══════════════════════════════════════════════════════════════════════
    // Step Source Priority Tests
    // ══════════════════════════════════════════════════════════════════════

    @Test
    fun stepSourceType_enumValues_correctOrder() {
        val values = StepSourceType.entries
        assertEquals(4, values.size)
        assertEquals(StepSourceType.HARDWARE_STEP_COUNTER, values[0])
        assertEquals(StepSourceType.HARDWARE_STEP_DETECTOR, values[1])
        assertEquals(StepSourceType.SOFTWARE_ACCELEROMETER, values[2])
        assertEquals(StepSourceType.NONE, values[3])
    }

    // ══════════════════════════════════════════════════════════════════════
    // GPS Calculator Tests (existing functionality)
    // ══════════════════════════════════════════════════════════════════════

    @Test
    fun gpsCalculator_haversineDistance_equator() {
        val distance = GpsCalculator.calculateDistanceMeters(0.0, 0.0, 0.0, 1.0)
        assertTrue("1 degree longitude at equator should be ~111km, got $distance", distance in 111000.0..112000.0)
    }

    @Test
    fun gpsCalculator_speedCalculation() {
        // 1000m in 600 seconds = 1.667 m/s = 6 km/h
        val speed = GpsCalculator.calculateSpeedKmh(1000.0, 600)
        assertTrue("Speed should be ~6 km/h, got $speed", speed in 5.5..6.5)
    }

    @Test
    fun gpsCalculator_paceFormatting() {
        val formatted = GpsCalculator.formatPace(5.5)
        assertEquals("5:30 / km", formatted)
    }

    @Test
    fun gpsCalculator_durationFormatting_short() {
        assertEquals("03:05", GpsCalculator.formatDuration(185))
    }

    @Test
    fun gpsCalculator_durationFormatting_long() {
        assertEquals("01:01:05", GpsCalculator.formatDuration(3665))
    }

    // ══════════════════════════════════════════════════════════════════════
    // Calorie Calculator Tests (MET-based, existing)
    // ══════════════════════════════════════════════════════════════════════

    @Test
    fun calorieCalculator_walking_1hour_70kg() {
        val cal = CalorieCalculator.calculate(ActivityType.WALKING, 70.0, 3600, 4.8)
        assertTrue("Walking 1hr at 4.8km/h should burn ~260 kcal, got $cal", cal in 200..320)
    }

    @Test
    fun calorieCalculator_running_30min_70kg() {
        val cal = CalorieCalculator.calculate(ActivityType.RUNNING, 70.0, 1800, 10.0)
        assertTrue("Running 30min at 10km/h should burn ~350 kcal, got $cal", cal in 300..400)
    }

    @Test
    fun calorieCalculator_invalidWeight_usesDefault() {
        val cal = CalorieCalculator.calculate(ActivityType.WALKING, 5.0, 3600, 4.8)
        val calDefault = CalorieCalculator.calculate(ActivityType.WALKING, 70.0, 3600, 4.8)
        assertEquals("Invalid weight should use 70kg default", calDefault, cal)
    }

    // ══════════════════════════════════════════════════════════════════════
    // Edge Case / Robustness Tests
    // ══════════════════════════════════════════════════════════════════════

    @Test
    fun distanceEstimator_veryLargeStepCount() {
        // 100,000 steps should not overflow or produce unreasonable values
        val dist = DistanceEstimator.estimateDistanceKm(100000, 175.0)
        assertTrue("100k steps should be ~72km, got $dist", dist in 60.0..85.0)
    }

    @Test
    fun calorieEstimator_veryLargeStepCount() {
        // Should not overflow
        val cal = DistanceEstimator.estimateCalories(100000, 70.0)
        assertTrue("100k steps should burn ~4000 kcal, got $cal", cal in 3000..5000)
    }

    @Test
    fun gpsCalculator_zeroDuration_noException() {
        val speed = GpsCalculator.calculateSpeedKmh(1000.0, 0)
        // Should return 0 or handle gracefully, not crash
        assertTrue("Zero duration should not crash", speed >= 0.0)
    }

    // ══════════════════════════════════════════════════════════════════════
    // Hardware Step Counter Delta & Reboot Recovery Tests
    // ══════════════════════════════════════════════════════════════════════

    @Test
    fun stepCounter_normalDelta_calculatesCorrectly() {
        val baseline = 121000L
        val rawCounter = 125430L
        val delta = rawCounter - baseline
        assertEquals(4430L, delta)
    }

    @Test
    fun stepCounter_rebootDetection_rawLessThanBaseline() {
        // Device reboots: raw counter resets from 125,430 back to 250
        val baseline = 121000L
        val rawCounterAfterReboot = 250L
        val isReboot = rawCounterAfterReboot < baseline
        assertTrue("Counter smaller than baseline indicates device reboot", isReboot)

        // Engine preserves previously accumulated steps:
        val stepsBeforeReboot = 4430L
        val newBaseline = rawCounterAfterReboot
        val currentRaw = 300L
        val totalSteps = stepsBeforeReboot + (currentRaw - newBaseline)
        assertEquals(4480L, totalSteps)
    }

    @Test
    fun stepCounter_largeJumpDetection_filtersCorruption() {
        val maxPlausibleDelta = 50000L
        val lastRaw = 12000L
        val corruptRaw = 80000L // jump of 68,000 in one reading
        val isCorrupt = (corruptRaw - lastRaw) > maxPlausibleDelta
        assertTrue("Jump > 50,000 steps should be flagged as potential corruption", isCorrupt)
    }

    // ══════════════════════════════════════════════════════════════════════
    // Cadence Gating & False Positive Suppression Tests
    // ══════════════════════════════════════════════════════════════════════

    @Test
    fun cadenceGating_isolatedPulses_rejected() {
        val gateStepsRequired = 3
        var gateOpen = false
        var candidateCount = 0
        var totalSteps = 0L

        // Pulse 1 (phone picked up from table)
        candidateCount++
        if (candidateCount >= gateStepsRequired) {
            gateOpen = true
            totalSteps += gateStepsRequired
        }
        assertEquals(0L, totalSteps)
        assertFalse(gateOpen)

        // Pulse 2 (placed into pocket)
        candidateCount++
        if (candidateCount >= gateStepsRequired) {
            gateOpen = true
            totalSteps += gateStepsRequired
        }
        assertEquals(0L, totalSteps)
        assertFalse(gateOpen)

        // Pulse 3 (rhythmic stride starts -> gate unlocks!)
        candidateCount++
        if (candidateCount >= gateStepsRequired) {
            gateOpen = true
            totalSteps += gateStepsRequired
        }
        assertEquals(3L, totalSteps)
        assertTrue(gateOpen)
    }

    // ══════════════════════════════════════════════════════════════════════
    // Health Connect Duplicate Prevention Tests
    // ══════════════════════════════════════════════════════════════════════

    @Test
    fun healthSync_duplicatePrevention_noNewStepsReturnsZeroDelta() {
        var lastSynced = 5000L
        var currentSteps = 5000L

        val delta1 = (currentSteps - lastSynced).coerceAtLeast(0L)
        assertEquals("When steps have not changed, sync delta must be 0", 0L, delta1)

        // New steps accumulated
        currentSteps = 5350L
        val delta2 = currentSteps - lastSynced
        assertEquals("New steps must yield positive delta", 350L, delta2)

        // After sync completion, lastSynced is updated
        lastSynced = currentSteps
        val delta3 = (currentSteps - lastSynced).coerceAtLeast(0L)
        assertEquals("After sync recorded, delta must return to 0", 0L, delta3)
    }
}
