package com.xah.transition.component

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.xah.transition.state.TransitionState
import com.xah.transition.style.transitionBackground
import com.xah.transition.util.TransitionPredictiveBackHandler
import com.xah.transition.util.allRouteStack
import com.xah.transition.util.currentRoute
import com.xah.transition.util.isCurrentRoute
import com.xah.transition.util.isInBottom
import com.xah.transition.util.previousRoute
import kotlinx.coroutines.delay

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.TransitionScaffold(
    animatedContentScope: AnimatedContentScope,
    route: String,
    navHostController : NavHostController,
    modifier: Modifier = containerShare(
        Modifier
            .fillMaxSize()
            .transitionBackground(navHostController, route)
            ,
        animatedContentScope,
        route,
        resize = false
    ),
    topBar: @Composable (() -> Unit) = {},
    bottomBar: @Composable (() -> Unit) = {},
    floatingActionButton: @Composable (() -> Unit) = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor : Color? = null,
    content: @Composable ((PaddingValues) -> Unit)
) {
    var scale by remember { mutableFloatStateOf(1f) }
    TransitionPredictiveBackHandler(navHostController) {
        scale = it
    }

    val speed = TransitionState.curveStyle.speedMs
    // 当从CustomScaffold1向CustomScaffold2时，CustomScaffold2先showSurface=false再true，而CustomScaffold1一直为true
    val isCurrentEntry = navHostController.isCurrentRoute(route)
    val isPreviousEntry = navHostController.previousRoute() == route
    // 当回退时，即从CustomScaffold2回CustomScaffold1时，CustomScaffold2立刻showSurface=false，而CustomScaffold1一直为true
    var show by rememberSaveable(route) { mutableStateOf(false) }

    LaunchedEffect(isCurrentEntry) {
        // 当前页面首次进入时播放动画
        if (isCurrentEntry && !show) {
            show = false
            delay(speed * 1L)
            show = true
        } else if(show) {
            if(navHostController.isInBottom(route)) {
                return@LaunchedEffect
            }
            show = false
        }
    }

    // 回退后恢复上一个页面的显示状态
    LaunchedEffect(isPreviousEntry) {
        if (isPreviousEntry) {
            show = true
        }
    }

    Scaffold(
        containerColor = containerColor ?: if(TransitionState.transplantBackground) Color.Transparent else MaterialTheme.colorScheme.surface,
        modifier = modifier.scale(scale),
        topBar = topBar,
        bottomBar = {
            AnimatedVisibility(
                visible = show,
                enter  = if(speed == 0) fadeIn(tween(durationMillis = 0)) else fadeIn(),
                exit = fadeOut(tween(durationMillis = 0))
            ) {
                bottomBar()
            }
        },
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
    ) { innerPadding ->
        AnimatedVisibility(
            visible = show,
            enter  = if(speed == 0) fadeIn(tween(durationMillis = 0)) else fadeIn(),
            exit = fadeOut(tween(durationMillis = 0))
        ) {
            content(innerPadding)
        }
    }
}
