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
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.xah.transition.state.LocalAnimatedContentScope
import com.xah.transition.state.LocalAppNavController
import com.xah.transition.state.LocalSharedTransitionScope
import com.xah.transition.state.NavAction
import com.xah.transition.state.TransitionConfig
import com.xah.transition.style.DefaultTransitionStyle
import com.xah.transition.style.TransitionLevel
import com.xah.transition.util.TransitionInitializer

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TransitionNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier.background(MaterialTheme.colorScheme.surface),
    startDestination: String,
    enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        if(TransitionConfig.transitionBackgroundStyle.level != TransitionLevel.NONE_ALL) {
            fadeIn(animationSpec = tween(durationMillis = TransitionConfig.curveStyle.speedMs),
            // 缺陷 打断动画
//            initialAlpha = if(!TransitionConfig.transplantBackground && TransitionConfig.action == NavAction.Pop) 1f else 0f
            )
        } else {
            fadeIn()
        }
    },
    exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        if(TransitionConfig.transitionBackgroundStyle.level != TransitionLevel.NONE_ALL) {
            fadeOut(
                animationSpec = tween(durationMillis = TransitionConfig.curveStyle.speedMs),
            // 缺陷 打断动画
//                targetAlpha = if(!TransitionConfig.transplantBackground && TransitionConfig.action == NavAction.Push) 1f else 0f
            )
        } else {
            fadeOut()
        }
    },
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
