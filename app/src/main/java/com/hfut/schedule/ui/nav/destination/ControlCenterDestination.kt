package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.screen.util.ControlCenterScreen
import com.xah.common.ui.util.text

object ControlCenterDestination : NavDestination() {
    override val key = "control_center"
    override val title = text("启动台")
    override val icon = R.drawable.flash_on

    @Composable
    override fun Content() {
        ControlCenterScreen()
    }
}