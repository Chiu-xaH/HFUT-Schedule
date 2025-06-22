package com.hfut.schedule.ui.style

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.ui.component.CenterScreen

// 色散效果（RGB 分散）
private const val DISPERSION_SHADER = """
uniform shader image;

uniform float2 size;
uniform float cornerRadius;
uniform float dispersionHeight;

float lerp(float a, float b, float t) {
    return a + (b - a) * t;
}

float2 normalToTangent(float2 normal) {
    return float2(normal.y, -normal.x);
}

float circleMap(float x) {
    return 1.0 - sqrt(1.0 - x * x);
}

float sdRectangle(float2 coord, float2 halfSize) {
    float2 d = abs(coord) - halfSize;
    float outside = length(max(d, 0.0));
    float inside = min(max(d.x, d.y), 0.0);
    return outside + inside;
}

float sdRoundedRectangle(float2 coord, float2 halfSize, float radius) {
    return sdRectangle(coord, halfSize) - radius;
}

const float eps = 0.5;
const float2 epsX = float2(eps, 0.0);
const float2 epsY = float2(0.0, eps);

float2 gradSdRoundedRectangle(float2 coord, float2 halfSize, float radius) {
    float dx = sdRoundedRectangle(coord + epsX, halfSize, radius)
        - sdRoundedRectangle(coord - epsX, halfSize, radius);
    float dy = sdRoundedRectangle(coord + epsY, halfSize, radius)
        - sdRoundedRectangle(coord - epsY, halfSize, radius);
    return normalize(float2(dx, dy));
}

half4 main(float2 coord) {
    float2 halfSize = size * 0.5;
    float2 centeredCoord = coord - halfSize;
    float2 innerHalfSize = halfSize - float2(cornerRadius);
    float sd = sdRoundedRectangle(centeredCoord, innerHalfSize, cornerRadius);

    if (sd < 0.0 && -sd < dispersionHeight) {
        float2 normal = gradSdRoundedRectangle(centeredCoord, innerHalfSize, cornerRadius);
        float2 tangent = normalToTangent(normal);

        half4 dispersedColor = half4(0.0);
        half4 weight = half4(0.0);

        float dispersionFraction = 1.0 - -sd / dispersionHeight;
        float dispersionWidth = dispersionHeight * 2.0 * pow(circleMap(dispersionFraction), 2.0);
        if (dispersionWidth < 2.0) {
            return image.eval(coord);
        }

        float maxI = min(dispersionWidth, 100.0);
        for (float i = 0.0; i < 100.0; i++) {
            float t = i / maxI;
            if (t > 1.0) break;
            half4 color = image.eval(coord + tangent * float2(t - 0.5) * dispersionWidth);
            if (t >= 0.0 && t < 0.5) {
                dispersedColor.b += color.b;
                weight.b += 1.0;
            }
            if (t > 0.25 && t < 0.75) {
                dispersedColor.g += color.g;
                weight.g += 1.0;
            }
            if (t > 0.5 && t <= 1.0) {
                dispersedColor.r += color.r;
                weight.r += 1.0;
            }
        }
        dispersedColor /= weight;
        dispersedColor.a = image.eval(coord).a;
        return dispersedColor;
    } else {
        return image.eval(coord);
    }
}
"""

// 饱和度增强
private const val CHROMA_SHADER = """
uniform shader image;
uniform float chromaMultiplier;

const half3 rgbToY = half3(0.299, 0.587, 0.114);

half3 saturateColor(half3 color, float amount) {
    half luma = dot(color, rgbToY);
    return clamp(mix(half3(luma), color, amount), 0.0, 1.0);
}

half4 main(float2 coord) {
    half4 color = image.eval(coord);
    color.rgb = saturateColor(color.rgb, chromaMultiplier);
    return color;
}
"""

// 折射效果（玻璃变形）
private const val REFRACTION_SHADER = """
uniform shader image;

uniform float2 size;
uniform float cornerRadius;
uniform float refractionHeight;
uniform float refractionAmount;
uniform float eccentricFactor;

float circleMap(float x) {
    return 1.0 - sqrt(1.0 - x * x);
}

float sdRectangle(float2 coord, float2 halfSize) {
    float2 d = abs(coord) - halfSize;
    float outside = length(max(d, 0.0));
    float inside = min(max(d.x, d.y), 0.0);
    return outside + inside;
}

float sdRoundedRectangle(float2 coord, float2 halfSize, float radius) {
    return sdRectangle(coord, halfSize) - radius;
}

const float eps = 0.5;
const float2 epsX = float2(eps, 0.0);
const float2 epsY = float2(0.0, eps);

float2 gradSdRoundedRectangle(float2 coord, float2 halfSize, float radius) {
    float dx = sdRoundedRectangle(coord + epsX, halfSize, radius)
        - sdRoundedRectangle(coord - epsX, halfSize, radius);
    float dy = sdRoundedRectangle(coord + epsY, halfSize, radius)
        - sdRoundedRectangle(coord - epsY, halfSize, radius);
    return normalize(float2(dx, dy));
}

half4 main(float2 coord) {
    float2 halfSize = size * 0.5;
    float2 centeredCoord = coord - halfSize;
    float2 innerHalfSize = halfSize - float2(cornerRadius);
    float sd = sdRoundedRectangle(centeredCoord, innerHalfSize, cornerRadius);

    if (sd < 0.0 && -sd < refractionHeight) {
        float2 normal = gradSdRoundedRectangle(centeredCoord, innerHalfSize, cornerRadius);
        float refractedDistance = circleMap(1.0 - -sd / refractionHeight) * refractionAmount;
        float2 refractedDirection = normalize(normal + eccentricFactor * normalize(centeredCoord));
        float2 refractedCoord = coord + refractedDistance * refractedDirection;
        return image.eval(refractedCoord);
    } else {
        return image.eval(coord);
    }
}
"""

/**
 * 液态玻璃
 *
 * @param modifier 外部修饰符，例如布局位置、大小等
 * @param cornerRadius 圆角半径，影响玻璃边界形状
 * @param dispersionHeight 色散高度，用于控制 RGB 分离区域范围（越大越明显）
 * @param refractionHeight 折射影响区域的高度，决定多少距离内发生折射变形
 * @param refractionAmount 折射强度，越大图像变形越明显（玻璃弯曲程度）
 * @param eccentricFactor 折射偏心系数，控制折射方向偏向中心或边缘（立体感）
 * @param blurRadius 模糊半径，用于产生背景模糊效果（背景虚化）
 * @param chromaMultiplier 饱和度增强因子，>1 表示色彩增强，<1 表示去饱和
 * @param opacity 不透明度，0 表示完全透明，1 表示完全不透明（可以用于黑色蒙版）
 * @param content 组件内部的内容区域
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Modifier.glass(
    dispersionHeight: Dp = 20.dp,
    refractionHeight: Dp = 25.dp,
    refractionAmount: Dp = 25.dp,
    eccentricFactor: Float = -25f,
    blurRadius: Dp = 0.dp,
    chromaMultiplier: Float = 1.5f,
    opacity: Float = 0.00f,
) : Modifier {
    val density = LocalDensity.current
    var rect by remember { mutableStateOf(Rect.Zero) }

    return this
        .onGloballyPositioned {
            rect = it.boundsInParent()
        }
        .drawBehind {
            drawRect(Color.Black.copy(alpha = opacity))
        }
        .graphicsLayer {
            // dispersion
            renderEffect = createDispersionEffect(
                size = size,
                dispersionHeight = dispersionHeight
            )
        }
        .graphicsLayer {
            // chroma
            renderEffect = createChromaBoostEffect(chromaMultiplier)
        }
        .graphicsLayer {
            // refraction
            renderEffect = createGlassRefractionEffect(
                size = size,
                refractionHeight = refractionHeight,
                refractionAmount = refractionAmount,
                eccentricFactor = eccentricFactor
            )
        }
        .graphicsLayer {
            val blur = with(density) { blurRadius.toPx() }
            if (blur > 0f) {
                renderEffect = RenderEffect.createBlurEffect(
                    blur, blur, Shader.TileMode.DECAL
                ).asComposeRenderEffect()
            }
        }
}



@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun createDispersionEffect(
    size: Size,
    dispersionHeight: Dp
): androidx.compose.ui.graphics.RenderEffect {
    val shader = RuntimeShader(DISPERSION_SHADER)
    shader.setFloatUniform("size", size.width, size.height)
    shader.setFloatUniform("dispersionHeight", dispersionHeight.value)
    return RenderEffect.createRuntimeShaderEffect(shader, "image").asComposeRenderEffect()
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun createChromaBoostEffect(chromaMultiplier: Float): androidx.compose.ui.graphics.RenderEffect {
    val shader = RuntimeShader(CHROMA_SHADER)
    shader.setFloatUniform("chromaMultiplier", chromaMultiplier)
    return RenderEffect.createRuntimeShaderEffect(shader, "image").asComposeRenderEffect()
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun createGlassRefractionEffect(
    size: Size,
    refractionHeight: Dp,
    refractionAmount: Dp,
    eccentricFactor: Float
): androidx.compose.ui.graphics.RenderEffect {
    val shader = RuntimeShader(REFRACTION_SHADER)
    shader.setFloatUniform("size", size.width, size.height)
    shader.setFloatUniform("refractionHeight", refractionHeight.value)
    shader.setFloatUniform("refractionAmount", refractionAmount.value)
    shader.setFloatUniform("eccentricFactor", eccentricFactor)
    return RenderEffect.createRuntimeShaderEffect(shader, "image").asComposeRenderEffect()
}

@Composable
@Preview
fun m() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.size(300.dp).align(Alignment.Center).glass()) {
            LazyColumn {
                items(100) {
                    LazyRow {
                        items(10) {
                            Icon(painterResource(R.drawable.hfut), tint = MaterialTheme.colorScheme.primary, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun m2() {
    val color = MaterialTheme.colorScheme.primary
    CenterScreen {
        Canvas(modifier = Modifier.fillMaxSize()) {
//            drawRect(
//                color = color,
//                size = size/2f
//            )
            val canvasWidth = size.width
            val canvasHeight = size.height
            drawLine(
                start = Offset(x = canvasWidth, y = 0f),
                end = Offset(x = 0f, y = canvasHeight),
                color = color,
                strokeWidth = 20f
            )
        }
    }
}