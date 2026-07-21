package com.xah.common.ui.component.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.align.ColumnVertical

data class StackedBarData(
    val label: String,
    val value: Float
)

@Composable
fun StackedBarChart(
    data: List<StackedBarData>,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
    title: String? = null,
    baseColor: Color = MaterialTheme.colorScheme.primary,
    colors: List<Color>? = null,
    barHeight: Float = 24f,
) {
    if (data.isEmpty()) {
        return
    }

    val total = data.sumOf { it.value.toDouble() }.toFloat()
    if (total == 0f) {
        return
    }

    val segmentColors = colors ?: remember(baseColor, data.size) {
        generateStackedColors(baseColor, data.size)
    }

    ColumnVertical(modifier = modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
        // 堆叠条形
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            data.forEachIndexed { index, entry ->
                val ratio = entry.value / total
                Box(
                    modifier = Modifier
                        .weight(ratio, fill = true)
                        .height(barHeight.dp)
                        .background(
                            color = segmentColors.getOrElse(index) { baseColor },
                            shape = RoundedCornerShape(
                                topStart = if (index == 0) 6.dp else 0.dp,
                                bottomStart = if (index == 0) 6.dp else 0.dp,
                                topEnd = if (index == data.lastIndex) 6.dp else 0.dp,
                                bottomEnd = if (index == data.lastIndex) 6.dp else 0.dp,
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (showLabel) {
                        Text(
                            text = entry.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Clip
                        )
                    }
                }
            }
        }

        /* 图例
        if (showLegend) {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                data.forEachIndexed { index, entry ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = segmentColors.getOrElse(index) { baseColor },
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )
                        Text(
                            text = entry.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                    }
                }
            }
        }
         */

        Spacer(Modifier.height(APP_HORIZONTAL_DP))
        title?.let { Text(it) }
    }
}

private fun generateStackedColors(
    baseColor: Color,
    count: Int
): List<Color> {
    if (count <= 0) return emptyList()

    val minAlpha = 0.35f
    val maxAlpha = 1f

    return List(count) { index ->
        val fraction = index.toFloat() / (count - 1).coerceAtLeast(1)
        val alpha = maxAlpha - (maxAlpha - minAlpha) * fraction
        baseColor.copy(alpha = alpha)
    }
}
