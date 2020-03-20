package com.imaec.mypay.base

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    constructor(clickCallback: (Any) -> Unit): this() {
        this.clickCallback = clickCallback
    }

    protected val TAG = this::class.java.name
    protected lateinit var clickCallback: (String) -> Unit

    protected fun <T : ViewDataBinding?> getBinding(view: View): T? {
        return DataBindingUtil.bind<T>(view)
    }

    open fun <T : Any> addItems(list: ArrayList<T>) {

    }
    open fun <T : Any> addItems(list: Array<T>) {

    }

    open fun <T : Any> getItem(position: Int, type: Class<T>): T? {
        return null
    }
}