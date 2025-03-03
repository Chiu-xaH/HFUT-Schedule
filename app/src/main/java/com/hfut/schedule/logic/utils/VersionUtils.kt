package com.hfut.schedule.logic.utils

import android.content.pm.PackageManager
import com.hfut.schedule.App.MyApplication

object VersionUtils {
    fun getVersionCode() : Int {
        var versionCode = 0
        try {
            versionCode = MyApplication.context.packageManager.getPackageInfo(MyApplication.context.packageName,0).versionCode
        } catch ( e : PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return versionCode
    }

    fun getVersionName() : String {
        var versionName = ""
        try {
            versionName = MyApplication.context.packageManager.getPackageInfo(MyApplication.context.packageName,0).versionName.toString()
        } catch ( e : PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return versionName
    }
}