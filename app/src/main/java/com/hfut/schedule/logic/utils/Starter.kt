package com.hfut.schedule.logic.utils

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.webkit.CookieManager
import androidx.navigation.NavHostController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.activity.MainActivity
import com.hfut.schedule.activity.shower.ShowerActivity
import com.hfut.schedule.logic.enums.ShowerScreen
import com.hfut.schedule.ui.activity.login.First
import com.hfut.schedule.ui.utils.components.MyToast

object Starter {
    //通过包名启动第三方应用
    @JvmStatic
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
    //传入应用URL打开
    @JvmStatic
    fun startAppUrl(url : String) {
        try {
            val intent = Intent(Intent.ACTION_DEFAULT, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            MyApplication.context.startActivity(intent)
        } catch (e : Exception) {
            MyToast("打开支付宝失败")
        }
    }
    //传入网页URL打开
    @JvmStatic
    fun startWebUrl(url : String,cookies : String? = null) {
        try {
            val it = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            if(cookies != null) {
                CookieManager.getInstance().setCookie(url, cookies)
                CookieManager.getInstance().flush() // 确保 Cookie 立即生效
            }

            MyApplication.context.startActivity(it)
        } catch (e : Exception) {
            MyToast("启动浏览器失败")
        }
    }
    //跳转Activity
    @JvmStatic
    fun startActivity(clazz: Class<*>, extras: Map<String, Any>? = null) {
        val intent = Intent(MyApplication.context, clazz).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            extras?.forEach { (key, value) ->
                when (value) {
                    is Boolean -> putExtra(key, value)
                    is Int -> putExtra(key, value)
                    is String -> putExtra(key, value)
                    // 你可以根据需要添加更多类型
                }
            }
        }
        MyApplication.context.startActivity(intent)
    }
    @JvmStatic
    fun refreshLogin() {
        val it = Intent(MyApplication.context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("nologin",false)
        }
        MyApplication.context.startActivity(it)
    }
    @JvmStatic
    fun loginGuaGua() {
//        countFunc = 0
        val it = Intent(MyApplication.context, ShowerActivity::class.java).apply {
            putExtra("FIRST",ShowerScreen.LOGIN.name)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        MyApplication.context.startActivity(it)
    }
    @JvmStatic
    fun startGuagua() {
//        countFunc = 0
        val it = Intent(MyApplication.context, ShowerActivity::class.java).apply {
            putExtra("FIRST",ShowerScreen.HOME.name)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        MyApplication.context.startActivity(it)
    }
//    @JvmStatic
//    fun noLogin(navHostController: NavHostController) = turnTo(navHostController,First.GUEST.name)
//        val it = Intent(MyApplication.context, AnonymityActivity::class.java).apply {
//            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        }
//        MyApplication.context.startActivity(it)

    @JvmStatic
    fun emailMe() {
        val it = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:zsh0908@outlook.com"))
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        MyApplication.context.startActivity(it)
    }
}
