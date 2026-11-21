package com.hfut.schedule.ui.nav.destination

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hfut.schedule.R
import com.hfut.schedule.network.api.model.response.json.community.CommunityPlacesAppointment
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.status.DevelopingIcon
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.align.CenterScreen
import com.xah.common.ui.util.text
import com.xah.navigation.util.LocalNavDependencies

data class AppointmentPlaceDestination(
    val item : CommunityPlacesAppointment
) : NavDestination() {
    override val key = "${KEY}_${item.id}"
    override val title = text("场地预约")
    override val description = item.name
    override val icon = ICON

    companion object {
        const val KEY = "appointment_place"
        val ICON = R.drawable.meeting_room
    }

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
            CenterScreen {
                DevelopingIcon()
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(APP_HORIZONTAL_DP)
            ) {
                TopBarNavigationIcon()
            }
        }
    }
}