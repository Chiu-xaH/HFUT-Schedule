package com.hfut.schedule.ui.style.shader

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import com.xah.shader.state.recordPosition
import org.intellij.lang.annotations.Language
import kotlin.math.abs


fun Modifier.enterAnimation(
    show : Boolean,
    anchorY : Float = 0.35f,
    animationSpec: AnimationSpec<Float> = tween(800),
) : Modifier = composed {
    require(anchorY in 0f..1f) { "anchorY must be between 0f and 1f, but was $anchorY" }

    val maxDistortFactor = 1f-abs((0.5f - anchorY))

    val distortFactor by animateFloatAsState(
        if(!show) maxDistortFactor else 0f,
        animationSpec
    )
    val dispersion by animateFloatAsState(
        if(!show) 5f else 0f,
        animationSpec
    )

    this
        .centerSelf(distortFactor, dispersion, Offset(0.5f,anchorY))
}


fun Modifier.centerSelf(
    distortFactor : Float = 0f,
    dispersion : Float = 0f,
    anchor : Offset = Offset(0.5f,0.5f)
): Modifier =
    if(Build.VERSION.SDK_INT < 33 || distortFactor == 0f) {
        this
    } else {
        composed {
            // 绘制面
            var rect by remember { mutableStateOf<Rect?>(null) }
            this
                .graphicsLayer {
                    clip = true
                    rect?.let { r ->
                        val runtimeShader =  RuntimeShader(CENTER_SHADER_CODE.trimIndent()).apply {
                            setFloatUniform("size", r.width, r.height)
                            setFloatUniform("dispersion", dispersion)
                            setFloatUniform("distortFactor", distortFactor)
                            setFloatUniform("anchor",anchor.x,anchor.y)
                        }
                        renderEffect = RenderEffect
                            .createRuntimeShaderEffect(runtimeShader, "content")
                            .asComposeRenderEffect()
                    }
                }
                .recordPosition {
                    rect = it
                }
        }
    }

@Language("AGSL")
private const val CENTER_SHADER_CODE = """
uniform shader content;
uniform float2 size;
uniform float2 anchor;       // 锚点，x,y 范围 0~1
uniform float dispersion;    // 色散强度
uniform float distortFactor; // 离心系数

half4 main(float2 fragCoord) {
    // --- 使用锚点计算中心 ---
    float2 center = size * anchor;

    // --- 两条对角线方向参考 ---
    float proj = (fragCoord.x + fragCoord.y) * 0.5;
    float2 nearest1 = float2(proj, proj);
    float2 nearest2 = float2(fragCoord.x, size.y - proj);
    float2 nearest = distance(fragCoord, nearest1) < distance(fragCoord, nearest2) ? nearest1 : nearest2;

    // --- 离心 ---
    float2 radial = fragCoord - center;
    float radialLen = length(radial);
    float radialMax = max(length(size * 0.5), 1e-5); // 防止 smoothstep 上限为 0
    float radialFactor = smoothstep(0.0, radialMax, radialLen);
    float2 distortVec = radial * (-distortFactor) * radialFactor;

    // 合成采样点
    float2 distorted = fragCoord + distortVec;

    // --- 色散 ---
    float2 dir = (radialLen > 0.0) ? radial / radialLen : float2(0.0);
    float2 maxSize = max(size, float2(1.0)); // 防止 clamp 上限为 0
    float2 redOffset   = clamp(distorted + dir * dispersion * 0.5, float2(0.0), maxSize);
    float2 greenOffset = clamp(distorted, float2(0.0), maxSize);
    float2 blueOffset  = clamp(distorted - dir * dispersion * 0.5, float2(0.0), maxSize);

    half r = content.eval(redOffset).r;
    half g = content.eval(greenOffset).g;
    half b = content.eval(blueOffset).b;
    half a = content.eval(distorted).a;

    return half4(r, g, b, a);
}
"""
