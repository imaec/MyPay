package com.imaec.mypay.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class NumberFormatTextWatcher(private val editText: EditText) : TextWatcher {

    // Number Type의 Text에 콤마를 추가
    override fun afterTextChanged(s: Editable?) {
        editText.removeTextChangedListener(this)

        val pay = NumberUtil.getKor(s)
        editText.setText(pay)
        editText.setSelection(pay.length)

        editText.addTextChangedListener(this)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}