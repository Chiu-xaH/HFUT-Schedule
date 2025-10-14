package com.hfut.schedule.ui.util.shader

import android.graphics.RenderEffect
import android.graphics.Shader
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import java.util.UUID

// 层级模糊

// 绘制内容
fun Modifier.blurLayer(
    state: ShaderState,
    clipShape: Shape,
) : Modifier = composed {
    var rect by remember { mutableStateOf<Rect?>(null) }
    this
        .clip(clipShape)
        .drawWithCache {
            onDrawBehind {
                val contentRect = state.rect ?: return@onDrawBehind
                val surfaceRect = rect ?: return@onDrawBehind

                val offset = surfaceRect.topLeft - contentRect.topLeft

                Log.d("当前组件",surfaceRect.toString())
                drawRect(
                    color = Color.Green,
                    topLeft = offset,
                    size = Size(surfaceRect.width, surfaceRect.height)
                )

                withTransform({
                    translate(-offset.x, -offset.y)
                    clipRect(0f, 0f, surfaceRect.width, surfaceRect.height)
                }) {
                    drawLayer(state.graphicsLayer)
                }
            }
        }
        .recordPosition {
            rect = it
        }
}

// 记录内容
fun Modifier.blurSource(
    state : ShaderState,
    blur : Dp,
) : Modifier =
    this
        .drawWithContent {
            drawContent()

            state.graphicsLayer.record {
                this@drawWithContent.drawContent()
            }

            val blurEffect = RenderEffect.createBlurEffect(blur.toPx(), blur.toPx(), Shader.TileMode.CLAMP)
            val enhanceEffect = enhanceColorShader(blur != 0.dp)
            val chained = RenderEffect.createChainEffect(enhanceEffect, blurEffect)

            state.graphicsLayer.renderEffect = chained.asComposeRenderEffect()
        }
        .onGloballyPositioned { layoutCoordinates ->
            state.rect = layoutCoordinates.boundsInRoot()
        }


