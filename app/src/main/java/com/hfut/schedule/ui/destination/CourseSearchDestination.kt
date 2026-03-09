package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.courseSearch.CourseSearchScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.uicommon.util.language.res

object CourseSearchDestination : NavDestination() {
    override val key = "course_search"
    override val title = res(R.string.navigation_label_course_search)
    override val icon = R.drawable.search

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        CourseSearchScreen(vm)
    }
}