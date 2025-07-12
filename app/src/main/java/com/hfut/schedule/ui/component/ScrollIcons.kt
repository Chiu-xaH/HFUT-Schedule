package com.hfut.schedule.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R

@Composable
fun RotatingIcon(icon: Int) {
    // 使用 rememberInfiniteTransition 创建一个无限动画
    val infiniteTransition = rememberInfiniteTransition(label = "")

    // 定义旋转动画，角度从 0 到 360 度
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    // 使用 Modifier.rotate 应用旋转效果
    Icon(
        painterResource(icon),
        contentDescription = null,
        modifier = Modifier.rotate(rotation)
    )
}

@Composable
fun LoadingIcon() = RotatingIcon(R.drawable.progress_activity)