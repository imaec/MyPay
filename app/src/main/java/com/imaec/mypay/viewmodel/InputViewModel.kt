package com.imaec.mypay.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.imaec.mypay.base.BaseViewModel
import com.imaec.mypay.utils.NumberUtil
import com.imaec.mypay.utils.SharedPreferenceManager
import com.imaec.mypay.utils.SharedPreferenceManager.KEY

class InputViewModel(context: Context) : BaseViewModel(context) {

    val pay = MutableLiveData<String>().set(getPay())
    val payDay = MutableLiveData<String>().set(getPayDay())
    val startTime = MutableLiveData<String>().set(getStartTime())
    val endTime = MutableLiveData<String>().set(getEndTime())
    val visible = MutableLiveData<Boolean>().set(false)

    private fun getPay() : String {
        val pay = SharedPreferenceManager.getInt(context, KEY.PREF_NAME_PAY)
        return if (pay == 0) "" else NumberUtil.getKor(pay)
    }

    private fun getPayDay() : String {
        val payDay = SharedPreferenceManager.getString(context, KEY.PREF_NAME_PAY_DAY)
        return if (payDay == "") "5일" else payDay
    }

    private fun getStartTime() : String {
        val startTime = SharedPreferenceManager.getString(context, KEY.PREF_NAME_WORK_START)
        return if (startTime == "") "오전 9시" else startTime
    }

    private fun getEndTime() : String {
        val endTime = SharedPreferenceManager.getString(context, KEY.PREF_NAME_WORK_END)
        return if (endTime == "") "오후 6시" else endTime
    }
}