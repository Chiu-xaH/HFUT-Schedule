package com.hfut.schedule.ui.util.webview

import android.content.Context
import android.webkit.WebSettings
import android.webkit.WebView

object WebViewPool {
    private val pool = ArrayDeque<WebView>()

    fun obtain(context: Context): WebView {
        return if (pool.isNotEmpty()) {
            pool.removeFirst()
        } else {
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.builtInZoomControls = true
                settings.displayZoomControls = false
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                WebView.enableSlowWholeDocumentDraw()
                WebView.setWebContentsDebuggingEnabled(true)
                settings.safeBrowsingEnabled = false
            }
        }
    }

    fun recycle(webView: WebView) {
        webView.stopLoading()
        webView.loadUrl("about:blank") // 清理状态
        pool.addLast(webView)
    }
}