package com.imaec.mypay.ui.view.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.imaec.mypay.R
import com.imaec.mypay.base.BaseFragment
import com.imaec.mypay.databinding.FragmentHomeBinding
import com.imaec.mypay.ui.view.CommonDialog
import com.imaec.mypay.utils.Calculator
import com.imaec.mypay.utils.DateUtil
import com.imaec.mypay.utils.DisplayUtil
import com.imaec.mypay.viewmodel.HomeViewModel

class HomeFragment : BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel

    private val callback: () -> Unit = {
        (context as Activity).runOnUiThread {
            viewModel.apply {
                setMyPay()

                binding.apply {

                    // 월급전날 근무 끝
                    if (DateUtil.getDate("yyyyMMdd") == Calculator.getEndDateStr() &&
                        DateUtil.getDate("HH").toInt() >= Calculator.getEndTime()) {
                        seekBar.setProgress(1f)
                    } else {
                        seekBar.setProgress((getPayOfCurrent() / Calculator.pay).toFloat())
                    }
                    percent.value = "${String.format("%.2f", seekBar.getProgress() * 100)}%"
                }
            }

            if (!DateUtil.isWorkDay() || DateUtil.getDate("HHmmss").toInt() > Calculator.getEndTimeStr().toInt()) onPause()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = setViewModel(HomeViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = getView(inflater, R.layout.fragment_home, container)
        binding.apply {
            viewModel = this@HomeFragment.viewModel
            lifecycleOwner = this@HomeFragment
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            seekBar.apply {
                layoutParams = DisplayUtil.getSeekBarSize(context, DisplayUtil.getDisplaySizeX(context as Activity), DisplayUtil.getDisplaySizeY(context as Activity))

                viewModel?.apply {
                    setProgress((getPayOfCurrent() / Calculator.pay).toFloat())
                    percent.value = "${String.format("%.2f", getProgress() * 100)}%"
                }
            }

            imageInfo.setOnClickListener(this@HomeFragment)
        }

        viewModel.setMyPay()

        adInit()
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

    override fun onClick(v: View) {
        when (v.id) {
            R.id.image_info -> {
                context?.let {
                    CommonDialog(it)
                        .setTitle(getString(R.string.license_title))
                        .setContent(getString(R.string.license_content))
                        .isOneButton(true)
                        .setOnClickConfirm("확인") { dialog ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }
        }
    }

    private fun adInit() {
        MobileAds.initialize(context) {}
        binding.adView.loadAd(AdRequest.Builder().build())
    }
}