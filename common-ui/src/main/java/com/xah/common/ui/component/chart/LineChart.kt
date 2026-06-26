package com.xah.common.ui.component.chart

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xah.common.ui.style.align.ColumnVertical
import kotlin.math.hypot

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun LineChart(
    data: Map<String, Float>,
    modifier: Modifier = Modifier,
    showLabel : Boolean = false,
    title : String? = null,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    yAxisFormatter: (Float) -> String = { it.toString() },
    xAxisFormatter: (String) -> String = { it },
    xLabelSpacing: Int = 1,
    maxXLabelCount: Int = 4
) {
    if (data.isEmpty()) return

    val xLabels = data.keys.toList()
    val yValues = data.values.toList()
    val safeXLabelSpacing = xLabelSpacing.coerceAtLeast(1)

    val maxY = yValues.maxOrNull() ?: 1f
    val minY = yValues.minOrNull() ?: 0f
    val yRange = (maxY - minY).takeIf { it != 0f } ?: 1f
    val density = LocalDensity.current
    val yAxisWidth = if (showLabel) 54.dp else 0.dp
    val xAxisHeight = if (showLabel) 36.dp else 0.dp
    val chartTopPadding = if (showLabel) 8.dp else 0.dp
    val chartHorizontalPadding = if (showLabel) 24.dp else 0.dp
    var selectedIndex by remember(data) { mutableIntStateOf(-1) }

    ColumnVertical {
        BoxWithConstraints(modifier = modifier.padding(horizontal = 8.dp)) {
            val width = constraints.maxWidth.toFloat()
            val height = constraints.maxHeight.toFloat()
            val visibleLabels = if (showLabel) {
                val safeMaxLabelCount = maxXLabelCount.coerceAtLeast(2)
                val readableSpacing = ((xLabels.size + safeMaxLabelCount - 1) / safeMaxLabelCount).coerceAtLeast(1)
                val finalSpacing = maxOf(safeXLabelSpacing, readableSpacing)
                val indexes = xLabels.indices.filter { index ->
                    index == 0 || index == xLabels.lastIndex || index % finalSpacing == 0
                }.let { indexes ->
                    if (indexes.size <= safeMaxLabelCount) {
                        indexes
                    } else {
                        buildList {
                            add(indexes.first())
                            val middleCount = safeMaxLabelCount - 2
                            if (middleCount > 0) {
                                val middleIndexes = indexes.drop(1).dropLast(1)
                                repeat(middleCount) { item ->
                                    val targetIndex = ((item + 1) * middleIndexes.size / (middleCount + 1))
                                        .coerceIn(0, (middleIndexes.size - 1).coerceAtLeast(0))
                                    middleIndexes.getOrNull(targetIndex)?.let(::add)
                                }
                            }
                            add(indexes.last())
                        }.distinct()
                    }
                }
                indexes.map { xAxisFormatter(xLabels[it]) }
            } else {
                emptyList()
            }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(data, width, height, showLabel) {
                        detectTapGestures { tap ->
                            val xAxisHeightPx = with(density) { xAxisHeight.toPx() }
                            val chartTopPaddingPx = with(density) { chartTopPadding.toPx() }
                            val chartHorizontalPaddingPx = with(density) { chartHorizontalPadding.toPx() }
                            val chartLeft = chartHorizontalPaddingPx
                            val chartTop = chartTopPaddingPx
                            val chartRight = (width - chartHorizontalPaddingPx).coerceAtLeast(chartLeft)
                            val chartBottom = (height - xAxisHeightPx).coerceAtLeast(chartTop)
                            val chartWidth = (chartRight - chartLeft).coerceAtLeast(1f)
                            val chartHeight = (chartBottom - chartTop).coerceAtLeast(1f)
                            val xStep = chartWidth / (xLabels.size - 1).coerceAtLeast(1)
                            val yRatio = chartHeight / yRange
                            val points = yValues.mapIndexed { index, y ->
                                Offset(
                                    x = chartLeft + index * xStep,
                                    y = chartBottom - (y - minY) * yRatio
                                )
                            }
                            val hitRadius = with(density) { 28.dp.toPx() }
                            val nearest = points
                                .mapIndexed { index, point ->
                                    index to hypot(tap.x - point.x, tap.y - point.y)
                                }
                                .minByOrNull { it.second }
                            selectedIndex = if (nearest != null && nearest.second <= hitRadius) nearest.first else -1
                        }
                    }
            ) {
                val xAxisHeightPx = with(density) { xAxisHeight.toPx() }
                val chartTopPaddingPx = with(density) { chartTopPadding.toPx() }
                val chartHorizontalPaddingPx = with(density) { chartHorizontalPadding.toPx() }
                val chartLeft = chartHorizontalPaddingPx
                val chartTop = chartTopPaddingPx
                val chartRight = (width - chartHorizontalPaddingPx).coerceAtLeast(chartLeft)
                val chartBottom = (height - xAxisHeightPx).coerceAtLeast(chartTop)
                val chartWidth = (chartRight - chartLeft).coerceAtLeast(1f)
                val chartHeight = (chartBottom - chartTop).coerceAtLeast(1f)
                val xStep = chartWidth / (xLabels.size - 1).coerceAtLeast(1)
                val yRatio = chartHeight / yRange

                // 画 X/Y 坐标轴
                drawLine(
                    color = Color.Gray,
                    start = Offset(chartLeft, chartBottom),
                    end = Offset(chartRight, chartBottom),
                    strokeWidth = 2f
                )
                drawLine(
                    color = Color.Gray,
                    start = Offset(chartLeft, chartTop),
                    end = Offset(chartLeft, chartBottom),
                    strokeWidth = 2f
                )

                // 计算折线点
                val points = yValues.mapIndexed { index, y ->
                    Offset(
                        x = chartLeft + index * xStep,
                        y = chartBottom - (y - minY) * yRatio
                    )
                }

                // 阴影区域填充
                val fillPath = Path().apply {
                    moveTo(points.first().x, chartBottom)
                    points.forEach { point ->
                        lineTo(point.x, point.y)
                    }
                    lineTo(points.last().x, chartBottom)
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
                points.forEach { point ->
                    drawCircle(color = lineColor, radius = 4.dp.toPx(), center = point)
                    drawCircle(
                        color = Color.White,
                        radius = 2.dp.toPx(),
                        center = point,
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
            }
            if(showLabel) {
                // 绘制X轴标签
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(xAxisHeight)
                        .padding(horizontal = chartHorizontalPadding)
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    visibleLabels.forEach { label ->
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Clip,
                            softWrap = false,
                            modifier = Modifier.width(48.dp)
                        )
                    }
                }
                // 绘制Y轴最大值和最小值标签
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(yAxisWidth)
                        .padding(bottom = xAxisHeight, top = chartTopPadding)
                        .align(Alignment.CenterStart),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = yAxisFormatter(maxY),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = yAxisFormatter(minY),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            if (selectedIndex in yValues.indices) {
                Text(
                    text = "${xAxisFormatter(xLabels[selectedIndex])}  ${yAxisFormatter(yValues[selectedIndex])}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
        title?.let { Text(it) }
    }
}
