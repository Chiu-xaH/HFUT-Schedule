package com.hfut.schedule.ui.screen.card.count

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.logic.model.huixin.BillMonth
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

//消费折线图
@Composable
fun drawLineChart(data: List<BillMonth>) {
    val path = Path()
    val size = Size(300f, 300f)
    val primaryColor = MaterialTheme.colorScheme.primary
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
            color = primaryColor,
            alpha = 0.5f,
            style = Stroke(width = 7f)
        )
    }
}


