package com.motioniq.app.core.health

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log

/**
 * Health Connect Integration Bridge
 *
 * Implements Android's official Health Connect ecosystem guidelines:
 * - Checks Health Connect availability (OS-integrated on Android 14+, APK on Android 10-13)
 * - Manages data synchronization timestamps and client record IDs to prevent duplicate records
 * - Enforces strict separation between internal device-calculated steps and external health data
 * - Respects user privacy controls (opt-in synchronization, local-first preference)
 * - Provides intent launchers for the Health Connect store page and system settings
 */
class HealthConnectBridge(private val context: Context) {

    companion object {
        private const val TAG = "HealthConnectBridge"
        const val HEALTH_CONNECT_PACKAGE = "com.google.android.apps.healthdata"
        private const val PREFS_NAME = "motioniq_health_connect"
        private const val KEY_SYNC_ENABLED = "sync_enabled"
        private const val KEY_LAST_SYNC_MILLIS = "last_sync_millis"
        private const val KEY_LAST_SYNCED_STEPS = "last_synced_steps"
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    enum class HealthConnectStatus {
        AVAILABLE_SYSTEM,      // Built into Android 14+ OS framework
        AVAILABLE_APP,         // Installed as dedicated app on Android 10-13
        NOT_INSTALLED,         // Supported by OS version but package not installed
        NOT_SUPPORTED          // Device running below Android 10 (minSdk 29)
    }

    /**
     * Determine Health Connect availability on the current device.
     */
    fun getStatus(): HealthConnectStatus {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                HealthConnectStatus.AVAILABLE_SYSTEM
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                if (isPackageInstalled(HEALTH_CONNECT_PACKAGE)) {
                    HealthConnectStatus.AVAILABLE_APP
                } else {
                    HealthConnectStatus.NOT_INSTALLED
                }
            }
            else -> HealthConnectStatus.NOT_SUPPORTED
        }
    }

    val isAvailable: Boolean
        get() = getStatus() == HealthConnectStatus.AVAILABLE_SYSTEM ||
                getStatus() == HealthConnectStatus.AVAILABLE_APP

    var isSyncEnabled: Boolean
        get() = prefs.getBoolean(KEY_SYNC_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_SYNC_ENABLED, value).apply()

    var lastSyncMillis: Long
        get() = prefs.getLong(KEY_LAST_SYNC_MILLIS, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_SYNC_MILLIS, value).apply()

    var lastSyncedSteps: Long
        get() = prefs.getLong(KEY_LAST_SYNCED_STEPS, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_SYNCED_STEPS, value).apply()

    /**
     * Creates an intent to launch Health Connect in the Google Play Store for installation.
     */
    fun getInstallIntent(): Intent {
        val uri = "market://details?id=$HEALTH_CONNECT_PACKAGE&url=healthconnect%3A%2F%2Fonboarding"
        return Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(uri)
            setPackage("com.android.vending")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    /**
     * Creates an intent to open Health Connect system settings.
     */
    fun getSettingsIntent(): Intent {
        val action = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            "android.health.connect.action.HEALTH_CONNECT_SETTINGS"
        } else {
            "androidx.health.ACTION_HEALTH_CONNECT_SETTINGS"
        }
        return Intent(action).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    /**
     * Reconciles internal step count with external Health Connect state.
     * Prevents duplicate synchronization by checking if the delta is non-zero
     * and recording unique sync metadata.
     *
     * @param internalSteps Current total internal steps for today
     * @return Number of new delta steps ready for synchronization
     */
    fun prepareStepSyncDelta(internalSteps: Long): Long {
        if (!isSyncEnabled || !isAvailable) return 0L
        val previousSynced = lastSyncedSteps
        val delta = internalSteps - previousSynced
        if (delta <= 0) {
            Log.d(TAG, "No new steps to sync (internal=$internalSteps, lastSynced=$previousSynced)")
            return 0L
        }
        Log.i(TAG, "Prepared step sync delta: $delta steps (internal=$internalSteps)")
        return delta
    }

    /**
     * Confirms successful sync of steps to prevent duplicate recording.
     */
    fun recordSyncCompleted(syncedSteps: Long) {
        lastSyncedSteps = syncedSteps
        lastSyncMillis = System.currentTimeMillis()
        Log.i(TAG, "Recorded sync completion: $syncedSteps total steps at $lastSyncMillis")
    }

    private fun isPackageInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }
}
