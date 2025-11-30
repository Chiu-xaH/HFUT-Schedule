package com.hfut.schedule.ui.component.webview

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.util.webview.WebViewBackHandler
import com.hfut.schedule.ui.util.webview.WebViewBackIcon
import com.hfut.schedule.ui.util.webview.WebViewContent
import com.hfut.schedule.ui.util.webview.WebViewTools
import com.hfut.schedule.ui.util.webview.WebViewTopBar
import com.hfut.schedule.ui.util.webview.evaluateJs
import com.hfut.schedule.ui.util.webview.getPureUrl
import com.hfut.schedule.ui.util.webview.getWebView
import com.hfut.schedule.ui.util.webview.isThemeDark
import com.hfut.schedule.ui.util.webview.scrollListener
import com.hfut.schedule.ui.util.webview.setInitial
import com.hfut.schedule.ui.util.webview.sharedInterceptRequest
import com.hfut.schedule.ui.util.webview.sharedOverrideUrlLoading
import com.hfut.schedule.ui.util.webview.updateTitle
import com.hfut.schedule.ui.util.webview.updateUrl
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import java.net.HttpURLConnection
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreenForActivity(
    url: String,
    cookies : String? = null,
    title : String = getPureUrl(url),
    icon : Int = R.drawable.net
) {
    var webView by remember { mutableStateOf<WebView?>(null) }
    var visible by remember { mutableStateOf(true) }
    val isDark = isThemeDark()
    val webViewDark by DataStoreManager.enableForceWebViewDark.collectAsState(initial = true)
    var currentUrl by remember { mutableStateOf(url) }
    var currentTitle by remember { mutableStateOf(title) }
    var fullScreen by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    var topColor by remember { mutableStateOf<Color?>(null) }
    val topBarTitleColor = topColor?.let {
        if (it.luminance() < 0.5f) Color.White else Color.Black
    } ?: MaterialTheme.colorScheme.primary

    val activity = LocalActivity.current

    DisposableEffect(Unit) {
        onDispose {
            webView?.destroy()
        }
    }

    WebViewBackHandler(
        webView,
        fullScreen,
        loading,
        { fullScreen = it },
    ) {
        activity?.finish()
    }

    val tools = @Composable {
        WebViewTools(
            webView,
            {  activity?.finish() },
            fullScreen,
            currentUrl,
            currentTitle,
            url,
            {
                fullScreen = it
                visible = it
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = topColor ?: MaterialTheme.colorScheme.primary,
        topBar = {
            WebViewTopBar(
                fullScreen,
                topColor,
                topBarTitleColor,
                visible,
                { visible = it },
                {
                    WebViewBackIcon(webView = webView, color = topBarTitleColor, icon = icon, route = null) {
                        activity?.finish()
                    }
                },
                currentTitle,
                currentUrl
            )
        },
    ) { innerPadding ->
        val bottom = innerPadding.calculateBottomPadding().value.toInt() + APP_HORIZONTAL_DP.value.toInt()
        WebViewContent(
            innerPadding,
            visible,
            tools,
            { visible = it },
            loading,
        ) { context ->
            webView = getWebView(context) {
                // 初始加载
                val cookieManager = CookieManager.getInstance()
                setInitial(cookieManager,cookies, url)
                // 标题获取
                updateTitle(currentUrl) {
                    currentTitle = it
                }

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        // 更新
                        webView = view
                        updateUrl(url) {
                            currentUrl = it
                        }
                        view?.evaluateJs(isDark, webViewDark, bottom) {
                            topColor = it
                        }
                        loading = false
                    }

                    override fun shouldInterceptRequest(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): WebResourceResponse? {
                        if(currentUrl.startsWith(MyApplication.UNI_APP_URL)) {
                            // 自己构建一个带 header 的请求，然后把 response 返回给 WebView
                            val url = request?.url?.toString() ?: return null

                            return try {
                                val conn = URL(url).openConnection() as HttpURLConnection
                                conn.setRequestProperty("Authorization", cookies)
                                conn.connect()

                                val input = conn.inputStream
                                val mime = conn.contentType ?: "text/html"
                                val encoding = conn.contentEncoding ?: "utf-8"

                                WebResourceResponse(mime, encoding, input)
                            } catch (e: Exception) {
                                Log.e("合工大教务",url)
                                e.printStackTrace()
                                null
                            }
                        }
                        sharedInterceptRequest(request,currentUrl,cookies,cookieManager)
                        return super.shouldInterceptRequest(view, request)
                    }
                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean =
                        sharedOverrideUrlLoading(request,context) {
                            loading = it
                        }
                }
                // 监听滚动
                scrollListener {
                    visible = it
                }
            }
            webView!!
        }
    }
}

