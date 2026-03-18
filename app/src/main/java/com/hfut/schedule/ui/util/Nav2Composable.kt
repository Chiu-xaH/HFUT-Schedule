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
import com.xah.navigation.util.LocalNavControllerSafely
import com.xah.shared.LogUtil

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.nav2Composable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
    ) {
        Nav2BackHandler()
        this.content(it)
    }
}

@Composable
private fun Nav2BackHandler() {
    val navHostTopController = LocalNavControllerSafely.current ?: return
    BackHandler(navHostTopController.isTransitioning) {
        navHostTopController.pop()
    }
}

