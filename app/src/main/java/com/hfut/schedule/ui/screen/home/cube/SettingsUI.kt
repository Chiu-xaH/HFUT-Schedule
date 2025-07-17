package com.hfut.schedule.ui.screen.home.cube

import com.hfut.schedule.ui.screen.home.cube.sub.CalendarSettingsScreen
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.logic.enumeration.FixBarItems
import com.hfut.schedule.ui.screen.fix.about.AboutUI
import com.hfut.schedule.ui.screen.fix.fix.FixUI
import com.hfut.schedule.ui.screen.home.cube.screen.APPScreen
import com.hfut.schedule.ui.screen.home.cube.screen.NetWorkScreen
import com.hfut.schedule.ui.screen.home.cube.screen.UIScreen
import com.hfut.schedule.ui.screen.home.cube.sub.DeveloperScreen
import com.hfut.schedule.ui.screen.home.cube.sub.DownloadMLUI
import com.hfut.schedule.ui.screen.home.cube.sub.EditPasswordScreen
import com.hfut.schedule.ui.screen.home.cube.sub.FocusCardSettings
import com.hfut.schedule.ui.screen.home.cube.sub.LockUI
import com.hfut.schedule.ui.screen.home.cube.sub.RequestArrange
import com.hfut.schedule.ui.screen.home.cube.sub.TEST
import com.hfut.schedule.ui.util.AppAnimationManager
import com.xah.transition.util.isCurrentRoute
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("SuspiciousIndentation", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsScreen(vm : NetWorkViewModel
                   , showlable : Boolean,
                   showlablechanged: (Boolean) -> Unit,
                   ifSaved : Boolean,
                   innerPaddings : PaddingValues,
                   vm1 : LoginViewModel,
                   hazeState: HazeState,
) {
    val navController = rememberNavController()
    Box {
        Scaffold(

            floatingActionButton = {
                navController.let {
                    if(!it.isCurrentRoute(Screen.HomeScreen.route)) {
                        FloatingActionButton (
                            modifier = Modifier.padding(innerPaddings),
                            onClick = { it.popBackStack() },
                        ) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "")
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.HomeScreen.route,
                enterTransition = {
                    AppAnimationManager.centerAnimation.enter
                },
                exitTransition = {
                    AppAnimationManager.centerAnimation.exit
                }
            ) {

                composable(Screen.HomeScreen.route) {
                    Scaffold {
                        HomeSettingScreen(navController,vm, showlable, showlablechanged, ifSaved, innerPaddings,hazeState)
                    }
                }
                composable(Screen.UIScreen.route) {
                    Scaffold {
                        UIScreen(innerPaddings,showlable, showlablechanged)
                    }
                }
                composable(Screen.APPScreen.route) {
                    Scaffold {
                        APPScreen(navController, innerPaddings)
                    }
                }
                composable(Screen.FIxAboutScreen.route) {
                    Scaffold {
                        AboutUI(innerPadding = innerPaddings, vm,true,navController,hazeState)
                    }
                }
                composable(Screen.NetWorkScreen.route) {
                    Scaffold {
                        NetWorkScreen(navController, innerPaddings,ifSaved,hazeState)
                    }
                }
                composable(FixBarItems.Fix.name) {
                    Scaffold {
                        FixUI(innerPadding = innerPaddings,vm1,vm,hazeState)
                    }
                }
                composable("DEBUG") {
                    Scaffold {
                        TEST(innerPaddings)
                    }
                }
                composable(Screen.DownloadScreen.route) {
                    Scaffold {
                        DownloadMLUI(innerPaddings)
                    }
                }
                composable(Screen.DeveloperScreen.route) {
                    Scaffold {
                        DeveloperScreen(vm,innerPaddings)
                    }
                }
                composable(Screen.CalendarScreen.route) {
                    Scaffold {
                        CalendarSettingsScreen(innerPaddings)
                    }
                }
                composable(Screen.LockScreen.route) {
                    Scaffold {
                        LockUI(innerPaddings,hazeState)
                    }
                }
                composable(Screen.FocusCardScreen.route) {
                    Scaffold {
                        FocusCardSettings(innerPaddings)
                    }
                }
                composable(Screen.RequestRangeScreen.route) {
                    Scaffold {
                        RequestArrange(innerPaddings)
                    }
                }
                composable(Screen.PasswordScreen.route) {
                    Scaffold {
                        EditPasswordScreen(hazeState,innerPaddings)
                    }
                }
            }
        }
    }
}