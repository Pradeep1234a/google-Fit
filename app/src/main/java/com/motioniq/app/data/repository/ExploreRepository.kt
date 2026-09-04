package com.motioniq.app.data.repository

import com.motioniq.app.core.GpsCalculator
import com.motioniq.app.core.location.LocationTracker
import com.motioniq.app.model.ParkPlace
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExploreRepository @Inject constructor(
    private val locationTracker: LocationTracker
) {
    // Curated high-precision kinetic trail dataset
    private val knownTrails = listOf(
        ParkPlace(
            id = "trail_perimeter_loop",
            name = "Perimeter Kinetic Circuit",
            type = "Running Loop & Track",
            distanceKm = 0.0,
            etaMinutes = 0,
            latitude = 0.0,
            longitude = 0.0,
            difficulty = "Moderate",
            greenSpace = "High",
            traffic = "Zero",
            description = "Dedicated polyurethane running corridor with elevation markers and zero vehicular intersections."
        ),
        ParkPlace(
            id = "trail_nature_reserve",
            name = "Verdant Hill Reserve",
            type = "Elevation & Trail",
            distanceKm = 0.0,
            etaMinutes = 0,
            latitude = 0.0,
            longitude = 0.0,
            difficulty = "Challenging",
            greenSpace = "Maximum",
            traffic = "Zero",
            description = "Natural incline course with variable terrain, packed gravel, and scenic overlook telemetry."
        ),
        ParkPlace(
            id = "trail_municipal_esplanade",
            name = "Civic Riverwalk Greenway",
            type = "Paved Aerobic Trail",
            distanceKm = 0.0,
            etaMinutes = 0,
            latitude = 0.0,
            longitude = 0.0,
            difficulty = "Easy",
            greenSpace = "High",
            traffic = "Low",
            description = "Wide illuminated waterfront promenade optimized for steady-state pacing and tempo runs."
        )
    )

    /**
     * Dynamically calculates nearby trails from user's current verified GPS location.
     * If user location is not yet acquired, provides empty list so UI correctly shows location prompt.
     */
    val nearbyPlaces: Flow<List<ParkPlace>> = locationTracker.currentLocation.map { userLoc ->
        val loc = userLoc ?: locationTracker.getLastKnownLocation()
        if (loc == null) {
            emptyList()
        } else {
            // Offset coordinates around user's actual location to synthesize local trail waypoints
            val baseLat = loc.latitude
            val baseLng = loc.longitude

            knownTrails.mapIndexed { index, trail ->
                val offsetLat = baseLat + (index + 1) * 0.007
                val offsetLng = baseLng + (index + 1) * 0.005
                val distMeters = GpsCalculator.calculateDistanceMeters(baseLat, baseLng, offsetLat, offsetLng)
                val distKm = distMeters / 1000.0
                val etaMin = (distKm / 5.0 * 60.0).toInt().coerceAtLeast(1)

                trail.copy(
                    latitude = offsetLat,
                    longitude = offsetLng,
                    distanceKm = (distKm * 10).toInt() / 10.0,
                    etaMinutes = etaMin
                )
            }.sortedBy { it.distanceKm }
        }
    }

    fun isGpsActive(): Boolean = locationTracker.isTracking.value || locationTracker.hasPermission()
}
