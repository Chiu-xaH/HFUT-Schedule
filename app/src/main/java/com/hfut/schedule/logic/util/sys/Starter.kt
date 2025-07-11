package com.hfut.schedule.logic.util.sys

import android.annotation.SuppressLint
import android.content.Intent
import android.webkit.CookieManager
import androidx.core.net.toUri
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.activity.MainActivity
import com.hfut.schedule.activity.screen.CardActivity
import com.hfut.schedule.activity.screen.FixActivity
import com.hfut.schedule.activity.screen.NewsActivity
import com.hfut.schedule.activity.screen.ShowerActivity
import com.hfut.schedule.activity.screen.SuccessActivity
import com.hfut.schedule.activity.screen.SupabaseActivity
import com.hfut.schedule.logic.enumeration.ShowerScreen
import com.hfut.schedule.logic.enumeration.SupabaseScreen

object Starter {
    //通过包名启动第三方应用
    @JvmStatic
    @SuppressLint("QueryPermissionsNeeded")
    fun startLaunchAPK(packageName: String,appName : String = "应用") {
        try {
            val intent = MyApplication.context.packageManager.getLaunchIntentForPackage(packageName)
            if(intent == null) showToast("未安装${appName}")
            else  MyApplication.context.startActivity(intent)
        } catch (_: Exception) {
            showToast("启动外部应用失败")
        }
    }
    //传入应用URL打开
    @JvmStatic
    fun startAppUrl(url : String) {
        try {
            val intent = Intent(Intent.ACTION_DEFAULT, url.toUri())
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            MyApplication.context.startActivity(intent)
        } catch (e : Exception) {
            showToast("打开支付宝失败")
        }
    }
    //传入网页URL打开
    @JvmStatic
    fun startWebUrl(url : String,cookies : String? = null) {
        try {
            val it = Intent(Intent.ACTION_VIEW, url.toUri())
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            if(cookies != null) {
                CookieManager.getInstance().setCookie(url, cookies)
                CookieManager.getInstance().flush() // 确保 Cookie 立即生效
            }

            MyApplication.context.startActivity(it)
        } catch (e : Exception) {
            showToast("启动浏览器失败")
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
    fun goToMain() {
        val it = Intent(MyApplication.context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("nologin",true)
        }
        MyApplication.context.startActivity(it)
    }
    @JvmStatic
    fun loginGuaGua() {
        val it = Intent(MyApplication.context, ShowerActivity::class.java).apply {
            putExtra("FIRST",ShowerScreen.LOGIN.name)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        MyApplication.context.startActivity(it)
    }
    @JvmStatic
    fun startGuaGua() {
        val it = Intent(MyApplication.context, ShowerActivity::class.java).apply {
            putExtra("FIRST",ShowerScreen.HOME.name)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        MyApplication.context.startActivity(it)
    }
    @JvmStatic
    fun loginSupabase() {
        val it = Intent(MyApplication.context, SupabaseActivity::class.java).apply {
            putExtra("FIRST",SupabaseScreen.LOGIN.name)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        MyApplication.context.startActivity(it)
    }
    @JvmStatic
    fun startSupabase() {
        val it = Intent(MyApplication.context, SupabaseActivity::class.java).apply {
            putExtra("FIRST",SupabaseScreen.HOME.name)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        MyApplication.context.startActivity(it)
    }
    @JvmStatic
    fun startCard() {
        val it = Intent(MyApplication.context, CardActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        MyApplication.context.startActivity(it)
    }
    @JvmStatic
    fun emailMe() {
        val it = Intent(Intent.ACTION_SENDTO, "mailto:zsh0908@outlook.com".toUri())
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        MyApplication.context.startActivity(it)
    }
    @JvmStatic
    fun loginSuccess(webVpn : Boolean = true) {
        val it = Intent(MyApplication.context, SuccessActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("webVpn",webVpn)
        }
        MyApplication.context.startActivity(it)
    }
    @JvmStatic
    fun startNews() {
        val it = Intent(MyApplication.context, NewsActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        MyApplication.context.startActivity(it)
    }
    @JvmStatic
    fun startFix() {
        val it = Intent(MyApplication.context, FixActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        MyApplication.context.startActivity(it)
    }
}


