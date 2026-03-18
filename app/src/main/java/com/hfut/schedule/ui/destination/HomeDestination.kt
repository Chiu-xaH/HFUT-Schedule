package com.hfut.schedule.ui.destination

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.datetime.Celebration
import com.hfut.schedule.ui.screen.home.MainScreen
import com.hfut.schedule.ui.screen.login.LoginScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.util.res

object HomeDestination : NavDestination() {
    override val key: String = "home"
    override val title: UiText = res(R.string.navigation_label_home)
    override val icon = R.drawable.home

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    override fun Content() {
        val networkVm = LocalNavDependencies.current.get<NetWorkViewModel>()
        val uiVm = LocalNavDependencies.current.get<UIViewModel>()
        val loginVm = LocalNavDependencies.current.get<LoginViewModel>()
        val login = LocalNavDependencies.current.get<Boolean>("login")
        val celebration = LocalNavDependencies.current.get<Celebration>()
        val isSuccessActivity = LocalNavDependencies.current.get<Boolean>("isSuccessActivity")
        val mainUI = @Composable { celebrationText : String? ->
            if(isSuccessActivity) {
                MainScreen(
                    vm = networkVm,
                    vmUI = uiVm,
                    celebrationText = celebrationText,
                    isLogin = true,
                )
            } else if(!login) {
                MainScreen(
                    networkVm,
                    uiVm,
                    celebrationText,
                    false,
                )
            } else LoginScreen(
                loginVm,
                networkVm,
            )
        }
        mainUI(celebration.str)
    }
}