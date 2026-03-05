package com.xah.container.overlay

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.xah.common.LogUtil
import com.xah.common.ScreenCornerHelper
import com.xah.container.anim.QuadraticBezierRectInterpolator
import com.xah.container.controller.SharedContainerRegistry
import com.xah.container.utils.LocalSharedContainerRegistry


@Composable
fun SharedContainerRoot(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        ScreenCornerHelper(view)
    }

    val registry = remember { SharedContainerRegistry(scope) }

    val screenHeightPx = with(density) {
        configuration.screenHeightDp.dp.toPx()
    }
    val screenWidthPx = with(density) {
        configuration.screenWidthDp.dp.toPx()
    }

    registry.rectInterpolator = QuadraticBezierRectInterpolator(screenHeightPx,screenWidthPx)

    CompositionLocalProvider(
        LocalSharedContainerRegistry provides registry
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // 界面
            content()
            // Overlay 永远在界面下面
            SharedContainerOverlay()
        }
    }
}