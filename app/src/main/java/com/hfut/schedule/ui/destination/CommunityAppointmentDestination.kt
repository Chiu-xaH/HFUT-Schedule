package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.community.appointment.AppointmentScreen
import com.hfut.schedule.ui.util.NavDestination
import com.xah.uicommon.util.language.res

object CommunityAppointmentDestination : NavDestination() {
    override val key = "community_appointment"
    override val title = res(R.string.navigation_label_community_appointment)

    @Composable
    override fun Content() {
        AppointmentScreen()
    }
}