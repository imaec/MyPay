package com.imaec.mypay.viewmodel

import android.content.Context
import com.imaec.mypay.base.BaseViewModel
import com.imaec.mypay.utils.AlertManager
import com.imaec.mypay.utils.Calculator
import com.imaec.mypay.utils.DateUtil
import com.imaec.mypay.utils.SharedPreferenceManager
import com.imaec.mypay.utils.SharedPreferenceManager.KEY

class MainViewModel(context: Context) : BaseViewModel(context) {

    fun checkInputData() : Boolean {
        if (SharedPreferenceManager.getInt(context, KEY.PREF_NAME_PAY) == 0 ||
            SharedPreferenceManager.getString(context, KEY.PREF_NAME_PAY_DAY).isEmpty() ||
            SharedPreferenceManager.getString(context, KEY.PREF_NAME_WORK_START).isEmpty() ||
            SharedPreferenceManager.getString(context, KEY.PREF_NAME_WORK_END).isEmpty()) {
            return false
        }
        return true
    }

    fun setAlert() {
        val nextPayDay = Calculator.getNextPayDay()
        val tomorrow = DateUtil.getTomorrow("yyyy-MM-dd")
        val startTime = Calculator.getStartTimeStr()
        val endTime = Calculator.getEndTimeStr()

        if (SharedPreferenceManager.getBool(context, KEY.PREF_NAME_ALERT_PAY_DAY, true)) {
            AlertManager.apply {
                setDate(nextPayDay.slice(IntRange(0, 3)).toInt(), nextPayDay.slice(IntRange(4, 5)).toInt(), nextPayDay.slice(IntRange(6, 7)).toInt())
                setTime(nextPayDay.slice(IntRange(8, 9)).toInt())
                regist(context, PAY)
            }
        }
        if (SharedPreferenceManager.getBool(context, KEY.PREF_NAME_ALERT_START, true)) {
            AlertManager.apply {
                setDate(tomorrow.split("-")[0].toInt(), tomorrow.split("-")[1].toInt(), tomorrow.split("-")[2].toInt())
                setTime(startTime.slice(IntRange(0, 1)).toInt())
                regist(context, START)
            }
        }
        if (SharedPreferenceManager.getBool(context, KEY.PREF_NAME_ALERT_END, true)) {
            AlertManager.apply {
                setDate(tomorrow.split("-")[0].toInt(), tomorrow.split("-")[1].toInt(), tomorrow.split("-")[2].toInt())
                setTime(endTime.slice(IntRange(0, 1)).toInt())
                regist(context, END)
            }
        }
//        SharedPreferenceManager.putValue(context, KEY.PREF_NAME_ALERT_REGISTERED, true)
    }
}