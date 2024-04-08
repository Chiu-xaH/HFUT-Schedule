package com.hfut.schedule.logic.utils

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.ui.UIUtils.MyToast

object StartApp {
    fun openAlipay(URL : String) {
        try {
            val intent = Intent(Intent.ACTION_DEFAULT, Uri.parse(URL))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            MyApplication.context.startActivity(intent)
        } catch (e : Exception) {
            MyToast("打开支付宝失败")
        }
    }
    fun StartUri(Uri : String) {
        try {
            val it = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(Uri))
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            MyApplication.context.startActivity(it)
        } catch (e : Exception) {
            MyToast("启动浏览器失败")
        }
    }
    fun startApp(packageName: String,componentName: String) {

        try {
            val intent = Intent()
            val componentName = ComponentName(packageName, packageName+componentName)
            intent.component = componentName
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            MyApplication.context.startActivity(intent)
        } catch (e : Exception) {
            e.printStackTrace()
            MyToast("启动外部应用失败")
        }
    }
}
