package com.xah.uicommon.style

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

enum class ClickScale(val scale : Float) {
    SMALL(0.9F),MEDIUM(0.95F)
}

fun Modifier.clickableWithScale(
    pressedScale: Float = ClickScale.MEDIUM.scale,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1f,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "pressScale"
    )

    this
        .graphicsLayer(
            scaleX = scale,
            scaleY = scale
        )
        .clickable(
            interactionSource = interactionSource,
            indication = LocalIndication.current,
            onClick = onClick
        )
}

fun Modifier.clickableWithRotation(
    pressedScale: Float = 0.95f,
    maxTilt: Float = 10f,
    onClick: () -> Unit
): Modifier = composed {
    var scale by remember { mutableFloatStateOf(1f) }
    var rotationX by remember { mutableFloatStateOf(0f) }
    var rotationY by remember { mutableFloatStateOf(0f) }

    // 使用 animateFloatAsState 来平滑过渡
    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        )
    )

    val animatedRotationX by animateFloatAsState(
        targetValue = rotationX,
        animationSpec = spring(
            stiffness = Spring.StiffnessMedium,
            dampingRatio = Spring.DampingRatioMediumBouncy
        )
    )

    val animatedRotationY by animateFloatAsState(
        targetValue = rotationY,
        animationSpec = spring(
            stiffness = Spring.StiffnessMedium,
            dampingRatio = Spring.DampingRatioMediumBouncy
        )
    )

    this.pointerInput(Unit) {
        detectTapGestures(
            onPress = { offset ->
                // offset 是按压位置
                val size = size
                val centerX = size.width / 2
                val centerY = size.height / 2
                val offsetX = (offset.x - centerX) / centerX
                val offsetY = (centerY - offset.y) / centerY

                // 设置按压时的缩放和旋转
                scale = pressedScale
                rotationX = maxTilt * offsetY
                rotationY = maxTilt * offsetX

                try {
                    // 等待松手或取消
                    awaitRelease()
                } finally {
                    // 回弹时恢复到初始状态
                    scale = 1f
                    rotationX = 0f
                    rotationY = 0f
                }

                onClick()
            }
        )
    }.graphicsLayer {
        this.scaleX = animatedScale
        this.scaleY = animatedScale
        this.rotationX = animatedRotationX
        this.rotationY = animatedRotationY
        this.cameraDistance = 12 * density
    }
}


fun Modifier.clickableWithRotation(
    onClick: () -> Unit
): Modifier = composed {
    var scale by remember { mutableFloatStateOf(1f) }
    var rotationX by remember { mutableFloatStateOf(0f) }
    var rotationY by remember { mutableFloatStateOf(0f) }

    // 使用 animateFloatAsState 来平滑过渡
    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        )
    )

    val animatedRotationX by animateFloatAsState(
        targetValue = rotationX,
        animationSpec = spring(
            stiffness = Spring.StiffnessMedium,
            dampingRatio = Spring.DampingRatioMediumBouncy
        )
    )

    val animatedRotationY by animateFloatAsState(
        targetValue = rotationY,
        animationSpec = spring(
            stiffness = Spring.StiffnessMedium,
            dampingRatio = Spring.DampingRatioMediumBouncy
        )
    )

    this.pointerInput(Unit) {
        detectTapGestures(
            onPress = { offset ->
                val size = size
                val centerX = size.width / 2
                val centerY = size.height / 2

                // 计算动态缩放比例，使其组件越小，scale 越小
                val minSize = 40.dp.toPx()  // 最小尺寸
                val maxSize = 400.dp.toPx() // 最大尺寸
                val compSize = minOf(size.width, size.height)

                // 线性映射到 0.8 ~ 0.95 范围，组件越小，scale 越小
                val dynamicScale = (compSize - minSize).coerceIn(0f, maxSize - minSize) / (maxSize - minSize) * 0.05f + 0.9f

                // 动态调整 maxTilt，组件越大，maxTilt 越小（范围：5 到 20）
                val dynamicMaxTilt = 15f - (compSize - minSize).coerceIn(0f, maxSize - minSize) / (maxSize - minSize) * 10f
                // 显示动态的缩放和倾斜值
//                showToast("Scale: $dynamicScale, MaxTilt: $dynamicMaxTilt")

                val offsetX = (offset.x - centerX) / centerX
                val offsetY = (centerY - offset.y) / centerY

                // 设置按压时的缩放和旋转
                scale = dynamicScale
                rotationX = dynamicMaxTilt * offsetY
                rotationY = dynamicMaxTilt * offsetX

                try {
                    // 等待松手或取消
                    awaitRelease()
                } finally {
                    // 回弹时恢复到初始状态
                    scale = 1f
                    rotationX = 0f
                    rotationY = 0f
                }

                onClick()
            }
        )
    }.graphicsLayer {
        this.scaleX = animatedScale
        this.scaleY = animatedScale
        this.rotationX = animatedRotationX
        this.rotationY = animatedRotationY
        this.cameraDistance = 12 * density
    }
}