package com.hfut.schedule.logic.util.other

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.hfut.schedule.application.MyApplication
import com.xah.uicommon.util.LogUtil
import java.security.MessageDigest
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

object AppVersion {
    enum class SplitType(val code : Int,val description: String) {
        COMMON(0,"通用"),ARM64(2,"ARM 64位"),ARM32(1,"ARM 32位"),X86(3,"X86 32位"),X86_64(4,"X86 64位"),
    }
    private val packageName = MyApplication.context.packageManager.getPackageInfo(MyApplication.context.packageName,0)
    val appPackageName = MyApplication.context.packageName

    private fun getSplitVersionCode() : Int {
        var versionCode = 0
        try {
            versionCode = packageName.versionCode
        } catch ( e : PackageManager.NameNotFoundException) {
            LogUtil.error(e)
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
            LogUtil.error(e)
        }
        return versionName
    }
    // 获取当前系统的API级别
    val sdkInt = Build.VERSION.SDK_INT
    // 获取当前系统的版本号
    val release = Build.VERSION.RELEASE

    val CAN_HAZE_BLUR_BAR = sdkInt >= 31
    val CAN_MOTION_BLUR = sdkInt >= 31

    // 华为、安卓12 单独对渐变模糊适配
    val HAZE_BLUR_FOR_S = sdkInt == 31 || sdkInt == 32

    val CAN_DYNAMIC_COLOR = sdkInt >= 31

    val deviceName: String = Build.MODEL

    val CAN_PREDICTIVE = sdkInt >= 33

    val CAN_SHADER = sdkInt >= 33

    fun isInDebugRunning() : Boolean = deviceName.startsWith("sdk_gphone") == true
    fun isPreview() : Boolean = getVersionName().contains("Preview")

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

    @RequiresApi(Build.VERSION_CODES.P)
    @JvmStatic
    fun getAppSignInfo(): List<String> {
        val pm = MyApplication.context.packageManager
        val packageInfo = pm.getPackageInfo(
            appPackageName,
            PackageManager.GET_SIGNING_CERTIFICATES
        )
        val signingInfo = packageInfo.signingInfo
        val signatures = signingInfo?.let {
            if (it.hasMultipleSigners()) {
                it.apkContentsSigners
            } else {
                it.signingCertificateHistory
            }
        } ?: return emptyList()

        return signatures.map { sig ->
            val certFactory = CertificateFactory.getInstance("X.509")
            val cert = certFactory.generateCertificate(sig.toByteArray().inputStream()) as X509Certificate

            buildString {
                appendLine("SubjectDN: ${cert.subjectDN.name}")   // 证书持有人
                appendLine("IssuerDN: ${cert.issuerDN.name}")     // 签发者
                appendLine("Valid From: ${cert.notBefore}")       // 有效期开始
                appendLine("Valid Until: ${cert.notAfter}")       // 有效期结束
                appendLine("Serial Number: ${cert.serialNumber}") // 序列号

                // SHA-256 指纹
                val md = MessageDigest.getInstance("SHA-256")
                val sha256 = md.digest(sig.toByteArray())
                appendLine("SHA-256: ${sha256.joinToString(":") { "%02X".format(it) }}")
            }
        }
    }
}