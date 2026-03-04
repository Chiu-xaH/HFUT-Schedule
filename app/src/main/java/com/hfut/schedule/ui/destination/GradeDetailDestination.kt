package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.community.GradeJxglstuResponse
import com.hfut.schedule.ui.screen.grade.GradeScreen
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.GradeDetailScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.uicommon.util.language.res

data class GradeDetailDestination(
    val bean : GradeJxglstuResponse,
    val allAvgScore : Float,
    val allAvgGpa : Float,
    val allTotalCredits : Float
) : NavDestination() {
    override val key = "grade_detail"
    override val title = res(R.string.navigation_label_grade_detail)

    @Composable
    override fun Content() {
        GradeDetailScreen(bean,allAvgGpa,allAvgScore,allTotalCredits)
    }
}