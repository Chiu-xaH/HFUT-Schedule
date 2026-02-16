package com.xah.uicommon.component.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.align.ColumnVertical
import com.xah.uicommon.util.safeDiv

data class PieChartData(
    val label: String,
    val value: Float
)

@Composable
fun PieChart(
    data: List<PieChartData>,
    modifier: Modifier = Modifier,
    pieModifier: Modifier = Modifier.size(200.dp),
    baseColor : Color = MaterialTheme.colorScheme.primary,
    title : String? = null,
) {
    val total = data.sumOf { it.value.toDouble() }.toFloat()
    val colors = remember(baseColor, data.size) {
        generateColors(baseColor, data.size)
    }
    // baseColor逐渐变淡
    ColumnVertical(modifier) {
        // 饼图
        Canvas(modifier = pieModifier) {
            var startAngle = -90f
            data.forEachIndexed { index, entry ->
                val sweepAngle = (entry.value safeDiv total) * 360f
                drawArc(
                    color = colors[index % colors.size],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true
                )
                startAngle += sweepAngle
            }
        }
        Spacer(Modifier.height(APP_HORIZONTAL_DP))
        title?.let {  Text(it) }
    }
}

private fun generateColors(
    baseColor: Color,
    count: Int
): List<Color> {
    if (count <= 0) return emptyList()

    val minAlpha = 0.2f   // 最浅不低于这个
    val maxAlpha = 1f

    return List(count) { index ->
        val fraction = index.toFloat() / (count - 1).coerceAtLeast(1)
        val alpha = maxAlpha - (maxAlpha - minAlpha) * fraction
        baseColor.copy(alpha = alpha)
    }
}

