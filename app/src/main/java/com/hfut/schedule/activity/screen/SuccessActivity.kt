package com.hfut.schedule.activity.screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hfut.schedule.activity.util.BaseActivity
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.datetime.getCelebration
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.MainHost
import com.hfut.schedule.ui.screen.grade.GradeScreen
import com.hfut.schedule.ui.screen.home.MainScreen
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.network.NetworkViewModelFactory
import kotlinx.coroutines.launch

class SuccessActivity : BaseActivity() {
    var webVpn = false
    val networkVms by lazy { ViewModelProvider(this, NetworkViewModelFactory(webVpn))[NetWorkViewModel::class.java] }
    @OptIn(ExperimentalSharedTransitionApi::class)
    @SuppressLint("NewApi")
    @Composable
    override fun UI() {
        MainHost(
            networkVms,
            loginVm,
            uiVm,
            false,
            true,
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
//                    Log.d("测试4",cookie.toString())
                    networkVms.jxglstuLogin(cookie!!)
                }
            }
        }
    }
}





