package com.hfut.schedule.ui.util.shader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow

@Composable
fun rememberShaderState(): ShaderState {
    val graphicsLayer = rememberGraphicsLayer()
    return remember(graphicsLayer) {
        ShaderState(graphicsLayer)
    }
}

class ShaderState internal constructor(
    internal val graphicsLayer: GraphicsLayer,
) {
    // 裁剪形状
    internal var rect: Rect? by mutableStateOf(null)
}

// 记录内容
fun Modifier.shaderSource(
    state : ShaderState
) : Modifier =
    this
        .drawWithContent {
            drawContent()

            state.graphicsLayer.record {
                val bounds = state.rect ?: return@record
                withTransform({
                    // 裁剪只录自己范围
                    clipRect(0f, 0f, bounds.width, bounds.height)
                }) {
                    this@drawWithContent.drawContent()
                }
            }
        }
        .onGloballyPositioned { layoutCoordinates ->
            state.rect = layoutCoordinates.boundsInRoot()
        }


fun Modifier.recordPosition(
    onResult : (Rect) -> Unit
) = this.onGloballyPositioned { layoutCoordinates ->
    val pos = layoutCoordinates.positionInWindow()
    val size = layoutCoordinates.size
    onResult(Rect(
        pos.x,
        pos.y,
        pos.x + size.width,
        pos.y + size.height
    ))
}
