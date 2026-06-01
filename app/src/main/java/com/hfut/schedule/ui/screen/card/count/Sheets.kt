package com.hfut.schedule.ui.screen.card.count

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.model.huixin.BillMonth
import com.xah.common.logic.safeDiv
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height

//消费折线图
@Composable
fun drawLineChart(data: List<BillMonth>, modifier: Modifier = Modifier) {
    if (data.isEmpty()) return
    val path = Path()
    val primaryColor = MaterialTheme.colorScheme.primary
    Canvas(modifier = modifier.fillMaxWidth().height(120.dp)) {
        val xInterval = if (data.size > 1) size.width / (data.size - 1) else size.width
        val maxBalance = data.maxOf { it.balance.toFloat() }
        val yInterval = if (maxBalance > 0) size.height / maxBalance else 0f
        
        path.moveTo(0f, size.height - data.first().balance.toFloat() * yInterval)
        data.forEachIndexed { index, pair ->
            val x = index * xInterval
            val y = size.height - pair.balance.toFloat() * yInterval
            path.lineTo(x, y)
        }
        drawPath(
            path = path,
            color = primaryColor,
            alpha = 0.5f,
            style = Stroke(width = 7f)
        )
    }
}


