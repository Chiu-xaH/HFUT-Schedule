package com.hfut.schedule.logic.util.sys

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.Settings
import androidx.compose.ui.graphics.Color
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.activity.MainActivity
import com.hfut.schedule.activity.screen.CardActivity
import com.hfut.schedule.activity.screen.FixActivity
import com.hfut.schedule.activity.screen.ShowerActivity
import com.hfut.schedule.activity.screen.SuccessActivity
import com.hfut.schedule.activity.screen.SupabaseActivity
import com.hfut.schedule.activity.util.WebViewActivity
import com.hfut.schedule.logic.enumeration.ShowerScreen
import com.hfut.schedule.logic.enumeration.SupabaseScreen
import com.hfut.schedule.logic.util.network.WebVpnUtil
import com.hfut.schedule.ui.util.webview.getPureUrl
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.search.function.school.webvpn.getWebVpnCookie
import com.hfut.schedule.ui.util.GlobalUIStateHolder
import com.hfut.schedule.ui.util.navigateForTransition

object Starter {
    enum class AppPackages(
        val packageName : String,
        val appName : String,
        val icon : Int,
        val iconBackgroundColor : Color,
    ) {
        TODAY_CAMPUS("com.wisedu.cpdaily","今日校园",R.drawable.today_campus_icon, Color(0xFF3452E6)),
        WECHAT("com.tencent.mm","微信",R.drawable.wechat_icon, Color(0xFF07C561)),
        CHAO_XING("com.chaoxing.mobile","学习通",R.drawable.chao_xing_icon, Color(0xFFD00521)),
        MOOC("com.netease.edu.ucmooc","中国大学MOOC",R.drawable.mooc_icon, Color(0xFFFFFFFF)),
        RAIN_CLASSROOM("com.xuetangx.ykt","雨课堂",R.drawable.rain_classroom_icon, Color(0xFF5097F5)),
        LE_PAO("com.yunzhi.tiyu","云运动",R.drawable.le_pao_icon, Color(0xFF4084FE)),
        ANHUI_HALL("com.iflytek.oshall.ahzwfw","皖事通",R.drawable.anhui_hall_icon, Color(0xFFE20311)),
        ALIPAY("com.eg.android.AlipayGphone","支付宝",R.drawable.alipay_icon, Color(0xFF1978FF))
    }
    //通过包名启动第三方应用
    @JvmStatic
    @SuppressLint("QueryPermissionsNeeded")
    fun startAppLaunch(app : AppPackages,context: Context) {
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
            if(intent == null) showToast("未安装${app.appName}")
            else context.startActivity(intent)
        } catch (_: Exception) {
            showToast("启动外部应用失败")
        }
    }

    @JvmStatic
    fun startWlanSettings(context: Context) {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
    //传入应用URL打开
    @JvmStatic
    fun startAppUrl(context: Context,url : String,appName : String? = null) {
        try {
            val intent = Intent(Intent.ACTION_DEFAULT, url.toUri())
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e : Exception) {
            val name = if(appName == null) {
                val scheme = url.substringBefore("://")
                 when(scheme) {
                    "alipays" -> "支付宝"
                    // ...
                    else -> scheme
                }
            } else appName
            
            showToast("打开${name}失败")
        }
    }
    //传入网页URL打开
    @JvmStatic
    fun startWebUrl(context: Context,url : String) {
        try {
            val it = Intent(Intent.ACTION_VIEW, url.toUri())
            if (context !is Activity) {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(it)
        } catch (e : Exception) {
            showToast("启动浏览器失败")
        }
    }
    @JvmStatic
    fun refreshLogin(context: Context) {
        val it = Intent(context, MainActivity::class.java).apply {
            putExtra("login",true)
            if (context !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        context.startActivity(it)
    }
    @JvmStatic
    fun goToMain(context: Context) {
        val it = Intent(context, MainActivity::class.java).apply {
            putExtra("login",false)
            if (context !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        context.startActivity(it)
    }
    @JvmStatic
    fun loginGuaGua(context: Context) {
        val it = Intent(context, ShowerActivity::class.java).apply {
            putExtra("FIRST",ShowerScreen.LOGIN.name)
            if (context !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        context.startActivity(it)
    }
    @JvmStatic
    fun startGuaGua(context: Context) {
        val it = Intent(context, ShowerActivity::class.java).apply {
            putExtra("FIRST",ShowerScreen.HOME.name)
            if (context !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        context.startActivity(it)
    }
    @JvmStatic
    fun loginSupabase(context: Context) {
        val it = Intent(context, SupabaseActivity::class.java).apply {
            putExtra("FIRST",SupabaseScreen.LOGIN.name)
            if (context !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        context.startActivity(it)
    }
    @JvmStatic
    suspend fun startWebView(
        context: Context,
        url : String,
        title : String = getPureUrl(url),
        cookie :String? = null,
        icon : Int? = null
    ) {
        if(GlobalUIStateHolder.globalWebVpn) {
            val cookieWebVpn = getWebVpnCookie()
            if(url.contains(MyApplication.WEBVPN_URL)) {
                goToWebView(context, url, title, cookieWebVpn,icon)
            } else {
                goToWebView(context, WebVpnUtil.getWebVpnUrl(url), title, cookieWebVpn,icon)
            }
        } else {
            goToWebView(context,url, title, cookie,icon)
        }
    }
    @JvmStatic
    suspend fun startWebView(
        navController: NavController,
        url : String,
        title : String = getPureUrl(url),
        cookie :String? = null,
        icon : Int = R.drawable.net,
        transplantBackground: Boolean = false
    ) {
        if(GlobalUIStateHolder.globalWebVpn) {
            val cookieWebVpn = getWebVpnCookie()
            if(url.contains(MyApplication.WEBVPN_URL)) {
                goToWebViewNavigation(navController, url, title, cookieWebVpn,icon,transplantBackground)
            } else {
                goToWebViewNavigation(navController, WebVpnUtil.getWebVpnUrl(url), title, cookieWebVpn,icon,transplantBackground)
            }
        } else {
            goToWebViewNavigation(navController,url, title, cookie,icon,transplantBackground)
        }
    }
    @JvmStatic
    private fun goToWebViewNavigation(
        navController: NavController,
        url : String,
        title : String = getPureUrl(url),
        cookie :String? = null,
        icon : Int = R.drawable.net,
        transplantBackground: Boolean = false
    ) {
        navController.navigateForTransition(
            AppNavRoute.WebView,
            AppNavRoute.WebView.withArgs(
                url = url,
                title = title,
                cookies = cookie,
                icon = icon,
            ),
            transplantBackground
        )
    }
    @JvmStatic
    private fun goToWebView(
        context: Context,
        url : String,
        title : String = getPureUrl(url),
        cookies: String? = null,
        icon : Int? = null
    ) {
        GlobalUIStateHolder.pushToFront(AppNavRoute.WebView.withArgs(url,title,cookies,icon ?: R.drawable.net), AppNavRoute.WebView)
        val it = Intent(context, WebViewActivity::class.java).apply {
            putExtra("url",url)
            putExtra("title",title)
            cookies?.let { putExtra("cookies",it) }
            icon?.let { putExtra("icon",it) }
            if (context !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        context.startActivity(it)
    }
    @JvmStatic
    fun startSupabase(context: Context) {
        val it = Intent(context, SupabaseActivity::class.java).apply {
            putExtra("FIRST",SupabaseScreen.HOME.name)
            if (context !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        context.startActivity(it)
    }
    @JvmStatic
    fun startCard(context: Context) {
        val it = Intent(context, CardActivity::class.java)
        if (context !is Activity) {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(it)
    }
    @JvmStatic
    fun emailMe(context: Context) {
        val it = Intent(Intent.ACTION_SENDTO, "mailto:zsh0908@outlook.com".toUri())
        if (context !is Activity) {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(it)
    }
    @JvmStatic
    fun loginSuccess(context: Context) {
        val it = Intent(context, SuccessActivity::class.java).apply {
//            putExtra("webVpn",GlobalUIStateHolder.webVpn)
            if (context !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        context.startActivity(it)
    }
    @JvmStatic
    fun startFix(context: Context) {
        val it = Intent(context, FixActivity::class.java)
        if (context !is Activity) {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(it)
    }
    @JvmStatic
    fun openDownloadFolder(activity: Activity) {
        val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val uri = Uri.fromFile(downloads) // 或者用 buildTreeDocumentUri() Android 10+

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
        }

        activity.startActivityForResult(intent, 100)
    }
}


