package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.program.ProgramConfirmationScreen
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.util.res
import com.xah.navigation.util.LocalNavDependencies

object ProgramConfirmationDestination : NavDestination() {
    override val key = "program_confirmation"
    override val title = res(R.string.navigation_label_program_confirmation)
    override val icon = R.drawable.check_circle

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        ProgramConfirmationScreen(vm)
    }
}