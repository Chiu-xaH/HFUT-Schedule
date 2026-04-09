package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.jxglstu.ProgramPerformanceDetailItem
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.program.ProgramCompetitionDetailScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

data class ProgramCompetitionDetailDestination(
    val item : ProgramPerformanceDetailItem,
) : NavDestination() {
    override val key = "program_competition_detail_${item.hashCode()}"
    override val title = TITLE
    override val icon = ICON

    companion object {
        val TITLE = res(R.string.navigation_label_program_competition_detail)
        val ICON = R.drawable.leaderboard
    }

    @Composable
    override fun Content() {
        ProgramCompetitionDetailScreen(item)
    }
}