package com.hfut.schedule.ui.screen.home.search.function.other.life

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.xah.uicommon.component.text.ScrollText
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.ui.style.special.topBarBlur
import com.xah.uicommon.style.color.topBarTransplantColor
import com.hfut.schedule.ui.util.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.xah.transition.component.iconElementShare
import com.xah.transition.state.LocalAnimatedContentScope
import com.xah.transition.state.LocalSharedTransitionScope
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun Life(
    navController : NavHostController,
) {
    val route = remember { AppNavRoute.Life.withArgs(false) }

    TransplantListItem(
        headlineContent = { ScrollText(text = AppNavRoute.Life.label) },
        leadingContent = {
            Icon(painterResource(AppNavRoute.Life.icon), contentDescription = null,modifier = Modifier.iconElementShare(route = route))
        },
        modifier = Modifier.clickable {
            navController.navigateForTransition(AppNavRoute.Life,route)
        }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LifeScreen(
    inFocus : Boolean,
    vm : NetWorkViewModel,
    navController : NavHostController,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.Life.withArgs(inFocus) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

        CustomTransitionScaffold (
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            route = route,
            navHostController = navController,
            topBar = {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.topBarBlur(hazeState, ),
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.Life.label) },
                    navigationIcon = {
                        TopBarNavigationIcon(navController,route, AppNavRoute.Life.icon)
                    },
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.hazeSource(hazeState)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                InnerPaddingHeight(innerPadding,true)
                LifeScreenMini(vm)
                InnerPaddingHeight(innerPadding,false)
            }
        }
//    }
}