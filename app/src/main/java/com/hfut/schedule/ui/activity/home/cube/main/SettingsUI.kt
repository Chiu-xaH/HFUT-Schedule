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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.viewmodel.LoginViewModel
import com.hfut.schedule.logic.enums.FixBarItems
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.activity.fix.about.AboutUI
import com.hfut.schedule.ui.activity.fix.fix.FixUI
import com.hfut.schedule.ui.activity.home.cube.items.NavUIs.APPScreen
import com.hfut.schedule.ui.activity.home.cube.items.main.HomeSettingScreen
import com.hfut.schedule.ui.activity.home.cube.items.NavUIs.NetWorkScreen
import com.hfut.schedule.ui.activity.home.cube.items.main.Screen
import com.hfut.schedule.ui.activity.home.cube.items.NavUIs.UIScreen
import com.hfut.schedule.ui.activity.home.cube.items.subitems.DownloadMLUI
import com.hfut.schedule.ui.activity.home.cube.items.subitems.TEST
import com.hfut.schedule.ui.utils.NavigateAndAnimationManager

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("SuspiciousIndentation", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(vm : NetWorkViewModel
                   ,showlable : Boolean,
                   showlablechanged: (Boolean) -> Unit,
                   ifSaved : Boolean,
                   innerPaddings : PaddingValues,
                   blur : Boolean,
                   blurchanged : (Boolean) -> Unit,
                   vm1 : LoginViewModel,
) {

    val navController = rememberNavController()

    var animation by remember { mutableStateOf(prefs.getInt("ANIMATION", MyApplication.Animation)) }
    Box{
        Scaffold(floatingActionButton = {
            //如果界面处于Screen.HomeScreen.route则不显示
            if(navController.currentBackStackEntryAsState().value?.destination?.route != Screen.HomeScreen.route)
                FloatingActionButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .padding(innerPaddings)
                        .align(Alignment.BottomEnd)
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "")
                }
        }) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.HomeScreen.route,
                enterTransition = {
                    NavigateAndAnimationManager.centerAnimation.enter
                },
                exitTransition = {
                    NavigateAndAnimationManager.centerAnimation.exit
                }
            ) {

                composable(Screen.HomeScreen.route) {
                    Scaffold {
                        HomeSettingScreen(navController,vm, showlable, showlablechanged, ifSaved, innerPaddings, blur, blurchanged)
                    }
                }
                composable(Screen.UIScreen.route) {
                Scaffold {
                    UIScreen(navController, innerPaddings,showlable, showlablechanged,blur, blurchanged)
                  }
                }
                composable(Screen.APPScreen.route) {
                    Scaffold {
                        APPScreen(navController, innerPaddings,ifSaved)
                    }
                }
                composable(Screen.FIxAboutScreen.route) {
                Scaffold {
                    AboutUI(innerPadding = innerPaddings, vm1,true,navController)
                }
                }
                composable(Screen.NetWorkScreen.route) {
                Scaffold {
                    NetWorkScreen(navController, innerPaddings,ifSaved)
                   }
                }
                composable(FixBarItems.Fix.name) {
                Scaffold {
                    FixUI(innerPadding = innerPaddings,vm1,vm)
                }
                }
                composable(FixBarItems.About.name) {
                    Scaffold {

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
            }
        }
    }
}