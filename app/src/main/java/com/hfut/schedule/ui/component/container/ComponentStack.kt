package com.hfut.schedule.ui.component.container

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import com.xah.common.ui.style.align.CenterScreen
import kotlin.math.abs
import kotlin.math.sign

@Composable
fun <T> ComponentStack(
    items: List<T>,
    modifier: Modifier = Modifier,
    initialIndex: Int = 0,
    minScale: Float = 0.9f,
    onIndexChanged: (Int) -> Unit = {},
    content: @Composable (T) -> Unit,
) {
    if (items.isEmpty()) {
        return
    }

    var currentIndex by remember {
        mutableIntStateOf(initialIndex.coerceIn(items.indices))
    }
    var dragOffsetPx by remember { mutableFloatStateOf(0f) }
    var pageHeightPx by remember { mutableFloatStateOf(0f) }

    val currentOnIndexChanged by rememberUpdatedState(onIndexChanged)
    val velocityThresholdPx = with(LocalDensity.current) { 600.dp.toPx() }

    if (currentIndex !in items.indices) {
        currentIndex = currentIndex.coerceIn(items.indices)
        dragOffsetPx = 0f
    }

    fun targetIndexFor(offset: Float): Int? = when {
        offset < 0f && currentIndex < items.lastIndex -> currentIndex + 1
        offset > 0f && currentIndex > 0 -> currentIndex - 1
        else -> null
    }

    val dragState = rememberDraggableState { delta ->
        if (pageHeightPx <= 0f) return@rememberDraggableState

        val proposed = dragOffsetPx + delta
        val hasTarget = targetIndexFor(proposed) != null

        dragOffsetPx = if (hasTarget) {
            proposed.coerceIn(-pageHeightPx, pageHeightPx)
        } else {
            (proposed * 0.22f).coerceIn(-pageHeightPx * 0.16f, pageHeightPx * 0.16f)
        }
    }

    Layout(
        modifier = modifier
            .clipToBounds()
            .draggable(
                state = dragState,
                orientation = Orientation.Vertical,
                enabled = items.size > 1,
                onDragStopped = { velocity ->
                    val height = pageHeightPx
                    val targetIndex = targetIndexFor(dragOffsetPx)
                    val movingTowardTarget =
                        dragOffsetPx != 0f && sign(velocity) == sign(dragOffsetPx)
                    val shouldSwitch = targetIndex != null && (
                            abs(dragOffsetPx) >= height * 0.5f ||
                                    movingTowardTarget && abs(velocity) >= velocityThresholdPx
                            )

                    val destination = if (shouldSwitch) {
                        sign(dragOffsetPx) * height
                    } else {
                        0f
                    }

                    val remainingFraction = if (height > 0f) {
                        (abs(destination - dragOffsetPx) / height).coerceIn(0f, 1f)
                    } else {
                        0f
                    }
                    val durationMillis = (120f + 140f * remainingFraction).toInt()

                    animate(
                        initialValue = dragOffsetPx,
                        targetValue = destination,
                        animationSpec = tween(
                            durationMillis = durationMillis,
                            easing = FastOutSlowInEasing,
                        ),
                    ) { value, _ ->
                        dragOffsetPx = value
                    }

                    if (shouldSwitch) {
                        val newIndex = requireNotNull(targetIndex)
                        currentIndex = newIndex
                        dragOffsetPx = 0f
                        currentOnIndexChanged(newIndex)
                    }
                },
            ),
        content = {
            val height = pageHeightPx.coerceAtLeast(1f)
            val progress = (abs(dragOffsetPx) / height).coerceIn(0f, 1f)
            val scalePhase = 1f - abs(2f * progress - 1f)
            val scale = 1f - (1f - minScale) * scalePhase
            val targetIndex = targetIndexFor(dragOffsetPx)

            key(currentIndex) {
                Box(
                    modifier = Modifier.graphicsLayer {
                        translationY = dragOffsetPx
                        scaleX = scale
                        scaleY = scale
                        transformOrigin = TransformOrigin.Center
                    },
                ) {
                    content(items[currentIndex])
                }
            }

            if (targetIndex != null) {
                val targetTranslationY = if (dragOffsetPx < 0f) {
                    dragOffsetPx + height
                } else {
                    dragOffsetPx - height
                }

                key(targetIndex) {
                    Box(
                        modifier = Modifier.graphicsLayer {
                            translationY = targetTranslationY
                            scaleX = scale
                            scaleY = scale
                            transformOrigin = TransformOrigin.Center
                        },
                    ) {
                        content(items[targetIndex])
                    }
                }
            }
        },
    ) { measurables, constraints ->
        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = measurables.map { it.measure(looseConstraints) }
        val current = placeables.first()
        val width = constraints.constrainWidth(current.width)
        val height = constraints.constrainHeight(current.height)
        pageHeightPx = height.toFloat()

        layout(width, height) {
            placeables.forEach { placeable ->
                placeable.placeRelative(0, 0)
            }
        }
    }
}


@Composable
@Preview
fun A() {
    CenterScreen {
        ComponentStack(
            listOf(
                1,2,3,4
            )
        ) {
            CardListItem(
                color = MaterialTheme.colorScheme.primaryContainer,
                headlineContent = {
                    Text("标题")
                },
                supportingContent = {
                    Text("副标题")
                },
                overlineContent = {
                    Text("备注")
                },
                leadingContent = {
                    Text(it.toString())
                }
            )
        }
    }
}