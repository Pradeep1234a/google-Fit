package com.motioniq.app.core

import com.motioniq.app.model.RoutePoint
import kotlin.math.*

object GpsCalculator {
    private const val EARTH_RADIUS_METERS = 6371000.0

    /**
     * Calculates distance between two coordinates using Haversine formula.
     */
    fun calculateDistanceMeters(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_METERS * c
    }

    /**
     * Calculates total distance along a route polyline.
     */
    fun calculateRouteDistanceMeters(points: List<RoutePoint>): Double {
        if (points.size < 2) return 0.0
        var total = 0.0
        for (i in 0 until points.size - 1) {
            val p1 = points[i]
            val p2 = points[i + 1]
            total += calculateDistanceMeters(p1.latitude, p1.longitude, p2.latitude, p2.longitude)
        }
        return total
    }

    /**
     * Calculates average speed in km/h.
     */
    fun calculateSpeedKmh(distanceMeters: Double, durationSeconds: Long): Double {
        if (durationSeconds <= 0) return 0.0
        val hours = durationSeconds / 3600.0
        val km = distanceMeters / 1000.0
        return km / hours
    }

    /**
     * Calculates pace in minutes per km.
     */
    fun calculatePaceMinPerKm(distanceMeters: Double, durationSeconds: Long): Double {
        if (distanceMeters < 10.0) return 0.0
        val km = distanceMeters / 1000.0
        val minutes = durationSeconds / 60.0
        return minutes / km
    }

    /**
     * Formats pace as M:SS / km
     */
    fun formatPace(paceMinPerKm: Double): String {
        if (paceMinPerKm <= 0.0 || paceMinPerKm.isInfinite() || paceMinPerKm.isNaN() || paceMinPerKm > 60.0) {
            return "--:-- / km"
        }
        val mins = paceMinPerKm.toInt()
        val secs = ((paceMinPerKm - mins) * 60).roundToInt().coerceIn(0, 59)
        return "%d:%02d / km".format(mins, secs)
    }

    /**
     * Formats duration as HH:MM:SS or MM:SS
     */
    fun formatDuration(totalSeconds: Long): String {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) {
            "%02d:%02d:%02d".format(hours, minutes, seconds)
        } else {
            "%02d:%02d".format(minutes, seconds)
        }
    }
}
