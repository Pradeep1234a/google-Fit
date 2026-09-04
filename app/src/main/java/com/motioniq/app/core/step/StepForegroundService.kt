package com.motioniq.app.core.step

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.motioniq.app.MainActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

/**
 * Foreground service for continuous step counting in the background.
 *
 * This service is ONLY started when the user explicitly begins an activity
 * tracking session (e.g., starts a walk/run from the Activity screen).
 * It is NOT an always-on service — the hardware step counter itself
 * is low-power and doesn't need a service to keep counting.
 *
 * The service serves two purposes:
 * 1. Keeps the step engine alive during active workout sessions
 * 2. Shows a persistent notification so the OS doesn't kill the process
 *
 * When no active workout is running, the engine relies on the hardware
 * step counter's batched delivery when the app is resumed.
 */
class StepForegroundService : Service() {

    companion object {
        private const val TAG = "StepForegroundService"
        private const val CHANNEL_ID = "motioniq_step_tracking"
        private const val NOTIFICATION_ID = 1001

        fun start(context: Context) {
            val intent = Intent(context, StepForegroundService::class.java)
            context.startForegroundService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, StepForegroundService::class.java)
            context.stopService(intent)
        }
    }

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var updateJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Log.d(TAG, "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = buildNotification("Tracking your movement...")
        startForeground(
            NOTIFICATION_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
        )
        Log.i(TAG, "Service started in foreground")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        updateJob?.cancel()
        serviceScope.cancel()
        Log.d(TAG, "Service destroyed")
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Step Tracking",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows when MOTIONIQ is actively tracking your movement"
            setShowBadge(false)
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun buildNotification(contentText: String): Notification {
        val launchIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_directions)
            .setContentTitle("MOTIONIQ")
            .setContentText(contentText)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
}
