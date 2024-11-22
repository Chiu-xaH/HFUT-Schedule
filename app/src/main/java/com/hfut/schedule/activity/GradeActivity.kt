package com.hfut.schedule.activity

import android.os.Bundle
import androidx.compose.runtime.Composable
import com.hfut.schedule.ui.Activity.success.search.Search.Grade.GradeUI
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GradeActivity : BaseActivity() {
    @Composable
    override fun UI() {
        GradeUI(intent.getBooleanExtra("saved",true),super.networkVm)
    }
}

