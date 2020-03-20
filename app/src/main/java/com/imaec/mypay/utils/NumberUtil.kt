package com.imaec.mypay.utils

import android.text.Editable
import android.util.Log
import java.lang.NumberFormatException
import java.text.DecimalFormat

class NumberUtil {
    companion object {

        private val TAG = NumberUtil::class.java.simpleName

        fun getKor(s: Editable?) : String {
            return s?.let {
                try {
                    DecimalFormat("#,###").format(it.toString().replace(",", "").toDouble())
                } catch (e: Throwable) {
                    "0"
                }
            } ?: "0"
        }

        fun getKor(i: Int) : String {
            return DecimalFormat("#,###").format(i)
        }

        fun getNumber(s: Editable?) : Int {
            return s?.toString()?.replace(",", "")?.toInt() ?: 0
        }
    }
}