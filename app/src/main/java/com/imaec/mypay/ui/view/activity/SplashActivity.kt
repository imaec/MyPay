package com.imaec.mypay.ui.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.imaec.mypay.R
import com.imaec.mypay.base.BaseActivity
import com.imaec.mypay.databinding.ActivitySplashBinding
import com.imaec.mypay.utils.Calculator
import com.imaec.mypay.utils.DateUtil
import com.imaec.mypay.utils.DisplayUtil
import com.imaec.mypay.viewmodel.SplashViewModel

class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var viewModel: SplashViewModel

    private val callback: () -> Unit = {
        runOnUiThread {
            binding.apply {
                seekBar.setProgress(seekBar.getProgress() + 0.01f)

                viewModel?.apply {
                    percent.value = "${(seekBar.getProgress() * 100).toInt()}%"
                }

                if (seekBar.getProgress() >= 1.0f) {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = setViewModel(SplashViewModel::class.java)
        binding = setContent(R.layout.activity_splash)
        binding.apply {
            lifecycleOwner = this@SplashActivity
            viewModel = this@SplashActivity.viewModel

            seekBar.apply {
                layoutParams = DisplayUtil.getSeekBarSize(context, DisplayUtil.getDisplaySizeX(context as Activity), DisplayUtil.getDisplaySizeY(context as Activity))

                setProgress(0f)
            }
        }

        viewModel.percent.observe(this, Observer {
            Log.d(TAG, it)
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.setOnTimerTaskCallback(callback)
        viewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.removeOnTimerTaskCallback()
        viewModel.onPause()
    }
}