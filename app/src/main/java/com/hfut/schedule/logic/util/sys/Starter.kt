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
import com.hfut.schedule.activity.util.WebViewActivity
import com.hfut.schedule.logic.enumeration.ShowerScreen
import com.hfut.schedule.logic.enumeration.SupabaseScreen
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.component.webview.getPureUrl

object Starter {
    enum class AppPackages(val packageName : String,val appName : String) {
        TODAY_CAMPUS("com.wisedu.cpdaily","今日校园"),
        WECHAT("com.tencent.mm","微信"),
        CHAO_XING("com.chaoxing.mobile","学习通"),
        MOOC("com.netease.edu.ucmooc","中国大学MOOC"),
        RAIN_CLASSROOM("com.xuetangx.ykt","雨课堂"),
        LEPAO("com.yunzhi.tiyu","云运动"),
    }
    //通过包名启动第三方应用
    @JvmStatic
    @SuppressLint("QueryPermissionsNeeded")
    fun startAppLaunch(app : AppPackages) {
        try {
            val intent = MyApplication.context.packageManager.getLaunchIntentForPackage(app.packageName)
            if(intent == null) showToast("未安装${app.appName}")
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
    fun startWebView(
        url : String,
        title : String = getPureUrl(url),
        cookie :String? = null,
    ) {
        val switch_startUri = prefs.getBoolean("SWITCHSTARTURI",true)
        if(switch_startUri) {
            goToWebView(url, title, cookie)
        } else {
            startWebUrl(url)
        }
    }
    @JvmStatic
    private fun goToWebView(
        url : String,
        title : String = getPureUrl(url),
        cookies: String? = null,
    ) {
        val it = Intent(MyApplication.context, WebViewActivity::class.java).apply {
            putExtra("url",url)
            putExtra("title",title)
            cookies?.let { putExtra("cookies",it) }
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


