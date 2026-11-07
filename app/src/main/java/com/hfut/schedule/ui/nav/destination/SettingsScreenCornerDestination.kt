package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.cube.sub.CornerSettingsScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.ui.util.res

data object SettingsScreenCornerDestination : NavDestination() {

    override val title = res(R.string.navigation_label_settings_corner)
    override val key = "settings_corner"
    override val icon = R.drawable.rounded_corner

    @Composable
    override fun Content() {
        CornerSettingsScreen()
    }
}