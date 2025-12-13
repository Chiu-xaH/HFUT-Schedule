package com.hfut.schedule.ui.screen.home.search.function.community.appointment

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.status.DevelopingIcon
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.uicommon.style.align.CenterScreen
import com.xah.uicommon.style.color.topBarTransplantColor
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AppointmentScreen(
    vm : NetWorkViewModel,
    navController : NavHostController,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.Appointment.route }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    CustomTransitionScaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        route = route,
        navHostController = navController,
        topBar = {
            Column(
                modifier = Modifier.topBarBlur(hazeState),
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.Appointment.label) },
                    navigationIcon = {
                        TopBarNavigationIcon(
                            navController,
                            route,
                            AppNavRoute.Appointment.icon
                        )
                    }
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .hazeSource(hazeState)
                .fillMaxSize()
        ) {
            CenterScreen { DevelopingIcon() }
        }
    }
}