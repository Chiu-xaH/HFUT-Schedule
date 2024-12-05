package com.hfut.schedule.activity

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hfut.schedule.activity.ui.theme.肥工课程表Theme
import com.hfut.schedule.ui.Activity.success.search.Search.News.NewsActivityUI
import org.jsoup.Connection.Base

class NewsActivity : BaseActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    override fun UI() {
        NewsActivityUI(super.networkVm)
    }
}
