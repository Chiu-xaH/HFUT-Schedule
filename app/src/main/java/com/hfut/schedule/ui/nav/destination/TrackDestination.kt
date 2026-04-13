package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.other.TrackScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.util.res
import com.xah.navigation.util.LocalNavDependencies

object TrackDestination : NavDestination() {
    override val key = "track"
    override val title = res(R.string.navigation_label_track)
    override val icon = R.drawable.target

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        TrackScreen(vm)
    }
}