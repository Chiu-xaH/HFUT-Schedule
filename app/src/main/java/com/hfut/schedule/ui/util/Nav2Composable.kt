package com.hfut.schedule.ui.util

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.hfut.schedule.ui.util.navigation.canPopBack
import com.xah.navigation.utils.LocalNavControllerSafely
import com.xah.uicommon.util.LogUtil

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.nav2Composable(
    route: String,
    navController : NavHostController,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
    ) {
        Nav2BackHandler(navController)
        this.content(it)
    }
}

@Composable
private fun Nav2BackHandler(navController : NavHostController) {
    val navHostTopController = LocalNavControllerSafely.current ?: return
    BackHandler(navController.canPopBack()) {
        LogUtil.debug("navHostTopController.isTransitioning=${navHostTopController.isTransitioning}")
        if(!navHostTopController.isTransitioning) {
            navController.popBackStack()
        } else {
            navHostTopController.pop()
        }
    }
}

