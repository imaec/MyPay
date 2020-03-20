package com.imaec.mypay.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseFragment : Fragment() {

    protected val TAG = this::class.java.name

    protected fun <T : ViewDataBinding?> getView(inflater: LayoutInflater, resId: Int, container: ViewGroup?): T {
        return DataBindingUtil.inflate<T>(inflater, resId, container, false)
    }

    protected fun <T : ViewModel?> setViewModel(viewModel: Class<T>): T {
        return ViewModelProvider(this, BaseViewModelFactory(context!!)).get(viewModel)
    }
}