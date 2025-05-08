package com.hfut.schedule.activity.screen

import android.os.Bundle
import androidx.compose.runtime.Composable
import com.hfut.schedule.activity.BaseActivity
import com.hfut.schedule.ui.screen.fix.Fix

class FixActivity : BaseActivity() {
    @Composable
    override fun UI() {
        Fix(super.loginVm,super.networkVm)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.loginVm.getMyApi()
    }
}
