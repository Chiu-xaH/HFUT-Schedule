package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.screen.home.cube.UpdateContents
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.util.text
import com.xah.navigation.util.LocalNavDependencies

object UpdateHistoryDestination : NavDestination() {
    override val key = "update_history"
    override val title = text("历史更新日志")
    override val icon = R.drawable.update

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        UpdateContents(vm)
    }
}