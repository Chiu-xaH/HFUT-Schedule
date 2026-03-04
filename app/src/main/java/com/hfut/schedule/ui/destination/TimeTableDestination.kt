package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.grade.GradeScreen
import com.hfut.schedule.ui.screen.home.search.function.community.workRest.TimeTableScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.uicommon.util.language.res

data class TimeTableDestination(
    val name : String
) : NavDestination() {
    override val key = "work_and_rest"
    override val title = res(R.string.navigation_label_work_and_rest)

    @Composable
    override fun Content() {
        TimeTableScreen(name)
    }
}