package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.screen.shower.ShowerGuaGua
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.util.res
import com.xah.navigation.util.LocalNavDependencies

object GuaGuaDestination : NavDestination() {
    override val key = "guagua_home"
    override val title = res(R.string.navigation_label_guagua)
    override val icon = R.drawable.shower

    @Composable
    override fun Content() {
        val networkVm = LocalNavDependencies.current.get<NetWorkViewModel>()
        ShowerGuaGua(networkVm)
    }
}