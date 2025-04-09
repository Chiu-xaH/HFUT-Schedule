package com.hfut.schedule.ui.activity.home.cube.main

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.viewmodel.LoginViewModel
import com.hfut.schedule.logic.enums.FixBarItems
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.ui.activity.fix.about.AboutUI
import com.hfut.schedule.ui.activity.fix.fix.FixUI
import com.hfut.schedule.ui.activity.home.cube.items.NavUIs.APPScreen
import com.hfut.schedule.ui.activity.home.cube.items.main.HomeSettingScreen
import com.hfut.schedule.ui.activity.home.cube.items.NavUIs.NetWorkScreen
import com.hfut.schedule.ui.activity.home.cube.items.main.Screen
import com.hfut.schedule.ui.activity.home.cube.items.NavUIs.UIScreen
import com.hfut.schedule.ui.activity.home.cube.items.subitems.DownloadMLUI
import com.hfut.schedule.ui.activity.home.cube.items.subitems.FocusCardSettings
import com.hfut.schedule.ui.activity.home.cube.items.subitems.LockUI
import com.hfut.schedule.ui.activity.home.cube.items.subitems.RequestArrange
import com.hfut.schedule.ui.activity.home.cube.items.subitems.TEST
import com.hfut.schedule.ui.utils.NavigateAnimationManager
import com.hfut.schedule.ui.utils.isCurrentRoute
import dev.chrisbanes.haze.HazeState

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("SuspiciousIndentation", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsScreen(vm : NetWorkViewModel
                   ,showlable : Boolean,
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
                    if(it.isCurrentRoute(Screen.HomeScreen.route)) {
                        FloatingActionButton (
                            modifier = Modifier.padding(innerPaddings),
                            onClick = { it.popBackStack() },
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.HomeScreen.route,
                enterTransition = {
                    NavigateAnimationManager.centerAnimation.enter
                },
                exitTransition = {
                    NavigateAnimationManager.centerAnimation.exit
                }
            ) {

                composable(Screen.HomeScreen.route) {
                    Scaffold {
                        HomeSettingScreen(navController,vm, showlable, showlablechanged, ifSaved, innerPaddings,hazeState)
                    }
                }
                composable(Screen.UIScreen.route) {
                    Scaffold {
                        UIScreen(navController, innerPaddings,showlable, showlablechanged)
                    }
                }
                composable(Screen.APPScreen.route) {
                    Scaffold {
                        APPScreen(navController, innerPaddings,ifSaved,hazeState)
                    }
                }
                composable(Screen.FIxAboutScreen.route) {
                    Scaffold {
                        AboutUI(innerPadding = innerPaddings, vm1,true,navController,hazeState)
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
                        TEST(innerPadding)
                    }
                }
                composable(Screen.DownloadScreen.route) {
                    Scaffold {
                        DownloadMLUI(innerPaddings)
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
            }
        }
    }
}