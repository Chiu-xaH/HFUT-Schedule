package com.hfut.schedule.activity.screen.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hfut.schedule.activity.BaseActivity
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.ui.activity.home.main.login.SuccessUI
import com.hfut.schedule.viewmodel.NetworkViewModelFactory
import com.hfut.schedule.viewmodel.NetWorkViewModel
import kotlinx.coroutines.launch

class SuccessActivity : BaseActivity() {

    var webVpn = false
    val networkVms by lazy { ViewModelProvider(this, NetworkViewModelFactory(webVpn)).get(NetWorkViewModel::class.java) }
    @Composable
    override fun UI() {
        SuccessUI(
            networkVms,
            super.loginVm,
            super.uiVm,
            webVpn
        )
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webVpn = intent.getBooleanExtra("webVpn",false)
        lifecycleScope.apply {
            if(!webVpn) {
                launch {
                    val cookie = prefs.getString("redirect", "")
                    networkVms.Jxglstulogin(cookie!!)
                }
            }
        }
    }
}





