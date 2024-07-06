package com.hfut.schedule.ui.Activity.success.cube.main

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.ViewModel.LoginViewModel
import com.hfut.schedule.logic.Enums.FixBarItems
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.Activity.Fix.AboutUI
import com.hfut.schedule.ui.Activity.Fix.FixUI
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.APPScreen
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.FixAboutScreen
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.HomeSettingScreen
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.NetWorkScreen
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.Screen
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.UIScreen

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SuspiciousIndentation", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(vm : LoginSuccessViewModel
                   ,showlable : Boolean,
                   showlablechanged: (Boolean) -> Unit,
                   ifSaved : Boolean,
                   innerPaddings : PaddingValues,
                   blur : Boolean,
                   blurchanged : (Boolean) -> Unit,
                   vm1 : LoginViewModel,
) {

    val navController = rememberNavController()

    var animation by remember { mutableStateOf(prefs.getInt("ANIMATION",MyApplication.Animation)) }
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
                    scaleIn(animationSpec = tween(durationMillis = animation)) + expandVertically(expandFrom = Alignment.CenterVertically,animationSpec = tween(durationMillis = animation))
                },
                exitTransition = {
                    scaleOut(animationSpec = tween(durationMillis = animation)) + shrinkVertically(shrinkTowards = Alignment.CenterVertically,animationSpec = tween(durationMillis = animation))
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
                    FixAboutScreen(navController, innerPaddings,vm)
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
                        AboutUI(innerPadding = innerPaddings, vm1)
                    }

                }
            }
        }
    }
}