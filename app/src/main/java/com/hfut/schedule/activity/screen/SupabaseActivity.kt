package com.hfut.schedule.activity.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.R
import com.hfut.schedule.activity.BaseActivity
import com.hfut.schedule.logic.enumeration.SupabaseScreen
import com.hfut.schedule.ui.component.StatusUI
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.screen.supabase.SupabaseHome
import com.hfut.schedule.ui.screen.supabase.login.SupabaseLoginScreen
import com.hfut.schedule.ui.util.AppAnimationManager
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

class SupabaseActivity : BaseActivity() {
    @Composable
    override fun UI() {
        if(getPersonInfo().username == null) {
            Box(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.align(Alignment.Center)){
                    StatusUI(R.drawable.visibility_off,"非合肥工业大学学生禁止使用")
                }
            }
        } else {
            val navController = rememberNavController()
            val first = if(intent.getStringExtra("FIRST") == SupabaseScreen.LOGIN.name) SupabaseScreen.LOGIN.name else SupabaseScreen.HOME.name
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
                // 登录
                composable(SupabaseScreen.LOGIN.name) {
                    SupabaseLoginScreen(super.networkVm,super.uiVm,navController)
                }
                // 主UI
                composable(SupabaseScreen.HOME.name) {
                    SupabaseHome(super.networkVm,navController,super.uiVm)
                }
            }
        }
    }
}

