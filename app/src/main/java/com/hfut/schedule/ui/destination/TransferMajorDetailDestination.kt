package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.TransferDetailScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

data class TransferMajorDetailDestination(
    val name : String,
    val batchId : String,
    val isHidden : Boolean
) : NavDestination() {
    override val key = "transfer_major_detail_${name}_${batchId}_${isHidden}"
    override val title = res(R.string.navigation_label_transfer_major_detail)
    override val icon = R.drawable.compare_arrows

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        TransferDetailScreen(isHidden,batchId,name,vm)
    }
}