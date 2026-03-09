package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.grade.analysis.AverageGradeScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.uicommon.util.language.res

data class AverageGradeDestination(
    val useUniAppData : Boolean
) : NavDestination() {
    override val key = "average_grade_$useUniAppData"
    override val title = res(R.string.navigation_label_average_grade)
    override val icon = R.drawable.leaderboard

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        AverageGradeScreen(vm,useUniAppData)
    }
}