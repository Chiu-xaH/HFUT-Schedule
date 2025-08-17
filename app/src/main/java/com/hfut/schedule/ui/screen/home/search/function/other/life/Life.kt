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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.text.ScrollText
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.cube.screen.HazeBlurLevel
import com.hfut.schedule.ui.style.InnerPaddingHeight
import com.hfut.schedule.ui.style.topBarBlur
import com.hfut.schedule.ui.style.topBarTransplantColor
import com.hfut.schedule.ui.util.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.component.TopBarNavigateIcon
import com.xah.transition.component.TransitionScaffold
import com.xah.transition.component.iconElementShare
import com.xah.transition.util.navigateAndSaveForTransition
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun Life(
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val route = remember { AppNavRoute.Life.route }

    TransplantListItem(
        headlineContent = { ScrollText(text = AppNavRoute.Life.label) },
        leadingContent = {
            with(sharedTransitionScope) {
                Icon(painterResource(AppNavRoute.Life.icon), contentDescription = null,modifier = iconElementShare(animatedContentScope = animatedContentScope, route = route))
            }
        },
        modifier = Modifier.clickable {
            navController.navigateForTransition(AppNavRoute.Life,route)
        }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LifeScreen(
    vm : NetWorkViewModel,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val hazeState = rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code)
    val route = remember { AppNavRoute.Life.route }

    with(sharedTransitionScope) {
        CustomTransitionScaffold (
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            topBar = {
                TopAppBar(
                    modifier = Modifier.topBarBlur(hazeState, ),
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.Life.label) },
                    navigationIcon = {
                        TopBarNavigateIcon(navController,animatedContentScope,route, AppNavRoute.Life.icon)
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
    }
}