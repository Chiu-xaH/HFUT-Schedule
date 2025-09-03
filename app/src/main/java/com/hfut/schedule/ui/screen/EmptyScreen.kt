package com.hfut.schedule.ui.screen

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.util.navigateForTransition

// 当转场的目标界面很复杂，用此界面进行中转，减少掉帧
@OptIn(ExperimentalSharedTransitionApi::class,)
@Composable
fun EmptyScreen(
    targetRoute : String,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val route = remember { AppNavRoute.Empty.withArgs(targetRoute) }
    with(sharedTransitionScope) {
        CustomTransitionScaffold(
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
        ) { innerPadding ->
            navController.navigateForTransition(targetRoute,transplantBackground = true)
        }
    }
}