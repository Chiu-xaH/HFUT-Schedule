package com.xah.transition.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.xah.transition.state.LocalSharedTransitionScope
import com.xah.transition.state.TransitionConfig
import com.xah.transition.style.DefaultTransitionStyle
import com.xah.transition.style.TransitionLevel
import com.xah.transition.util.popBackStackForTransition
import kotlinx.coroutines.delay

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TopBarNavigateIcon(
    navController : NavHostController,
    route : String,
    icon :  Painter,
    restoreIcon : Boolean = TransitionConfig.transitionBackgroundStyle.level == TransitionLevel.NONE_ALL
) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    var show by remember { mutableStateOf(true) }
    if(!TransitionConfig.transplantBackground) {
        LaunchedEffect(Unit) {
            show = true
            sharedTransitionScope.awaitTransition()
            delay(1500L)
            show = false
            if(restoreIcon) {
                delay(3000L)
                show = true
            }
        }
    }


    IconButton(onClick = {
        navController.popBackStackForTransition()
    }) {
        Box() {
            AnimatedVisibility(
                visible = show,
                enter = DefaultTransitionStyle.centerAllAnimation.enter,
                exit = DefaultTransitionStyle.centerAllAnimation.exit
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary,modifier = Modifier.iconElementShare(route = route))
            }
            AnimatedVisibility(
                visible = !show,
                enter = DefaultTransitionStyle.centerAllAnimation.enter,
                exit = DefaultTransitionStyle.centerAllAnimation.exit
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}



@Composable
fun TopBarNavigateIcon(navController : NavController) {
    IconButton(onClick = {
        navController.popBackStackForTransition()
    }) {
        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
    }
}

