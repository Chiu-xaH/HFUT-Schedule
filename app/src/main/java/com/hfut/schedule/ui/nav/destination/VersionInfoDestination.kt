package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.screen.welcome.VersionInfoScreen
import com.xah.common.ui.util.res

object VersionInfoDestination : NavDestination() {
    override val key = "version_info"
    override val title = res(R.string.navigation_label_version_info)
    override val icon = R.drawable.info

    @Composable
    override fun Content() {
        VersionInfoScreen()
    }
}