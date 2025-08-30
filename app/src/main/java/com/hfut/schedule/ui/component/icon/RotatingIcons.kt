package com.hfut.schedule.ui.component.icon

import androidx.compose.animation.core.DurationBasedAnimationSpec
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.LoadingIndicatorDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.xah.uicommon.style.align.CenterScreen
import kotlinx.coroutines.delay

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


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoadingIconNew() {
    val originalList = LoadingIndicatorDefaults.IndeterminateIndicatorPolygons
    val newList = listOf(
        originalList[0],
        originalList[1],
        originalList[4],
        originalList[2],//五边形
        LoadingIndicatorDefaults.DeterminateIndicatorPolygons[0] //圆形
    )

    val time = 650
    var isLarge by remember { mutableStateOf(true) }
    val scale by animateFloatAsState(
        targetValue = if (isLarge) 1f else 0.9f,
        animationSpec = tween(durationMillis = time), label = ""
    )

    LaunchedEffect(Unit) {
        while (true) {
            isLarge = !isLarge
            delay(time.toLong()) // 动画交替间隔时间
        }
    }


    LoadingIndicator(
        modifier = Modifier
            .size(24.dp)
            .scale(scale),
        color = MaterialTheme.colorScheme.outline,
        polygons = newList,
    )
}



@Composable
fun WindmillIcon() = RotatingIcon(R.drawable.toys_fan)


//@Composable
//@Preview
//fun P() {
//    CenterScreen {
//        WindmillIcon()
//    }
//}