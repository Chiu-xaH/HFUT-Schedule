package com.xah.transition.util

import androidx.activity.BackEventCompat
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.get
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.cancellation.CancellationException

@Composable
fun TransitionPredictiveBackHandler(
    navController : NavHostController,
    onScale: (Float) -> Unit
) {
    PredictiveBackHandler(navController.allRouteStack().size > 1) { progress: Flow<BackEventCompat> ->
        // code for gesture back started
        try {
            progress.collect { backEvent ->
                // code for progress
                onScale(1f - (0.1f * backEvent.progress))
            }
            // code for completion
            navController.popBackStack()
        } catch (e: CancellationException) {
            // code for cancellation
            onScale(1f)
        }
    }
}