package com.hfut.schedule.ui.component.screen

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.util.GlobalUIStateHolder
import com.xah.transition.component.TransitionScaffold
import com.xah.transition.component.containerShare
import com.xah.transition.state.TransitionConfig
import com.xah.transition.style.TransitionLevel
import com.xah.transition.style.transitionBackground
import com.xah.transition.style.transitionSkip
import com.xah.transition.util.isCurrentRouteWithoutArgs
import kotlinx.coroutines.delay

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CustomTransitionScaffold(
    modifier: Modifier = Modifier,
    roundShape : Shape = MaterialTheme.shapes.small,
    route: String,
    navHostController : NavHostController,
    topBar: @Composable (() -> Unit) = {},
    bottomBar: @Composable (() -> Unit) = {},
    floatingActionButton: @Composable (() -> Unit) = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor : Color? = null,
    enablePredictive : Boolean = true,
    backHandler : @Composable ((Float) -> Unit)? = null, // 自定义返回 将覆盖原有逻辑 可传入预测式
    content: @Composable ((PaddingValues) -> Unit)
) {
    val predictive by DataStoreManager.enablePredictive.collectAsState(initial = AppVersion.CAN_PREDICTIVE)
    TransitionScaffold (
        route = route,
        roundShape = roundShape,
        navHostController = navHostController,
        topBar = topBar,
        modifier = modifier.transitionBackgroundCustom(navHostController, route).containerShare(
            route,
            roundShape = roundShape,
        ),
        enablePredictive = predictive && enablePredictive,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        containerColor = containerColor,
        content = content,
        backHandler = backHandler
    )
}

@Composable
private fun Modifier.transitionBackgroundCustom(
    navHostController: NavHostController,
    route : String,
) : Modifier = transitionSkip(transitionBackgroundC(navHostController,route))

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun Modifier.transitionBackgroundC(
    navHostController: NavHostController,
    route : String,
) : Modifier = with(TransitionConfig.transitionBackgroundStyle) {
    val isExpanded = !navHostController.isCurrentRouteWithoutArgs(route)

    if(level.code >= TransitionLevel.MEDIUM.code) {
        LaunchedEffect(isExpanded) {
            GlobalUIStateHolder.isTransiting = true
            delay(TransitionConfig.curveStyle.speedMs*2L)
            GlobalUIStateHolder.isTransiting = false
        }
    }

    return transitionBackground(navHostController,route)
}