package com.hfut.schedule.logic.util.other

import android.content.pm.PackageManager
import com.hfut.schedule.App.MyApplication

object AppVersion {
    enum class SplitType(val code : Int,val description: String) {
        COMMON(0,"通用"),ARM64(2,"ARM 64位"),ARM32(1,"ARM 32位"),X86(3,"X86 32位"),X86_64(4,"X86 64位"),
//        COMMON_DEBUG(5),ARM64_DEBUG(6),ARM32_DEBUG(7),X86_DEBUG(8),X86_64_DEBUG(9)
    }
    private val packageName = MyApplication.context.packageManager.getPackageInfo(MyApplication.context.packageName,0)

    fun isPreview() : Boolean = getVersionName().contains("Preview")

    private fun getSplitVersionCode() : Int {
        var versionCode = 0
        try {
            versionCode = packageName.versionCode
        } catch ( e : PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return versionCode
    }
    fun getVersionCode() : Int = getSplitVersionCode().let { if(it >= 1000) it/10 else it }

    fun getSplitType() : SplitType = if(getSplitVersionCode() < 1000) {
        SplitType.COMMON
    } else {
        when(getSplitVersionCode() % 10) {
            SplitType.X86.code -> SplitType.X86
            SplitType.COMMON.code -> SplitType.COMMON
            SplitType.ARM32.code -> SplitType.ARM32
            SplitType.ARM64.code -> SplitType.ARM64
            SplitType.X86_64.code -> SplitType.X86_64
            else -> SplitType.COMMON
        }
    }

    fun getVersionName() : String {
        var versionName = ""
        try {
            versionName = packageName.versionName.toString()
        } catch ( e : PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return versionName
    }
    // 获取当前系统的API级别
    val sdkInt = android.os.Build.VERSION.SDK_INT
    // 获取当前系统的版本号
    val release = android.os.Build.VERSION.RELEASE

    val CAN_HAZE_BLUR = sdkInt >= 31

    val CAN_MOTION_BLUR = sdkInt >= 31

    // 华为、安卓12 单独对渐变模糊适配
    val HAZE_BLUR_FOR_S = sdkInt == 31 || sdkInt == 32

    val CAN_DYNAMIC_COLOR = sdkInt >= 31



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
    @JvmStatic
    fun androidVersionToSDK(androidVersion : String) : Int? {
        return when(androidVersion) {
            "16" -> 36
            "15" -> 35
            "14" -> 34
            "13" -> 33
            "12X" -> 32
            "12" -> 31
            "11" -> 30
            "10" -> 29
            "9" -> 28
            "8.1" -> 27
            "8" -> 26
            else -> null
        }
    }
}