package com.hfut.schedule.ui.component.screen

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.ui.style.transitionBackgroundF
import com.xah.transition.component.TransitionScaffold
import com.xah.transition.component.containerShare
import com.xah.transition.style.transitionBackground

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SharedTransitionScope.CustomTransitionScaffold(
    animatedContentScope: AnimatedContentScope,
    route: String,
    navHostController : NavHostController,
    modifier: Modifier = containerShare(
        Modifier
            .fillMaxSize()
            .transitionBackgroundF(navHostController, route)
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
    enablePredictive : Boolean = true,
    content: @Composable ((PaddingValues) -> Unit)
) {
//    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    val predictive by DataStoreManager.enablePredictive.collectAsState(initial = AppVersion.CAN_PREDICTIVE)
//    val hazeState = rememberHazeState(blurEnabled = blur)
    TransitionScaffold (
        route = route,
        animatedContentScope = animatedContentScope,
        navHostController = navHostController,
        topBar = topBar,
        modifier = modifier,
        enablePredictive = predictive && enablePredictive,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        containerColor = containerColor,
        content = content
    )
}