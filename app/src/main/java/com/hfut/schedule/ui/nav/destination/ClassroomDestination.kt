package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.school.classroom.ClassroomScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

data class ClassroomDestination(
    val origin : String = "Search"
) : NavDestination() {
    override val key = "${KEY}_$origin"
    override val title = TITLE
    override val icon = ICON

    companion object {
        const val KEY = "classroom"
        val TITLE = res(R.string.navigation_label_classroom)
        val ICON = R.drawable.meeting_room
    }

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        ClassroomScreen(vm)
    }
}