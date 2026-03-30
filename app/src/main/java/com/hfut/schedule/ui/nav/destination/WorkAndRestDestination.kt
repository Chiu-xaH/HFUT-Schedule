package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.community.workRest.TimeTableScreen
import com.hfut.schedule.ui.util.NavDestination
import com.xah.common.ui.util.res

data class WorkAndRestDestination(
    val name : String?
) : NavDestination() {
    override val key = "work_and_rest_$name"
    override val title = TITLE
    override val icon = ICON

    companion object {
        val TITLE = res(R.string.navigation_label_work_and_rest)
        val ICON = R.drawable.schedule
    }

    @Composable
    override fun Content() {
        TimeTableScreen(name)
    }
}