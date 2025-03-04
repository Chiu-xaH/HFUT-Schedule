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
    // 获取当前系统的API级别
    val sdkInt = android.os.Build.VERSION.SDK_INT
    // 获取当前系统的版本号
    val release = android.os.Build.VERSION.RELEASE

    val canBlur = sdkInt >= 32

    val canMonet = sdkInt >= 31

    /*
    * 安卓15 35
    * 安卓14 34
    * 安卓13 33
    * 安卓12X 32
    * 安卓12 31
    * 安卓11 30
    * 安卓10 29
    * 安卓9 28
    * 安卓8 27
    * 安卓7 26
     */
    fun androidVersionToSDK(androidVersion : String) : Int? {
        return when(androidVersion) {
            "15" -> 35
            "14" -> 34
            "13" -> 33
            "12X" -> 32
            "12" -> 31
            "11" -> 30
            "10" -> 29
            "9" -> 28
            "8" -> 27
            "7" -> 26
            else -> null
        }
    }
}