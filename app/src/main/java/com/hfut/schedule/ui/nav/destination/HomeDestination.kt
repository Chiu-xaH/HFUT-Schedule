package com.hfut.schedule.ui.nav.destination

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.BottomBarItems
import com.hfut.schedule.logic.util.sys.datetime.Celebration
import com.hfut.schedule.ui.screen.home.MainScreen
import com.hfut.schedule.ui.screen.login.LoginScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.util.res

data class HomeDestination(
    val subStartDestination: BottomBarItems? = null
) : NavDestination() {
    override val key: String = KEY
    override val title: UiText = res(R.string.navigation_label_home)
    override val icon = R.drawable.home


    companion object {
        const val KEY = "home"
    }

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
                    subStartDestination = subStartDestination
                )
            } else if(!login) {
                MainScreen(
                    vm = networkVm,
                    vmUI = uiVm,
                    celebrationText = celebrationText,
                    isLogin = false,
                    subStartDestination = subStartDestination
                )
            } else LoginScreen(
                loginVm,
                networkVm,
            )
        }
        mainUI(celebration.str)
    }
}