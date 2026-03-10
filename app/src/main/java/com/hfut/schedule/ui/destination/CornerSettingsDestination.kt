package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.cube.sub.CornerSettingsScreen
import com.hfut.schedule.ui.util.NavDestination
import com.xah.uicommon.util.language.res
import com.xah.uicommon.util.language.text

data object CornerSettingsDestination : NavDestination() {

    override val title = res(R.string.navigation_label_settings_corner)
    override val key = "settings_corner"
    override val icon = R.drawable.rounded_corner

    @Composable
    override fun Content() {
        CornerSettingsScreen()
    }
}