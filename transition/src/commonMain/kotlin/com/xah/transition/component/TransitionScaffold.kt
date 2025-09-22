package com.xah.transition.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.navigation.NavHostController
import com.xah.transition.state.LocalSharedTransitionScope
import com.xah.transition.state.TransitionState
import com.xah.transition.style.transitionBackground
import com.xah.transition.util.TransitionPredictiveBackHandler
import com.xah.transition.util.isCurrentRouteWithoutArgs
import com.xah.transition.util.isInBottom
import com.xah.transition.util.previousRouteWithArgWithoutValues
import kotlinx.coroutines.delay

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TransitionScaffold(
    roundShape : Shape = MaterialTheme.shapes.medium,
    route: String,
    navHostController : NavHostController,
    modifier: Modifier = Modifier.clip(roundShape)
        .transitionBackground(navHostController, route).containerShare(
        route,
        roundShape,
    ),
    topBar: @Composable (() -> Unit) = {},
    bottomBar: @Composable (() -> Unit) = {},
    floatingActionButton: @Composable (() -> Unit) = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor : Color? = null,
    enablePredictive : Boolean = true,
    content: @Composable ((PaddingValues) -> Unit)
) {
    if(route in TransitionState.firstStartRoute) {
        // 首页 无需进行延迟显示
        Scaffold(
            modifier = modifier,
            containerColor = containerColor ?:  MaterialTheme.colorScheme.surface,
            topBar = topBar,
            bottomBar = bottomBar,
            floatingActionButton = floatingActionButton,
            floatingActionButtonPosition = floatingActionButtonPosition,
        ) { innerPadding ->
            content(innerPadding)
        }
        return
    }

    var scale by remember { mutableStateOf(1f) }

    val speed = TransitionState.curveStyle.speedMs
    // 当从CustomScaffold1向CustomScaffold2时，CustomScaffold2先showSurface=false再true，而CustomScaffold1一直为true
    val isCurrentEntry = navHostController.isCurrentRouteWithoutArgs(route)
    val isPreviousEntry = navHostController.previousRouteWithArgWithoutValues() == route
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

    var useBackHandler by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if(useBackHandler == false) {
            delay(speed*1L)
            useBackHandler = true
        }
    }

    TransitionPredictiveBackHandler(navHostController,useBackHandler && enablePredictive) {
        scale = it
    }

    // 回退后恢复上一个页面的显示状态
    LaunchedEffect(isPreviousEntry) {
        if (isPreviousEntry) {
            show = true
        }
    }
    val targetColor =  containerColor ?: if(TransitionState.transplantBackground) Color.Transparent else MaterialTheme.colorScheme.surface

    Scaffold(
        containerColor =  targetColor,
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TransitionSurface(
    roundShape : Shape = MaterialTheme.shapes.medium,
    route: String,
    navHostController : NavHostController,
    modifier: Modifier = Modifier.clip(roundShape)
        .transitionBackground(navHostController, route).containerShare(
            route,
            roundShape,
        ),
    containerColor : Color? = null,
    enablePredictive : Boolean = true,
    content: @Composable (() -> Unit)
) {
    if(route in TransitionState.firstStartRoute) {
        // 首页 无需进行延迟显示
        Surface(
            modifier = modifier,
            color = containerColor ?: Color.Transparent,
            content = content
        )
        return
    }

    var scale by remember { mutableStateOf(1f) }

    val speed = TransitionState.curveStyle.speedMs
    // 当从CustomScaffold1向CustomScaffold2时，CustomScaffold2先showSurface=false再true，而CustomScaffold1一直为true
    val isCurrentEntry = navHostController.isCurrentRouteWithoutArgs(route)
    val isPreviousEntry = navHostController.previousRouteWithArgWithoutValues() == route
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

    var useBackHandler by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if(useBackHandler == false) {
            delay(speed*1L)
            useBackHandler = true
        }
    }

    TransitionPredictiveBackHandler(navHostController,useBackHandler && enablePredictive) {
        scale = it
    }

    // 回退后恢复上一个页面的显示状态
    LaunchedEffect(isPreviousEntry) {
        if (isPreviousEntry) {
            show = true
        }
    }
    val targetColor =  containerColor ?: if(TransitionState.transplantBackground) Color.Transparent else MaterialTheme.colorScheme.surface

    Surface(
        color =  targetColor,
        modifier = modifier.scale(scale),
    ) {
        AnimatedVisibility(
            visible = show,
            enter  = if(speed == 0) fadeIn(tween(durationMillis = 0)) else fadeIn(),
            exit = fadeOut(tween(durationMillis = 0))
        ) {
            content()
        }
    }
}
