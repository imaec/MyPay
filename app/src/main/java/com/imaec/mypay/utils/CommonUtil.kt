package com.imaec.mypay.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager


class CommonUtil {

    companion object {
        fun getVersion(context: Context?) : String {
            var version = "Unknown"
            val packageInfo: PackageInfo

            if (context == null) return version

            try {
                packageInfo = context.applicationContext
                    .packageManager
                    .getPackageInfo(context.applicationContext.packageName, 0)
                version = packageInfo.versionName
            } catch (e: PackageManager.NameNotFoundException) {
                return version
            }
            return version
        }
    }
}