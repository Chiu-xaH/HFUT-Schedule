package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.ClassmatesScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.text

data class CourseClassmatesScreen(
    val lessonId : Int,
    val courseName : String?,
    val isScheduled : Boolean
) : NavDestination() {
    override val key = "${KEY}_${lessonId}_$courseName"
    override val description = courseName
    override val title = text(TITLE)
    override val icon = ICON

    companion object {
        const val KEY = "course_classmates"
        const val TITLE = "同班同学"
        val ICON = R.drawable.group
    }

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        ClassmatesScreen(vm,lessonId,text("$TITLE-$description"),isScheduled)
    }
}