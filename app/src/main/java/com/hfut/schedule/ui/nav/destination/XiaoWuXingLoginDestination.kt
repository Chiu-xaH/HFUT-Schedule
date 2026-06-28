package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.screen.xwx.XwxLoginScreen
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.util.text
import com.xah.navigation.util.LocalNavDependencies

object XiaoWuXingLoginDestination : NavDestination() {
    override val key = "xwx_login"
    override val title = text("校务行-登录")
    override val icon = R.drawable.login

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        XwxLoginScreen(vm)
    }
}