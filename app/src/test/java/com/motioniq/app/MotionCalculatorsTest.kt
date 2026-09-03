package com.motioniq.app

import com.motioniq.app.core.CalorieCalculator
import com.motioniq.app.core.GpsCalculator
import com.motioniq.app.model.ActivityType
import com.motioniq.app.model.RoutePoint
import org.junit.Assert.*
import org.junit.Test

class MotionCalculatorsTest {

    @Test
    fun testCalorieCalculator_walking() {
        val calories = CalorieCalculator.calculate(
            activityType = ActivityType.WALKING,
            weightKg = 70.0,
            durationSeconds = 3600, // 1 hour
            speedKmh = 4.8
        )
        // 3.8 MET * 70kg * 1h = ~266 kcal
        assertTrue("Calories should be around 266, was $calories", calories in 250..280)
    }

    @Test
    fun testCalorieCalculator_running() {
        val calories = CalorieCalculator.calculate(
            activityType = ActivityType.RUNNING,
            weightKg = 70.0,
            durationSeconds = 1800, // 30 min (0.5h)
            speedKmh = 10.0
        )
        // 10.0 MET * 70kg * 0.5h = ~350 kcal
        assertTrue("Running calories should be around 350, was $calories", calories in 330..370)
    }

    @Test
    fun testGpsCalculator_haversineDistance() {
        // Known distance between (0, 0) and (0, 1 degree lon) at equator ~ 111.3 km
        val distance = GpsCalculator.calculateDistanceMeters(0.0, 0.0, 0.0, 1.0)
        assertTrue("Distance should be ~111.3 km, was $distance", distance in 111000.0..112000.0)
    }

    @Test
    fun testGpsCalculator_paceFormatting() {
        val paceFormatted = GpsCalculator.formatPace(5.5) // 5 min 30 sec / km
        assertEquals("5:30 / km", paceFormatted)
    }

    @Test
    fun testGpsCalculator_durationFormatting() {
        val formattedShort = GpsCalculator.formatDuration(185) // 3m 5s
        assertEquals("03:05", formattedShort)

        val formattedLong = GpsCalculator.formatDuration(3665) // 1h 1m 5s
        assertEquals("01:01:05", formattedLong)
    }
}
