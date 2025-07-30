package com.hfut.schedule.ui.component.divider

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.util.AppAnimationManager

@Composable
fun PaddingHorizontalDivider(
    startPadding : Boolean = true,
    endPadding : Boolean = true,
    isDashed : Boolean = false,
    color : Color = if(isDashed) MaterialTheme.colorScheme.outline.copy(.5f) else DividerDefaults.color.copy(.5f),
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
private fun dividerColor(isAtStart : Boolean): Color {
    val color by animateColorAsState(
        targetValue = if(isAtStart) Color.Transparent else DividerDefaults.color.copy(.5f),
        animationSpec = tween(AppAnimationManager.ANIMATION_SPEED, easing = FastOutSlowInEasing),
        label = "",
    )
    return color
}
// 默认隐藏，滚动时显示
@Composable
fun ScrollHorizontalDivider(state: LazyListState) {
    val isAtStart by remember { derivedStateOf { state.firstVisibleItemIndex == 0 && state.firstVisibleItemScrollOffset == 0 } }
    PaddingHorizontalDivider(color = dividerColor(isAtStart))
}

@Composable
fun ScrollHorizontalDivider(state: ScrollState) {
    val isAtStart by remember { derivedStateOf { state.value == 0 } }
    PaddingHorizontalDivider(color = dividerColor(isAtStart))
}

@Composable
fun ScrollHorizontalDivider(state: LazyGridState) {
    val isAtStart by remember { derivedStateOf { state.firstVisibleItemIndex == 0 && state.firstVisibleItemScrollOffset == 0 } }
    PaddingHorizontalDivider(color = dividerColor(isAtStart))
}