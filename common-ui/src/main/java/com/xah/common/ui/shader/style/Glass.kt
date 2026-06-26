package com.xah.common.ui.shader.style

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xah.common.ui.shader.ShaderState
import com.xah.common.ui.shader.recordPosition
import org.intellij.lang.annotations.Language

data class GlassStyle(
    val blur : Dp = 0.dp,
    val border : Float = 30f,
    val dispersion : Float = 0f,
    val distortFactor : Float = 0.075f,
    val stretchFactor : Float = 0.0f,
    val overlayColor : Color = Color.Transparent
)

val smallStyle = GlassStyle(
    blur = smallBlurRadius.dp,
    border = 30f,
    dispersion = 0f,
    distortFactor = largeDistortFactor,
)

val largeStyle = GlassStyle(
    blur  = mediumBlurRadius.dp,
    border  = 40f,
    dispersion  = 0f,
    distortFactor = smallDistortFactor,
)

const val largeDistortFactor = 0.1f
const val mediumDistortFactor = 0.05f
const val smallDistortFactor = 0.0f

const val smallBlurRadius = 3.5f
const val mediumBlurRadius = 5f
const val largeBlurRadius = 10f


// 绘制内容
fun Modifier.glassLayer(
    state: ShaderState,
    style: GlassStyle = largeStyle,
    enabled : Boolean = true,
) : Modifier =
    if(!enabled || style.overlayColor.alpha == 1f || Build.VERSION.SDK_INT < 31) {
        // 只有蒙版
        this.background(style.overlayColor)
    } else {
        composed {
            var rect by remember { mutableStateOf<Rect?>(null) }
            val localLayer = rememberGraphicsLayer()
            val density = LocalDensity.current
            val customRenderEffect =
            if(Build.VERSION.SDK_INT in 31 until 33) {
                // 只有模糊
                remember(style) {
                    val blurDp = with(density) { style.blur.toPx() }
                    RenderEffect.createBlurEffect(blurDp, blurDp, Shader.TileMode.CLAMP).asComposeRenderEffect()
                }
            } else {
                // 着色器
                remember(rect, style) {
                    if (rect == null) {
                        return@remember null
                    }

                    val runtimeShader = if(style.distortFactor != 0f) {
                        RuntimeShader(GLASS_SHADER_CODE_VERSION_1.trimIndent()).apply {
                            setFloatUniform("size", rect!!.width, rect!!.height)
                            setFloatUniform("border", style.border)
                            setFloatUniform("dispersion", style.dispersion)
                            setFloatUniform("distortFactor", style.distortFactor)
                        }
                    } else {
                        RuntimeShader(GLASS_SHADER_CODE_VERSION_2.trimIndent()).apply {
                            setFloatUniform("size", rect!!.width, rect!!.height)
                            setFloatUniform("border", style.border)
                            setFloatUniform("dispersion", style.dispersion)
                            setFloatUniform("stretchFactor",style.stretchFactor)
                        }
                    }


                    val enhanceEffect = enhanceColorShader(true)

                    val blurDp = with(density) { style.blur.toPx() }
                    val blurEffect =  RenderEffect.createBlurEffect(blurDp, blurDp, Shader.TileMode.CLAMP)

                    val mirrorShader = RenderEffect.createRuntimeShaderEffect(runtimeShader, "content")
                    val chained = RenderEffect.createChainEffect(enhanceEffect, mirrorShader)
                    RenderEffect.createChainEffect(blurEffect, chained).asComposeRenderEffect()
                }
            }

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
                        localLayer.renderEffect = customRenderEffect
                        rect?.let {
                            withTransform({
                                clipRect(0f, 0f, it.width, it.height)
                            }) {
                                // 裁切录制的内容
                                drawLayer(localLayer)
                                if (style.overlayColor.alpha > 0f) {
                                    drawRect(
                                        color = style.overlayColor,
                                        size = Size(it.width, it.height),
                                        alpha = style.overlayColor.alpha
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
                    rect = it
                }
        }
    }
// 版本2 待实现离心
@Language("AGSL")
private const val GLASS_SHADER_CODE_VERSION_2 = """
uniform shader content;
uniform float2 size;
uniform float border;       // 折射边缘宽度
uniform float dispersion;   // 色散强度
uniform float stretchFactor;// 拉伸系数（不带方向，纯粹向外）

half4 main(float2 fragCoord) {
    float2 innerMin = float2(border, border);
    float2 innerMax = size - innerMin;

    float2 sampleCoord;

    // 计算当前像素距离上下左右边界的距离
    float2 distToMin = fragCoord - innerMin;
    float2 distToMax = innerMax - fragCoord;

    // 判断当前像素最接近哪条边（0=左,1=右,2=上,3=下）
    bool isLeft   = distToMin.x < distToMax.x && distToMin.x <= min(distToMin.y, distToMax.y);
    bool isRight  = distToMax.x < distToMin.x && distToMax.x <= min(distToMin.y, distToMax.y);
    bool isTop    = distToMin.y < distToMax.y && distToMin.y <= min(distToMin.x, distToMax.x);
    bool isBottom = distToMax.y < distToMin.y && distToMax.y <= min(distToMin.x, distToMax.x);

    float2 stretchDir = float2(0.0);
    if (isLeft)   stretchDir = float2(-1.0, 0.0);
    if (isRight)  stretchDir = float2( 1.0, 0.0);
    if (isTop)    stretchDir = float2(0.0, -1.0);
    if (isBottom) stretchDir = float2(0.0,  1.0);

    // 主区域：直接拉伸
    if (fragCoord.x >= innerMin.x && fragCoord.x <= innerMax.x &&
        fragCoord.y >= innerMin.y && fragCoord.y <= innerMax.y) {

        float2 distToEdge = min(distToMin, distToMax);
        float edgeFactor = clamp(min(distToEdge.x, distToEdge.y) / border, 0.0, 1.0);
        sampleCoord = fragCoord + stretchDir * (-stretchFactor) * (1.0 - edgeFactor) * border;

    } else {
        // 边界区域：镜像 + 拉伸
        float2 clamped = clamp(fragCoord, innerMin, innerMax);
        float2 delta = fragCoord - clamped;
        float2 mirroredInside = clamp(clamped - delta, innerMin, innerMax);

        float2 distToEdgeMi = min(mirroredInside - innerMin, innerMax - mirroredInside);
        float edgeFactorMi = clamp(min(distToEdgeMi.x, distToEdgeMi.y) / border, 0.0, 1.0);

        sampleCoord = mirroredInside + stretchDir * (-stretchFactor) * (1.0 - edgeFactorMi) * border;
    }

    // ===============================
    // 色散处理（RGB 微偏移）
    // ===============================
    float2 dir = normalize(stretchDir);
    if (dir.x == 0.0 && dir.y == 0.0) dir = float2(0.0, 0.0);

    float2 redOffset   = clamp(sampleCoord + dir * dispersion * 0.5, innerMin, innerMax);
    float2 greenOffset = clamp(sampleCoord, innerMin, innerMax);
    float2 blueOffset  = clamp(sampleCoord - dir * dispersion * 0.5, innerMin, innerMax);

    half r = content.eval(redOffset).r;
    half g = content.eval(greenOffset).g;
    half b = content.eval(blueOffset).b;
    half a = content.eval(greenOffset).a;

    return half4(r, g, b, a);
}
"""
// 初版 无拉伸 有离心
@Language("AGSL")
private const val GLASS_SHADER_CODE_VERSION_1 = """
uniform shader content;
uniform float2 size;
uniform float border;   // 折射边缘宽度 
uniform float dispersion; // 色散强度
uniform float distortFactor; // 离心系数，越大扭曲越明显 (0.0~1.0)

half4 main(float2 fragCoord) {
    float2 innerMin = float2(border, border);
    float2 innerMax = size - innerMin;

    // 主体区域：完全不变
    if (fragCoord.x >= innerMin.x && fragCoord.x <= innerMax.x &&
        fragCoord.y >= innerMin.y && fragCoord.y <= innerMax.y) {
        return content.eval(fragCoord);
    }

    // 最近的内区点（在 innerRect 边上）
    float2 nearest = clamp(fragCoord, innerMin, innerMax);

    // 到内区边缘的距离（0..border）
    float dist = distance(fragCoord, nearest);
    float edgeFactor = clamp(dist / border, 0.0, 1.0);

    // --- 镜面对称采样点 ---
    float2 mirrored = 2.0 * nearest - fragCoord;

    // 中心点
    float2 center = size * 0.5;

    // 离心扭曲向量：越靠外，向四角拉伸
    float2 radial = (center - fragCoord) * distortFactor * edgeFactor; // 反向

    // 镜面采样加上离心扭曲
    float2 distorted = mirrored + radial;

    // 方向向量：从内区边缘指向当前像素，用于色散
    float2 dir = normalize(fragCoord - nearest);
    if (dir.x == 0.0 && dir.y == 0.0) dir = float2(0.0, 0.0);

    // 色散偏移
    float2 redOffset   = distorted + dir * dispersion * 0.5;
    float2 greenOffset = distorted;
    float2 blueOffset  = distorted - dir * dispersion * 0.5;

    // 保证采样点在内区
    redOffset   = clamp(redOffset, innerMin, innerMax);
    greenOffset = clamp(greenOffset, innerMin, innerMax);
    blueOffset  = clamp(blueOffset, innerMin, innerMax);

    // 分通道采样
    half r = content.eval(redOffset).r;
    half g = content.eval(greenOffset).g;
    half b = content.eval(blueOffset).b;
    half a = content.eval(distorted).a; // alpha 保持原样

    return half4(r, g, b, a);
}
"""
