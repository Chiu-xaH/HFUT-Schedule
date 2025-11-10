package com.hfut.schedule.activity.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.R
import com.hfut.schedule.activity.util.BaseActivity
import com.hfut.schedule.logic.enumeration.SupabaseScreen
import com.hfut.schedule.ui.component.status.StatusUI
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.screen.supabase.SupabaseHome
import com.hfut.schedule.ui.screen.supabase.login.SupabaseLoginScreen
import com.hfut.schedule.ui.util.navigation.AppAnimationManager

class SupabaseActivity : BaseActivity() {
    @Composable
    override fun UI() {
//        if(getPersonInfo().studentId == null) {
//            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
//                Box(modifier = Modifier.align(Alignment.Center)){
//                    StatusUI(R.drawable.visibility_off,"请先刷新登录状态证明是合肥工业大学在校生")
//                }
//            }
//        } else {
//
//        }
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
                SupabaseLoginScreen(super.networkVm,navController)
            }
            // 主UI
            composable(SupabaseScreen.HOME.name) {
                SupabaseHome(super.networkVm,navController,super.uiVm)
            }
        }
    }
}

