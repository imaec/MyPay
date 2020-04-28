package com.imaec.mypay.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.imaec.mypay.base.BaseViewModel
import com.imaec.mypay.utils.*
import com.imaec.mypay.utils.SharedPreferenceManager.KEY
import com.kakao.kakaolink.v2.KakaoLinkResponse
import com.kakao.kakaolink.v2.KakaoLinkService
import com.kakao.message.template.ButtonObject
import com.kakao.message.template.ContentObject
import com.kakao.message.template.FeedTemplate
import com.kakao.message.template.LinkObject
import com.kakao.network.ErrorResult
import com.kakao.network.callback.ResponseCallback

class SettingViewModel(context: Context) : BaseViewModel(context) {

    val pay = MutableLiveData<String>()
    val payDay = MutableLiveData<String>()
    val start = MutableLiveData<String>()
    val end = MutableLiveData<String>()
    val appVersion = MutableLiveData<String>()

    val alertPayDay = MutableLiveData<Boolean>().set(getAlert(KEY.PREF_NAME_ALERT_PAY_DAY))
    val alertStart = MutableLiveData<Boolean>().set(getAlert(KEY.PREF_NAME_ALERT_START))
    val alertEnd = MutableLiveData<Boolean>().set(getAlert(KEY.PREF_NAME_ALERT_END))

    fun setPayInf() {
        pay.value = "${NumberUtil.getKor(SharedPreferenceManager.getInt(context, KEY.PREF_NAME_PAY, 0))}원"
        payDay.value = SharedPreferenceManager.getString(context, KEY.PREF_NAME_PAY_DAY, "")
        start.value = SharedPreferenceManager.getString(context, KEY.PREF_NAME_WORK_START, "")
        end.value = SharedPreferenceManager.getString(context, KEY.PREF_NAME_WORK_END, "")
    }

    fun captureGraph() {

    }

    fun share(title: String, description: String, callback: (KakaoLinkResponse?) -> Unit) {
        val params = FeedTemplate
            .newBuilder(
                ContentObject.newBuilder(
                    title,
                    "http://mud-kage.kakao.co.kr/dn/NTmhS/btqfEUdFAUf/FjKzkZsnoeE4o19klTOVI1/openlink_640x640s.jpg",
                    LinkObject.newBuilder()
                        .setAndroidExecutionParams("")
                        .build()
                )
                    .setDescrption(description)
                    .build()
            )
            .addButton(
                ButtonObject(
                    "자세히 보기",
                    LinkObject.newBuilder()
                        .setAndroidExecutionParams("")
                        .build()
                )
            )
            .build()

        val serverCallbackArgs: MutableMap<String, String> =
            HashMap()
        serverCallbackArgs["user_id"] = "\${current_user_id}"
        serverCallbackArgs["product_id"] = "\${shared_product_id}"

        KakaoLinkService.getInstance().sendDefault(context, params, serverCallbackArgs, object : ResponseCallback<KakaoLinkResponse?>() {
                override fun onFailure(errorResult: ErrorResult) {
                    Log.e(TAG, errorResult.toString())
                    Toast.makeText(context, "공유하기에 실패했습니다.\n잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess(result: KakaoLinkResponse?) {
                    callback(result)
                }
            })
    }

    fun setAlert(key: KEY, isChecked: Boolean, alarmId: Int) {
        if (SharedPreferenceManager.getBool(context, key, true) && isChecked) {
            AlertManager.cancel(context, alarmId)
            return
        }
        SharedPreferenceManager.putValue(context, key, isChecked)

        if (!isChecked) return

        val nextPayDay = Calculator.getNextPayDay()
        val tomorrow = DateUtil.getTomorrow("yyyy-MM-dd")
        val startTime = Calculator.getStartTimeStr()
        val endTime = Calculator.getEndTimeStr()
        when (key) {
            KEY.PREF_NAME_ALERT_PAY_DAY -> {
                AlertManager.apply {
                    setDate(nextPayDay.slice(IntRange(0, 3)).toInt(), nextPayDay.slice(IntRange(4, 5)).toInt(), nextPayDay.slice(IntRange(6, 7)).toInt())
                    setTime(nextPayDay.slice(IntRange(8, 9)).toInt())
                    regist(context, PAY)
                }
            }
            KEY.PREF_NAME_ALERT_START -> {
                AlertManager.apply {
                    setDate(tomorrow.split("-")[0].toInt(), tomorrow.split("-")[1].toInt(), tomorrow.split("-")[2].toInt())
                    setTime(startTime.slice(IntRange(0, 1)).toInt())
                    regist(context, START)
                }
            }
            KEY.PREF_NAME_ALERT_END -> {
                AlertManager.apply {
                    setDate(tomorrow.split("-")[0].toInt(), tomorrow.split("-")[1].toInt(), tomorrow.split("-")[2].toInt())
                    setTime(endTime.slice(IntRange(0, 1)).toInt())
                    regist(context, END)
                }
            }
            else -> {}
        }
    }

    private fun getAlert(key: KEY) : Boolean {
        return SharedPreferenceManager.getBool(context, key, true)
    }
}