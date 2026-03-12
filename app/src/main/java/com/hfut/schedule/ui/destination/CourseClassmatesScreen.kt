package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.ClassmatesScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.uicommon.util.language.text

data class CourseClassmatesScreen(
    val lessonId : Int,
    val courseName : String,
) : NavDestination() {
    override val key = "course_classmates_${lessonId}_$courseName"
    override val title = text("同班同学-$courseName")
    override val icon = R.drawable.group

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        ClassmatesScreen(vm,lessonId,title)
    }
}