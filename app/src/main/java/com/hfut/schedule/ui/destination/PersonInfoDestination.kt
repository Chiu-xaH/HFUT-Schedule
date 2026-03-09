package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.PersonScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.uicommon.util.language.res

object PersonInfoDestination : NavDestination() {
    override val key = "person_info"
    override val title = res(R.string.navigation_label_person_info)
    override val icon = R.drawable.person

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        PersonScreen(vm)
    }
}