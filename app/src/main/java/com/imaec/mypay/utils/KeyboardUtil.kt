package com.imaec.mypay.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import com.imaec.mypay.ui.callback.KeyboardVisibilityListener

class KeyboardUtil {

    companion object {

        var mAppHeight: Int = 0
        var currentOrientation = -1

        fun setKeyboardVisibilityListener(activity: Activity, keyboardVisibilityListener: KeyboardVisibilityListener) {
            val contentView = activity.findViewById<View>(android.R.id.content)

            contentView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

                private var mPreviousHeight: Int = 0

                override fun onGlobalLayout() {
                    val newHeight = contentView.height

                    if (newHeight == mPreviousHeight) return

                    mPreviousHeight = newHeight

                    if (activity.resources.configuration.orientation != currentOrientation) {
                        currentOrientation = activity.resources.configuration.orientation
                        mAppHeight = 0
                    }

                    if (newHeight >= mAppHeight) {
                        mAppHeight = newHeight
                    }

                    if (newHeight != 0) {
                        if (mAppHeight > newHeight) {
                            // Height decreased: keyboard was shown
                            keyboardVisibilityListener.onKeyboardVisibilityChanged(true)
                        } else {
                            // Height increased: keyboard was hidden
                            keyboardVisibilityListener.onKeyboardVisibilityChanged(false)
                        }
                    }
                }
            })
        }

        fun hideKeyboardFrom(context: Context) {
            try {
                val imm = (context as Activity).getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                context.currentFocus?.let {
                    imm.hideSoftInputFromWindow(it.windowToken, 0)
                }
            } catch (e: Exception) {
                Log.d("exception :::: ", e.toString())
            }
        }

        fun showKeyboard(context: Context) {
            try {
                val imm = (context as Activity).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                context.currentFocus?.let {
                    imm.toggleSoftInputFromWindow(it.windowToken, InputMethodManager.SHOW_FORCED, 0)
                }
            } catch (e: Exception) {
                Log.d("exception :::: ", e.toString())
            }
        }
    }
}