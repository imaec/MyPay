package com.imaec.mypay.utils

import android.annotation.SuppressLint
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class Calculator {

    companion object {

        private val TAG = Calculator::class.java.simpleName

        var pay = 0
        var payDay = ""
        var startTime = ""
        var endTime = ""

        fun getNextPayDay() : String {
            var year = DateUtil.getDate("yyyy")
            var month = DateUtil.getDate("MM")
            val day = if (getStartDate() < 10) "0${getStartDate()}" else "${getStartDate()}"
            val hour = if (getStartTime() - 1 < 10) "0${getStartTime() - 1}" else "${getStartTime() - 1}"

            if (getStartDate() <= DateUtil.getDate("dd").toInt()) {
                // 다음달
                if (month.toInt() == 12) { // 내년
                    year = "${year.toInt() + 1}"
                    month = "01"
                } else {
                    month = if (month.toInt() + 1 < 10) "0${month.toInt() + 1}" else "${month.toInt() + 1}"
                }
            }
            return year + month + day + hour
        }

        fun getStartTime() : Int {
            var s = startTime.split(" ")[1].replace("시", "").toInt()
            s = if (startTime.split(" ")[0] == "오전") s else s + 12
            return s
        }

        fun getStartTimeStr() : String {
            val s = getStartTime()
            return if (s < 10) "0${s}0000" else "${s}0000"
        }

        fun getEndTime() : Int {
            var e = endTime.split(" ")[1].replace("시", "").toInt()
            e = if (endTime.split(" ")[0] == "오전") e else e + 12

            if (getStartTime() > e) {
                return 24 + e
            }
            return e
        }

        fun getEndTimeStr() : String {
            val e = getEndTime()
            return if (e < 10) "0${e}0000" else "${e}0000"
        }

        fun getWorkTime() : Int {
            val s = getStartTime()
            val e = getEndTime()

            return e - s
        }

        fun getStartMonth() : Int {
            return getStartDateStr().slice(IntRange(4, 5)).toInt()
        }

        fun getStartDateStr() : String {
            val year = DateUtil.getDate("yyyy").toInt()

            val startMonth = if (getStartDate() <= DateUtil.getDate("dd").toInt()) { DateUtil.getDate("MM").toInt() } else { DateUtil.getDate("MM").toInt() - 1 }
            val month = if (startMonth == 0) "12" else if (startMonth < 10) "0$startMonth" else "$startMonth"

            val startDate = getStartDate()
            val day = if (startMonth == 2 && (startDate == 30 || startDate == 31)) {
                if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) { "29" } else { "28" }
            } else if (startDate < 10) "0$startDate" else "$startDate"
            return "${DateUtil.getDate("yyyy").toInt()}$month${day}"
        }

        @SuppressLint("SimpleDateFormat")
        fun getStartDate() : Int {
            val strPayDay = payDay.replace("일", "")
            return if (strPayDay == "마지막날") {
                val cal = Calendar.getInstance().apply { time = SimpleDateFormat("yyyyMMdd").parse(DateUtil.getDate("yyyyMMdd"))!! }
                cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            } else strPayDay.toInt()
        }

        fun getEndMonth() : Int {
            return getEndDateStr().slice(IntRange(4, 5)).toInt()
        }

        fun getEndDateStr() : String {
            val year = DateUtil.getDate("yyyy").toInt()

            val endMonth = if (getStartDate() == 1) { getStartMonth() } else { getStartMonth() + 1 }
            val month = if (endMonth == 0) "12" else if (endMonth < 10) "0$endMonth" else "$endMonth"

            val endDate = getEndDate()
            val day =
                if (endMonth == 2 && (getStartDate() == 30 || getStartDate() == 31)) {
                    if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) { "29" } else { "28" }
                } else if (endDate < 10) "0$endDate" else "$endDate"
            return "${DateUtil.getDate("yyyy").toInt()}$month${day}"
        }

        @SuppressLint("SimpleDateFormat")
        fun getEndDate() : Int {
            val startDate = getStartDate()
            val year = DateUtil.getDate("yyyy").toInt()
            val month = DateUtil.getDate("MM").toInt()

            return if (startDate == 1) {
                val cal = Calendar.getInstance().apply { time = SimpleDateFormat("yyyyMMdd").parse(DateUtil.getDate("yyyyMMdd"))!! }
                cal.getActualMaximum(Calendar.DAY_OF_MONTH)

                return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            } else {
                startDate - 1
            }
        }

        @SuppressLint("SimpleDateFormat")
        fun getWorkDay() : Int {
            val sc = Calendar.getInstance().apply { time = SimpleDateFormat("yyyyMMdd").parse(getStartDateStr())!! }
            val ec = Calendar.getInstance().apply { time = SimpleDateFormat("yyyyMMdd").parse(getEndDateStr())!! }

            var workingDays = 0
            while (!sc.after(ec)) {
                val day = sc.get (Calendar.DAY_OF_WEEK)
                if (DateUtil.isWorkDay(day))
                    workingDays++
                sc.add(Calendar.DATE, 1)
            }

            return workingDays
        }

        @SuppressLint("SimpleDateFormat")
        fun getWorkedDay(): Int {
            val sc = Calendar.getInstance().apply { time = SimpleDateFormat("yyyyMMdd").parse(getStartDateStr())!! }
            val ec = Calendar.getInstance().apply { time = SimpleDateFormat("yyyyMMdd").parse(DateUtil.getDate("yyyyMMdd"))!! }

            var workingDays = 0
            while (!sc.after(ec)) {
                val day = sc.get(Calendar.DAY_OF_WEEK)
                if (DateUtil.isWorkDay(day))
                    workingDays++
                sc.add(Calendar.DATE, 1)
            }

            // 오늘 날짜 제외
            if (DateUtil.isWorkDay()) workingDays -= 1

            // 현재시간이 퇴근시간 후면
            if (DateUtil.getDate("HH").toInt() >= getEndTime() && DateUtil.isWorkDay()) workingDays += 1

            return workingDays
        }

        @SuppressLint("SimpleDateFormat")
        fun getWorkedTimeOfDay() : Long {
            val sdf = SimpleDateFormat("HHmmss").apply { timeZone = TimeZone.getTimeZone("GMT") }

            var h = DateUtil.getDate("HH").toInt()  // 현재 시간
            val s = getStartTime()                          // 출근 시간
            val e = getEndTime()                            // 퇴근 시간
            if (e >= 24) h += 24

            // 현재 요일이 토요일 또는 일요일이면 0
            val day = Calendar.getInstance().apply { time = SimpleDateFormat("yyyyMMdd").parse(DateUtil.getDate("yyyyMMdd")) }.get(Calendar.DAY_OF_WEEK)

            return when {
                !DateUtil.isWorkDay() -> return 0
                h in s until e -> {
                    val startTime = sdf.parse(getStartTimeStr())
                    val curTime = sdf.parse(DateUtil.getDate("HHmmss"))
                    val diff = curTime.time - startTime.time
                    diff
                }
                h >= e -> {
                    val startTime = sdf.parse(getStartTimeStr())
                    val curTime = sdf.parse(getEndTimeStr())
                    val diff = curTime.time - startTime.time
                    diff
                }
                else -> 0
            }
        }

        @SuppressLint("SimpleDateFormat")
        fun getWorkedHourOfDay() : Int {
            val workedHour = (getWorkedTimeOfDay() / (1000 * 60 * 60)).toInt()
            return if (workedHour < 0) workedHour + 23 else workedHour
        }

        @SuppressLint("SimpleDateFormat")
        fun getWorkedMinuteOfDay() : Int {
            val workedMinute = ((getWorkedTimeOfDay() % (1000 * 60 * 60)) / (1000 * 60)).toInt()
            return if (workedMinute < 0) workedMinute + 59 else workedMinute
        }

        @SuppressLint("SimpleDateFormat")
        fun getWorkedSecondOfDay() : Int {
            val workedSecond = ((((getWorkedTimeOfDay() % (1000 * 60 * 60)) % (1000 * 60))) / 1000).toInt()
            return if (workedSecond < 0) workedSecond + 60 else workedSecond
        }

        fun getPayOfDay(format: String = "%.1f") : Double {
            return String.format(format, pay.toDouble() / getWorkDay()).toDouble()
        }

        fun getPayOfHour() : Int {
            return (getPayOfDay() / getWorkTime()).toInt()
        }

        fun getPayOfMinute() : Int {
            return getPayOfHour() / 60
        }

        fun getPayOfSecond() : Int {
            return getPayOfMinute() / 60
        }

        fun getPayOfCurrent() : Double {
            // 현재시간이 퇴근시간 후면
            if (DateUtil.getDate("HH").toInt() >= getEndTime()) {
                return getWorkedDay() * getPayOfDay("%.2f")
            }
            return getWorkedDay() * getPayOfDay("%.2f") +
                    getWorkedHourOfDay() * getPayOfHour() +
                    getWorkedMinuteOfDay() * getPayOfMinute() +
                    getWorkedSecondOfDay() * getPayOfSecond()
        }
    }
}