package com.hfut.schedule.ui.adaptive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class AppWindowSize {
    Compact,
    Medium,
    Expanded
}

data class AppAdaptiveInfo(
    val windowSize: AppWindowSize,
    val maxWidth: Dp,
    val maxHeight: Dp
) {
    val useNavigationRail: Boolean
        get() = windowSize != AppWindowSize.Compact

    val searchGridMinSize: Dp
        get() = when (windowSize) {
            AppWindowSize.Compact -> 168.dp
            AppWindowSize.Medium -> 184.dp
            AppWindowSize.Expanded -> 204.dp
        }

    val settingsGridMinSize: Dp
        get() = when (windowSize) {
            AppWindowSize.Compact -> 112.dp
            AppWindowSize.Medium -> 136.dp
            AppWindowSize.Expanded -> 156.dp
        }
}

@Composable
fun AppAdaptiveScope(
    content: @Composable (AppAdaptiveInfo) -> Unit
) {
    BoxWithConstraints {
        val windowSize = when {
            maxWidth < 600.dp -> AppWindowSize.Compact
            maxWidth < 840.dp -> AppWindowSize.Medium
            else -> AppWindowSize.Expanded
        }
        content(
            AppAdaptiveInfo(
                windowSize = windowSize,
                maxWidth = maxWidth,
                maxHeight = maxHeight
            )
        )
    }
}

@Composable
fun AdaptivePageContent(
    modifier: Modifier = Modifier,
    maxWidth: Dp = 920.dp,
    content: @Composable () -> Unit
) {
    AppAdaptiveScope { adaptive ->
        Box(modifier = modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (adaptive.windowSize == AppWindowSize.Compact) {
                            Modifier
                        } else {
                            Modifier.widthIn(max = maxWidth)
                        }
                    )
                    .align(Alignment.TopCenter)
            ) {
                content()
            }
        }
    }
}
