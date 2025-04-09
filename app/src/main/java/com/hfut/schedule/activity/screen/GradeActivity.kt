package com.hfut.schedule.activity.screen

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import com.hfut.schedule.activity.BaseActivity
import com.hfut.schedule.logic.utils.data.WebVpn
import com.hfut.schedule.ui.activity.grade.main.GradeScreen
import com.hfut.schedule.viewmodel.NetworkViewModelFactory
import com.hfut.schedule.viewmodel.NetWorkViewModel

class GradeActivity : BaseActivity() {
    var webVpn = WebVpn.webVpn
    private val networkVms by lazy { ViewModelProvider(this, NetworkViewModelFactory(webVpn)).get(
        NetWorkViewModel::class.java) }

    @Composable
    override fun UI() {
        GradeScreen(intent.getBooleanExtra("saved",true),networkVms)
    }
}

