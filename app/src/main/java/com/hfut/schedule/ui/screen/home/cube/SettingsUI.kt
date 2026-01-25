package com.hfut.schedule.ui.screen.home.cube

//import com.hfut.schedule.ui.screen.home.cube.sub.TEST
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.logic.enumeration.FixBarItems
import com.hfut.schedule.ui.screen.fix.about.AboutUI
import com.hfut.schedule.ui.screen.fix.fix.FixUI
import com.hfut.schedule.ui.screen.home.cube.screen.AppSettingsScreen
import com.hfut.schedule.ui.screen.home.cube.screen.NetworkSettingsScreen
import com.hfut.schedule.ui.screen.home.cube.screen.AppearanceSettingsScreen
import com.hfut.schedule.ui.screen.home.cube.sub.BackupScreen
import com.hfut.schedule.ui.screen.home.cube.sub.CalendarSettingsScreen
import com.hfut.schedule.ui.screen.home.cube.sub.DeveloperScreen
import com.hfut.schedule.ui.screen.home.cube.sub.DownloadMLUI
import com.hfut.schedule.ui.screen.home.cube.sub.EditJxglstuPasswordScreen
import com.hfut.schedule.ui.screen.home.cube.sub.EditPasswordScreen
import com.hfut.schedule.ui.screen.home.cube.sub.FocusCardSettings
import com.hfut.schedule.ui.screen.home.cube.sub.FocusWidgetSettingsScreen
import com.hfut.schedule.ui.screen.home.cube.sub.GestureStudyScreen
import com.hfut.schedule.ui.screen.home.cube.sub.LockUI
import com.hfut.schedule.ui.screen.home.cube.sub.TEST
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.uicommon.style.APP_HORIZONTAL_DP
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
@OptIn(ExperimentalSharedTransitionApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("SuspiciousIndentation", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsScreen(vm : NetWorkViewModel,
                   ifSaved : Boolean,
                   innerPaddings : PaddingValues,
                   hazeState: HazeState,
                   navHostTopController : NavController,
) {
    val navController = rememberNavController()
    Scaffold() { _ ->
        NavHost(
            navController = navController,
            startDestination = Screen.HomeScreen.route,
            enterTransition = {
                AppAnimationManager.centerAnimation.enter
            },
            exitTransition = {
                AppAnimationManager.centerAnimation.exit
            },
        ) {

            composable(Screen.HomeScreen.route) {
                Scaffold(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    HomeSettingScreen(navController, innerPaddings,vm,navHostTopController)
                }
            }
            composable(Screen.UIScreen.route) {
                Scaffold(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    AppearanceSettingsScreen(innerPaddings,navController)
                }
            }
            composable(Screen.APPScreen.route) {
                Scaffold(containerColor = MaterialTheme.colorScheme.surfaceContainer)  {
                    AppSettingsScreen(navController, innerPaddings)
                }
            }
            composable(Screen.FIxAboutScreen.route) {
                Scaffold (containerColor = MaterialTheme.colorScheme.surfaceContainer){
                    AboutUI(innerPadding = innerPaddings, vm,true,navController,hazeState)
                }
            }
            composable(Screen.NetWorkScreen.route) {
                Scaffold(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    NetworkSettingsScreen(navController, innerPaddings,ifSaved)
                }
            }
            composable(FixBarItems.Fix.name) {
                Scaffold(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    FixUI(innerPadding = innerPaddings,vm,navController)
                }
            }
            composable(Screen.DebugScreen.route) {
                Scaffold {
                    TEST(vm,innerPaddings,navController)
                }
            }
            composable(Screen.FocusWidgetSettingsScreen.route) {
                Scaffold(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    FocusWidgetSettingsScreen(innerPaddings,navController)
                }
            }
            composable(Screen.DownloadScreen.route) {
                Scaffold(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    DownloadMLUI(innerPaddings,navController)
                }
            }
            composable(Screen.DeveloperScreen.route) {
                Scaffold(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    DeveloperScreen(vm,innerPaddings,navController)
                }
            }
            composable(Screen.CalendarScreen.route) {
                Scaffold(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    CalendarSettingsScreen(innerPaddings,navController)
                }
            }
            composable(Screen.LockScreen.route) {
                Scaffold {
                    LockUI(innerPaddings,hazeState,navController)
                }
            }
            composable(Screen.FocusCardScreen.route) {
                Scaffold (containerColor = MaterialTheme.colorScheme.surfaceContainer){
                    FocusCardSettings(innerPaddings,navController)
                }
            }
            composable(Screen.HuiXinPasswordScreen.route) {
                Scaffold (containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    EditPasswordScreen(hazeState,innerPaddings,navController)
                }
            }
            composable(Screen.JxglstuPasswordScreen.route) {
                Scaffold (containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    EditJxglstuPasswordScreen(innerPaddings,navController)
                }
            }
            composable(Screen.GestureStudyScreen.route) {
                Scaffold (containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    GestureStudyScreen(hazeState,innerPaddings,navController)
                }
            }
            composable(Screen.BackupScreen.route) {
                Scaffold (containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    BackupScreen(innerPaddings,navController)
                }
            }
        }
    }
}