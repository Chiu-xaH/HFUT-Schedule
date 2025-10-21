package com.hfut.schedule.activity.screen

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import com.hfut.schedule.activity.util.BaseActivity
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.ui.screen.MainHost
import com.hfut.schedule.ui.util.state.GlobalUIStateHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        )
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.apply {
            if(!GlobalUIStateHolder.webVpn) {
                launch(Dispatchers.IO) {
                    val cookie = prefs.getString("redirect", "")
                    networkVm.jxglstuLogin(cookie!!)
                }
            }
        }
    }
}





