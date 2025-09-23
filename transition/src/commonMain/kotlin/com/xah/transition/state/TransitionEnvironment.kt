package com.xah.transition.state

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = staticCompositionLocalOf<SharedTransitionScope> {
    error("未提供SharedTransitionScope,请确认是否使用了本Library的TransitionNavHost")
}

val LocalAnimatedContentScope = staticCompositionLocalOf<AnimatedContentScope> {
    error("未提供AnimatedContentScope,请确认是否使用了本Library的transitionComposable")
}
// APP的根导航
val LocalAppNavController = staticCompositionLocalOf<NavHostController> {
    error("未提供根NavController,请确认是否使用了本Library的TransitionNavHost,并且传入的是全局唯一根")
}




