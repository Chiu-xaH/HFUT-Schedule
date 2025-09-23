package com.xah.transition.component

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.xah.transition.state.LocalAnimatedContentScope
import com.xah.transition.state.LocalAppNavController
import com.xah.transition.state.LocalSharedTransitionScope
import com.xah.transition.state.TransitionState

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TransitionNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier.background(MaterialTheme.colorScheme.surface),
    startDestination: String,
    enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = { fadeIn(animationSpec = tween(durationMillis = TransitionState.curveStyle.speedMs)) },
    exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = { fadeOut(animationSpec = tween(durationMillis = TransitionState.curveStyle.speedMs)) },
    builder: NavGraphBuilder.() -> Unit
) {
    SharedTransitionLayout(
        modifier = modifier
    ) {
        CompositionLocalProvider(
            LocalSharedTransitionScope provides this,
            LocalAppNavController provides navController
        ) {
            NavHost(
                navController = navController,
                startDestination = startDestination,
                enterTransition = enterTransition,
                exitTransition = exitTransition,
                builder = builder
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.transitionComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
    ) {
        CompositionLocalProvider(
            LocalAnimatedContentScope provides this
        ) {
            this.content(it)
        }
    }
}
