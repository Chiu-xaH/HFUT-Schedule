package com.hfut.schedule.ui.component.button

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.kyant.backdrop.Backdrop
import com.xah.transition.component.TopBarNavigateIcon
import com.xah.transition.component.awaitTransition
import com.xah.transition.component.iconElementShare
import com.xah.transition.state.LocalSharedTransitionScope
import com.xah.transition.state.TransitionConfig
import com.xah.transition.style.DefaultTransitionStyle
import com.xah.transition.util.popBackStackForTransition
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import kotlinx.coroutines.delay

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TopBarNavigationIcon(
    navController : NavHostController,
    route : String,
    icon : Int,
) = TopBarNavigateIcon(navController,route, painterResource(icon))



@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LiquidTopBarNavigateIcon(
    backdrop: Backdrop,
    navController : NavHostController,
    route : String,
    icon :  Int,
) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    var show by remember { mutableStateOf(true) }
    if(!TransitionConfig.transplantBackground) {
        LaunchedEffect(Unit) {
            show = true
            sharedTransitionScope.awaitTransition()
            delay(1500L)
            show = false
        }
    }


    LiquidButton (
        onClick = { navController.popBackStackForTransition() },
        backdrop = backdrop,
        isCircle = true,
        modifier = Modifier.padding(start = APP_HORIZONTAL_DP-2.5.dp, end = 9.dp)
    ) {
        Box() {
            androidx.compose.animation.AnimatedVisibility(
                visible = show,
                enter = DefaultTransitionStyle.centerAllAnimation.enter,
                exit = DefaultTransitionStyle.centerAllAnimation.exit
            ) {
                Icon(painterResource(icon), contentDescription = null, modifier = Modifier.iconElementShare(route = route))
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = !show,
                enter = DefaultTransitionStyle.centerAllAnimation.enter,
                exit = DefaultTransitionStyle.centerAllAnimation.exit
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        }
    }
}
