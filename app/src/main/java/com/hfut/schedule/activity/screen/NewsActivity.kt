package com.hfut.schedule.activity.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.hfut.schedule.activity.BaseActivity
import com.hfut.schedule.ui.activity.news.main.NewsActivityUI

class NewsActivity : BaseActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    override fun UI() {
        NewsActivityUI(super.networkVm)
    }
}
