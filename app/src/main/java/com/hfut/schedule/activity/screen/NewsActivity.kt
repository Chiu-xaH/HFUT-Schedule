package com.hfut.schedule.activity.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.hfut.schedule.activity.BaseActivity
import com.hfut.schedule.ui.screen.news.NewsActivityUI

class NewsActivity : BaseActivity() {
    @Composable
    override fun UI() {
        NewsActivityUI(super.networkVm)
    }
}
