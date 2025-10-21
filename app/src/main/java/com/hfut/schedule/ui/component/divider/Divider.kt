package com.hfut.schedule.ui.component.divider

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun PaddingHorizontalDivider(
    startPadding : Boolean = true,
    endPadding : Boolean = true,
    isDashed : Boolean = false,
    color : Color = if(isDashed) MaterialTheme.colorScheme.outline.copy(.5f) else defaultDividerColor(),
) = if(isDashed) {
    Box(
        modifier = Modifier.padding(
            start = if(startPadding) APP_HORIZONTAL_DP else 0.dp,
            end = if(endPadding) APP_HORIZONTAL_DP else 0.dp
        )
    ) {
        DashedDivider(color = color)
    }

} else {
    HorizontalDivider(
        modifier = Modifier.padding(
            start = if(startPadding) APP_HORIZONTAL_DP else 0.dp,
            end = if(endPadding) APP_HORIZONTAL_DP else 0.dp
        ),
        color = color
    )
}


@Composable
fun DashedDivider(
    color: Color = DividerDefaults.color,
    strokeWidth: Dp = 1.dp,
    dashLength: Dp = 5.dp,
    gapLength: Dp = 5.dp,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    val stroke = with(LocalDensity.current) {
        Stroke(
            width = strokeWidth.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(dashLength.toPx(), gapLength.toPx())
            )
        )
    }

   Canvas (modifier = modifier.height(strokeWidth)) {
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = stroke.width,
            pathEffect = stroke.pathEffect
        )
   }
}




@Composable
private fun dividerColor(isAtStart : Boolean,color : Color = defaultDividerColor()): Color {
    val color by animateColorAsState(
        targetValue = if(isAtStart) Color.Transparent else color,
        animationSpec = tween(AppAnimationManager.ANIMATION_SPEED, easing = FastOutSlowInEasing),
        label = "",
    )
    return color
}
// 默认隐藏，滚动时显示
@Composable
fun ScrollHorizontalTopDivider(
    state: LazyListState,
    startPadding : Boolean = true,
    endPadding : Boolean = true,
    color : Color = defaultDividerColor()
) {
    val isAtStart by remember { derivedStateOf { state.firstVisibleItemIndex == 0 && state.firstVisibleItemScrollOffset == 0 } }
    PaddingHorizontalDivider(color = dividerColor(isAtStart,color),startPadding = startPadding, endPadding = endPadding)
}

@Composable
fun ScrollHorizontalBottomDivider(
    state: LazyListState,
    startPadding : Boolean = true,
    endPadding : Boolean = true,
    color : Color = defaultDividerColor()
) {
    val isAtEnd by remember {
        derivedStateOf {
            val layoutInfo = state.layoutInfo
            val totalItemsCount = layoutInfo.totalItemsCount
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (totalItemsCount == 0) {
                false
            } else {
                val lastVisibleItem = visibleItemsInfo.lastOrNull()
                // 最后一个可见的 item 刚好是最后一个且完全显示
                lastVisibleItem != null &&
                        lastVisibleItem.index == totalItemsCount - 1 &&
                        lastVisibleItem.offset + lastVisibleItem.size <= layoutInfo.viewportEndOffset
            }
        }
    }
    PaddingHorizontalDivider(color = dividerColor(isAtEnd,color), startPadding = startPadding, endPadding = endPadding)
}

@Composable
fun ScrollHorizontalTopDivider(
    state: ScrollState,
    startPadding : Boolean = true,
    endPadding : Boolean = true,
    color : Color = defaultDividerColor()
) {
    val isAtStart by remember { derivedStateOf { state.value == 0 } }
    PaddingHorizontalDivider(color = dividerColor(isAtStart,color), startPadding = startPadding, endPadding = endPadding)
}

@Composable
fun ScrollHorizontalBottomDivider(
    state: ScrollState,
    startPadding : Boolean = true,
    endPadding : Boolean = true,
    color : Color = defaultDividerColor()
) {
    val isAtEnd by remember { derivedStateOf { state.value == state.maxValue } }
    PaddingHorizontalDivider(color = dividerColor(isAtEnd,color), startPadding = startPadding, endPadding = endPadding)
}


@Composable
fun ScrollHorizontalTopDivider(
    state: LazyGridState,
    startPadding : Boolean = true,
    endPadding : Boolean = true,
    color : Color = defaultDividerColor()
) {
    val isAtStart by remember { derivedStateOf { state.firstVisibleItemIndex == 0 && state.firstVisibleItemScrollOffset == 0 } }
    PaddingHorizontalDivider(color = dividerColor(isAtStart,color),startPadding = startPadding, endPadding = endPadding)
}

@Composable
fun defaultDividerColor() = DividerDefaults.color.copy(.5f)

@Composable
fun ScrollHorizontalBottomDivider(
    state: LazyGridState,
    startPadding : Boolean = true,
    endPadding : Boolean = true,
    color : Color = defaultDividerColor()
) {
    val isAtEnd by remember {
        derivedStateOf {
            val info = state.layoutInfo
            val last = info.visibleItemsInfo.lastOrNull()
            val total = info.totalItemsCount
            last != null &&
                    last.index == total - 1 &&
                    (last.offset.y + last.size.height) <= info.viewportEndOffset
        }
    }
    PaddingHorizontalDivider(color = dividerColor(isAtEnd,color), startPadding = startPadding, endPadding = endPadding)
}

