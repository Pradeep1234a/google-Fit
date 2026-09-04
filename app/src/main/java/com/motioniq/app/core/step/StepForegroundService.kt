package com.motioniq.app.core.step

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.motioniq.app.MainActivity

/**
 * Foreground service for continuous activity tracking and background movement monitoring.
 *
 * Capabilities:
 * - Keeps tracking engine alive during active workouts
 * - Dynamically updates ongoing notification with live workout metrics (duration, distance, steps)
 * - Safe foregroundServiceType declaration (health + location when permitted)
 * - Graceful stop and lifecycle cleanup
 */
class StepForegroundService : Service() {

    companion object {
        private const val TAG = "StepForegroundService"
        private const val CHANNEL_ID = "motioniq_step_tracking"
        private const val NOTIFICATION_ID = 1001

        const val ACTION_START = "com.motioniq.app.ACTION_START"
        const val ACTION_UPDATE = "com.motioniq.app.ACTION_UPDATE"
        const val ACTION_STOP = "com.motioniq.app.ACTION_STOP"

        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_CONTENT = "extra_content"

        fun start(context: Context, title: String = "MOTIONIQ", content: String = "Tracking your movement...") {
            val intent = Intent(context, StepForegroundService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_CONTENT, content)
            }
            try {
                context.startForegroundService(intent)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start foreground service", e)
            }
        }

        fun update(context: Context, title: String = "MOTIONIQ", content: String) {
            val intent = Intent(context, StepForegroundService::class.java).apply {
                action = ACTION_UPDATE
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_CONTENT, content)
            }
            try {
                context.startService(intent)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to send update to foreground service", e)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, StepForegroundService::class.java).apply {
                action = ACTION_STOP
            }
            try {
                context.stopService(intent)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to stop foreground service", e)
            }
        }
    }

    private var currentTitle = "MOTIONIQ"
    private var currentContent = "Tracking your movement..."

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Log.d(TAG, "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        val newTitle = intent?.getStringExtra(EXTRA_TITLE) ?: currentTitle
        val newContent = intent?.getStringExtra(EXTRA_CONTENT) ?: currentContent
        currentTitle = newTitle
        currentContent = newContent

        val notification = buildNotification(currentTitle, currentContent)

        if (intent?.action == ACTION_UPDATE) {
            val manager = getSystemService(NotificationManager::class.java)
            manager?.notify(NOTIFICATION_ID, notification)
            return START_STICKY
        }

        val hasLocationPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val fgType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (hasLocationPermission) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH or ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            } else {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
        } else {
            0
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(NOTIFICATION_ID, notification, fgType)
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
            Log.i(TAG, "Service started in foreground with type $fgType")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to startForeground with type $fgType, falling back to basic startForeground", e)
            try {
                startForeground(NOTIFICATION_ID, notification)
            } catch (e2: Exception) {
                Log.e(TAG, "Fatal foreground start failure", e2)
            }
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        Log.d(TAG, "Service destroyed")
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Activity Tracking",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows live workout and movement metrics"
            setShowBadge(false)
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    private fun buildNotification(title: String, contentText: String): Notification {
        val launchIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_directions)
            .setContentTitle(title)
            .setContentText(contentText)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
}
