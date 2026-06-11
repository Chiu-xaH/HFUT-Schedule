package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.TotalCourseScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

data class TermCoursesDestination(
    val ifSaved : Boolean,
    val origin : String,
) : NavDestination() {
    override val key = "${KEY}_${ifSaved}_$origin"
    override val title = TITLE
    override val icon = ICON

    companion object {
        const val KEY = "term_courses"
        val TITLE = res(R.string.navigation_label_term_courses)
        val ICON = R.drawable.category
    }

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        TotalCourseScreen(vm,ifSaved)
    }
}