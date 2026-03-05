package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.school.classroom.ClassroomLessonsScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.uicommon.util.language.res

data class ClassroomLessonsDestination(
    val roomId : Int,
    val name : String
) : NavDestination() {
    override val key = "classroom_course_table"
    override val title = res(R.string.navigation_label_classroom_course_table)

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        ClassroomLessonsScreen(vm,roomId,name)
    }
}