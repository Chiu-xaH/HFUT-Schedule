package com.xah.container.container

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xah.common.ScreenCornerHelper
import com.xah.container.model.ContainerFilledStrategy
import com.xah.container.utils.LocalSharedRegistry

fun Modifier.sharedContainer(
    key : Any,
    shape : Shape,
    containerColor : Color?,
    shadow : Dp = 0.dp,
): Modifier {
    return this
        .shadow(shadow,shape)
        .clip(shape)
        .sharedContainer(
            key,
            if(containerColor == null) {
                ContainerFilledStrategy.Pixel(ContainerFilledStrategy.Clip)
            } else {
                ContainerFilledStrategy.Pixel(ContainerFilledStrategy.Color(containerColor))
            },
            shape as CornerBasedShape
        )
}

fun Modifier.sharedContainer(
    key : Any,
    shape : Shape,
    shadow : Dp = 0.dp,
    containerFilledStrategy : ContainerFilledStrategy = ContainerFilledStrategy.Pixel(),
): Modifier {
    return this
        .shadow(shadow,shape)
        .clip(shape)
        .sharedContainer(key,containerFilledStrategy,shape as CornerBasedShape)
}

private fun Modifier.sharedContainer(
    key : Any,
    containerFilledStrategy : ContainerFilledStrategy,
    corner : CornerBasedShape,
): Modifier = composed {
    val registry = LocalSharedRegistry.current
    if(!registry.enabled) {
        return@composed this
    }
    val state = remember { registry.getOrCreate(key) }
    val graphicsLayer = rememberGraphicsLayer()
    val graphicsLayerForPixel = if(containerFilledStrategy.getFinalStrategy(registry.enableShader) is ContainerFilledStrategy.Pixel) {
        rememberGraphicsLayer()
    } else {
        null
    }

    LaunchedEffect(Unit) {
        state.containerFilledStrategy = containerFilledStrategy
        state.containerCorner = corner
        state.containerLayerForPixel = graphicsLayerForPixel
        state.containerLayer = graphicsLayer
    }

    return@composed this
        .drawWithContent {
            // 隐藏原组件
            if (!state.isRunning) {
                drawContent()
            }
            if (state.isRunning) {
                graphicsLayerForPixel?.record {
                    this@drawWithContent.drawContent()
                }
                graphicsLayer.record {
                    this@drawWithContent.drawContent()
                }
            }
        }
        // 记录两个组件的位置、大小
        .onGloballyPositioned { coordinates ->
            val position = coordinates.positionInRoot()
            val size = coordinates.size

            state.containerRect = Rect(
                left = position.x,
                top = position.y,
                right = position.x + size.width,
                bottom = position.y + size.height
            )
        }
}

fun Modifier.sharedContent(
    key : Any,
    shape : CornerBasedShape,
): Modifier = composed {
    val registry = LocalSharedRegistry.current
    if(!registry.enabled) {
        return@composed this
    }

    val state = remember { registry.get(key) }
    if(state == null) {
        return@composed this
    }
    val graphicsLayer = rememberGraphicsLayer()

    LaunchedEffect(Unit) {
        state.contentCorner = shape
        state.contentLayer = graphicsLayer
    }

    this
        .drawWithContent {
            // 隐藏原组件
            if (!state.isRunning) {
                drawContent()
            }
            if (state.isRunning) {
                graphicsLayer.record {
                    this@drawWithContent.drawContent()
                }
            }
        }
        // 记录组件的位置、大小
        .onGloballyPositioned { coordinates ->
            val position = coordinates.positionInRoot()
            val size = coordinates.size

            state.contentRect = Rect(
                left = position.x,
                top = position.y,
                right = position.x + size.width,
                bottom = position.y + size.height
            )
        }
}

/**
 * 共享容器的内容
 * @param key 两个容器之间的Key
 * @param shape 屏幕圆角
 */
@Composable
fun SharedContent(
    key : Any,
    modifier : Modifier = Modifier,
    shape: Shape = RoundedCornerShape(ScreenCornerHelper.corner),
    content : @Composable () -> Unit
)  {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier.sharedContent(key,shape as CornerBasedShape)
        ) {
            content()
        }
    }
}

/** 共享容器的容器
 * @param key 两个容器之间的Key
 * @param containerFilledStrategy 容器填充策略
 * @param shape 容器圆角
 */
@Composable
fun SharedContainer(
    key : Any,
    shape : Shape,
    modifier : Modifier = Modifier,
    shadow : Dp = 0.dp,
    containerFilledStrategy : ContainerFilledStrategy = ContainerFilledStrategy.Pixel(),
    content : @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(shadow,shape)
            .clip(shape)
    ) {
        Box(
            modifier = Modifier.sharedContainer(key, containerFilledStrategy, shape as CornerBasedShape)
        ) {
            content()
        }
    }
}

/** 共享容器的容器
 * @param key 两个容器之间的Key
 * @param containerColor 优先使用底部1像素填充，SDK低于33时若containerColor为null则使用填充方案，否则使用containerColor填充
 * @param shape 容器圆角
 */
@Composable
fun SharedContainer(
    key : Any,
    shape : Shape,
    modifier : Modifier = Modifier,
    shadow : Dp = 0.dp,
    containerColor : Color?,
    content : @Composable () -> Unit
) = SharedContainer(
    key,
    shape,
    modifier,
    shadow,
    if(containerColor == null) {
        ContainerFilledStrategy.Pixel(ContainerFilledStrategy.Clip)
    } else {
        ContainerFilledStrategy.Pixel(ContainerFilledStrategy.Color(containerColor))
    },
    content
)