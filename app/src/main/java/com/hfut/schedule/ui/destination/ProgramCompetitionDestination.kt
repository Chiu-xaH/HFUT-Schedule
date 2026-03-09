package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.program.ProgramCompetitionScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.uicommon.util.language.res

data class ProgramCompetitionDestination(
    val ifSaved : Boolean
) : NavDestination() {
    override val key = "program_competition_$ifSaved"
    override val title = res(R.string.navigation_label_program_competition)
    override val icon = R.drawable.leaderboard

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        ProgramCompetitionScreen(vm,ifSaved)
    }
}