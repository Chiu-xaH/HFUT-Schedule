package com.hfut.schedule.ui.util.shader

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import org.intellij.lang.annotations.Language

fun enhanceColorShader(
    enabled : Boolean
): RenderEffect {
    val enhanceColor = RuntimeShader(COLOR_ENHANCE_SHADER_CODE.trimIndent())
    enhanceColor.setFloatUniform("saturation", if(enabled) 1.25f else 1f)
    enhanceColor.setFloatUniform("contrast", if(enabled) 1.1f else 1f)

    val enhanceEffect = RenderEffect.createRuntimeShaderEffect(enhanceColor, "content")
    return enhanceEffect
}

@Language("ASGL")
private const val COLOR_ENHANCE_SHADER_CODE = """
uniform shader content;     
uniform float saturation;    // 饱和度提升系数，1.0 = 原始
uniform float contrast;      // 对比度增强系数，1.0 = 原始

half4 main(float2 fragCoord) {
    half4 color = content.eval(fragCoord);

    // ---- 提升饱和度 ----
    const half3 rgbToY = half3(0.2126, 0.7152, 0.0722);
    half luma = dot(color.rgb, rgbToY);
    color.rgb = mix(half3(luma, luma, luma), color.rgb, saturation);

    // ---- 增强对比度 ----
    color.rgb = (color.rgb - 0.5) * contrast + 0.5;

    return color;
}
"""
