package com.imaec.mypay.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*


class DateUtil {

    companion object {

        @SuppressLint("SimpleDateFormat")
        fun getDate(format: String = "yyyyMMddHHmm") : String {
            return SimpleDateFormat(format).format(Date())
        }

        @SuppressLint("SimpleDateFormat")
        fun getTomorrow(format: String = "yyyyMMddHHmm") : String {
            val tomorrow = Calendar.getInstance().apply {
                add(Calendar.DATE, 1)
            }.time
            return SimpleDateFormat(format).format(tomorrow)
        }

        @SuppressLint("SimpleDateFormat")
        fun isWorkDay(day: Int = Calendar.getInstance().apply { time = SimpleDateFormat("yyyyMMdd").parse(getDate("yyyyMMdd")) }.get(Calendar.DAY_OF_WEEK)) : Boolean {
            return (day != Calendar.SATURDAY) && (day != Calendar.SUNDAY)
        }
    }
}