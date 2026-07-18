package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.network.api.model.response.json.jxglstu.lesson.JxglstuLesson
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.courseSearch.CourseSearchCalendarScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.ui.util.res

data class CourseSearchTableDestination(
    val term : Int?,
    val name : String?,
    val code : String?,
    val classes : String?,
    val list: List<JxglstuLesson>
) : NavDestination() {
    override val key = "course_search_table_${term}_${name}_${code}_$classes"
    override val title = TITLE
    override val icon = ICON

    companion object {
        val TITLE = res(R.string.navigation_label_course_search_table)
        val ICON = R.drawable.calendar
    }

    @Composable
    override fun Content() {
        CourseSearchCalendarScreen(term,name,code,classes,list)
    }
}