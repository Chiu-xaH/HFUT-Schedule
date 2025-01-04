package com.hfut.schedule.ui.utils.components

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(url: String,header : Map<String, String>? = null) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.builtInZoomControls = true
                settings.displayZoomControls = false
//                setInitialScale(100) // 100表示不缩放
                if (header != null) {
                    loadUrl(url, header)
                } else {
                    loadUrl(url)
                }
                webViewClient = object : WebViewClient() {
                    @Deprecated("Deprecated in Java")
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        request?.url?.let {
                            if (header != null) {
                                view?.loadUrl(it.toString(), header)
                            } else {
                                view?.loadUrl(it.toString())
                            }
                        }
                            return true // 这里返回true，表示让WebView处理URL，不跳转到外部浏览器
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun mapScreen() {
   // val myWebView = MyWebView(MyApplication.context)
    //v/al maWenViewWrapper = MAWebViewWrapper(myWebView)


   // val aMapWrapper = AMapWrapper(MyApplication.context,maWenViewWrapper)
    //aMapWrapper.onCreate()
    //aMapWrapper.getMapAsyn()
   // AndroidView(
     //   factory = { context ->
       //     com.amap.
       // },
       // modifier = Modifier.fillMaxSize()
    //)
}

