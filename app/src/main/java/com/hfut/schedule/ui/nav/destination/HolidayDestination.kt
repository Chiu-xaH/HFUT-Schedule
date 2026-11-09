package com.hfut.schedule.ui.nav.destination

import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.my.holiday.HolidayScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.xah.common.ui.util.res

object HolidayDestination : NavDestination() {
    override val key = "holiday"
    override val title = res(R.string.navigation_label_holiday)
    override val icon = R.drawable.beach_access
    override val orientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

    @Composable
    override fun Content() {
        HolidayScreen()
    }
}