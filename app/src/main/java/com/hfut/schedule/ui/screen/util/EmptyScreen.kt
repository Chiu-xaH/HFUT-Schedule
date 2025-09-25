package com.hfut.schedule.ui.screen.util

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.util.navigateForTransition
import com.xah.transition.state.LocalAnimatedContentScope
import com.xah.transition.state.LocalSharedTransitionScope

// 当转场的目标界面很复杂，用此界面进行中转，减少掉帧
@OptIn(ExperimentalSharedTransitionApi::class,)
@Composable
fun EmptyScreen(
    targetRoute : String,
    navController : NavHostController,
) {
    val route = remember { AppNavRoute.Empty.withArgs(targetRoute) }
    CustomTransitionScaffold(
        route = route,
        navHostController = navController,
    ) { innerPadding ->
        LaunchedEffect(Unit) {
            navController.navigateForTransition(targetRoute,transplantBackground = true)
        }
    }
}

// 当转场的目标界面很复杂，用此界面进行中转，减少掉帧
@OptIn(ExperimentalSharedTransitionApi::class,)
@Composable
fun OpenOuterApplicationScreen(
    app : Starter.AppPackages,
    navController : NavHostController,
) {
    val context = LocalContext.current
    val route = remember { AppNavRoute.OpenOuterApplication.withArgs(app) }
    CustomTransitionScaffold(
        route = route,
        containerColor = app.iconBackgroundColor,
        navHostController = navController,
        roundShape = RoundedCornerShape(7.5.dp)
    ) { innerPadding ->
        LaunchedEffect(Unit) {
            Starter.startAppLaunch(app,context)
        }
    }
}