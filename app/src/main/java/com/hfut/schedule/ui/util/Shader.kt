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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.other.AppVersion
import org.intellij.lang.annotations.Language


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
                state.graphicsLayer.record {
                    this@drawWithContent.drawContent()
                }
            }
            .onGloballyPositioned { layoutCoordinates ->
                state.rect = layoutCoordinates.boundsInRoot()
            }
    }


// 绘制内容
fun Modifier.shaderLayer(
    state: ShaderState,
    clipShape: Shape,
) : Modifier =
    if(!AppVersion.CAN_SHADER) {
        this
    } else {
        this
            .graphicsLayer {
                clip = true
                shape = clipShape
            }
            .drawWithCache {
                // 模糊与反射混合效果
                val blurEffect = RenderEffect.createBlurEffect(
                    0f,0f, Shader.TileMode.CLAMP
                )

                val runtimeShader = RuntimeShader(SHADER_CODE.trimIndent())

                onDrawBehind {
                    val contentRect = state.rect ?: return@onDrawBehind
                    val surfaceRect = state.sRect ?: return@onDrawBehind

                    val offset = surfaceRect.topLeft - contentRect.topLeft

                    val renderEffect = RenderEffect
                        .createRuntimeShaderEffect(runtimeShader, "content")
                        .apply {
                            runtimeShader.setFloatUniform("size", contentRect.width,contentRect.height)
                            runtimeShader.setFloatUniform("scale", 0.9f)
                        }
                    val combinedEffect = RenderEffect.createChainEffect(
                        blurEffect,
                        renderEffect,
                    )

                    val effect = combinedEffect.asComposeRenderEffect()
                    state.graphicsLayer.renderEffect = effect

                    withTransform({
                        translate(-offset.x, -offset.y)
                        clipRect(0f, 0f, contentRect.width, contentRect.height)
                    }) {
                        drawLayer(state.graphicsLayer)
                    }
                }
            }
            .onGloballyPositioned { layoutCoordinates ->
                // 记录位置
                state.sRect = layoutCoordinates.boundsInRoot()
            }
    }



// 绘制内容
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
                    rect = layoutCoordinates.boundsInRoot()
                }
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