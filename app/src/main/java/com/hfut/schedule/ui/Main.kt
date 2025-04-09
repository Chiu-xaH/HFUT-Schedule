package com.hfut.schedule.ui

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.logic.utils.data.SharePrefs
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.logic.utils.getCelebration
import com.hfut.schedule.ui.activity.grade.main.GradeScreen
import com.hfut.schedule.ui.activity.home.main.MainScreen
import com.hfut.schedule.ui.activity.login.MainNav
import com.hfut.schedule.ui.activity.login.LoginScreen
import com.hfut.schedule.ui.activity.login.UseAgreementScreen
import com.hfut.schedule.ui.activity.nologin.GuestMainScreen
import com.hfut.schedule.ui.utils.NavigateAnimationManager
import com.hfut.schedule.ui.utils.components.Party
import com.hfut.schedule.viewmodel.LoginViewModel
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.viewmodel.UIViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@SuppressLint("NewApi")
@Composable
fun MainHost(networkVm : NetWorkViewModel,loginVm : LoginViewModel,uiVm : UIViewModel,login : Boolean) {
    val switchUpload by remember { mutableStateOf(prefs.getBoolean("SWITCHUPLOAD",true )) }
    val startActivity by remember { mutableStateOf(prefs.getBoolean("SWITCHFASTSTART",prefs.getString("TOKEN","")?.isNotEmpty() ?: false)) }
    val navController = rememberNavController()
    val first by remember { mutableStateOf(if(prefs.getBoolean("canUse",false)) MainNav.HOME.name else MainNav.USE_AGREEMENT.name) }
    var value by remember { mutableIntStateOf(0) }

    // 初始化网络请求
    LaunchedEffect(Unit) {
        // 如果进入的是登陆界面 未登录做准备
        if(!(startActivity && login)) {
            //从服务器获取信息
            launch { loginVm.My() }
            launch { loginVm.getCookie() }
            launch { SharePrefs.saveString("tip","0") }
            launch {  loginVm.getKey() }
            launch {
                async { loginVm.getTicket() }.await()
                launch {
                    Handler(Looper.getMainLooper()).post{
                        loginVm.webVpnTicket.observeForever { result ->
                            if (result != null) {
                                if (result.contains("wengine_vpn_ticketwebvpn_hfut_edu_cn")) {
                                    val ticket = result.substringAfter("wengine_vpn_ticketwebvpn_hfut_edu_cn=").substringBefore(";")
                                    loginVm.putKey(ticket)
                                }
                            }
                        }
                        loginVm.status.observeForever { result ->
                            if (result == 200) {
                                loginVm.getKeyWebVpn()
                            }
                        }
                    }
                }
            }
        } else { // 否则进入的是主界面
            //上传用户统计数据
            if(switchUpload && value == 0) {
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
        enterTransition = { NavigateAnimationManager.fadeAnimation.enter },
        exitTransition = { NavigateAnimationManager.fadeAnimation.exit }
    ) {
        // 主UI
        composable(MainNav.HOME.name) {
            val mainUI = @Composable { celebrationText : String? ->
                if(startActivity && login) {
                    MainScreen(networkVm,loginVm,uiVm,celebrationText,false,false)
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
        // 游客模式
        composable(MainNav.GUEST.name) {
            getCelebration().let {
                Party(show = it.use, timeSecond = it.time) {
                    GuestMainScreen(networkVm,loginVm,uiVm, it.str)
                }
            }
        }
    }
}