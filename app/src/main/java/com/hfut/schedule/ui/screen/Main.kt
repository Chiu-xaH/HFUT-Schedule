package com.hfut.schedule.ui.screen

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.hfut.schedule.logic.util.sys.queryCalendars
import com.hfut.schedule.ui.screen.home.MainScreen
import com.hfut.schedule.ui.screen.login.MainNav
import com.hfut.schedule.ui.screen.login.LoginScreen
import com.hfut.schedule.ui.screen.login.UseAgreementScreen
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.ui.component.Party
import com.hfut.schedule.ui.screen.grade.GradeScreen
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@SuppressLint("NewApi")
@Composable
fun MainHost(networkVm : NetWorkViewModel, loginVm : LoginViewModel, uiVm : UIViewModel, login : Boolean) {
    val switchUpload by remember { mutableStateOf(prefs.getBoolean("SWITCHUPLOAD",true )) }
    val startActivity by produceState<Boolean>(initialValue = prefs.getBoolean("SWITCHFASTSTART",prefs.getString("TOKEN","")?.isNotEmpty() ?: false)) {
        value = DataStoreManager.firstStart.first()
    }
    val navController = rememberNavController()
    val first by remember { mutableStateOf(if(prefs.getBoolean("canUse",false)) MainNav.HOME.name else MainNav.USE_AGREEMENT.name) }
    var value by remember { mutableIntStateOf(0) }

    // 初始化网络请求
    LaunchedEffect(Unit) {
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
//                launch {
//                    Handler(Looper.getMainLooper()).post{
//                        loginVm.webVpnTicket.observeForever { result ->
//                            if (result != null) {
//                                if (result.contains("wengine_vpn_ticketwebvpn_hfut_edu_cn")) {
//                                    val ticket = result.substringAfter("wengine_vpn_ticketwebvpn_hfut_edu_cn=").substringBefore(";")
//                                    loginVm.putKey(ticket)
//                                }
//                            }
//                        }
//                        loginVm.status.observeForever { result ->
//                            if (result == 200) {
//                                loginVm.getKeyWebVpn()
//                            }
//                        }
//                    }
//                }
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

    NavHost(
        navController = navController,
        startDestination = first,
        enterTransition = { AppAnimationManager.fadeAnimation.enter },
        exitTransition = { AppAnimationManager.fadeAnimation.exit }
    ) {
        // 主UI
        composable(MainNav.HOME.name) {
            val mainUI = @Composable { celebrationText : String? ->
                if(startActivity && login) {
                    MainScreen(networkVm,loginVm,uiVm,celebrationText,false,false,navHostTopController = navController)
                } else LoginScreen(loginVm,navController)
            }
            // 如果庆祝为true则庆祝
            getCelebration().let {
                Party(show = it.use, timeSecond = it.time) {
                    mainUI(it.str)
                }
            }
        }
        // 用户协议
        composable(MainNav.USE_AGREEMENT.name) {
            Party(timeSecond = 3L) {
                UseAgreementScreen(navController)
            }
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