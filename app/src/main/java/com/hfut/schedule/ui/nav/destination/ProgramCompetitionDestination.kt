package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.program.ProgramCompetitionScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

data class ProgramCompetitionDestination(
    val ifSaved : Boolean,
) : NavDestination() {
    override val key = "${KEY}_${ifSaved}"
    override val title = TITLE
    override val icon = ICON

    companion object {
        const val KEY = "program_competition"
        val TITLE = res(R.string.navigation_label_program_competition)
        val ICON = R.drawable.leaderboard
    }

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        ProgramCompetitionScreen(vm,ifSaved)
    }
}