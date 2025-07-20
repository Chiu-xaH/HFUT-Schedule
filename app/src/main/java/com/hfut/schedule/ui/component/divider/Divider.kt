package com.hfut.schedule.ui.component.divider

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.util.AppAnimationManager

@Composable
fun PaddingHorizontalDivider(startPadding : Boolean = true, endPadding : Boolean = true,color : Color = DividerDefaults.color.copy(.5f)) =
    HorizontalDivider(
        modifier = Modifier.padding(
            start = if(startPadding) APP_HORIZONTAL_DP else 0.dp,
            end = if(endPadding) APP_HORIZONTAL_DP else 0.dp
        ),
        color = color
    )


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