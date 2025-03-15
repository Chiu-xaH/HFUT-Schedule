package com.hfut.schedule.activity.screen

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.hfut.schedule.activity.BaseActivity
import com.hfut.schedule.ui.activity.fix.main.Fix

class FixActivity : BaseActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    override fun UI() {
        Fix(super.loginVm,super.networkVm)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.loginVm.My()
    }
}
