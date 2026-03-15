package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.school.hall.OfficeHallScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.common.ui.util.res

object OfficeHallDestination : NavDestination() {
    override val key = "office_hall"
    override val title = res(R.string.navigation_label_office_hall)
    override val icon = R.drawable.person_play

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        OfficeHallScreen(vm)
    }
}