package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.grade.GradeScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.common.ui.util.res

data class GradeDestination(
    val ifSaved : Boolean
) : NavDestination() {
    override val key = "grade_$ifSaved"
    override val title = res(R.string.navigation_label_grade)
    override val icon = R.drawable.article

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        GradeScreen(ifSaved,vm)
    }
}