package com.hfut.schedule.activity.util

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import com.hfut.schedule.ui.component.webview.WebViewScreenForActivity
import com.hfut.schedule.ui.component.webview.getPureUrl
import com.hfut.schedule.ui.theme.AppTheme

class WebViewActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val url = intent.getStringExtra("url") ?: return
        val title = intent.getStringExtra("title") ?: getPureUrl(url)
        val cookies = intent.getStringExtra("cookies")

        setContent {
            AppTheme {
                WebViewScreenForActivity(url,cookies,title)
            }
        }
    }
}
