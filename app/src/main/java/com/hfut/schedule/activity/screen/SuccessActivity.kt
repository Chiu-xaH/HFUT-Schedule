package com.hfut.schedule.activity.screen

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import com.hfut.schedule.activity.util.BaseActivity
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.ui.nav.destination.HomeDestination
import com.hfut.schedule.ui.screen.MainHost
import com.hfut.schedule.ui.util.state.GlobalStateHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Deprecated("为KMP适配计划的开始做铺垫，即将被合入至SharedNav统一管理")
class SuccessActivity : BaseActivity() {
    @OptIn(ExperimentalSharedTransitionApi::class)
    @SuppressLint("NewApi")
    @Composable
    override fun UI() {
        MainHost(
            networkVm,
            loginVm,
            uiVm,
            false,
            true,
            HomeDestination()
        )
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.apply {
            if(!GlobalStateHolder.webVpn) {
                launch(Dispatchers.IO) {
                    val cookie = prefs.getString("redirect", "")
                    networkVm.jxglstuLogin(cookie!!)
                }
            }
        }
    }
}





