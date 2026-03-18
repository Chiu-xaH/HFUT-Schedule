package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.TransferScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

object TransferMajorDestination : NavDestination() {
    override val key = "transfer_major"
    override val title = res(R.string.navigation_label_transfer_major)
    override val icon = R.drawable.compare_arrows

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        TransferScreen(vm)
    }
}