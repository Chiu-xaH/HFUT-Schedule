package com.hfut.schedule.ui.UIUtils

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
fun mapScreen() {
    AndroidView(
        factory = { context ->
            com.amap.api.maps.MapView(context).apply {
                onCreate(null)
            }
        },update = {mapView ->
            mapView.getMapAsyn {  }

        },
        modifier = Modifier.fillMaxSize()
    )
}