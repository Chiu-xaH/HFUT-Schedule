package com.hfut.schedule.logic.utils

import android.content.Intent
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.hfut.schedule.App.MyApplication
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception

object ShareAPK {
    fun ShareAPK() {
        val path : String = "${ MyApplication.context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) }/HFUT-Schedule ${APPVersion.getVersionName()}.apk"
        try {
            getAPK(path)
            val file = File(path)
            val apkUri = FileProvider.getUriForFile(MyApplication.context,"com.hfut.schedule.provider",file)
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
        } catch (e : Exception) {
            Log.d("错误", e.toString())
        }
    }

    fun getAPK(destinationPath : String) {
        val apkFile = File(MyApplication.context.packageCodePath)
        val destinationFile = File(destinationPath)
        try {
            val sourceChannel = FileInputStream(apkFile).channel
            val destinationChannel = FileOutputStream(destinationFile).channel
            destinationChannel.transferFrom(sourceChannel,0,sourceChannel.size())
            sourceChannel.close()
            destinationChannel.close()
        } catch (e : IOException) {
            e.printStackTrace()
        }
    }

}