package com.imaec.mypay.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.imaec.mypay.R
import com.imaec.mypay.base.BaseAdapter
import com.imaec.mypay.databinding.ItemOptionBinding

class OptionAdapter(clickCallback: (Any) -> Unit) : BaseAdapter(clickCallback) {

    private val listItem = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_option, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int = listItem.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.onBind(listItem[position])
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = getBinding<ItemOptionBinding>(itemView)

        fun onBind(item: String) {
            binding?.apply {
                this.item = item
                textOption?.setOnClickListener {
                    clickCallback(item)
                }
            }
        }
    }

    override fun <T : Any> addItems(list: Array<T>) {
        super.addItems(list)
        list.forEach {
            listItem.add(it as String)
        }
    }
}