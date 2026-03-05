package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.TotalCourseScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.uicommon.util.language.res

data class TotalCourseDestination(
    val ifSaved : Boolean,
    val origin : String,
) : NavDestination() {
    override val key = "term_courses"
    override val title = res(R.string.navigation_label_term_courses)

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        TotalCourseScreen(vm,origin,ifSaved)
    }
}