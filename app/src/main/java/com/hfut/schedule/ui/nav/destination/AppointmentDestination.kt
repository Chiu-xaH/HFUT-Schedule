package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.community.appointment.AppointmentScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.util.res
import com.xah.navigation.util.LocalNavDependencies

object AppointmentDestination : NavDestination() {
    override val key = "appointment"
    override val title = res(R.string.navigation_label_community_appointment)
    override val icon = R.drawable.where_to_vote

    @Composable
    override fun Content() {
        val networkVm = LocalNavDependencies.current.get<NetWorkViewModel>()
        AppointmentScreen(networkVm)
    }
}