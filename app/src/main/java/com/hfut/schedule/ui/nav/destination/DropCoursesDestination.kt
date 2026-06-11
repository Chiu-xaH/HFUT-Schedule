package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.selectCourse.DropCourseScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

data class DropCoursesDestination(
    val courseId : Int,
    val name : String,
) : NavDestination() {
    override val key = "drop_courses_${courseId}_$name"
    override val description = name
    override val title = TITLE
    override val icon = ICON

    companion object {
        val TITLE = res(R.string.navigation_label_drop_courses)
        val ICON = R.drawable.ads_click
    }

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        DropCourseScreen(vm,courseId,name)
    }
}
