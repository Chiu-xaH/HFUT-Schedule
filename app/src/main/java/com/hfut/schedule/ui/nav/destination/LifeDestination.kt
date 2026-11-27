package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.other.life.LifeBarItems
import com.hfut.schedule.ui.screen.home.search.function.other.life.LifeScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

data class LifeDestination(
    val subStartDestination: LifeBarItems = LifeBarItems.WEATHER
) : NavDestination() {
    override val key = "${KEY}_${subStartDestination}"
    override val title = TITLE
    override val icon = ICON

    companion object {
        const val KEY = "life"
        val TITLE = res(R.string.navigation_label_life)
        val ICON = R.drawable.near_me
    }

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        LifeScreen(vm, subStartDestination)
    }
}