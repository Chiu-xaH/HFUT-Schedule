package com.xah.transition.component

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.xah.transition.R
import com.xah.transition.state.TransitionState
import com.xah.transition.style.DefaultTransitionStyle
import com.xah.transition.util.canPopBack
import kotlinx.coroutines.delay

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.TopBarNavigateIcon(
    navController : NavHostController,
    animatedContentScope: AnimatedContentScope,
    route : String,
    icon : Int,
    restoreIcon : Boolean = true
) {
    val speed = TransitionState.curveStyle.speedMs
    var show by remember { mutableStateOf(true) }
    LaunchedEffect(restoreIcon) {
        show = true
        delay(speed*1L)
        delay(1500L)
        show = false
        if(restoreIcon || TransitionState.transplantBackground) {
            delay(3000L)
            show = true
        }
    }

    IconButton(
        onClick = {
            if(navController.canPopBack()) {
                navController.popBackStack()
            }
        },
    ) {
        Box() {
            AnimatedVisibility(
                visible = show,
                enter = DefaultTransitionStyle.centerAllAnimation.enter,
                exit = DefaultTransitionStyle.centerAllAnimation.exit
            ) {
                Icon(painterResource(icon), contentDescription = null, tint = MaterialTheme.colorScheme.primary,modifier = Modifier.iconElementShare(this@TopBarNavigateIcon,animatedContentScope = animatedContentScope, route = route))
            }
            AnimatedVisibility(
                visible = !show,
                enter = DefaultTransitionStyle.centerAllAnimation.enter,
                exit = DefaultTransitionStyle.centerAllAnimation.exit
            ) {
                Icon(painterResource(R.drawable.arrow_back), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}


@Composable
fun TopBarNavigateIcon(navController : NavController) {
    IconButton(
        onClick = {
            if(navController.canPopBack()) {
                navController.popBackStack()
            }
        },
    ) {
        Icon(painterResource(R.drawable.arrow_back), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
    }
}

