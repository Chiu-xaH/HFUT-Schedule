package com.hfut.schedule.ui.UIUtils

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(url: String) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                loadUrl(url)
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        url: String?
                    ): Boolean {
                        url?.let { view?.loadUrl(it) }
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

