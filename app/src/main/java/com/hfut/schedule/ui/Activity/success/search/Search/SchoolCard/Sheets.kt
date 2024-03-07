package com.hfut.schedule.ui.Activity.success.search.Search.SchoolCard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.datamodel.zjgd.BillMonth

@Composable
fun drawLineChart(data: List<BillMonth>) {
    val path = Path()
    val size = Size(300f, 300f)
    Canvas(modifier = Modifier.size(600.dp,120.dp)) {
        val xInterval = 30f
        val yInterval = size.height / data.maxOf { it.balance.toFloat() }
        path.moveTo(0f, size.height - data.first().balance.toFloat() * yInterval)
        data.forEachIndexed { index, pair ->
            val x = index * xInterval
            val y = size.height - pair.balance.toFloat() * yInterval
            path.lineTo(x, y)
        }
        drawPath(
            path = path,
            color = Color.Black,
            alpha = 0.5f,
            style = Stroke(width = 7f)
        )
    }
}