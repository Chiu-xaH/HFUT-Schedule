package com.xah.common.ui.shader.style

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.xah.common.ui.shader.recordPosition
import org.intellij.lang.annotations.Language


// 绘制内容
fun Modifier.scaleMirror(
    scale: Float = 1f,
    clipShape: Shape = RoundedCornerShape(0.dp),
): Modifier =
    if(Build.VERSION.SDK_INT < 33 || scale == 1f) {
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
                        val runtimeShader = RuntimeShader(MIRROR_SHADER_CODE.trimIndent())
                        runtimeShader.setFloatUniform("size", r.width, r.height)
                        runtimeShader.setFloatUniform("scale", scale)

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


@Language("agsl")
private const val MIRROR_SHADER_CODE = """
    uniform shader content;
    uniform float2 size;
    uniform float scale;

    // 无缝镜像折叠：将任意坐标映射到 [0, maxVal] 的三角波
    float mirrorFold(float v, float maxVal) {
        float period = 2.0 * maxVal;
        // 先把负数折到正数范围
        v = abs(v);
        // 取模得到 [0, period) 内的值
        v = mod(v, period);
        // 超过 maxVal 的部分再折回来
        if (v > maxVal) v = period - v;
        return v;
    }

    half4 main(float2 fragCoord) {
        float2 center = size * 0.5;
        float2 offset = fragCoord - center;

        // 缩放
        float2 sampleCoord = center + offset / scale;

        // 镜面折叠（支持多次反射）
        sampleCoord.x = mirrorFold(sampleCoord.x, size.x);
        sampleCoord.y = mirrorFold(sampleCoord.y, size.y);

        // 收缩半像素，防止浮点误差导致边缘双线性采样混入透明像素
        sampleCoord = clamp(sampleCoord, float2(0.5), size - float2(0.5));

        return content.eval(sampleCoord);
    }
"""



