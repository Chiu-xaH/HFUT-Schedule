package com.hfut.schedule.activity.screen

import android.annotation.SuppressLint
import android.os.Bundle
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
import com.hfut.schedule.activity.BaseActivity
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.datetime.getCelebration
import com.hfut.schedule.ui.AppNavRoute
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
        val navController = rememberNavController()
        SharedTransitionLayout(
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            NavHost(
                navController = navController,
                startDestination = AppNavRoute.Home.route,
                enterTransition = { AppAnimationManager.fadeAnimation.enter },
                exitTransition = { AppAnimationManager.fadeAnimation.exit }
            ) {
                // 主UI
                composable(AppNavRoute.Home.route) {
                    MainScreen(
                        vm = networkVms,
                        vm2 = super.loginVm,
                        vmUI = super.uiVm,
                        celebrationText = getCelebration().str,
                        webVpn = webVpn,
                        isLogin = true,
                        navHostTopController = navController,
                        this@SharedTransitionLayout,
                        this@composable
                    )
                }
                // 成绩
                composable(
                    route = AppNavRoute.Grade.receiveRoute(),
                    arguments = listOf(
                        navArgument("ifSaved") {
                            type = NavType.BoolType
                        }
                    )
                ) { backStackEntry ->
                    val ifSaved = backStackEntry.arguments?.getBoolean("ifSaved") ?: true
                    GradeScreen(
                        ifSaved,
                        networkVm,
                        navController,
                        this@SharedTransitionLayout,
                        this@composable,
                    )
                }
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webVpn = intent.getBooleanExtra("webVpn",false)
        lifecycleScope.apply {
            if(!webVpn) {
                launch {
                    val cookie = prefs.getString("redirect", "")
                    networkVms.jxglstuLogin(cookie!!)
                }
            }
        }
    }
}





