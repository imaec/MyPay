package com.imaec.mypay.base

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.imaec.mypay.viewmodel.*

class BaseViewModelFactory(var context: Context) : ViewModelProvider.Factory {

    private val TAG = this::class.java.simpleName

    /**
     * ViewModel을 생성
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel(context) as T
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(context) as T
            modelClass.isAssignableFrom(InputViewModel::class.java) -> InputViewModel(context) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(context) as T
            modelClass.isAssignableFrom(SettingViewModel::class.java) -> SettingViewModel(context) as T
            else -> throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}