package com.xah.uicommon.component.status

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
import com.xah.uicommon.util.language.BaseChoice


@Composable
inline fun <reified T> CustomSingleChoiceRow(
    selected: T,
    modifier: Modifier = Modifier,
    enabled : Boolean = true,
    itemShape: CornerBasedShape = MaterialTheme.shapes.small,
    noinline onItemSelected: (T) -> Unit
) where T : Enum<T>, T : BaseChoice =
    CustomSingleChoiceRow(
        selected.code,
        modifier,
        enabled,
        itemShape,
        onItemSelected
    )

@Composable
inline fun <reified T> CustomSingleChoiceRow(
    selected: Int,
    modifier: Modifier = Modifier,
    enabled : Boolean = true,
    itemShape: CornerBasedShape = MaterialTheme.shapes.small,
    noinline onItemSelected: (T) -> Unit
) where T : Enum<T>, T : BaseChoice =
    CustomSingleChoiceRow(
        enumValues<T>().toList(),
        selected,
        modifier,
        enabled,
        itemShape,
        onItemSelected
    )


@Composable
fun <T> CustomSingleChoiceRow(
    options: List<T>,
    selected: T,
    modifier: Modifier = Modifier,
    enabled : Boolean = true,
    itemShape: CornerBasedShape = MaterialTheme.shapes.small,
    onItemSelected: (T) -> Unit
) where T : Enum<T>, T : BaseChoice =
    CustomSingleChoiceRow(
        options,
        selected.code,
        modifier,
        enabled,
        itemShape,
        onItemSelected
    )

/**
 * @author Today1337
 */
@Composable
fun <T> CustomSingleChoiceRow(
    options: List<T>,
    selected: Int,
    modifier: Modifier = Modifier,
    enabled : Boolean = true,
    itemShape: CornerBasedShape = MaterialTheme.shapes.small,
    onItemSelected: (T) -> Unit
) where T : Enum<T>, T : BaseChoice = SingleChoiceSegmentedButtonRow(
    modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = APP_HORIZONTAL_DP)
    // 不撑满就会出现神秘文本测量问题 😎
) {
    options.forEachIndexed { index, mode ->
        val isSelected = selected == mode.code
        val scrollState = rememberScrollState()
        val textOverflow = scrollState.canScrollBackward || scrollState.canScrollForward

        // 有个缺点是不能为某一个选项单独设置宽度，如果在上面的 Row 里面指定 space 那么在下面的自定义颜色中又会导致边框堆叠
        SegmentedButton(
            enabled = enabled,
            selected = isSelected,
            onClick = { onItemSelected(mode) },
            shape = SegmentedButtonDefaults.itemShape(
                index = index,
                count = options.size,
                baseShape = itemShape
            ),
            colors = SegmentedButtonDefaults.colors(
                activeContainerColor = MaterialTheme.colorScheme.primary,
                activeContentColor = MaterialTheme.colorScheme.onPrimary,
                activeBorderColor = MaterialTheme.colorScheme.primary,
                // pC 描边与选中颜色背景一致，但是相邻选项之间感觉少一条线
                inactiveBorderColor = MaterialTheme.colorScheme.outlineVariant,
                // 适配 enabled==false 态
                disabledInactiveContentColor = MaterialTheme.colorScheme.outlineVariant,
                disabledActiveContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledActiveContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                disabledActiveBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledInactiveBorderColor = MaterialTheme.colorScheme.outlineVariant,
            ),
            icon = {
                if (!textOverflow) {
                    SegmentedButtonDefaults.Icon(isSelected)
                }
            },
            label = {
                Text(
                    modifier = Modifier.horizontalScroll(scrollState),
                    text = mode.label.asString(),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        )
    }
}

