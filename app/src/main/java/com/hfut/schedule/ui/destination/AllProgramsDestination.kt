package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.program.ProgramSearchScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.common.ui.util.res

data class AllProgramsDestination(
    val ifSaved : Boolean
) : NavDestination() {
    override val key = "all_programs_$ifSaved"
    override val title = res(R.string.navigation_label_all_programs)
    override val icon = R.drawable.conversion_path

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        ProgramSearchScreen(vm,ifSaved)
    }
}