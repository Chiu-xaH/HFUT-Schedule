package com.hfut.schedule.activity.shower

import androidx.compose.runtime.Composable
import com.hfut.schedule.activity.main.BaseActivity
import com.hfut.schedule.ui.activity.shower.main.ShowerGuaGua
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowerActivity : BaseActivity() {
    @Composable
    override fun UI() {
        ShowerGuaGua(super.showerVm,super.networkVm)
    }
}
