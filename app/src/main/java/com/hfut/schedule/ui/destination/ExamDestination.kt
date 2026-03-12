package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam.ExamScreen
import com.hfut.schedule.ui.util.NavDestination
import com.xah.uicommon.util.language.res

data class ExamDestination(
    val origin : String?,
) : NavDestination() {
    override val key = "exam_$origin"
    override val title = res(R.string.navigation_label_exam)
    override val icon = R.drawable.draw

    @Composable
    override fun Content() {
        ExamScreen(origin)
    }
}