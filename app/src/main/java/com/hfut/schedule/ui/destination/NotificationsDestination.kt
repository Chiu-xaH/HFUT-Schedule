package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.my.notification.NotificationsScreen
import com.hfut.schedule.ui.util.NavDestination
import com.xah.uicommon.util.language.res

object NotificationsDestination : NavDestination() {
    override val key = "notifications"
    override val title = res(R.string.navigation_label_notifications)

    @Composable
    override fun Content() {
        NotificationsScreen()
    }
}