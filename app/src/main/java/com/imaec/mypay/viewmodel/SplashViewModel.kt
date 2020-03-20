package com.imaec.mypay.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.imaec.mypay.base.BaseViewModel
import java.util.*

class SplashViewModel(context: Context) : BaseViewModel(context) {

    private var timer: Timer? = null
    private var callback: (() -> Unit)? = null

    val percent = MutableLiveData<String>().set("0%")

    override fun onResume() {
        timer = Timer().apply {
            schedule(createTimerTask(), 0, 10)
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

    private fun createTimerTask() : TimerTask {
        return object : TimerTask() {
            override fun run() {
                callback?.let { it() }
            }
        }
    }
}