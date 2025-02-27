package com.hfut.schedule.ui.utils.components

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.ui.activity.home.cube.items.subitems.MyAPIItem


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(url: String,cookies : String? = null,showDialog : Boolean,showChanged : () -> Unit,title : String) {
    var webView by remember { mutableStateOf<WebView?>(null) }
    var isDesktopMode by remember { mutableStateOf(false) } // 电脑模式状态

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    Row{
                        IconButton(onClick = {
                            if (webView?.canGoBack() == true) {
                                webView?.goBack()
                            } else {
                                MyToast("无上一页")
                            }
                        }) { Icon(Icons.Default.ArrowBack, contentDescription = "") }


                        IconButton(onClick = { webView?.reload() }) { Icon(
                            painterResource(id = R.drawable.rotate_right), contentDescription = "") }



//                        IconButton(onClick = {
//                            webView?.let { webView ->
//                                isDesktopMode = !isDesktopMode
//                                webView.settings.userAgentString = if (isDesktopMode) {
//                                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36 Edg/129.0.0.0"
//                                } else {
//                                    WebSettings.getDefaultUserAgent(MyApplication.context) // 恢复默认 User-Agent
//                                }
//                                webView.reload()
//                            }
//                        }) { Icon(painterResource(id = if(isDesktopMode) R.drawable.computer else R.drawable.smartphone), contentDescription = "") }

                        IconButton(onClick = { Starter.startWebUrl(url) }) { Icon(
                            painterResource(id = R.drawable.net), contentDescription = "") }

                        IconButton(onClick = showChanged) { Icon(painterResource(id = R.drawable.close), contentDescription = "") }
                    }
                },
                title = { Text(title) }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.builtInZoomControls = true
                        settings.displayZoomControls = false
//                setInitialScale(100) // 100表示不缩放
//                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW // 允许加载 HTTP 资源



                        // 启用 Cookie
                        val cookieManager = CookieManager.getInstance()
                        cookieManager.setAcceptCookie(true)
                        cookieManager.setAcceptThirdPartyCookies(this, true)
                        // 设置 Cookie
                        // **清除已有 Cookie**
                        cookieManager.removeSessionCookies(null)
                        cookieManager.removeAllCookies(null)
                        cookieManager.flush()

                        // **异步设置 Cookie，并确保生效后再加载 URL**
                        cookies?.let {
                            cookieManager.setCookie(url, it) {
                                cookieManager.flush()
                                post {
                                    loadUrl(url)
                                }
                            }
                        } ?: run {
                            loadUrl(url) // 没有 Cookie 直接加载
                        }

//                if (header != null) {
//                    loadUrl(url, header)
//                } else {
//                    loadUrl(url)
//                }
                        webViewClient = object : WebViewClient() {
                            //                    @Deprecated("Deprecated in Java")
//                    override fun shouldOverrideUrlLoading(
//                        view: WebView?,
//                        request: WebResourceRequest?
//                    ): Boolean {
//                        request?.url?.let {
//                            view?.loadUrl(it.toString())
//                        }
//                        return true // 这里返回true，表示让WebView处理URL，不跳转到外部浏览器
//                    }
                            override fun shouldInterceptRequest(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): WebResourceResponse? {
                                request?.url?.toString()?.let { requestUrl ->
                                    cookies?.let {
                                        cookieManager.setCookie(requestUrl, it)
                                    }
                                }
                                return super.shouldInterceptRequest(view, request)
                            }
                        }
                        webView = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

