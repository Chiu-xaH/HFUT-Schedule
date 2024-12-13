package com.hfut.schedule.activity.funiction

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.hfut.schedule.activity.main.BaseActivity
import com.hfut.schedule.ui.activity.news.main.NewsActivityUI

class NewsActivity : BaseActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    override fun UI() {
        NewsActivityUI(super.networkVm)
    }
}
