package com.imaec.mypay.ui.view.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.imaec.mypay.R
import com.imaec.mypay.base.BaseFragment
import com.imaec.mypay.databinding.FragmentSettingBinding
import com.imaec.mypay.utils.Calculator
import com.imaec.mypay.utils.CommonUtil
import com.imaec.mypay.utils.PermissionUtil
import com.imaec.mypay.utils.SharedPreferenceManager
import com.imaec.mypay.utils.SharedPreferenceManager.KEY
import com.imaec.mypay.viewmodel.SettingViewModel

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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        var granted = true
        if (grantResults.isNotEmpty()) {
            for (grantResult in grantResults) {
                granted = grantResult != PackageManager.PERMISSION_GRANTED
            }
        }

        if (granted) {
            viewModel.captureGraph()
        } else {
            Toast.makeText(context, "권한 설정에 동의하시면 서비스를 이용 하실 수 있습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        when (buttonView.id) {
            // R.id.switch_pay_day -> viewModel.setAlert(KEY.PREF_NAME_ALERT_PAY_DAY, isChecked, 0)
            // R.id.switch_start -> viewModel.setAlert(KEY.PREF_NAME_ALERT_START, isChecked, 1)
            // R.id.switch_end -> viewModel.setAlert(KEY.PREF_NAME_ALERT_END, isChecked, 2)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.text_share_kakao -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (PermissionUtil.checkPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        viewModel.captureGraph()
                        viewModel.share(getString(R.string.app_name), "월급을 ${(Calculator.getPayOfCurrent() / Calculator.pay).toFloat() * 100}% 벌었습니다.") {

                        }
                    } else {
                        PermissionUtil.requestPermission(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    }
                }
            }
        }
    }

    private fun adInit() {
        MobileAds.initialize(context) {}
        binding.adView.loadAd(AdRequest.Builder().build())
    }
}