package com.hfut.schedule.ui.Activity.success.cube.main

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.ViewModel.LoginViewModel
import com.hfut.schedule.logic.Enums.FixBarItems
import com.hfut.schedule.ui.Activity.Fix.AboutUI
import com.hfut.schedule.ui.Activity.Fix.FixUI
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.APPScreen
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.FixAboutScreen
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.HomeSettingScreen
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.NetWorkScreen
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.Screen
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.UIScreen

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(vm : LoginSuccessViewModel
                   ,showlable : Boolean,
                   showlablechanged: (Boolean) -> Unit,
                   ifSaved : Boolean,
                   innerPaddings : PaddingValues,
                   blur : Boolean,
                   blurchanged : (Boolean) -> Unit,
                   vm1 : LoginViewModel
) {

    val navController = rememberNavController()

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
            NavHost(navController = navController, startDestination = Screen.HomeScreen.route) {
                composable(Screen.HomeScreen.route) { HomeSettingScreen(navController,vm, showlable, showlablechanged, ifSaved, innerPaddings, blur, blurchanged) }
                composable(Screen.UIScreen.route) { UIScreen(navController, innerPaddings,showlable, showlablechanged,blur, blurchanged) }
                composable(Screen.APPScreen.route) { APPScreen(navController, innerPaddings,ifSaved) }
                composable(Screen.FIxAboutScreen.route) { FixAboutScreen(navController, innerPaddings) }
                composable(Screen.NetWorkScreen.route) { NetWorkScreen(navController, innerPaddings,ifSaved) }
                composable(FixBarItems.Fix.name) { FixUI(innerPadding = innerPaddings,vm1) }
                composable(FixBarItems.About.name) { AboutUI(innerPadding = innerPaddings, vm1) }
            }
        }
    }
}