package com.hfut.schedule.activity.main

import androidx.compose.runtime.Composable
import com.hfut.schedule.activity.main.BaseActivity
import com.hfut.schedule.ui.activity.nologin.NoLoginUI

class AnonymityActivity : BaseActivity() {
    @Composable
    override fun UI() {
        NoLoginUI(super.networkVm,super.loginVm,super.uiVm)
    }
}

