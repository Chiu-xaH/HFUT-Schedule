package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.selectCourse.DropCourseScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.uicommon.util.language.res

data class DropCoursesDestination(
    val index : Int,
    val name : String,
) : NavDestination() {
    override val key = "drop_courses"
    override val title = res(R.string.navigation_label_drop_courses)

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        DropCourseScreen(vm,index,name)
    }
}
