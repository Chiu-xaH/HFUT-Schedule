package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.other.life.LifeScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

data class LifeDestination(
    val inFocus: Boolean
) : NavDestination() {
    override val key = "life_$inFocus"
    override val title = res(R.string.navigation_label_life)
    override val icon = R.drawable.near_me

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        LifeScreen(vm)
    }
}