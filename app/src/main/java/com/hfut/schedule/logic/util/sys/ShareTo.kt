package com.hfut.schedule.logic.util.sys

import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.other.AppVersion
import com.xah.uicommon.util.LogUtil
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception

object ShareTo {
    @JvmStatic
    fun shareAPK() {
        val path : String = "${ MyApplication.context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) }/HFUT-Schedule ${AppVersion.getVersionName()}.apk"
        try {
            getAPK(path)
            val file = File(path)
            val apkUri = FileProvider.getUriForFile(MyApplication.context,"${AppVersion.appPackageName}.provider",file)
            val shareIntent = Intent().apply {
                setAction(Intent.ACTION_SEND)
                setType("application/vnd.android.package-archive")
                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(Intent.EXTRA_STREAM,apkUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            MyApplication.context.startActivity(
                Intent.createChooser(shareIntent,"分享安装包").addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (e : Exception) { }
    }
    @JvmStatic
    private fun getAPK(destinationPath : String) {
        val apkFile = File(MyApplication.context.packageCodePath)
        val destinationFile = File(destinationPath)
        try {
            val sourceChannel = FileInputStream(apkFile).channel
            val destinationChannel = FileOutputStream(destinationFile).channel
            destinationChannel.transferFrom(sourceChannel,0,sourceChannel.size())
            sourceChannel.close()
            destinationChannel.close()
        } catch (e : IOException) {
            LogUtil.error(e)
        }
    }
    // 分享字符串
    @JvmStatic
    fun shareString(str : String) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, str)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            MyApplication.context.startActivity(Intent.createChooser(intent, "分享文本").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (e: Exception) {
            LogUtil.error(e)
        }
    }
}