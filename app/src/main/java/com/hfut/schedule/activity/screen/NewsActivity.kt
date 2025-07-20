package com.hfut.schedule.activity.screen

import androidx.compose.runtime.Composable
import com.hfut.schedule.activity.util.BaseActivity
import com.hfut.schedule.ui.screen.news.home.NewsActivityUI

class NewsActivity : BaseActivity() {
    @Composable
    override fun UI() = NewsActivityUI(super.networkVm)
}
