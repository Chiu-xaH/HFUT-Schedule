package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.NotificationBoxScreen
import com.hfut.schedule.ui.util.NavDestination
import com.xah.uicommon.util.language.res

object NotificationBoxDestination : NavDestination() {
    override val key = "notification_box"
    override val title = res(R.string.navigation_label_notification_box)

    @Composable
    override fun Content() {
        NotificationBoxScreen()
    }
}