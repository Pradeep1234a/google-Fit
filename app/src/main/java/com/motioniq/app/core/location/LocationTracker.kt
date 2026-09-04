package com.motioniq.app.core.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.motioniq.app.core.GpsCalculator
import com.motioniq.app.model.RoutePoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Production-grade device location tracker using Android platform LocationManager.
 *
 * Capabilities:
 * - Direct AOSP platform implementation with zero external API key requirements
 * - Dual provider fallback: GPS (high accuracy outdoor) + Network (coarse fast fix)
 * - Noise & jitter suppression: rejects fixes with accuracy > 35m or distance < 2.0m
 * - Speed anomaly detection: filters out unrealistic multipath jumps (> 45 m/s)
 * - Safe lifecycle management with graceful degradation when permissions are denied
 */
class LocationTracker(private val context: Context) {

    companion object {
        private const val TAG = "LocationTracker"
        private const val MIN_TIME_MS = 2000L      // 2 seconds between updates
        private const val MIN_DISTANCE_M = 2.5f    // 2.5 meters minimum movement
        private const val MAX_ACCURACY_M = 35.0f   // Reject fixes with poor accuracy
        private const val MAX_HUMAN_SPEED_MPS = 45.0 // ~162 km/h upper limit for movement
    }

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private var activeListener: LocationListener? = null
    private var lastAcceptedLocation: Location? = null
    private var onRoutePointCallback: ((RoutePoint) -> Unit)? = null

    /**
     * Checks whether location permissions have been granted by the user.
     */
    fun hasPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    /**
     * Checks whether any location provider (GPS or Network) is enabled on the device.
     */
    fun isLocationEnabled(): Boolean {
        val lm = locationManager ?: return false
        val gpsEnabled = try { lm.isProviderEnabled(LocationManager.GPS_PROVIDER) } catch (_: Exception) { false }
        val netEnabled = try { lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) } catch (_: Exception) { false }
        return gpsEnabled || netEnabled
    }

    /**
     * Returns the best last-known location across available providers.
     */
    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(): Location? {
        if (!hasPermission() || locationManager == null) return null
        var bestLocation: Location? = null
        try {
            val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER)
            for (provider in providers) {
                if (locationManager.isProviderEnabled(provider)) {
                    val loc = locationManager.getLastKnownLocation(provider) ?: continue
                    if (bestLocation == null || loc.accuracy < bestLocation.accuracy || loc.time > bestLocation.time) {
                        bestLocation = loc
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get last known location", e)
        }
        if (bestLocation != null) {
            _currentLocation.value = bestLocation
        }
        return bestLocation
    }

    /**
     * Starts continuous GPS / Network location tracking for active workouts.
     */
    @SuppressLint("MissingPermission")
    fun startTracking(onPointRecorded: ((RoutePoint) -> Unit)? = null): Boolean {
        if (!hasPermission()) {
            Log.w(TAG, "Cannot start tracking: Location permissions not granted")
            return false
        }
        val lm = locationManager ?: return false

        stopTracking()
        onRoutePointCallback = onPointRecorded
        lastAcceptedLocation = getLastKnownLocation()

        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                processNewLocation(location)
            }

            override fun onProviderEnabled(provider: String) {
                Log.d(TAG, "Provider enabled: $provider")
            }

            override fun onProviderDisabled(provider: String) {
                Log.d(TAG, "Provider disabled: $provider")
            }

            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }

        activeListener = listener
        var registeredAny = false

        try {
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                lm.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_MS,
                    MIN_DISTANCE_M,
                    listener,
                    Looper.getMainLooper()
                )
                registeredAny = true
                Log.i(TAG, "Registered GPS_PROVIDER location updates")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not register GPS provider", e)
        }

        try {
            if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                lm.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_MS,
                    MIN_DISTANCE_M,
                    listener,
                    Looper.getMainLooper()
                )
                registeredAny = true
                Log.i(TAG, "Registered NETWORK_PROVIDER location updates")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not register Network provider", e)
        }

        _isTracking.value = registeredAny
        return registeredAny
    }

    /**
     * Stops active location tracking and releases listeners.
     */
    fun stopTracking() {
        activeListener?.let { listener ->
            try {
                locationManager?.removeUpdates(listener)
                Log.i(TAG, "Removed location updates")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to remove location updates", e)
            }
        }
        activeListener = null
        onRoutePointCallback = null
        lastAcceptedLocation = null
        _isTracking.value = false
    }

    /**
     * Filters, validates, and records an incoming GPS coordinate fix.
     */
    internal fun processNewLocation(location: Location) {
        _currentLocation.value = location

        // 1. Accuracy Filter: reject fixes with poor precision
        if (location.hasAccuracy() && location.accuracy > MAX_ACCURACY_M) {
            Log.d(TAG, "Filtered out location: accuracy ${location.accuracy}m > ${MAX_ACCURACY_M}m")
            return
        }

        val lastLoc = lastAcceptedLocation
        if (lastLoc != null) {
            val distMeters = GpsCalculator.calculateDistanceMeters(
                lastLoc.latitude, lastLoc.longitude,
                location.latitude, location.longitude
            )
            val timeDeltaSeconds = (location.time - lastLoc.time) / 1000.0

            // 2. Minimum Movement Filter: avoid GPS drift when standing still
            if (distMeters < MIN_DISTANCE_M) {
                return
            }

            // 3. Speed Anomaly Filter: reject impossible jumps / teleportation
            if (timeDeltaSeconds > 0.1) {
                val impliedSpeed = distMeters / timeDeltaSeconds
                if (impliedSpeed > MAX_HUMAN_SPEED_MPS && distMeters > 50.0) {
                    Log.w(TAG, "Rejected GPS jump: implied speed ${impliedSpeed}m/s over ${distMeters}m")
                    return
                }
            }
        }

        lastAcceptedLocation = location

        val point = RoutePoint(
            latitude = location.latitude,
            longitude = location.longitude,
            altitudeMeters = if (location.hasAltitude()) location.altitude else null,
            speedMps = if (location.hasSpeed()) location.speed else null,
            timestampMillis = location.time.coerceAtLeast(System.currentTimeMillis() - 60000L)
        )

        onRoutePointCallback?.invoke(point)
    }
}
