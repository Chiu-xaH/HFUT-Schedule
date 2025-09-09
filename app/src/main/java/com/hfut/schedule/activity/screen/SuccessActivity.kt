package com.hfut.schedule.activity.screen

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import com.hfut.schedule.activity.util.BaseActivity
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.screen.MainHost
import com.hfut.schedule.ui.util.GlobalUIStateHolder
import kotlinx.coroutines.launch

class SuccessActivity : BaseActivity() {
//    var webVpn = false
//    val networkVms by lazy { ViewModelProvider(this, NetworkViewModelFactory(GlobalUIStateHolder.webVpn))[NetWorkViewModel::class.java] }
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
//            webVpn
        )
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        webVpn = intent.getBooleanExtra("webVpn",false)
        lifecycleScope.apply {
            if(!GlobalUIStateHolder.webVpn) {
                launch {
                    val cookie = prefs.getString("redirect", "")
//                    Log.d("测试4",cookie.toString())
                    networkVm.jxglstuLogin(cookie!!)
                }
            }
        }
    }
}





