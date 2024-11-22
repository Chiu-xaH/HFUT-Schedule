package com.hfut.schedule.activity

import android.os.Bundle
import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.Activity.shower.main.ShowerGuaGua
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShowerActivity : BaseActivity() {
    @Composable
    override fun UI() {
        ShowerGuaGua(super.showerVm)
    }
}
