package com.motioniq.app

import com.motioniq.app.data.local.db.entity.ActivitySessionEntity
import com.motioniq.app.data.local.db.entity.DailyStatsEntity
import com.motioniq.app.data.local.db.entity.RoutePointEntity
import com.motioniq.app.model.*
import org.junit.Assert.*
import org.junit.Test

class DatabaseEntityTest {

    @Test
    fun activitySessionEntity_toDomainAndFromDomain_matchesExactly() {
        val original = MovementActivity(
            id = "test-session-001",
            type = ActivityType.RUNNING,
            startTimeMillis = 1700000000000L,
            endTimeMillis = 1700003600000L,
            durationSeconds = 3600L,
            steps = 5400L,
            distanceMeters = 5200.5,
            caloriesKcal = 420,
            avgSpeedKmh = 5.2,
            avgPaceMinPerKm = 11.53,
            startPlaceName = "GPS Origin",
            endPlaceName = "GPS Finish",
            confidenceLevel = ConfidenceLevel.HIGH,
            stepSource = StepSource.HARDWARE_SENSOR
        )

        val entity = ActivitySessionEntity.fromDomain(original)
        assertEquals("test-session-001", entity.id)
        assertEquals("RUNNING", entity.type)
        assertEquals(5400L, entity.steps)
        assertEquals(5200.5, entity.distanceMeters, 0.001)
        assertEquals(420, entity.caloriesKcal)

        val convertedBack = entity.toDomain()
        assertEquals(original.id, convertedBack.id)
        assertEquals(original.type, convertedBack.type)
        assertEquals(original.steps, convertedBack.steps)
        assertEquals(original.distanceMeters, convertedBack.distanceMeters, 0.001)
        assertEquals(original.caloriesKcal, convertedBack.caloriesKcal)
        assertEquals(original.confidenceLevel, convertedBack.confidenceLevel)
        assertEquals(original.stepSource, convertedBack.stepSource)
    }

    @Test
    fun routePointEntity_toDomainAndFromDomain_matches() {
        val point = RoutePoint(
            latitude = 37.7749,
            longitude = -122.4194,
            altitudeMeters = 45.2,
            speedMps = 2.5f,
            timestampMillis = 1700000005000L
        )

        val entity = RoutePointEntity.fromDomain("session-abc", point)
        assertEquals("session-abc", entity.sessionId)
        assertEquals(37.7749, entity.latitude, 0.0001)
        assertEquals(-122.4194, entity.longitude, 0.0001)
        assertEquals(45.2, entity.altitudeMeters!!, 0.01)
        assertEquals(2.5f, entity.speedMps!!, 0.01f)

        val domainPoint = entity.toDomain()
        assertEquals(point.latitude, domainPoint.latitude, 0.0001)
        assertEquals(point.longitude, domainPoint.longitude, 0.0001)
        assertEquals(point.altitudeMeters!!, domainPoint.altitudeMeters!!, 0.01)
        assertEquals(point.speedMps!!, domainPoint.speedMps!!, 0.01f)
    }

    @Test
    fun dailyStatsEntity_toDomainAndFromDomain_preservesMetrics() {
        val summary = DailySummary(
            date = "2026-09-04",
            steps = 8450L,
            distanceMeters = 6200.0,
            caloriesKcal = 380,
            activeMinutes = 52,
            activityCount = 2
        )

        val entity = DailyStatsEntity.fromDomain(summary)
        assertEquals("2026-09-04", entity.date)
        assertEquals(8450L, entity.steps)
        assertEquals(6200.0, entity.distanceMeters, 0.001)
        assertEquals(380, entity.caloriesKcal)
        assertEquals(52, entity.activeMinutes)
        assertEquals(2, entity.activityCount)

        val back = entity.toDomain()
        assertEquals(summary, back)
    }
}
