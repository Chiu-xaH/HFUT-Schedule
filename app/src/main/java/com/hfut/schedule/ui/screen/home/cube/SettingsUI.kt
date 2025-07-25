package com.hfut.schedule.ui.screen.home.cube

import com.hfut.schedule.ui.screen.home.cube.sub.CalendarSettingsScreen
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.logic.enumeration.FixBarItems
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
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
import com.hfut.schedule.ui.style.zIndexBlur
import com.hfut.schedule.ui.util.AppAnimationManager
import com.xah.transition.util.isCurrentRoute
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState


@Composable
fun BackButton(onBack : () -> Unit) {
    FilledTonalButton(
        onClick = onBack,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth().padding(APP_HORIZONTAL_DP),
        colors = ButtonDefaults.filledTonalButtonColors()
    ) {
        Text("返回上一级", fontSize = 17.sp)
    }
}
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("SuspiciousIndentation", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsScreen(vm : NetWorkViewModel,
                   ifSaved : Boolean,
                   innerPaddings : PaddingValues,
                   vm1 : LoginViewModel,
                   hazeState: HazeState,
) {
    val navController = rememberNavController()
    Box {
        Scaffold { innerPadding ->
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
                        HomeSettingScreen(navController, innerPaddings,hazeState)
                    }
                }
                composable(Screen.UIScreen.route) {
                    Scaffold(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                        UIScreen(innerPaddings)
                    }
                }
                composable(Screen.APPScreen.route) {
                    Scaffold(containerColor = MaterialTheme.colorScheme.surfaceContainer)  {
                        APPScreen(navController, innerPaddings)
                    }
                }
                composable(Screen.FIxAboutScreen.route) {
                    Scaffold (containerColor = MaterialTheme.colorScheme.surfaceContainer){
                        AboutUI(innerPadding = innerPaddings, vm,true,navController,hazeState)
                    }
                }
                composable(Screen.NetWorkScreen.route) {
                    Scaffold(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                        NetWorkScreen(navController, innerPaddings,ifSaved,hazeState)
                    }
                }
                composable(FixBarItems.Fix.name) {
                    Scaffold(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                        FixUI(innerPadding = innerPaddings,vm1,vm,hazeState)
                    }
                }
                composable("DEBUG") {
                    Scaffold {
                        TEST(innerPaddings)
                    }
                }
                composable(Screen.DownloadScreen.route) {
                    Scaffold(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                        DownloadMLUI(innerPaddings)
                    }
                }
                composable(Screen.DeveloperScreen.route) {
                    Scaffold(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                        DeveloperScreen(vm,innerPaddings)
                    }
                }
                composable(Screen.CalendarScreen.route) {
                    Scaffold(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                        CalendarSettingsScreen(innerPaddings)
                    }
                }
                composable(Screen.LockScreen.route) {
                    Scaffold {
                        LockUI(innerPaddings,hazeState)
                    }
                }
                composable(Screen.FocusCardScreen.route) {
                    Scaffold (containerColor = MaterialTheme.colorScheme.surfaceContainer){
                        FocusCardSettings(innerPaddings)
                    }
                }
                composable(Screen.RequestRangeScreen.route) {
                    Scaffold (containerColor = MaterialTheme.colorScheme.surfaceContainer){
                        RequestArrange(innerPaddings)
                    }
                }
                composable(Screen.PasswordScreen.route) {
                    Scaffold (containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                        EditPasswordScreen(hazeState,innerPaddings)
                    }
                }
            }
        }
    }
}