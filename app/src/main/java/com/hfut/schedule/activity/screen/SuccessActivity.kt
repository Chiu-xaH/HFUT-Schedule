package com.hfut.schedule.activity.screen

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hfut.schedule.activity.BaseActivity
import com.hfut.schedule.logic.util.storage.SharePrefs.prefs
import com.hfut.schedule.logic.util.parse.getCelebration
import com.hfut.schedule.ui.screen.home.MainScreen
import com.hfut.schedule.viewmodel.network.NetworkViewModelFactory
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import kotlinx.coroutines.launch

class SuccessActivity : BaseActivity() {

    var webVpn = false
    val networkVms by lazy { ViewModelProvider(this, NetworkViewModelFactory(webVpn))[NetWorkViewModel::class.java] }
    @SuppressLint("NewApi")
    @Composable
    override fun UI() {
        MainScreen(
            vm = networkVms,
            vm2 = super.loginVm,
            vmUI = super.uiVm,
            webVpn = webVpn,
            celebrationText = getCelebration().str,
            isLogin = true
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





