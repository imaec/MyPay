package com.imaec.mypay.utils

import android.app.Activity
import android.content.Context
import android.graphics.Point
import androidx.constraintlayout.widget.ConstraintLayout

class DisplayUtil {

    companion object {

        fun getDisplaySizeX(activity: Activity) : Int {
            return Point().apply {
                activity.windowManager.defaultDisplay.getSize(this)
            }.x
        }

        fun getDisplaySizeY(activity: Activity) : Int {
            return Point().apply {
                activity.windowManager.defaultDisplay.getSize(this)
            }.y
        }

        fun getSeekBarSize(context: Context, x: Int, y: Int) : ConstraintLayout.LayoutParams {
            return ConstraintLayout.LayoutParams(x*4/5, y/2).apply {
                leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            }
        }

        private fun Int.dp(context: Context) : Int {
            val d = context.resources.displayMetrics.density
            return (this * d).toInt()
        }
    }
}