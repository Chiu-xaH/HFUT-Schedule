package com.hfut.schedule.activity.screen

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.runtime.Composable
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
import com.hfut.schedule.ui.component.Party
import com.hfut.schedule.ui.screen.grade.GradeScreen
import com.hfut.schedule.ui.screen.home.MainScreen
import com.hfut.schedule.ui.screen.login.LoginScreen
import com.hfut.schedule.ui.screen.login.MainNav
import com.hfut.schedule.ui.screen.login.UseAgreementScreen
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.viewmodel.network.NetworkViewModelFactory
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import kotlinx.coroutines.launch

class SuccessActivity : BaseActivity() {
    var webVpn = false
    val networkVms by lazy { ViewModelProvider(this, NetworkViewModelFactory(webVpn))[NetWorkViewModel::class.java] }
    @SuppressLint("NewApi")
    @Composable
    override fun UI() {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = MainNav.HOME.name,
            enterTransition = { AppAnimationManager.fadeAnimation.enter },
            exitTransition = { AppAnimationManager.fadeAnimation.exit }
        ) {
            // 主UI
            composable(MainNav.HOME.name) {
                MainScreen(
                    vm = networkVms,
                    vm2 = super.loginVm,
                    vmUI = super.uiVm,
                    webVpn = webVpn,
                    celebrationText = getCelebration().str,
                    isLogin = true,
                    navHostTopController = navController
                )
            }
            // 成绩
            composable(
                route = "${MainNav.GRADE.name}?ifSaved={ifSaved}",
                arguments = listOf(
                    navArgument("ifSaved") {
                        type = NavType.BoolType
                    }
                )
            ) { backStackEntry ->
                val ifSaved = backStackEntry.arguments?.getBoolean("ifSaved") ?: true
                GradeScreen(ifSaved,networkVm,navController)
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





