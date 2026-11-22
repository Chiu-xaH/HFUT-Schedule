package com.hfut.schedule.ui.nav.destination

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.hfut.schedule.R
import com.hfut.schedule.network.api.model.response.json.community.CommunityPlacesAppointment
import com.hfut.schedule.network.api.model.response.json.community.CommunityPlacesAppointmentTimeFrame
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.status.DevelopingIcon
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.style.special.rememberHazeBlur
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.style.align.CenterScreen
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.util.text
import com.xah.navigation.util.LocalNavDependencies
import dev.chrisbanes.haze.hazeSource

data class AppointmentPlaceDestination(
    val item : CommunityPlacesAppointment,
    val timeFrame: CommunityPlacesAppointmentTimeFrame? = null
) : NavDestination() {
    override val key = "${KEY}_${item.id}_${timeFrame?.timeFrame}"
    override val title = text("场地预约")
    override val description = item.name
    override val icon = ICON

    companion object {
        const val KEY = "appointment_place"
        val ICON = R.drawable.meeting_room
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val hazeState = rememberHazeBlur()

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    modifier = Modifier.topBarBlur(hazeState),
                    scrollBehavior = scrollBehavior,
                    title = { Text(description) },
                    colors = topBarTransplantColor(),
                    navigationIcon = {
                        TopBarNavigationIcon()
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .hazeSource(hazeState)
            ) {
                CenterScreen {
                    DevelopingIcon()
                }
            }
        }
    }
}