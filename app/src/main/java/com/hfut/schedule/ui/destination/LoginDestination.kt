package com.hfut.schedule.ui.destination

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.login.LoginScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.uicommon.util.language.UiText
import com.xah.uicommon.util.language.res

object LoginDestination : NavDestination() {
    override val key: String = "login"
    override val title: UiText = res(R.string.navigation_label_login)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        val loginVm = LocalNavDependencies.current.get<LoginViewModel>()
        LoginScreen(loginVm,vm)
    }
}