package com.xah.common.ui.shader

import android.graphics.RenderEffect
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
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
                    // 录全屏
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
// 自定义效果
fun Modifier.shaderLayer(
    state: ShaderState,
    overlayColor : Color,
    renderEffect :  RenderEffect?,
    rect : Rect?,
    enabled : Boolean = true,
    onRect : (Rect) -> Unit,
) : Modifier =
    if(!enabled || overlayColor.alpha == 1f || Build.VERSION.SDK_INT < 33) {
        // 只有蒙版
        this.background(overlayColor)
    } else {
        composed {
            val localLayer = rememberGraphicsLayer()

            this
                .drawWithCache {
                    onDrawWithContent {
                        localLayer.apply {
                            val contentRect = state.rect ?: return@apply
                            val surfaceRect = rect ?: return@apply
                            val offset = surfaceRect.topLeft - contentRect.topLeft

                            record {
                                withTransform({
                                    translate(-offset.x, -offset.y)
                                }) {
                                    drawLayer(state.graphicsLayer)
                                }
                            }
                        }
                        localLayer.renderEffect = renderEffect?.asComposeRenderEffect()
                        rect?.let {
                            withTransform({
                                clipRect(0f, 0f, it.width, it.height)
                            }) {
                                // 裁切录制的内容
                                drawLayer(localLayer)
                                if (overlayColor.alpha > 0f) {
                                    drawRect(
                                        color = overlayColor,
                                        size = Size(it.width, it.height),
                                        alpha = overlayColor.alpha
                                    )
                                }
                            }
                        }
                        // 原内容
                        drawContent()
                    }
                }
                // 记录位置
                .recordPosition {
                    onRect(it)
                }
        }
    }



