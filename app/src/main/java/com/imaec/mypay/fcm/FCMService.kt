package com.imaec.mypay.fcm

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.imaec.mypay.R
import com.imaec.mypay.ui.view.activity.MainActivity
import com.imaec.mypay.utils.SharedPreferenceManager
import com.imaec.mypay.utils.SharedPreferenceManager.KEY

class FCMService : FirebaseMessagingService() {

    private val listNotificationChannels = arrayListOf("PAY", "START", "END")

    @SuppressLint("InvalidWakeLockTag")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.data.isNotEmpty()) {
            val data = remoteMessage.data
            val title = data["title"] ?: getString(R.string.app_name)
            val message = data["message"] ?: getString(R.string.app_name)
            val alarmId = if (data["alarmId"] == null) -1 else data["alarmId"]!!.toInt()

            showNotification(this, alarmId, title, message)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
                val isScreenOn = pm.isInteractive
                Log.e("screen on :::: ", "" + isScreenOn)
                if (!isScreenOn) {
                    val wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE, "MyLock")
                    wl.acquire(10000)
                    val wlCpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock")
                    wlCpu.acquire(10000)
                }
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d("token :::: ", token)

        SharedPreferenceManager.putValue(this, KEY.PREF_NAME_FCM_TOKEN, token)
    }

    private fun showNotification(context: Context, alarmId: Int, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel("$alarmId", listNotificationChannels[alarmId], importance).apply {
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(1000, 2000)
                description = message
                lightColor = getColor(R.color.colorAccent)
            }

            // 노티피케이션 채널을 시스템에 등록
            notificationManager.createNotificationChannel(channel)
        }

        val myIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(context, "$alarmId")
            .setSmallIcon(R.drawable.ic_thumb)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setDefaults(Notification.DEFAULT_VIBRATE)

        notificationManager.notify(alarmId, builder.build())
    }
}