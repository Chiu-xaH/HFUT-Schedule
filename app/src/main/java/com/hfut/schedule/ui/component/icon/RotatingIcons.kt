package com.hfut.schedule.ui.component.icon

import androidx.compose.animation.core.DurationBasedAnimationSpec
import androidx.compose.animation.core.Easing
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
import com.xah.uicommon.style.align.CenterScreen

@Composable
fun RotatingIcon(icon: Int, animation: DurationBasedAnimationSpec<Float> = tween(durationMillis = 1000,easing = LinearEasing)) {
    // 使用 rememberInfiniteTransition 创建一个无限动画
    val infiniteTransition = rememberInfiniteTransition(label = "")

    // 定义旋转动画，角度从 0 到 360 度
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = animation,
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



@Composable
fun WindmillIcon() = RotatingIcon(R.drawable.toys_fan)


//@Composable
//@Preview
//fun P() {
//    CenterScreen {
//        WindmillIcon()
//    }
//}