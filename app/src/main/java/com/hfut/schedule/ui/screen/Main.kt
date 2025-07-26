package com.hfut.schedule.ui.screen

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.datetime.getCelebration
import com.hfut.schedule.ui.screen.home.MainScreen
import com.hfut.schedule.ui.screen.login.LoginScreen
import com.hfut.schedule.ui.screen.login.UseAgreementScreen
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.ui.component.screen.Party
import com.hfut.schedule.ui.screen.grade.GradeScreen
import com.hfut.schedule.ui.screen.home.calendar.communtiy.CourseDetailApi
import com.hfut.schedule.ui.screen.home.calendar.communtiy.CourseDetailApiScreen
import com.hfut.schedule.ui.screen.home.search.function.school.admission.AdmissionRegionScreen
import com.hfut.schedule.ui.screen.home.search.function.school.admission.AdmissionScreen
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("NewApi")
@Composable
fun MainHost(
    networkVm : NetWorkViewModel,
    loginVm : LoginViewModel,
    uiVm : UIViewModel,
    login : Boolean,
    isSuccessActivity: Boolean,
    isSuccessActivityWebVpn : Boolean
) {
    val switchUpload by remember { mutableStateOf(prefs.getBoolean("SWITCHUPLOAD",true )) }
    val startActivity by produceState<Boolean>(initialValue = prefs.getBoolean("SWITCHFASTSTART",prefs.getString("TOKEN","")?.isNotEmpty() ?: false)) {
        value = DataStoreManager.firstStart.first()
    }
    val navController = rememberNavController()
    val first by remember { mutableStateOf(if(prefs.getBoolean("canUse",false)) AppNavRoute.Home.route else AppNavRoute.UseAgreement.route) }
    var value by remember { mutableIntStateOf(0) }

    // 初始化网络请求
    if(!isSuccessActivity)
        LaunchedEffect(Unit) {
            launch { AppAnimationManager.updateAnimationSpeed() }
            // 如果进入的是登陆界面 未登录做准备
            if(!(startActivity && login)) {
                //从服务器获取信息
                launch { loginVm.getMyApi() }
                launch { loginVm.getCookie() }
                launch { SharedPrefs.saveString("tip","0") }
                launch {  loginVm.getKey() }
                launch {
                    loginVm.getTicket()
                    val cookie = (loginVm.webVpnTicket.state.value as? UiState.Success)?.data ?: return@launch
                    loginVm.putKey(cookie)
                    val status = (loginVm.status.state.value as? UiState.Success)?.data ?: return@launch
                    if(status) {
                        loginVm.getKeyWebVpn()
                    }
                }
            } else { // 否则进入的是主界面
                //上传用户统计数据
                if(switchUpload && value == 0 && !AppVersion.isPreview() && !AppVersion.isInDebugRunning()) {
                    launch {
                        networkVm.postUser()
                        value++
                    }
                }
            }
        }

    SharedTransitionLayout(
        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
    ) {
        NavHost(
            navController = navController,
            startDestination = first,
            enterTransition = { AppAnimationManager.fadeAnimation.enter },
            exitTransition = { AppAnimationManager.fadeAnimation.exit },
        ) {
            // 主UI
            composable(AppNavRoute.Home.route) {
                val mainUI = @Composable { celebrationText : String? ->
                    if(isSuccessActivity) {
                        MainScreen(
                            vm = networkVm,
                            vm2 = loginVm,
                            vmUI = uiVm,
                            celebrationText = celebrationText,
                            webVpn = isSuccessActivityWebVpn,
                            isLogin = true,
                            navHostTopController = navController,
                            this@SharedTransitionLayout,
                            this@composable
                        )
                    } else if(startActivity && login) {
                        MainScreen(
                            networkVm,
                            loginVm,
                            uiVm,
                            celebrationText,
                            false,
                            false,
                            navHostTopController = navController,
                            this@SharedTransitionLayout,
                            this@composable,
                        )
                    } else LoginScreen(loginVm)
                }
                // 如果庆祝为true则庆祝
                getCelebration().let {
                    Party(show = it.use, timeSecond = it.time) {
                        mainUI(it.str)
                    }
                }
            }
            // 用户协议
            composable(AppNavRoute.UseAgreement.route) {
                Party(timeSecond = 3L) {
                    UseAgreementScreen(navController)
                }
            }
            // 成绩
            composable(
                route = AppNavRoute.Grade.receiveRoute(),
                arguments = getArgs(AppNavRoute.Grade.Args.entries)
            ) { backStackEntry ->
                val ifSaved = backStackEntry.arguments?.getBoolean(AppNavRoute.Grade.Args.IF_SAVED.argName) ?: (AppNavRoute.Grade.Args.IF_SAVED.default as Boolean)
                GradeScreen(
                    ifSaved,
                    networkVm,
                    navController,
                    this@SharedTransitionLayout,
                    this@composable,
                )
            }
            // 招生
            composable(AppNavRoute.Admission.route) {
                AdmissionScreen(
                    networkVm,
                    this@SharedTransitionLayout,
                    this@composable,
                    navController
                )
            }
            // 招生 二级界面
            composable(
                route = AppNavRoute.AdmissionRegionDetail.receiveRoute(),
                arguments = getArgs(AppNavRoute.AdmissionRegionDetail.Args.entries)
            ) { backStackEntry ->
                val index = backStackEntry.arguments?.getInt(AppNavRoute.AdmissionRegionDetail.Args.INDEX.argName) ?: (AppNavRoute.AdmissionRegionDetail.Args.INDEX.default as Int)
                val type = backStackEntry.arguments?.getString(AppNavRoute.AdmissionRegionDetail.Args.TYPE.argName) ?: (AppNavRoute.AdmissionRegionDetail.Args.TYPE.default as String)

                AdmissionRegionScreen(
                    networkVm,
                    this@SharedTransitionLayout,
                    this@composable,
                    navController,
                    type,
                    index,
                )
            }
            // 课程详情
            composable(
                route = AppNavRoute.CourseDetail.receiveRoute(),
                arguments = getArgs(AppNavRoute.CourseDetail.Args.entries)
            ) { backStackEntry ->
                val courseName = backStackEntry.arguments?.getString(AppNavRoute.CourseDetail.Args.NAME.argName) ?: (AppNavRoute.CourseDetail.Args.NAME.default as String)
                val index = backStackEntry.arguments?.getInt(AppNavRoute.CourseDetail.Args.INDEX.argName) ?: (AppNavRoute.CourseDetail.Args.INDEX.default as Int)

                CourseDetailApiScreen(
                    courseName,
                    index,
                    networkVm,
                    this@SharedTransitionLayout,
                    this@composable,
                    navController
                )
            }
        }
    }
}