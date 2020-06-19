package com.imaec.mypay.ui.view.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.imaec.mypay.R
import com.imaec.mypay.base.BaseFragment
import com.imaec.mypay.databinding.FragmentSettingBinding
import com.imaec.mypay.ui.adapter.ViewPagerAdapter
import com.imaec.mypay.ui.view.activity.MainActivity
import com.imaec.mypay.utils.Calculator
import com.imaec.mypay.utils.CommonUtil
import com.imaec.mypay.utils.SharedPreferenceManager.KEY
import com.imaec.mypay.viewmodel.SettingViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.io.ByteArrayOutputStream

class SettingFragment : BaseFragment(), CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private lateinit var binding: FragmentSettingBinding
    private lateinit var viewModel: SettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = setViewModel(SettingViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = getView(inflater, R.layout.fragment_setting, container)
        binding.apply {
            viewModel = this@SettingFragment.viewModel
            lifecycleOwner = this@SettingFragment
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            // switchPayDay.setOnCheckedChangeListener(this@SettingFragment)
            // switchStart.setOnCheckedChangeListener(this@SettingFragment)
            // switchEnd.setOnCheckedChangeListener(this@SettingFragment)
            textShareKakao.setOnClickListener(this@SettingFragment)
        }
        viewModel.appVersion.value = CommonUtil.getVersion(context)

        adInit()
    }

    override fun onResume() {
        super.onResume()
        viewModel.setPayInf()
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        when (buttonView.id) {
            // R.id.switch_pay_day -> viewModel.setAlert(KEY.PREF_NAME_ALERT_PAY_DAY, isChecked, 0)
            // R.id.switch_start -> viewModel.setAlert(KEY.PREF_NAME_ALERT_START, isChecked, 1)
            // R.id.switch_end -> viewModel.setAlert(KEY.PREF_NAME_ALERT_END, isChecked, 2)
            R.id.switch_include -> viewModel.setPref(KEY.PREF_NAME_ALERT_INCLUDE, isChecked)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.text_share_kakao -> {
                viewModel.isProgressVisible.value = true
                val viewPager = (activity as MainActivity).view_pager
                val fragment = (viewPager.adapter as ViewPagerAdapter).getFragment(0) as HomeFragment
                fragment.adView.visibility = View.GONE
                if (!binding.switchInclude.isChecked) {
                    fragment.text_pay_title.visibility = View.GONE
                    fragment.text_pay.visibility = View.GONE
                }
                val baos = getBaos(fragment.view!!)
                fragment.text_pay_title.visibility = View.VISIBLE
                fragment.text_pay.visibility = View.VISIBLE

                viewModel.upload(baos) {
                    viewModel.isProgressVisible.value = false
                    viewModel.share(getString(R.string.app_name), "월급을 ${String.format("%.2f", (Calculator.getPayOfCurrent() / Calculator.pay).toFloat() * 100)}% 벌었습니다.", it) {
                        // Success
                    }
                }

            }
        }
    }

    private fun adInit() {
        MobileAds.initialize(context) {}
        binding.adView.loadAd(AdRequest.Builder().build())
    }

    private fun getBaos(view: View) : ByteArrayOutputStream {
        view.apply {
            isDrawingCacheEnabled = true
            buildDrawingCache()
        }
        val bitmap = view.drawingCache
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

        return baos
    }
}