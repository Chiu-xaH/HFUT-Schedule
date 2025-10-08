package com.xah.transition.util

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
actual fun TransitionBackHandler(navController : NavHostController, enablePredictive : Boolean, onScale: (Float) -> Unit) = onScale(1f)
