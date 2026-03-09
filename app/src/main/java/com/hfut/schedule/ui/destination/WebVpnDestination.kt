package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.school.webvpn.WebVpnScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.uicommon.util.language.res

object WebVpnDestination : NavDestination() {
    override val key = "webvpn"
    override val title = res(R.string.navigation_label_webvpn)
    override val icon = R.drawable.vpn_key

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        WebVpnScreen(vm)
    }
}