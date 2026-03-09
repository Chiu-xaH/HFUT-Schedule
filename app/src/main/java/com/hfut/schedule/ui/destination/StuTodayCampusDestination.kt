package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.community.library.LibraryScreen
import com.hfut.schedule.ui.screen.home.search.function.school.student.StuTodayCampusScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.uicommon.util.language.res

object StuTodayCampusDestination : NavDestination() {
    override val key = "stu"
    override val title = res(R.string.navigation_label_stu)
    override val icon = R.drawable.handshake

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        StuTodayCampusScreen(vm)
    }
}