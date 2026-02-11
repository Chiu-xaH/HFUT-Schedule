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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.xah.uicommon.util.safeDiv
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
    BoxWithConstraints(modifier = modifier.height(height.dp), propagateMinConstraints = true) {
        val density = LocalDensity.current
        val size = data.size
        val count = size * 10000
        val pickerHeight = maxHeight
        val pickerHeightPx = density.run { pickerHeight.toPx() }
        val pickerCenterLinePx = pickerHeightPx / 2
        val itemHeight = pickerHeight / visibleCount
        val itemHeightPx = pickerHeightPx / visibleCount
        val startIndex = count / 2
        val listState = rememberLazyListState(
            initialFirstVisibleItemIndex = startIndex - startIndex.floorMod(size) + selectIndex,
            initialFirstVisibleItemScrollOffset = ((itemHeightPx - pickerHeightPx) / 2).roundToInt(),
        )
        val layoutInfo by remember { derivedStateOf { listState.layoutInfo } }
        LazyColumn(
            modifier = Modifier,
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(listState),
        ) {
            items(count) { index ->
                val currIndex = (index - startIndex).floorMod(size)
                val item = layoutInfo.visibleItemsInfo.find { it.index == index }
                var currentsAdjust = 1f
                var itemCenterY = 0   // 提前声明，保证 graphicsLayer 可见

                if (item != null) {
                    itemCenterY = item.offset + item.size / 2
                    currentsAdjust = 0.75f + 0.25f * if (itemCenterY < pickerCenterLinePx) {
                        itemCenterY / pickerCenterLinePx
                    } else {
                        1 - (itemCenterY - pickerCenterLinePx) safeDiv pickerCenterLinePx
                    }
                    if (!listState.isScrollInProgress
                        && item.offset < pickerCenterLinePx
                        && item.offset + item.size > pickerCenterLinePx
                    ) {
                        onSelect(currIndex, data[currIndex])
                    }
                }
                val selected = itemCenterY == pickerCenterLinePx.toInt()
                val colorAlpha = if(selected) 1f else 0f

                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth()
                        .background(
                             selectedColor.copy(colorAlpha),
                            shape = MaterialTheme.shapes.medium
                        )
                        .graphicsLayer {
                            alpha = currentsAdjust
                            scaleX = currentsAdjust
                            scaleY = currentsAdjust
                            rotationX = if (itemCenterY < pickerCenterLinePx) {
                                // 在上方 → 朝上翻
                                (1 - currentsAdjust) * 90f
                            } else if(itemCenterY > pickerCenterLinePx) {
                                // 在下方 → 朝下翻
                                -(1 - currentsAdjust) * 90f
                            } else {
                                0f
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .padding(CARD_NORMAL_DP*2)
                    ) {
                        content(data[currIndex])
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
