package com.hfut.schedule.logic.util.other

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.hfut.schedule.BuildConfig
import com.hfut.schedule.application.MyApplication
import com.hjq.device.compat.DeviceOs
import com.xah.common.logic.util.LogUtil
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.text.SimpleDateFormat
import java.util.Locale

object AppVersion {
    enum class SplitType(val code : Int,val description: String) {
        COMMON(0,"通用"),
        ARM64(2,"ARM 64位"),
        ARM32(1,"ARM 32位"),
        X86(3,"X86 32位"),
        X86_64(4,"X86 64位"),
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

    // 获取当前系统的API
    val sdkInt = Build.VERSION.SDK_INT

    val isDebug = BuildConfig.DEBUG

    val CAN_HAZE_BLUR_BAR = sdkInt >= 31
    val CAN_MOTION_BLUR = sdkInt >= 31

    // 华为、安卓12 单独对渐变模糊适配
    val HAZE_BLUR_FOR_S = sdkInt == 31 || sdkInt == 32
    val CAN_DYNAMIC_COLOR = sdkInt >= 31

    val CAN_PREDICTIVE = sdkInt >= 33
    val CAN_LIVE_UPDATE = sdkInt >= 36
    val CAN_SHADER = sdkInt >= 33

    val deviceName: String = Build.MODEL

    private const val SIGN_SHA_256 = "64:D1:58:37:3D:30:91:CA:A8:AD:70:AF:31:5F:EB:65:A6:A3:21:83:79:AD:E8:F6:CA:8D:FF:FF:7F:5C:52:09"
    private const val SIGN_SHA_1 = "66:5C:5E:35:73:52:A4:10:41:58:14:FC:5A:AA:86:11:07:C7:2C:7A"

    private fun getIsSignatureValid(): Boolean {
        return try {
            val pm = MyApplication.context.packageManager

            @Suppress("DEPRECATION")
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val info = pm.getPackageInfo(appPackageName, PackageManager.GET_SIGNING_CERTIFICATES)
                info.signingInfo?.run {
                    if (hasMultipleSigners()) apkContentsSigners else signingCertificateHistory
                }
            } else {
                pm.getPackageInfo(appPackageName, PackageManager.GET_SIGNATURES).signatures
            } ?: return false

            val expected = SIGN_SHA_256.replace(":", "").uppercase()
            val digest = MessageDigest.getInstance("SHA-256")

            signatures.any { sig ->
                digest.reset()
                val actual = digest.digest(sig.toByteArray()).joinToString("") { "%02X".format(it) }
                actual == expected
            }
        } catch (e: Exception) {
            LogUtil.error(e)
            false
        }
    }

    val isSignatureValid by lazy { getIsSignatureValid() }
    val isRunningOnAvd = deviceName.startsWith("sdk_gphone") == true || deviceName.startsWith("Android SDK built for") == true
    val isRunningOnWsa = deviceName.startsWith("Subsystem for Android")
    val isDev : Boolean = !Regex("^\\d+\\.\\d+(\\.\\d+)*$").matches(getVersionName())

    /**
     * 卓易通
     */
    val isHarmonyNext = DeviceOs.isHarmonyOsNextAndroidCompatible()
    data class SignatureInfo(
        val issuer: String,
        val subject: String,
        val validFrom: String,
        val validUntil: String
    )

    fun Context.getSignatureInfo(): SignatureInfo? {
        val cert = getSignature() ?: return null
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return SignatureInfo(
            issuer = cert.issuerX500Principal.name,
            subject = cert.subjectX500Principal.name,
            validFrom = formatter.format(cert.notBefore),
            validUntil = formatter.format(cert.notAfter)
        )
    }

    private fun Context.getSignature(): X509Certificate? {
        return try {
            val packageInfo = if (sdkInt >= Build.VERSION_CODES.P) {
                packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                )
            } else {
                packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNATURES
                )
            }

            val signatureBytes = if (sdkInt >= Build.VERSION_CODES.P) {
                packageInfo.signingInfo?.apkContentsSigners?.firstOrNull()?.toByteArray()
            } else {
                packageInfo.signatures?.firstOrNull()?.toByteArray()
            } ?: return null

            val cert = CertificateFactory.getInstance("X.509").generateCertificate(ByteArrayInputStream(signatureBytes)) as X509Certificate
            return cert
        } catch (e: Exception) {
            LogUtil.error(e)
            null
        }
    }
}