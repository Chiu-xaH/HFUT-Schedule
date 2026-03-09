package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.ClassmatesScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.uicommon.util.language.res

object ClassmatesDestination : NavDestination() {
    override val key = "classmates"
    override val title = res(R.string.navigation_label_classmates)
    override val icon = R.drawable.sensor_door

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        ClassmatesScreen(vm)
    }
}