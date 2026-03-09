package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.courseSearch.CourseSearchCalendarScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.uicommon.util.language.res

data class CourseSearchTableDestination(
    val term : Int,
    val name : String?,
    val code : String?,
    val classes : String?
) : NavDestination() {
    override val key = "course_search_table_${term}_${name}_${code}_$classes"
    override val title = res(R.string.navigation_label_course_search_table)
    override val icon = R.drawable.calendar

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        CourseSearchCalendarScreen(term,name,code,classes,vm)
    }
}