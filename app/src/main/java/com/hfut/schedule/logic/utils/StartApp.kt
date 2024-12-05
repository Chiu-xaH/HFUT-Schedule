package com.hfut.schedule.logic.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.activity.ShowerLoginActivity
import com.hfut.schedule.ui.UIUtils.MyToast

object StartApp {
    //通过包名启动第三方应用
    @SuppressLint("QueryPermissionsNeeded")
    fun startLaunchAPK(packageName: String,appName : String = "应用") {
        try {
            val intent = MyApplication.context.packageManager.getLaunchIntentForPackage(packageName)
            if(intent == null) MyToast("未安装${appName}")
            else  MyApplication.context.startActivity(intent)
        } catch (_: Exception) {
            MyToast("启动外部应用失败")
        }
    }


    fun openAlipay(URL : String) {
        try {
            val intent = Intent(Intent.ACTION_DEFAULT, Uri.parse(URL))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            MyApplication.context.startActivity(intent)
        } catch (e : Exception) {
            MyToast("打开支付宝失败")
        }
    }
    fun startUri(Uri : String) {
        try {
            val it = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(Uri))
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            MyApplication.context.startActivity(it)
        } catch (e : Exception) {
            MyToast("启动浏览器失败")
        }
    }

    fun <T> startActivity(activity : Class<T>) {
        val it = Intent(MyApplication.context, activity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        MyApplication.context.startActivity(it)
    }
}
