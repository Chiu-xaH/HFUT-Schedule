package com.hfut.schedule.ui.screen.home.calendar.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
fun DraggableWeekButton(
    modifier: Modifier = Modifier,
    dragThreshold: Float = 5f,
    currentWeek : Long,
    key : Any?,
    onNext : () -> Unit,
    onPrevious : () -> Unit,
    onClick: () -> Unit,
) {
    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) } // 动画偏移
    var totalDragX by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .pointerInput(key) {
                detectDragGestures(
                    onDragEnd = {
                        // 根据水平累积偏移触发事件
                        when {
                            totalDragX > dragThreshold -> onNext()
                            totalDragX < -dragThreshold -> onPrevious()
                        }
                        totalDragX = 0f
                        // 松开手指
                        scope.launch {
                            offset.animateTo(
                                Offset.Zero,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val (dx, dy) = dragAmount
                        totalDragX += dx
                        // 手指拖动
                        scope.launch {
                            offset.snapTo(
                                Offset(
                                    x = (offset.value.x + dx / 2f).coerceIn(-50f, 50f),
                                    y = (offset.value.y + dy / 2f).coerceIn(-50f, 50f)
                                )
                            )
                        }
                    }
                )
            }
    ) {
        ExtendedFloatingActionButton(
            onClick = onClick,
            modifier = Modifier
                .align(Alignment.Center)
                .offset { IntOffset(offset.value.x.roundToInt(), offset.value.y.roundToInt()) }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                val iconSize = 19.dp // 图标大小
                val textFontSize = 15.sp // 字体大小，可根据需求调整
                val padding = 14.dp

                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier
                        .size(iconSize)
                        .clickable { onPrevious() }
                )

                Spacer(modifier = Modifier.width(padding))


                AnimatedContent(
                    targetState = currentWeek,
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideInVertically { height -> height } + fadeIn() togetherWith
                                    slideOutVertically { height -> -height } + fadeOut()
                        } else {
                            slideInVertically { height -> -height } + fadeIn() togetherWith
                                    slideOutVertically { height -> height } + fadeOut()
                        }
                    }
                ) { week ->
                    Text(
                        text = "第 $week 周",
                        fontSize = textFontSize
                    )
                }

                Spacer(modifier = Modifier.width(padding)) // 文字与右箭头间距

                Icon(
                    Icons.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier
                        .size(iconSize)
                        .clickable { onNext() }
                )
            }
        }
    }
}
