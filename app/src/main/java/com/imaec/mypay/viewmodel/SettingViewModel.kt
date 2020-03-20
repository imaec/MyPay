package com.imaec.mypay.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.imaec.mypay.base.BaseViewModel
import com.imaec.mypay.utils.*
import com.imaec.mypay.utils.SharedPreferenceManager.KEY

class SettingViewModel(context: Context) : BaseViewModel(context) {

    val pay = MutableLiveData<String>()
    val payDay = MutableLiveData<String>()
    val start = MutableLiveData<String>()
    val end = MutableLiveData<String>()

    val alertPayDay = MutableLiveData<Boolean>().set(getAlert(KEY.PREF_NAME_ALERT_PAY_DAY))
    val alertStart = MutableLiveData<Boolean>().set(getAlert(KEY.PREF_NAME_ALERT_START))
    val alertEnd = MutableLiveData<Boolean>().set(getAlert(KEY.PREF_NAME_ALERT_END))

    fun setPayInf() {
        pay.value = "${NumberUtil.getKor(SharedPreferenceManager.getInt(context, KEY.PREF_NAME_PAY, 0))}원"
        payDay.value = SharedPreferenceManager.getString(context, KEY.PREF_NAME_PAY_DAY, "")
        start.value = SharedPreferenceManager.getString(context, KEY.PREF_NAME_WORK_START, "")
        end.value = SharedPreferenceManager.getString(context, KEY.PREF_NAME_WORK_END, "")
    }

    fun setAlert(key: KEY, isChecked: Boolean, alarmId: Int) {
        if (SharedPreferenceManager.getBool(context, key, true) && isChecked) {
            AlertManager.cancel(context, alarmId)
            return
        }
        SharedPreferenceManager.putValue(context, key, isChecked)

        if (!isChecked) return

        val nextPayDay = Calculator.getNextPayDay()
        val tomorrow = DateUtil.getTomorrow("yyyy-MM-dd")
        val startTime = Calculator.getStartTimeStr()
        val endTime = Calculator.getEndTimeStr()
        when (key) {
            KEY.PREF_NAME_ALERT_PAY_DAY -> {
                AlertManager.apply {
                    setDate(nextPayDay.slice(IntRange(0, 3)).toInt(), nextPayDay.slice(IntRange(4, 5)).toInt(), nextPayDay.slice(IntRange(6, 7)).toInt())
                    setTime(nextPayDay.slice(IntRange(8, 9)).toInt())
                    regist(context, PAY)
                }
            }
            KEY.PREF_NAME_ALERT_START -> {
                AlertManager.apply {
                    setDate(tomorrow.split("-")[0].toInt(), tomorrow.split("-")[1].toInt(), tomorrow.split("-")[2].toInt())
                    setTime(startTime.slice(IntRange(0, 1)).toInt())
                    regist(context, START)
                }
            }
            KEY.PREF_NAME_ALERT_END -> {
                AlertManager.apply {
                    setDate(tomorrow.split("-")[0].toInt(), tomorrow.split("-")[1].toInt(), tomorrow.split("-")[2].toInt())
                    setTime(endTime.slice(IntRange(0, 1)).toInt())
                    regist(context, END)
                }
            }
            else -> {}
        }
    }

    private fun getAlert(key: KEY) : Boolean {
        return SharedPreferenceManager.getBool(context, key, true)
    }
}