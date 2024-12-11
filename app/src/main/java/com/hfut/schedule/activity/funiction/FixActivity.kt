package com.hfut.schedule.activity.funiction

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.hfut.schedule.activity.main.BaseActivity
import com.hfut.schedule.ui.activity.fix.main.Fix
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
