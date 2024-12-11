package com.hfut.schedule.activity.search

import androidx.compose.runtime.Composable
import com.hfut.schedule.activity.main.BaseActivity
import com.hfut.schedule.ui.activity.life.main.LifeUI

class LifeActivity : BaseActivity() {
    @Composable
    override fun UI() {
        LifeUI(super.networkVm)
    }
}
