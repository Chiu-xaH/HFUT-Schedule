package com.xah.transition.util

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
expect fun TransitionBackHandler(navController : NavHostController, enablePredictive : Boolean, onScale : (Float) -> Unit)