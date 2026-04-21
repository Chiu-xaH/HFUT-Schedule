package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.MyApply
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.util.res
import com.xah.navigation.util.LocalNavDependencies


data class TransferMajorAppliedDetailDestination(
    val batchId : String,
    val index : Int,
) : NavDestination() {
    override val key = "transfer_major_applied_detail_${index}"
    override val title = TITLE
    override val icon = ICON

    companion object {
        val TITLE = res(R.string.navigation_label_transfer_major_applied_detail)
        val ICON = R.drawable.check_circle
    }

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        MyApply(vm,batchId,index)
    }
}