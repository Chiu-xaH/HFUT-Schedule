package com.hfut.schedule.ui.ComposeUI.Search.Electric

import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun WebViewScreen(url: String) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                loadUrl(url)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun WebViewScreen2(url: String,Cookie : String) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                val headers = mapOf("Cookie" to Cookie)
                loadUrl(url,headers)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}