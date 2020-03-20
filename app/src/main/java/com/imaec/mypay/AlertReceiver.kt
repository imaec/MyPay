package com.imaec.mypay

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.imaec.mypay.ui.view.activity.SplashActivity
import com.imaec.mypay.utils.AlertManager
import com.imaec.mypay.utils.Calculator
import com.imaec.mypay.utils.DateUtil
import com.imaec.mypay.utils.SharedPreferenceManager
import com.imaec.mypay.utils.SharedPreferenceManager.KEY

//private const val FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send"
//private const val SERVER_KEY = "AAAA5pYJlIE:APA91bEmKW11E_MztU806KzAdEqLrPZhlRGwVYcRctFFmRPHBOApqfMXX8eOM4vAIaSSIx8MUfcjkButzJZwSvfbwtfdYA_26PoIcBLurTF0lePY-48XIryBIDWnXwy35YQ-VCyl2DYX"

class AlertReceiver : BroadcastReceiver() {

    private val listNotificationChannels = arrayListOf("PAY", "START", "END")

    @SuppressLint("SimpleDateFormat")
    override fun onReceive(context: Context, intent: Intent) {
        Calculator.apply {
            this.pay = SharedPreferenceManager.getInt(context, KEY.PREF_NAME_PAY, 0)
            this.payDay = SharedPreferenceManager.getString(context, KEY.PREF_NAME_PAY_DAY, "")
            this.startTime = SharedPreferenceManager.getString(context, KEY.PREF_NAME_WORK_START, "")
            this.endTime = SharedPreferenceManager.getString(context, KEY.PREF_NAME_WORK_END, "")
        }

        val title = intent.getStringExtra("TITLE") ?: "TITLE 없음"
        val message = intent.getStringExtra("MESSAGE") ?: "MESSAGE 없음"
        val alarmId = intent.getIntExtra("ID", -1)

        when (alarmId) {
            AlertManager.PAY -> {
                val nextPayDay = Calculator.getNextPayDay()
                AlertManager.apply {
                    setDate(nextPayDay.slice(IntRange(0, 3)).toInt(), nextPayDay.slice(IntRange(4, 5)).toInt(), nextPayDay.slice(IntRange(6, 7)).toInt())
                    setTime(nextPayDay.slice(IntRange(8, 9)).toInt())
                }
            }
            AlertManager.START -> {
                val tomorrow = DateUtil.getTomorrow("yyyy-MM-dd")
                val startTime = Calculator.getStartTimeStr()
                AlertManager.apply {
                    setDate(tomorrow.split("-")[0].toInt(), tomorrow.split("-")[1].toInt(), tomorrow.split("-")[2].toInt())
                    setTime(startTime.slice(IntRange(0, 1)).toInt())
                }
            }
            AlertManager.END -> {
                val tomorrow = DateUtil.getTomorrow("yyyy-MM-dd")
                val endTime = Calculator.getEndTimeStr()
                AlertManager.apply {
                    setDate(tomorrow.split("-")[0].toInt(), tomorrow.split("-")[1].toInt(), tomorrow.split("-")[2].toInt())
                    setTime(endTime.slice(IntRange(0, 1)).toInt())
                }
            }
        }
        AlertManager.regist(context, alarmId)

        // 평일에만 알림
        if ((alarmId == AlertManager.START || alarmId == AlertManager.END) && !DateUtil.isWorkDay()) return

        wakeUp(context)
        showNotification(context, alarmId, title, message)
    }

    @SuppressLint("InvalidWakeLockTag")
    private fun wakeUp(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val isScreenOn = pm.isInteractive
            Log.e("screen on :::: ", "" + isScreenOn)
            if (!isScreenOn) {
                pm.newWakeLock(
                    PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
                    context.getString(R.string.app_name)
                ).acquire(10000)
                pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock").acquire(10000)
            }
        }
    }

    private fun showNotification(context: Context, alarmId: Int, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val myIntent = Intent(context, SplashActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(context, "$alarmId")
            .setSmallIcon(R.drawable.ic_thumb)
            .setContentTitle(title)
            .setContentText(message)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(longArrayOf(1000, 1000))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setContentIntent(pendingIntent)
            .setFullScreenIntent(pendingIntent, true)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) builder.setStyle(
            NotificationCompat.BigTextStyle()
                .bigText(message)
                .setBigContentTitle(if (title != "") title else context.getString(R.string.app_name))
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel("$alarmId", listNotificationChannels[alarmId], importance).apply {
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(1000, 1000)
                description = message
                lightColor = context.getColor(R.color.colorAccent)
            }

            // 노티피케이션 채널을 시스템에 등록
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(alarmId, builder.build())

    }
}