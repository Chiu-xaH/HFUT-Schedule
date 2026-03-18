package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.calendar.communtiy.CourseDetailApiScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

data class CourseApiDetailDestination(
    val courseName : String,
    val id : String,
    val classroom : String?,
) : NavDestination() {
    override val key = "course_api_detail_${courseName}_${id}_$classroom"
    override val title = res(R.string.navigation_label_course_detail)
    override val icon = R.drawable.category

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        CourseDetailApiScreen(courseName, classroom, vm)
    }
}