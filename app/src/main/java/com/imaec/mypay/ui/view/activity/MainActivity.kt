package com.imaec.mypay.ui.view.activity

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomappbar.BottomAppBar
import com.imaec.mypay.*
import com.imaec.mypay.base.BaseActivity
import com.imaec.mypay.databinding.ActivityMainBinding
import com.imaec.mypay.ui.adapter.ViewPagerAdapter
import com.imaec.mypay.ui.view.CommonDialog
import com.imaec.mypay.ui.view.fragment.HomeFragment
import com.imaec.mypay.ui.view.fragment.SettingFragment
import com.imaec.mypay.utils.AlertManager
import com.imaec.mypay.utils.DateUtil
import com.imaec.mypay.utils.DisplayUtil
import com.imaec.mypay.viewmodel.MainViewModel

private const val CENTER = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
private const val END = BottomAppBar.FAB_ALIGNMENT_MODE_END
private const val PAGER_DURATION = 300.toLong()

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    private var currentItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = setViewModel(MainViewModel::class.java)
        binding = setContent(R.layout.activity_main)
        binding.apply {
            lifecycleOwner = this@MainActivity
            viewModel = this@MainActivity.viewModel
        }

        viewModel.apply {
            if (!checkInputData()) {
                startActivityForResult(Intent(this@MainActivity, InputActivity::class.java), 0)
            } else {
                initLayout()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        binding.viewPager.setCurrentItem(currentItem, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_INPUT_CHANGED) {
            viewModel.setAlert()
            initLayout()
        }
        else if (resultCode == RESULT_FINISH) finish()
    }

    override fun onBackPressed() {
        if (binding.bottomAppBar.fabAlignmentMode == CENTER) {
            binding.bottomAppBar.fabAlignmentMode = END
            binding.viewPager.setCurrentItem(0, PAGER_DURATION)
            binding.fab.setImageResource(R.drawable.ic_setting)
        } else {
            super.onBackPressed()
        }
    }

    private fun initLayout() {
        binding.apply {
            viewPager.apply {
                isUserInputEnabled = false
                adapter = ViewPagerAdapter(this@MainActivity).apply {
                    setFragments(arrayListOf(HomeFragment(), SettingFragment()))
                }
            }

            bottomAppBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_edit -> {
                        startActivityForResult(Intent(this@MainActivity, InputActivity::class.java).apply {
                            putExtra(EXTRA_ACTION, ACTION_EDIT)
                        }, 0)
                    }
                }
                true
            }

            fab.setOnClickListener {
                if (bottomAppBar.fabAlignmentMode == END) {
                    currentItem = 1
                    bottomAppBar.fabAlignmentMode = CENTER
                    viewPager.setCurrentItem(1, PAGER_DURATION)
                    fab.setImageResource(R.drawable.ic_home)
                } else {
                    currentItem = 0
                    bottomAppBar.fabAlignmentMode = END
                    viewPager.setCurrentItem(0, PAGER_DURATION)
                    fab.setImageResource(R.drawable.ic_setting)
                }
            }
        }
    }

    private fun ViewPager2.setCurrentItem(item: Int, duration: Long, pagePxWidth: Int = width, interpolator: TimeInterpolator = AccelerateDecelerateInterpolator()) {
        val pxToDrag: Int = pagePxWidth * (item - currentItem)
        var previousValue = 0
        ValueAnimator.ofInt(0, pxToDrag).apply {
            addUpdateListener { valueAnimator ->
                val currentValue = valueAnimator.animatedValue as Int
                val currentPxToDrag = (currentValue - previousValue).toFloat()
                fakeDragBy(-currentPxToDrag)
                previousValue = currentValue
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) { beginFakeDrag() }
                override fun onAnimationEnd(animation: Animator?) { endFakeDrag() }
                override fun onAnimationCancel(animation: Animator?) { /* Ignored */ }
                override fun onAnimationRepeat(animation: Animator?) { /* Ignored */ }
            })
            this.duration = duration
            start()
        }
    }
}
