package com.hfut.schedule.ui.util

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.xah.floating.component.FloatingBackHandler
import com.xah.floating.util.LocalFloatingControllerSafely
import com.xah.navigation.component.NavigationBackHandler
import com.xah.navigation.util.LocalNavControllerSafely

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
        SharedNavBackHandler()
//        DefaultBackHandler()
        this.content(it)
    }
}

@Composable
private fun SharedNavBackHandler() {
    val floatingController = LocalFloatingControllerSafely.current
    val navController = LocalNavControllerSafely.current

    if(navController?.isTransitioning == true) {
        NavigationBackHandler()
    }
    if(floatingController?.isRunning == true) {
        FloatingBackHandler()
    }
}


