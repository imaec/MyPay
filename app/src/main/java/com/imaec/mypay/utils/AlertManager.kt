package com.imaec.mypay.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.imaec.mypay.AlertReceiver
import java.text.SimpleDateFormat
import java.util.*

class AlertManager {

    companion object {

        const val PAY = 0
        const val START = 1
        const val END = 2

        private var calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, DateUtil.getDate("HH").toInt())
            set(Calendar.MINUTE, DateUtil.getDate("mm").toInt())
            set(Calendar.SECOND, DateUtil.getDate("ss").toInt())
        }

        fun setDate(year: Int, month: Int, day: Int) : Companion {
            calendar.apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month - 1)
                set(Calendar.DATE, day)
            }
            return this
        }

        fun setTime(hour: Int, minute: Int = 0, second: Int = 0) : Companion {
            calendar.apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, second)
            }
            return this
        }

        @SuppressLint("SimpleDateFormat")
        fun regist(context: Context, alarmId: Int) {
            Log.d("time :::: ", SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.time))
            cancel(context, alarmId)

            val intent = Intent(context, AlertReceiver::class.java).apply {
                action = "ALARM"
                addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
                putExtra("ID", alarmId)
                putExtra("MESSAGE",
                    when (alarmId) {
                        PAY -> "오늘은 월급날 입니다!!"
                        START -> "출근시간 입니다!"
                        END -> "퇴근시간 입니다!"
                        else -> ""
                    }
                )
                putExtra("TITLE",
                    when (alarmId) {
                        PAY -> "축하드려요~\uD83C\uDF89\uD83D\uDCB0"
                        START -> "오늘도 힘내세요!\uD83D\uDE03"
                        END -> "오늘도 수고하셨어요~\uD83D\uDE04"
                        else -> ""
                    }
                )
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val am = (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)

            when {
                // Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> am.setAlarmClock(AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent), pendingIntent)
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> am.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                else -> am.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }
        }

        fun cancel(context: Context, alarmId: Int) {
            val intent = Intent(context, AlertReceiver::class.java).apply { action = "ALARM" }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(pendingIntent)
        }
    }

}