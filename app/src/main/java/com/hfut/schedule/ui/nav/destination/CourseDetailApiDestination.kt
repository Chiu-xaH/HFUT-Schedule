package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.screen.home.calendar.communtiy.CourseDetailApiScreen
import com.xah.common.ui.util.res

data class CourseDetailApiDestination(
    val courseName : String,
    val endKey : String,
    // 如果有code则优先匹配code DetailInfosUI没适配，因为现在社区不会返回课表了
    val courseCode : String? = null,
    // 重新规定Key的拼接，避免出现碰撞：origin_startTime_endTime
    val classroom : String? = null,
) : NavDestination() {
    override val key = "course_detail_api_${endKey}_${courseName}_$courseCode"
    override val description = courseName
    override val title = TITLE
    override val icon = ICON

    companion object {
        val TITLE = res(R.string.navigation_label_course_detail)
        val ICON = R.drawable.category
    }

    @Composable
    override fun Content() {
        CourseDetailApiScreen(courseName, classroom, courseCode)
    }
}