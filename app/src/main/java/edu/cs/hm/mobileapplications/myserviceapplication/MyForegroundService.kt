package edu.cs.hm.mobileapplications.myserviceapplication

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN


class MyForegroundService : Service() {

    val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    // Notification ID cannot be 0.
    val ONGOING_NOTIFICATION_ID = 11999

    override fun onCreate() {
        // Enable Notification Channel for Android OREO
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("my_service", "My Channel")
            } else {
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                ""
            }

        val stopSelf = Intent(this, MyForegroundService::class.java)
        stopSelf.action = ACTION_STOP_SERVICE
        val pendingIntent: PendingIntent =
            Intent(this, FirstFragment::class.java).let {
                PendingIntent.getService(this, 0, stopSelf, PendingIntent.FLAG_CANCEL_CURRENT)
            }

        val notificationBuilder = NotificationCompat.Builder(this, channelId )
        val notification = notificationBuilder
            .setContentTitle(getText(R.string.notification_title))
            .setContentText(getText(R.string.notification_message))
            .addAction(R.drawable.ic_launcher_background, "Stop Service", pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        startForeground(ONGOING_NOTIFICATION_ID, notification)

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (ACTION_STOP_SERVICE == intent.action) {
            stopForeground(ONGOING_NOTIFICATION_ID)
            stopSelf()
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val channel = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_NONE)
        channel.lightColor = Color.BLUE
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
        return channelId
    }
}
