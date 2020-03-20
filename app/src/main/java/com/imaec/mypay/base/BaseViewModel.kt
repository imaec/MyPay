package com.imaec.mypay.base

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel(var context: Context) : ViewModel() {

    protected val TAG = this::class.java.name

    /**
     * Activity LifeCycle에서 호출 될 메소드
     */
    open fun onCreate() {}
    open fun onResume() {}
    open fun onPause() {}
    open fun onDestroy() {}
    open fun onBackPressed() {}
    open fun onLowMemory() {}
    open fun onTrimMemory() {}

    /**
     * ViewModel이 LifeCycle에서 종료되었을 때 호출되어 불필요한 메모리 초기화
     */
    override fun onCleared() {
        super.onCleared()
    }

    fun MutableLiveData<String>.set(str: String) : MutableLiveData<String> {
        value = str
        return this
    }

    fun MutableLiveData<Boolean>.set(bool: Boolean) : MutableLiveData<Boolean> {
        value = bool
        return this
    }
}