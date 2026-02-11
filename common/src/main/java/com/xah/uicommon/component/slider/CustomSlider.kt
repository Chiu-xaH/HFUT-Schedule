package com.xah.uicommon.component.slider

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.util.safeDiv
import kotlin.math.abs

private val SLIDER_SIZE = 23.dp
private val SLIDER_HEIGHT = 6.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    onValueChangeFinished: (() -> Unit)? = null,
    steps: Int = 0,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    showProcessText : Boolean = false,
    processText : String? = null
) {
    // 监听是否按下
    var isPressed by remember { mutableStateOf(false) }
    val thumbSize by animateFloatAsState(
        targetValue = if (isPressed) 1.5f else 1f
    )
    val alpha by animateFloatAsState(
        targetValue = if (isPressed) 0.5f else 1f
    )
    val shadow by animateDpAsState(
        targetValue = if (!isPressed) APP_HORIZONTAL_DP else 0.dp
    )


    val density = LocalDensity.current
    val startPadding by animateDpAsState(
        targetValue = if(value != valueRange.start) {
            abs( SLIDER_SIZE.value -APP_HORIZONTAL_DP.value* thumbSize).dp
        } else APP_HORIZONTAL_DP
    )
    val endPadding by animateDpAsState(
        targetValue = if(value != valueRange.endInclusive) {
            abs( SLIDER_SIZE.value -APP_HORIZONTAL_DP.value* thumbSize).dp
        } else APP_HORIZONTAL_DP
    )


    Slider(
        value = value,
        onValueChange ={
            isPressed = true
            onValueChange.invoke(it)
        },
        onValueChangeFinished = {
            isPressed = false
            onValueChangeFinished?.invoke()
        },
        thumb = {
            val shape =  if (isPressed) RoundedCornerShape(percent = 50) else CircleShape
            Box(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha),
                        shape = shape // 圆角矩形，左右圆弧
                    )
                    .size(width = SLIDER_SIZE*thumbSize*thumbSize, height = SLIDER_SIZE*thumbSize)
                    .shadow(
                        elevation = shadow,
                        shape = shape
                    )
            ) {
                if(isPressed && showProcessText) {
                    val percentage = ((value - valueRange.start) safeDiv (valueRange.endInclusive - valueRange.start)) * 100f
                    Text(
                        processText ?:
                         (percentage.toString().substringBefore(".") + "%"),
                        modifier= Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
//            Box(
//                modifier = Modifier
//                    .size(SLIDER_SIZE * thumbSize)
//                    .background(MaterialTheme.colorScheme.primary.copy(alpha), CircleShape)
//                    .shadow(
//                        elevation = shadow,
//                        shape = CircleShape,
//                    )
//            )
        },
        track = { sliderState ->
            SliderDefaults.Track(
                modifier = Modifier.height(SLIDER_HEIGHT*thumbSize), // 轨道高度
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