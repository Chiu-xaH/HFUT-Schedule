package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.school.classroom.ClassroomLessonsScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.util.res
import com.xah.navigation.util.LocalNavDependencies

data class ClassroomCourseTableDestination(
    val roomId : Int,
    val name : String
) : NavDestination() {
    override val key = "classroom_course_table_${roomId}_$name"
    override val description = name
    override val title = TITLE
    override val icon = ICON

    companion object {
        val TITLE = res(R.string.navigation_label_classroom_course_table)
        val ICON = R.drawable.meeting_room
    }

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        ClassroomLessonsScreen(vm,roomId,name)
    }
}