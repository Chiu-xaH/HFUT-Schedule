package com.hfut.schedule.activity.funiction

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import com.hfut.schedule.activity.main.BaseActivity
import com.hfut.schedule.logic.utils.WebVpn
import com.hfut.schedule.ui.activity.grade.main.GradeUI
import com.hfut.schedule.viewmodel.LoginSuccessViewModelFactory
import com.hfut.schedule.viewmodel.NetWorkViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GradeActivity : BaseActivity() {
    var webVpn = WebVpn.webVpn
    private val networkVms by lazy { ViewModelProvider(this, LoginSuccessViewModelFactory(webVpn)).get(
        NetWorkViewModel::class.java) }

    @Composable
    override fun UI() {
        GradeUI(intent.getBooleanExtra("saved",true),networkVms)
    }
}

