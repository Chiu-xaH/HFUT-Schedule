package com.hfut.schedule.ui.util

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.other.AppVersion
import org.intellij.lang.annotations.Language


// 绘制自身
fun Modifier.shaderSelf(
    scale: Float = 1f,
    clipShape: Shape = RoundedCornerShape(0.dp),
): Modifier =
    if(scale == 1f || !AppVersion.CAN_SHADER) {
        this
    } else {
        composed {
            // 绘制面
            var rect by remember { mutableStateOf<Rect?>(null) }

            this
                .graphicsLayer {
                    clip = true
                    shape = clipShape
                    rect?.let { r ->
                        val runtimeShader = RuntimeShader(SHADER_CODE.trimIndent())
                        runtimeShader.setFloatUniform("size", r.width, r.height)
                        runtimeShader.setFloatUniform("scale", scale)

                        renderEffect = RenderEffect
                            .createRuntimeShaderEffect(runtimeShader, "content")
                            .asComposeRenderEffect()
                    }
                }
                .onGloballyPositioned { layoutCoordinates ->
                    val pos = layoutCoordinates.positionInWindow()
                    val size = layoutCoordinates.size
                    rect = Rect(
                        pos.x,
                        pos.y,
                        pos.x + size.width,
                        pos.y + size.height
                    )
                }
        }
    }


// 记录内容
fun Modifier.shaderSource(
    state : ShaderState
) : Modifier =
    if(!AppVersion.CAN_SHADER) {
        this
    } else {
        this
            .drawWithContent {
                drawContent()
                val bounds = state.rect ?: return@drawWithContent

                state.graphicsLayer.record {
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
    }


// 绘制内容
fun Modifier.shaderLayer(
    state: ShaderState,
    scale : Float,
    clipShape: Shape,
    tint : Color,
    blur : Dp = 0.dp,
) : Modifier =
    this
        .clip(clipShape)
        .drawWithCache {
            onDrawWithContent {
                drawContent()
                drawRect(tint)
            }
        }
        .blur(blur)
        .shaderLayer(state,scale)


// 绘制内容
fun Modifier.shaderLayer(
    state: ShaderState,
    scale : Float,
) : Modifier =
    if(!AppVersion.CAN_SHADER) {
        this
    } else {
        this
            .graphicsLayer {
                state.sRect?.let { r ->
                    val runtimeShader = RuntimeShader(SHADER_CODE.trimIndent())
                    runtimeShader.setFloatUniform("size", r.width, r.height)
                    runtimeShader.setFloatUniform("scale", scale)

                    val mirrorShader = RenderEffect.createRuntimeShaderEffect(runtimeShader, "content")
                    renderEffect = mirrorShader.asComposeRenderEffect()
                }
                clip = true
            }
            .drawWithCache {
                onDrawBehind {
                    val contentRect = state.rect ?: return@onDrawBehind
                    val surfaceRect = state.sRect ?: return@onDrawBehind

                    val offset = surfaceRect.topLeft - contentRect.topLeft
                    // 绘制原画面
                    withTransform({
                        translate(-offset.x, -offset.y)
                    }) {
                        drawLayer(state.graphicsLayer)
                    }
                }
            }
            // 记录位置
            .onGloballyPositioned { layoutCoordinates ->
                val pos = layoutCoordinates.positionInWindow()
                val size = layoutCoordinates.size
                state.sRect = Rect(
                    pos.x,
                    pos.y,
                    pos.x + size.width,
                    pos.y + size.height
                )
            }
    }


@Composable
fun rememberShaderState(): ShaderState {
    val graphicsLayer = rememberGraphicsLayer()
    return remember(graphicsLayer) {
        ShaderState(graphicsLayer)
    }
}

class ShaderState internal constructor(
    internal val graphicsLayer: GraphicsLayer
) {
    // 裁剪形状
    internal var rect: Rect? by mutableStateOf(null)
    internal var sRect: Rect? by mutableStateOf(null)
}

@Language("ASGL")
private const val SHADER_CODE = """
    uniform shader content;
    uniform float2 size;   // 原始画面宽高
    uniform float scale;   // 缩放比例，
        
    half4 main(float2 fragCoord) {
        float2 center = size * 0.5;
        float2 offset = fragCoord - center;
        
        // 缩放
        float2 scaled = offset / scale;
        float2 sampleCoord = center + scaled;
        
        // 镜面反射逻辑
        if(sampleCoord.x < 0.0) sampleCoord.x = -sampleCoord.x;
        if(sampleCoord.x > size.x) sampleCoord.x = 2.0*size.x - sampleCoord.x;
        
        if(sampleCoord.y < 0.0) sampleCoord.y = -sampleCoord.y;
        if(sampleCoord.y > size.y) sampleCoord.y = 2.0*size.y - sampleCoord.y;
        
        return content.eval(sampleCoord);
    }
"""
