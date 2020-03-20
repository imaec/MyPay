package com.imaec.mypay.base

import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseActivity : AppCompatActivity() {

    protected val TAG = this::class.java.simpleName

    protected fun <T : ViewDataBinding?> setContent(@LayoutRes layoutRes: Int): T {
        return DataBindingUtil.setContentView<T>(this, layoutRes)
    }

    protected fun <T : ViewModel?> setViewModel(viewModel: Class<T>): T {
        return ViewModelProvider(this, BaseViewModelFactory(this)).get(viewModel)
    }
}