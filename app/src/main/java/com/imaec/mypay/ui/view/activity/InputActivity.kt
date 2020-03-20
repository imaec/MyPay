package com.imaec.mypay.ui.view.activity

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.imaec.mypay.*
import com.imaec.mypay.base.BaseActivity
import com.imaec.mypay.databinding.ActivityInputBinding
import com.imaec.mypay.viewmodel.InputViewModel
import com.imaec.mypay.ui.adapter.OptionAdapter
import com.imaec.mypay.ui.callback.KeyboardVisibilityListener
import com.imaec.mypay.ui.view.CommonDialog
import com.imaec.mypay.utils.*
import com.imaec.mypay.utils.SharedPreferenceManager.KEY

class InputActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityInputBinding
    private lateinit var viewModel: InputViewModel
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = setViewModel(InputViewModel::class.java)
        binding = setContent(R.layout.activity_input)
        binding.apply {
            lifecycleOwner = this@InputActivity
            viewModel = this@InputActivity.viewModel

            bottomSheetBehavior = BottomSheetBehavior.from(linearBottomSheet).hide()
        }

        initListener()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.text_pay_day -> showOption(resources.getStringArray(R.array.pay_day), viewModel.payDay)
            R.id.text_work_start -> showOption(resources.getStringArray(R.array.work_time), viewModel.startTime)
            R.id.text_work_end -> showOption(resources.getStringArray(R.array.work_time), viewModel.endTime)
            R.id.view_bg -> bottomSheetBehavior.hide()
            R.id.btn_confirm -> {
                // 급여 입력 확인
                if (binding.editPay.text.isEmpty()) {
                    Toast.makeText(this, getString(R.string.toast_pay), Toast.LENGTH_SHORT).show()
                    return
                }
                val pay = NumberUtil.getNumber(binding.editPay.text)

                // 급여일, 출/퇴근 시간 확인
                val payString = binding.editPay.text.toString()
                val payDay = binding.textPayDay.text.toString()
                val workStart = binding.textWorkStart.text.toString()
                val workEnd = binding.textWorkEnd.text.toString()

                Calculator.apply {
                    this.pay = pay
                    this.payDay = payDay
                    this.startTime = workStart
                    this.endTime = workEnd
                }

                val title = "입력하신 정보가 맞습니까?"
                val content = "급여 : ${payString}원\n" +
                        "급여일 : 매월 ${payDay}\n" +
                        "출근 : ${workStart}\n" +
                        "퇴근 : ${workEnd}\n" +
                        "근무시간 : ${Calculator.getWorkTime()}시간 (식사시간 포함)"
                showDialog(title, content, "확인", {
                    SharedPreferenceManager.putValue(this, KEY.PREF_NAME_PAY, pay)
                    SharedPreferenceManager.putValue(this, KEY.PREF_NAME_PAY_DAY, payDay)
                    SharedPreferenceManager.putValue(this, KEY.PREF_NAME_WORK_START, workStart)
                    SharedPreferenceManager.putValue(this, KEY.PREF_NAME_WORK_END, workEnd)
                    it.dismiss()
                    setResult(RESULT_INPUT_CHANGED)
                    finish()
                })
            }
        }
    }

    /**
     * 뒤로가기 버튼으로 Activity가 꺼지지 않도록 설정
     * BottomSheetBehavior가 열려있으면 닫음
     */
    override fun onBackPressed() {
        when {
            bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN -> bottomSheetBehavior.hide()
            intent.getStringExtra(EXTRA_ACTION) == ACTION_EDIT -> super.onBackPressed()
            else -> {
                showDialog("안내", "앱을 종료하시겠습니까?", "확인", {
                    it.dismiss()
                    setResult(RESULT_FINISH)
                    finish()
                })
            }
        }
    }

    /**
     * EditText, TextView Listener 설정
     * BottomSheetBehavior Callback 설정
     * Keyboard Visibility Listener 설정
     */
    private fun initListener() {
        binding.apply {
            editPay.addTextChangedListener(NumberFormatTextWatcher(editPay))
            textPayDay.setOnClickListener(this@InputActivity)
            textWorkStart.setOnClickListener(this@InputActivity)
            textWorkEnd.setOnClickListener(this@InputActivity)
            viewBg.setOnClickListener(this@InputActivity)
            btnConfirm.setOnClickListener(this@InputActivity)
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                viewModel.visible.value = newState != BottomSheetBehavior.STATE_HIDDEN

                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    KeyboardUtil.hideKeyboardFrom(this@InputActivity)
                }
            }
        })

        KeyboardUtil.setKeyboardVisibilityListener(this, object : KeyboardVisibilityListener {
            override fun onKeyboardVisibilityChanged(keyboardVisible: Boolean) {
                if (keyboardVisible) {
                    bottomSheetBehavior.hide()
                }
            }
        })
    }

    /**
     * 급여일, 출/퇴근 시간 선택 옵션
     */
    private fun showOption(arr: Array<String>, liveVar: MutableLiveData<String>) {
        binding.recyclerOption.adapter = OptionAdapter {
            liveVar.value = it as String
            bottomSheetBehavior.hide()
        }.apply {
            addItems(arr)
        }

        bottomSheetBehavior.expand()
    }

    private fun BottomSheetBehavior<LinearLayout>.expand() : BottomSheetBehavior<LinearLayout> {
        state = BottomSheetBehavior.STATE_EXPANDED
        return this
    }

    private fun BottomSheetBehavior<LinearLayout>.hide() : BottomSheetBehavior<LinearLayout> {
        state = BottomSheetBehavior.STATE_HIDDEN
        return this
    }

    private fun showDialog(title: String, content: String, confirm: String, confirmListener: (Dialog) -> Unit, cancel: String? = null, cancelDialog: ((Dialog) -> Unit)? = null, isOneButton: Boolean = false) {
        CommonDialog(this)
            .setTitle(title)
            .setContent(content)
            .setOnClickConfirm(confirm, confirmListener)
            .setOnClickCancel(cancel ?: "취소", cancelDialog ?: { it.dismiss() })
            .isOneButton(isOneButton)
            .show()
    }
}