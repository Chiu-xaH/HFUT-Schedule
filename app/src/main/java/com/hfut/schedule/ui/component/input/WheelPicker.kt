package com.hfut.schedule.ui.component.input

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import kotlin.math.abs
import kotlin.math.roundToInt

//滚轮 组件
private const val visibleCount = 3
private const val height = 120

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> WheelPicker(
    data: List<T>,
    selectIndex: Int = 0,
    modifier: Modifier = Modifier,
    selectedColor : Color = MaterialTheme.colorScheme.surfaceContainer,
    onSelect: (index: Int, item: T) -> Unit,
    content: @Composable (item: T) -> Unit,
) {
    val enableInfiniteWheelPicker by DataStoreManager.enableInfiniteWheelPicker.collectAsState(initial = true)
    BoxWithConstraints(modifier = modifier.height(height.dp), propagateMinConstraints = true) {
        val density = LocalDensity.current
        val size = data.size
        // 伪装无限滚动
        val count = if (enableInfiniteWheelPicker) {
            size * 10000
        } else {
            val paddingCount = visibleCount / 2
            size + paddingCount * 2
        }
        val pickerHeight = maxHeight
        val pickerHeightPx = density.run { pickerHeight.toPx() }
        val pickerCenterLinePx = pickerHeightPx / 2
        val itemHeight = pickerHeight / visibleCount
        val itemHeightPx = pickerHeightPx / visibleCount
        val startIndex = count / 2
        val listState = rememberLazyListState(
            initialFirstVisibleItemIndex = if (enableInfiniteWheelPicker) {
                startIndex - startIndex.floorMod(size) + selectIndex
            } else {
                selectIndex
            },
            initialFirstVisibleItemScrollOffset = ((itemHeightPx - pickerHeightPx) / 2).roundToInt(),
        )
        val layoutInfo by remember { derivedStateOf { listState.layoutInfo } }

        // 手势处理
        val nestedScrollConnection = remember(listState, enableInfiniteWheelPicker) {
            object : NestedScrollConnection {

                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {

                    // 无限模式：永远不拦截（让自己随便滚）
                    if (enableInfiniteWheelPicker) return Offset.Zero

                    val delta = available.y

                    val isAtTop = !listState.canScrollBackward
                    val isAtBottom = !listState.canScrollForward

                    val shouldBlock = when {
                        delta > 0 && isAtTop -> true     // 向下拉，但已经在顶部
                        delta < 0 && isAtBottom -> true  // 向上推，但已经在底部
                        else -> false
                    }

                    return if (shouldBlock) {
                        // 只在边界时拦截（阻止传递给父布局）
                        Offset(0f, available.y)
                    } else {
                        // 让 LazyColumn 自己处理
                        Offset.Zero
                    }
                }

                override suspend fun onPreFling(available: Velocity): Velocity {

                    if (enableInfiniteWheelPicker) return Velocity.Zero

                    val velocityY = available.y

                    val isAtTop = !listState.canScrollBackward
                    val isAtBottom = !listState.canScrollForward

                    val shouldBlock = when {
                        velocityY > 0 && isAtTop -> true
                        velocityY < 0 && isAtBottom -> true
                        else -> false
                    }

                    return if (shouldBlock) {
                        Velocity(0f, velocityY)
                    } else {
                        Velocity.Zero
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.nestedScroll(nestedScrollConnection),
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(listState),
        ) {
            items(count) { index ->

                val paddingCount = visibleCount / 2

                val currIndex = if (enableInfiniteWheelPicker) {
                    (index - startIndex).floorMod(size)
                } else {
                    index - paddingCount
                }

                val isPaddingItem = !enableInfiniteWheelPicker && (index < paddingCount || index >= paddingCount + size)

                val item = layoutInfo.visibleItemsInfo.find { it.index == index }

                var currentsAdjust = 1f
                var itemCenterY = 0f

                if (item != null) {
                    val viewportStart = layoutInfo.viewportStartOffset
                    itemCenterY = item.offset - viewportStart + item.size / 2f

                    val distance = abs(itemCenterY - pickerCenterLinePx)
                    val fraction = 1f - (distance / pickerCenterLinePx).coerceIn(0f, 1f)

                    // 透明度
                    currentsAdjust = 0.6f + 0.4f * fraction

                    if (!listState.isScrollInProgress
                        && !isPaddingItem
                        && item.offset < pickerCenterLinePx
                        && item.offset + item.size > pickerCenterLinePx
                    ) {
                        onSelect(currIndex, data[currIndex])
                    }
                }

                val selected = abs(itemCenterY - pickerCenterLinePx) < itemHeightPx / 2
                val colorAlpha = if (selected && !isPaddingItem) 1f else 0f

                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth()
                        .background(
                            selectedColor.copy(colorAlpha),
                            shape = MaterialTheme.shapes.medium
                        )
                        .graphicsLayer {
                            alpha = if (isPaddingItem) 0f else currentsAdjust
                            scaleX = currentsAdjust
                            scaleY = currentsAdjust
                            rotationX = if (itemCenterY < pickerCenterLinePx) {
                                (1 - currentsAdjust) * 90f
                            } else if (itemCenterY > pickerCenterLinePx) {
                                -(1 - currentsAdjust) * 90f
                            } else {
                                0f
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (!isPaddingItem) {
                        Box(
                            modifier = Modifier.padding(CARD_NORMAL_DP * 2)
                        ) {
                            content(data[currIndex])
                        }
                    }
                }
            }
        }
    }
}

private fun Int.floorMod(other: Int): Int = when (other) {
    0 -> this
    else -> this - floorDiv(other) * other
}
