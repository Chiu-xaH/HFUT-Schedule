package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.community.GradeJxglstuResponse
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.GradeDetailScreen
import com.hfut.schedule.ui.util.NavDestination
import com.xah.common.ui.util.res

data class GradeDetailDestination(
    val bean : GradeJxglstuResponse,
    val allAvgGpa : Float,
    val allAvgScore : Float,
    val allTotalCredits : Float
) : NavDestination() {
    override val key = "grade_detail_${bean.lessonCode}"
    override val description = bean.courseName
    override val title = TITLE
    override val icon = ICON

    companion object {
        val TITLE = res(R.string.navigation_label_grade_detail)
        val ICON = R.drawable.article
    }

    @Composable
    override fun Content() {
        GradeDetailScreen(bean,allAvgGpa,allAvgScore,allTotalCredits)
    }
}