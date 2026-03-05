package com.xah.container.container

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import com.xah.container.model.ExtensionDirection
import org.intellij.lang.annotations.Language


fun Modifier.pixelExtension(
    parentGraphicsLayer: GraphicsLayer,
    parentRect: Rect?,
    direction : ExtensionDirection
): Modifier {
    if(parentRect == null) {
        return this
    }
    return composed {
        if (Build.VERSION.SDK_INT < 33) {
            // 使用defaultColor延展填充extensionHeight
            this
        } else {
            // 底部1像素延展填充
            val customRenderEffect = remember(parentRect) {
                val runtimeShader = RuntimeShader(
                    when(direction) {
                        ExtensionDirection.END -> END_SHADER_CODE
                        ExtensionDirection.TOP -> TOP_SHADER_CODE
                        ExtensionDirection.START -> START_SHADER_CODE
                        ExtensionDirection.BOTTOM -> BOTTOM_SHADER_CODE
                        ExtensionDirection.VERTICAL -> VERTICAL_SHADER_CODE
                        ExtensionDirection.HORIZONTAL -> HORIZONTAL_SHADER_CODE
                    }.trimIndent()
                )
                runtimeShader.setFloatUniform("size", parentRect.width, parentRect.height)

                RenderEffect.createRuntimeShaderEffect(runtimeShader, "content").asComposeRenderEffect()
            }

            this.drawWithCache {
                onDrawWithContent {
                    // 使用缓存的自定义渲染效果
                    parentGraphicsLayer.renderEffect = customRenderEffect
                    drawLayer(parentGraphicsLayer)
                }
            }
        }
    }
}

/**
 * @param isLandscape 是否是横屏，为true则取右侧1像素，否则取底部1像素
 * @param isSingle 是否取双边延展
 */
fun Modifier.pixelExtension(
    parentGraphicsLayer: GraphicsLayer,
    parentRect: Rect?,
    isLandscape : Boolean,
    isDouble : Boolean = false,
): Modifier = pixelExtension(
    parentGraphicsLayer,
    parentRect,
    if(isLandscape) {
        if(!isDouble) {
            ExtensionDirection.END
        } else {
            ExtensionDirection.HORIZONTAL
        }
    } else {
        if(!isDouble) {
            ExtensionDirection.BOTTOM
        } else {
            ExtensionDirection.VERTICAL
        }
    }
)

@Language("AGSL")
private const val BOTTOM_SHADER_CODE = """
    uniform shader content;
    uniform float2 size;          

    half4 main(float2 fragCoord) {
        // 采样底部1像素行
        float2 bottomCoord = float2(fragCoord.x, size.y - 1.0);
        return content.eval(bottomCoord);
    }
"""

@Language("AGSL")
private const val END_SHADER_CODE = """
    uniform shader content;
    uniform float2 size;          

    half4 main(float2 fragCoord) {
        // 采样右侧1像素列
        float2 endCoord = float2(size.x - 1.0, fragCoord.y);
        return content.eval(endCoord);
    }
"""

@Language("AGSL")
private const val START_SHADER_CODE = """
    uniform shader content;
    uniform float2 size;          

    half4 main(float2 fragCoord) {
        // 采样左侧1像素行
        float2 leftCoord = float2(1.0, fragCoord.y);
        return content.eval(leftCoord);
    }
"""

@Language("AGSL")
private const val TOP_SHADER_CODE = """
    uniform shader content;
    uniform float2 size;          

    half4 main(float2 fragCoord) {
        // 采样顶部1像素行
        float2 topCoord = float2(fragCoord.x, 1.0);
        return content.eval(topCoord);
    }
"""

@Language("AGSL")
private const val HORIZONTAL_SHADER_CODE = """
    uniform shader content;
    uniform float2 size;          

    half4 main(float2 fragCoord) {

        float x = fragCoord.x < size.x * 0.5
            ? 1.0
            : size.x - 1.0;

        float2 coord = float2(x, fragCoord.y);
        return content.eval(coord);
    }
"""

@Language("AGSL")
private const val VERTICAL_SHADER_CODE = """
    uniform shader content;
    uniform float2 size;          

    half4 main(float2 fragCoord) {

        float y = fragCoord.y < size.y * 0.5
            ? 1.0
            : size.y - 1.0;

        float2 coord = float2(fragCoord.x, y);
        return content.eval(coord);
    }
"""


