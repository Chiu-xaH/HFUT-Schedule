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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.ShareTwoContainer2D
import com.xah.mirror.shader.glassLayer
import com.xah.mirror.shader.largeStyle
import com.xah.mirror.util.ShaderState
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private const val dragThreshold  = 10f
@Composable
fun DraggableWeekButton(
    modifier: Modifier = Modifier,
    currentWeek: Long,
    key: Any?,
    shaderState: ShaderState?,
    expanded: Boolean = true,
    containerColor : Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor : Color = MaterialTheme.colorScheme.primary,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onClick: () -> Unit,
) {
    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    var totalDragX by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()

    // 内边距动画
    val padding = 14.dp

    val textUI = @Composable {
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
            },
            label = "weekChange"
        ) { week ->
            Text(
                text =  if(expanded) "第 $week 周" else "第${week}周",
                fontSize = 15.sp,
                color = contentColor
            )
        }
    }

    val hasBackground = shaderState != null
    val customBackgroundAlpha by DataStoreManager.customCalendarSquareAlpha.collectAsState(initial = 1f)
    val enableLiquidGlass by DataStoreManager.enableLiquidGlass.collectAsState(initial = AppVersion.CAN_SHADER)

    Box(
        modifier = modifier
            .pointerInput(key) {
                detectDragGestures(
                    onDragEnd = {
                        when {
                            totalDragX > dragThreshold -> onNext()
                            totalDragX < -dragThreshold -> onPrevious()
                        }
                        totalDragX = 0f
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
        ShareTwoContainer2D(
            show = !expanded,
            defaultContent = {
                ExtendedFloatingActionButton(
                    onClick = onClick,
                    containerColor = if(hasBackground) Color.Transparent else containerColor,
                    elevation = FloatingActionButtonDefaults.elevation(0.dp,0.dp,0.dp,0.dp),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset { IntOffset(offset.value.x.roundToInt(), offset.value.y.roundToInt()) }
                        .let {
                            if(hasBackground) {
                                it
                                    .clip(FloatingActionButtonDefaults. extendedFabShape)
                                    .glassLayer(
                                        shaderState,
                                        style = largeStyle.copy(
                                            overlayColor = MaterialTheme.colorScheme.surface.copy(customBackgroundAlpha)
                                        ),
                                        enabled = enableLiquidGlass
                                    )
                            } else {
                                it
                            }
                        }
                    ,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        val iconSize = 19.dp

                        // 左箭头（展开时显示）
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier
                                .size(iconSize)
                                .clickable { onPrevious() }
                        )

                        Spacer(modifier = Modifier.width(padding))

                        textUI()

                        Spacer(modifier = Modifier.width(padding))

                        // 右箭头（展开时显示）
                        Icon(
                            Icons.Filled.ArrowForward,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier
                                .size(iconSize)
                                .clickable { onNext() }
                        )
                    }
                }
            },
            secondContent = {
                Surface(
                    color = if(hasBackground) Color.Transparent else containerColor,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset { IntOffset(offset.value.x.roundToInt(), offset.value.y.roundToInt()) }
                        .let {
                            if(hasBackground) {
                                it
                                    .clip(MaterialTheme.shapes.small)
                                    .glassLayer(
                                        shaderState,
                                        style = largeStyle.copy(
                                            overlayColor = MaterialTheme.colorScheme.surface.copy(customBackgroundAlpha)
                                        ),
                                        enabled = enableLiquidGlass
                                    )
                            } else {
                                it
                            }
                        }
                        .clickable {
                            onClick()
                        },
                ) {
                    Box(modifier = Modifier.padding(horizontal = CARD_NORMAL_DP*3, vertical = CARD_NORMAL_DP/2)) {
                        textUI()
                    }
                }
            }
        )
    }
}
