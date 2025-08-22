package com.hfut.schedule.ui.component.chart

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hfut.schedule.ui.style.align.ColumnVertical

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun LineChart(
    data: Map<String, Float>,
    modifier: Modifier = Modifier,
    showLabel : Boolean = false,
    title : String? = null,
    lineColor: Color = MaterialTheme.colorScheme.primary
) {
    if (data.isEmpty()) return

    val xLabels = data.keys.toList()
    val yValues = data.values.toList()

    val maxY = yValues.maxOrNull() ?: 1f
    val minY = yValues.minOrNull() ?: 0f
    val yRange = (maxY - minY).takeIf { it != 0f } ?: 1f

    ColumnVertical {
        BoxWithConstraints(modifier = modifier.padding(horizontal = 16.dp)) {
            val width = constraints.maxWidth.toFloat()
            val height = constraints.maxHeight.toFloat()

            Canvas(modifier = Modifier.fillMaxSize()) {
                val xStep = width / (xLabels.size - 1).coerceAtLeast(1)
                val yRatio = height / yRange

                // 画 X/Y 坐标轴
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, height),
                    end = Offset(width, height),
                    strokeWidth = 2f
                )
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, 0f),
                    end = Offset(0f, height),
                    strokeWidth = 2f
                )

                // 计算折线点
                val points = yValues.mapIndexed { index, y ->
                    Offset(x = index * xStep, y = height - (y - minY) * yRatio)
                }

                // 阴影区域填充
                val fillPath = Path().apply {
                    moveTo(points.first().x, height)
                    points.forEach { point ->
                        lineTo(point.x, point.y)
                    }
                    lineTo(points.last().x, height)
                    close()
                }
//                drawPath(
//                    path = fillPath,
//                    color = lineColor.copy(alpha = 0.3f)
//                )
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            lineColor,
//                                .copy(alpha = 0.3f),
                            Color.Transparent
                        ),
                        startY = fillPath.getBounds().top,
                        endY = fillPath.getBounds().bottom
                    )
                )

                // 折线绘制
                points.zipWithNext { a, b ->
                    drawLine(color = lineColor, start = a, end = b, strokeWidth = 3f)
                }
            }
            if(showLabel) {
                // 绘制X轴标签
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    xLabels.forEach { label ->
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(IntrinsicSize.Min)
                        )
                    }
                }
                // 绘制Y轴最大值和最小值标签
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.CenterStart),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = maxY.toString(), style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = minY.toString(), style = MaterialTheme.typography.labelSmall)
                }
            }
        }
        title?.let { Text(it) }
    }
}
