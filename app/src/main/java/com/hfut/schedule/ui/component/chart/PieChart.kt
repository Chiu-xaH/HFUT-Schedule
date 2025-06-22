package com.hfut.schedule.ui.component.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hfut.schedule.ui.component.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.style.ColumnVertical

data class PieChartData(
    val label: String,
    val value: Float
)

@Composable
fun PieChart(
    data: List<PieChartData>,
    modifier: Modifier = Modifier,
    pieModifier: Modifier = Modifier.size(200.dp),
    title : String? = null,
) {
    val total = data.sumOf { it.value.toDouble() }.toFloat()
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.errorContainer
    )
    ColumnVertical(modifier) {
        // 饼图
        Canvas(modifier = pieModifier) {
            var startAngle = -90f
            data.forEachIndexed { index, entry ->
                val sweepAngle = (entry.value / total) * 360f
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

