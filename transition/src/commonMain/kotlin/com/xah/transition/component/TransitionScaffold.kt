package com.xah.transition.component

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalSharedTransitionApi::class)
suspend fun SharedTransitionScope.awaitTransition() {
    snapshotFlow { this.isTransitionActive }
        .filter { active -> !active }
        .first()
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TransitionScaffold(
    roundShape : Shape = MaterialTheme.shapes.small,
    route: String,
    navHostController : NavHostController,
    modifier: Modifier = Modifier,
    topBar: @Composable (() -> Unit) = {},
    bottomBar: @Composable (() -> Unit) = {},
    floatingActionButton: @Composable (() -> Unit) = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor : Color? = null,
    enablePredictive : Boolean = true,
    backHandler : @Composable ((Float) -> Unit)? = null, // 自定义返回 将覆盖原有逻辑 可传入预测式
    content: @Composable ((PaddingValues) -> Unit)
) {
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
}

