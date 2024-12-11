package com.hfut.schedule.activity.search

import androidx.compose.runtime.Composable
import com.hfut.schedule.activity.main.BaseActivity
import com.hfut.schedule.ui.activity.grade.main.GradeUI
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GradeActivity : BaseActivity() {
    @Composable
    override fun UI() {
        GradeUI(intent.getBooleanExtra("saved",true),super.networkVm)
    }
}

