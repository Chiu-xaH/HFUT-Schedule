package com.xah.transition.util

import androidx.activity.BackEventCompat
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.cancellation.CancellationException

private const val targetScale = 0.075f

@Composable
actual fun TransitionPredictiveBackHandler(
    navController : NavHostController,
    enable : Boolean,
    onScale: (Float) -> Unit
) {
    PredictiveBackHandler(navController.allRouteStack().size > 1 && enable) { progress: Flow<BackEventCompat> ->
        // code for gesture back started
        try {
            progress.collect { backEvent ->
                // code for progress
                onScale(1f - (targetScale * backEvent.progress))
            }
            // code for completion
            onScale(0f)
            navController.popBackStackForTransition()
        } catch (e: CancellationException) {
            // code for cancellation
            onScale(1f)
        }
    }
}