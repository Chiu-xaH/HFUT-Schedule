package com.hfut.schedule.ui.screen.welcome

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.hfut.schedule.ui.component.button.BUTTON_END_PADDING
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.nav.destination.UpdateHistoryDestination
import com.hfut.schedule.ui.nav.destination.VersionInfoDestination
import com.hfut.schedule.ui.screen.home.cube.sub.VersionInfo
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.rememberHazeBlur
import com.hfut.schedule.ui.style.special.topBarBlur
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.navigation.util.LocalNavController
import dev.chrisbanes.haze.hazeSource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun VersionInfoScreen() {
    val navController = LocalNavController.current
    val hazeState = rememberHazeBlur()
    val backDrop = rememberLayerBackdrop()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                modifier = Modifier.topBarBlur(hazeState, ),
                colors = topBarTransplantColor(),
                title = { Text(VersionInfoDestination.title.asString()) },
                navigationIcon = {
                    TopBarNavigationIcon()
                },
                actions = {
                    LiquidButton (
                        onClick = { navController.push(UpdateHistoryDestination) },
                        modifier = Modifier.padding(end = BUTTON_END_PADDING),
                        backdrop = backDrop
                    ) {
                        Text("历史更新日志")
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .backDropSource(backDrop)
                .hazeSource(hazeState)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            InnerPaddingHeight(innerPadding,true)
            VersionInfo()
            InnerPaddingHeight(innerPadding,false)
        }
    }
}