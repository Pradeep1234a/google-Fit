package com.motioniq.app.core.health

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import com.motioniq.app.model.MovementActivity
import java.time.Instant
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production Health Connect Integration Bridge
 *
 * Implements Android's official Health Connect ecosystem guidelines:
 * - Checks Health Connect availability (OS-integrated on Android 14+, APK on Android 10-13)
 * - Manages read and write operations for Steps, Distance, Calories, and Exercise Sessions
 * - Deduplicates records using unique client IDs
 * - Safe error handling when Health Connect is unavailable or uninstalled
 */
@Singleton
class HealthConnectBridge @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "HealthConnectBridge"
        const val HEALTH_CONNECT_PACKAGE = "com.google.android.apps.healthdata"
        private const val PREFS_NAME = "motioniq_health_connect"
        private const val KEY_SYNC_ENABLED = "sync_enabled"
        private const val KEY_LAST_SYNC_MILLIS = "last_sync_millis"
        private const val KEY_LAST_SYNCED_STEPS = "last_synced_steps"

        val REQUIRED_PERMISSIONS = setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getWritePermission(StepsRecord::class),
            HealthPermission.getReadPermission(DistanceRecord::class),
            HealthPermission.getWritePermission(DistanceRecord::class),
            HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
            HealthPermission.getWritePermission(TotalCaloriesBurnedRecord::class),
            HealthPermission.getReadPermission(ExerciseSessionRecord::class),
            HealthPermission.getWritePermission(ExerciseSessionRecord::class)
        )
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    enum class HealthConnectStatus {
        AVAILABLE_SYSTEM,
        AVAILABLE_APP,
        NOT_INSTALLED,
        NOT_SUPPORTED
    }

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
        get() = try {
            HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE
        } catch (_: Exception) {
            getStatus() == HealthConnectStatus.AVAILABLE_SYSTEM || getStatus() == HealthConnectStatus.AVAILABLE_APP
        }

    val client: HealthConnectClient?
        get() = try {
            if (isAvailable) HealthConnectClient.getOrCreate(context) else null
        } catch (e: Exception) {
            Log.w(TAG, "Failed to obtain HealthConnectClient", e)
            null
        }

    var isSyncEnabled: Boolean
        get() = prefs.getBoolean(KEY_SYNC_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_SYNC_ENABLED, value).apply()

    var lastSyncMillis: Long
        get() = prefs.getLong(KEY_LAST_SYNC_MILLIS, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_SYNC_MILLIS, value).apply()

    var lastSyncedSteps: Long
        get() = prefs.getLong(KEY_LAST_SYNCED_STEPS, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_SYNCED_STEPS, value).apply()

    fun getInstallIntent(): Intent {
        val uri = "market://details?id=$HEALTH_CONNECT_PACKAGE&url=healthconnect%3A%2F%2Fonboarding"
        return Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(uri)
            setPackage("com.android.vending")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

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

    suspend fun hasAllPermissions(): Boolean {
        val hc = client ?: return false
        return try {
            val granted = hc.permissionController.getGrantedPermissions()
            granted.containsAll(REQUIRED_PERMISSIONS)
        } catch (e: Exception) {
            Log.w(TAG, "Error checking Health Connect permissions", e)
            false
        }
    }

    suspend fun syncCompletedActivity(activity: MovementActivity): Boolean {
        if (!isSyncEnabled || !isAvailable) return false
        val hc = client ?: return false

        return try {
            val startTime = Instant.ofEpochMilli(activity.startTimeMillis)
            val endTime = Instant.ofEpochMilli(activity.endTimeMillis)
            val zoneOffset = ZoneOffset.systemDefault().rules.getOffset(startTime)

            val clientRecordId = "motioniq_session_${activity.id}"

            // 1. Write Exercise Session Record
            val exerciseSession = ExerciseSessionRecord(
                startTime = startTime,
                startZoneOffset = zoneOffset,
                endTime = endTime,
                endZoneOffset = zoneOffset,
                exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_WALKING,
                title = "MOTIONIQ ${activity.type.displayName}",
                metadata = Metadata.manualEntry(clientRecordId = clientRecordId)
            )

            // 2. Write Steps Record
            val stepsRecord = StepsRecord(
                startTime = startTime,
                startZoneOffset = zoneOffset,
                endTime = endTime,
                endZoneOffset = zoneOffset,
                count = activity.steps,
                metadata = Metadata.manualEntry(clientRecordId = "${clientRecordId}_steps")
            )

            // 3. Write Distance Record
            val distanceRecord = DistanceRecord(
                startTime = startTime,
                startZoneOffset = zoneOffset,
                endTime = endTime,
                endZoneOffset = zoneOffset,
                distance = Length.meters(activity.distanceMeters),
                metadata = Metadata.manualEntry(clientRecordId = "${clientRecordId}_dist")
            )

            // 4. Write Calories Record
            val caloriesRecord = TotalCaloriesBurnedRecord(
                startTime = startTime,
                startZoneOffset = zoneOffset,
                endTime = endTime,
                endZoneOffset = zoneOffset,
                energy = Energy.kilocalories(activity.caloriesKcal.toDouble()),
                metadata = Metadata.manualEntry(clientRecordId = "${clientRecordId}_cals")
            )

            hc.insertRecords(listOf(exerciseSession, stepsRecord, distanceRecord, caloriesRecord))
            lastSyncMillis = System.currentTimeMillis()
            Log.i(TAG, "Successfully synced activity ${activity.id} to Health Connect")
            true
        } catch (e: Exception) {
            Log.w(TAG, "Failed to write activity to Health Connect", e)
            false
        }
    }

    suspend fun readTodayStepsFromHealthConnect(): Long {
        if (!isAvailable) return 0L
        val hc = client ?: return 0L

        return try {
            val now = Instant.now()
            val startOfDay = now.atZone(ZoneOffset.systemDefault()).toLocalDate().atStartOfDay().toInstant(ZoneOffset.systemDefault().rules.getOffset(now))
            val response = hc.readRecords(
                ReadRecordsRequest(
                    recordType = StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startOfDay, now)
                )
            )
            response.records.sumOf { it.count }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to read steps from Health Connect", e)
            0L
        }
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
