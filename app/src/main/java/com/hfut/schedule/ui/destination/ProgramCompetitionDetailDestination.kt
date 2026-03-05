package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.program.ProgramCompetitionDetailScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.uicommon.util.language.res

data class ProgramCompetitionDetailDestination(
    val index : Int,
    val name : String
) : NavDestination() {
    override val key = "program_competition_detail"
    override val title = res(R.string.navigation_label_program_competition_detail)

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        ProgramCompetitionDetailScreen(vm,name,index)
    }
}