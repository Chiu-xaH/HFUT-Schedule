package com.hfut.schedule.ui.component.slider

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP

val SLIDER_SIZE = 23.dp
private val SLIDER_HEIGHT = 6.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    steps: Int = 0,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f
) {
    val density = LocalDensity.current
    val startPadding by animateDpAsState(
        targetValue = if(value != valueRange.start) SLIDER_SIZE-APP_HORIZONTAL_DP else APP_HORIZONTAL_DP
    )
    val endPadding by animateDpAsState(
        targetValue = if(value != valueRange.endInclusive) SLIDER_SIZE-APP_HORIZONTAL_DP else APP_HORIZONTAL_DP
    )
    Slider(
        value = value,
        onValueChange =onValueChange,
        onValueChangeFinished = onValueChangeFinished,
        thumb = {
            Box(
                modifier = Modifier
                    .size(SLIDER_SIZE)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .shadow(APP_HORIZONTAL_DP)
            )
        },
        track = { sliderState ->
            SliderDefaults.Track(
                modifier = Modifier.height(SLIDER_HEIGHT), // 轨道高度
                sliderState = sliderState,
                thumbTrackGapSize = 0.dp,
                trackInsideCornerSize = 0.dp,
                colors = SliderDefaults.colors(
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    activeTickColor = MaterialTheme.colorScheme.onPrimary,
                    inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    inactiveTickColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                drawTick = { offset, color ->
                    with(density) {
                        drawCircle(color = color, center = offset, radius = SLIDER_HEIGHT.toPx() / 4f)
                    }
                }
            )
        },
        steps = steps,
        valueRange = valueRange,
        modifier = modifier.padding(
            start = startPadding,
            end = endPadding,
        )
    )
}