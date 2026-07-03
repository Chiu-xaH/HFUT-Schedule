package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.screen.shower.login.ShowerLogin
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.util.text
import com.xah.navigation.util.LocalNavDependencies

object GuaGuaLoginDestination : NavDestination() {
    override val key = "guagua_login"
    override val title = text("呱呱物联-登录")
    override val icon = R.drawable.login

    @Composable
    override fun Content() {
        val networkVm = LocalNavDependencies.current.get<NetWorkViewModel>()
        ShowerLogin(networkVm)
    }
}