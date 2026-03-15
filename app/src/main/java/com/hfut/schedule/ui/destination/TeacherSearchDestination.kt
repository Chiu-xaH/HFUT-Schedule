package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.school.teacherSearch.TeacherSearchScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.common.ui.util.res

object TeacherSearchDestination : NavDestination() {
    override val key = "teacher_search"
    override val title = res(R.string.navigation_label_teacher_search)
    override val icon = R.drawable.group_search

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        TeacherSearchScreen(vm)
    }
}