package com.imaec.mypay.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi

class PermissionUtil {

    companion object {

        @RequiresApi(Build.VERSION_CODES.M)
        fun checkPermission(context: Context, permission: String) : Boolean {
            return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        }

        @RequiresApi(Build.VERSION_CODES.M)
        fun requestPermission(activity: Activity, arrPermission: Array<String>) {
            activity.requestPermissions(arrPermission, 10101)
        }
    }
}