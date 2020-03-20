package com.imaec.mypay.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.imaec.mypay.base.BaseViewModel
import com.imaec.mypay.utils.Calculator
import com.imaec.mypay.utils.DateUtil
import com.imaec.mypay.utils.NumberUtil
import com.imaec.mypay.utils.SharedPreferenceManager
import com.imaec.mypay.utils.SharedPreferenceManager.KEY
import java.util.*

class HomeViewModel(context: Context) : BaseViewModel(context) {

    private var isInit = false

    private var timer: Timer? = null
    private var callback: (() -> Unit)? = null

    val dDay = MutableLiveData<String>()
    val myPay = MutableLiveData<String>()
    val percent = MutableLiveData<String>().set("0%")

    init {
        isInit = true

        Calculator.apply {
            this.pay = SharedPreferenceManager.getInt(context, KEY.PREF_NAME_PAY, 0)
            this.payDay = SharedPreferenceManager.getString(context, KEY.PREF_NAME_PAY_DAY, "")
            this.startTime = SharedPreferenceManager.getString(context, KEY.PREF_NAME_WORK_START, "")
            this.endTime = SharedPreferenceManager.getString(context, KEY.PREF_NAME_WORK_END, "")
        }

        dDay.value = getDDay()
        myPay.value = getMyPay()
    }

    override fun onResume() {
        val gab = 1000 - DateUtil.getDate("SSS").toInt()
        timer = Timer().apply {
            schedule(createTimerTask(), gab.toLong(), 999)
        }
    }

    override fun onPause() {
        timer?.cancel()
        timer = null
    }

    fun setOnTimerTaskCallback(callback: () -> Unit) {
        this.callback = callback
    }

    fun removeOnTimerTaskCallback() {
        this.callback = null
    }

    fun getPayOfCurrent() : Double {
        return Calculator.getPayOfCurrent()
    }

    fun setMyPay() {
        myPay.value = getMyPay()
    }

    private fun getDDay() : String {
        return "남은 근무 : ${Calculator.getWorkDay() - Calculator.getWorkedDay()}일"
    }

    private fun getMyPay() : String {
        return if (getPayOfCurrent().toInt() >= Calculator.pay) "${Calculator.pay.getKor()}원 / ${Calculator.pay.getKor()}원" else "${getPayOfCurrent().toInt().getKor()}원 / ${Calculator.pay.getKor()}원"
    }

    private fun createTimerTask() : TimerTask {
        return object : TimerTask() {
            override fun run() {
                callback?.let { it() }
            }
        }
    }

    private fun Int.getKor() : String {
        return NumberUtil.getKor(this)
    }
}