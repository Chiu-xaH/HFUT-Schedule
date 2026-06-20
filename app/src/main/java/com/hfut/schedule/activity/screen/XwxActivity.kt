package com.hfut.schedule.activity.screen

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.activity.util.BaseActivity
import com.hfut.schedule.logic.enumeration.XwxScreen
import com.hfut.schedule.ui.screen.xwx.XwxLoginScreen
import com.hfut.schedule.ui.screen.xwx.XwxMainScreen
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.viewmodel.network.XwxViewModel

@Deprecated("为KMP适配计划的开始做铺垫，即将被合入至SharedNav统一管理")
class XwxActivity : BaseActivity() {
    @Composable
    override fun UI() {
        val networkVm = viewModel { XwxViewModel() }
        val navController = rememberNavController()
        val first = if(intent.getStringExtra("FIRST") == XwxScreen.HOME.name) XwxScreen.HOME.name else XwxScreen.LOGIN.name
        NavHost(
            navController = navController,
            startDestination = first,
            enterTransition = {
                AppAnimationManager.fadeAnimation.enter
            },
            exitTransition = {
                AppAnimationManager.fadeAnimation.exit
            }
        ) {
            // 主UI
            composable(XwxScreen.HOME.name) {
                XwxMainScreen(networkVm,navController)
            }
            // 游客模式
            composable(XwxScreen.LOGIN.name) {
                XwxLoginScreen(networkVm,navController)
            }
        }
    }
}



