package com.imaec.mypay.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    private lateinit var listFragment: ArrayList<Fragment>

    override fun getItemCount(): Int = listFragment.size

    override fun createFragment(position: Int): Fragment = listFragment[position]

    fun setFragments(listFragment: ArrayList<Fragment>) {
        this.listFragment = listFragment
    }
}