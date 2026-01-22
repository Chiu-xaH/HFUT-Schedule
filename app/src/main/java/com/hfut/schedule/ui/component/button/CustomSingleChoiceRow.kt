package com.hfut.schedule.ui.component.button

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.xah.uicommon.style.APP_HORIZONTAL_DP


/**
 * 待实现基类
 */
@Composable
fun <T> CustomSingleChoiceRow(
    options : Map<T, String>,
    selected : T,
    modifier: Modifier = Modifier,
    itemShape : CornerBasedShape = MaterialTheme.shapes.small,
    onItemSelected : (T) -> Unit
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = APP_HORIZONTAL_DP)
        // 不撑满就会出现神秘文本测量问题 😎
    ) {
        options.toList().forEachIndexed { index, item ->
            val isSelected = selected == item
            val scrollState = rememberScrollState()
            val textOverflow = scrollState.canScrollBackward || scrollState.canScrollForward

            // 有个缺点是不能为某一个选项单独设置宽度，如果在上面的 Row 里面指定 space 那么在下面的自定义颜色中又会导致边框堆叠
            SegmentedButton(
                selected = isSelected,
                onClick = {
                    onItemSelected(item.first)
                },
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size,
                    baseShape = itemShape // 圆角
                ),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.primary,
                    activeContentColor = MaterialTheme.colorScheme.onPrimary,
                    activeBorderColor = MaterialTheme.colorScheme.primary,
                    // pC 描边与选中颜色背景一致，但是相邻选项之间感觉少一条线
                    inactiveBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                icon = {
                    if (!textOverflow) {
                        SegmentedButtonDefaults.Icon(isSelected)
                    }
                },
                label = {
                    Text(
                        modifier = Modifier
                            .horizontalScroll(scrollState),
                        text = item.second,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            )
        }
    }
}
