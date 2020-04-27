package com.imaec.mypay.ui.view.fragment

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
import com.imaec.mypay.utils.SharedPreferenceManager
import com.imaec.mypay.utils.SharedPreferenceManager.KEY
import com.imaec.mypay.viewmodel.SettingViewModel

class SettingFragment : BaseFragment(), CompoundButton.OnCheckedChangeListener {

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
            switchPayDay.setOnCheckedChangeListener(this@SettingFragment)
            switchStart.setOnCheckedChangeListener(this@SettingFragment)
            switchEnd.setOnCheckedChangeListener(this@SettingFragment)
        }

        adInit()
    }

    override fun onResume() {
        super.onResume()
        viewModel.setPayInf()
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        when (buttonView.id) {
            R.id.switch_pay_day -> viewModel.setAlert(KEY.PREF_NAME_ALERT_PAY_DAY, isChecked, 0)
            R.id.switch_start -> viewModel.setAlert(KEY.PREF_NAME_ALERT_START, isChecked, 1)
            R.id.switch_end -> viewModel.setAlert(KEY.PREF_NAME_ALERT_END, isChecked, 2)
        }
    }

    private fun adInit() {
        MobileAds.initialize(context) {}
        binding.adView.loadAd(AdRequest.Builder().build())
    }
}