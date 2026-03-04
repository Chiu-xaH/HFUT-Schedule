package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.grade.GradeScreen
import com.hfut.schedule.ui.screen.home.search.function.one.emptyRoom.ClassroomDetailScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.uicommon.util.language.res

data class ClassroomDetailDestination(
    val code : String,
    val name : String,
) : NavDestination() {
    override val key = "classroom_detail"
    override val title = res(R.string.navigation_label_classroom_detail)

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        ClassroomDetailScreen(vm,code,name)
    }
}
