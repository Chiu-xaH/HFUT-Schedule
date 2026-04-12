package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.grade.analysis.AverageGradeScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

data class AverageGradeDestination(
    val useUniAppData : Boolean
) : NavDestination() {
    override val key = "average_grade_$useUniAppData"
    override val description = if(useUniAppData) "合工大教务数据源" else "教务系统数据源"
    override val title = TITLE
    override val icon = ICON

    companion object {
        val TITLE = res(R.string.navigation_label_average_grade)
        val ICON = R.drawable.leaderboard
    }

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        AverageGradeScreen(vm,useUniAppData)
    }
}