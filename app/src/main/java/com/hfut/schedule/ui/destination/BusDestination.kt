package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.community.bus.BusScreen
import com.hfut.schedule.ui.screen.home.search.function.community.library.LibraryScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.uicommon.util.language.res

object BusDestination : NavDestination() {
    override val key = "bus"
    override val title = res(R.string.navigation_label_bus)

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        BusScreen(vm)
    }
}