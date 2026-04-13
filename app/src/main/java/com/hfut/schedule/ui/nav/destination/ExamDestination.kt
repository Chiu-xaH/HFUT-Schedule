package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam.ExamScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.ui.util.res

data class ExamDestination(
    val origin : String?,
) : NavDestination() {
    override val key = "exam_$origin"
    override val title = TITLE
    override val icon = ICON

    companion object {
        val TITLE = res(R.string.navigation_label_exam)
        val ICON = R.drawable.draw
    }

    @Composable
    override fun Content() {
        ExamScreen(origin)
    }
}