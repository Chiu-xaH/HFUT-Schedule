package com.hfut.schedule.ui.component.button

import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastCoerceAtMost
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.lerp
import com.hfut.schedule.R
import com.hfut.schedule.ui.util.state.GlobalUIStateHolder
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.refraction
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.highlight.Highlight
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tanh

private const val SHADER = """
uniform float2 size;
layout(color) uniform half4 color;
uniform float radius;
uniform float2 offset;

half4 main(float2 coord) {
    float2 center = offset;
    float dist = distance(coord, center);
    float intensity = smoothstep(radius, radius * 0.5, dist);
    return color * intensity;
}"""

val BUTTON_PADDING = 6.25.dp


@Composable
fun LiquidButton(
    onClick: () -> Unit,
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    enabled : Boolean = true,
    isCircle : Boolean = false,
    innerPadding : Dp = if(!isCircle) 20.dp else 9.5.dp,
    surfaceColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(if(enabled).5f else .9f),
    content: @Composable RowScope.() -> Unit
) {
    val textStyle = LocalTextStyle.current.copy(
        fontSize = 14.5.sp,
        color = if(!enabled) Color.Gray else Color.Unspecified
    )
    val tint = Color.Unspecified
    val animationScope = rememberCoroutineScope()
    val progressAnimation = remember { Animatable(0f) }
    var pressStartPosition by remember { mutableStateOf(Offset.Zero) }
    val offsetAnimation = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

    val interactiveHighlightShader = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            RuntimeShader(SHADER)
        } else {
            Unit
        }
    }
    Row(
        modifier
            .drawBackdrop(
                highlight = {
                    Highlight.Default.copy(width = 0.25.dp)
                },
                backdrop = backdrop,
                shape = { CircleShape },
                effects = {
                    vibrancy()
                    blur(2f.dp.toPx())
                    refraction(12f.dp.toPx(), 24f.dp.toPx())
                },
                shadow = null,
                layerBlock = if (enabled) {
                    {
                        val width = size.width
                        val height = size.height

                        val progress = progressAnimation.value
                        val maxScale = 0.1f
                        val scale = lerp(1f, 1f + maxScale, progress)

                        val maxOffset = size.minDimension
                        val initialDerivative = 0.05f
                        val offset = offsetAnimation.value
                        translationX = maxOffset * tanh(initialDerivative * offset.x / maxOffset)
                        translationY = maxOffset * tanh(initialDerivative * offset.y / maxOffset)

                        val maxDragScale = 0.1f
                        val offsetAngle = atan2(offset.y, offset.x)
                        scaleX =
                            scale +
                                    maxDragScale * abs(cos(offsetAngle) * offset.x / size.maxDimension) *
                                    (width / height).fastCoerceAtMost(1f)
                        scaleY =
                            scale +
                                    maxDragScale * abs(sin(offsetAngle) * offset.y / size.maxDimension) *
                                    (height / width).fastCoerceAtMost(1f)
                    }
                } else {
                    null
                },
                onDrawSurface = {
                    if (tint.isSpecified) {
                        drawRect(tint, blendMode = BlendMode.Hue)
                        drawRect(tint.copy(alpha = 0.75f))
                    }
                    if (surfaceColor.isSpecified) {
                        drawRect(surfaceColor)
                    }
                    if (enabled) {
                        val progress = progressAnimation.value.fastCoerceIn(0f, 1f)
                        if (progress > 0f) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && interactiveHighlightShader is RuntimeShader) {
                                drawRect(
                                    Color.White.copy(0.1f * progress),
                                    blendMode = BlendMode.Plus
                                )
                                interactiveHighlightShader.apply {
                                    val offset = pressStartPosition + offsetAnimation.value
                                    setFloatUniform("size", size.width, size.height)
                                    setColorUniform("color", Color.White.copy(0.15f * progress).toArgb())
                                    setFloatUniform("radius", size.maxDimension)
                                    setFloatUniform(
                                        "offset",
                                        offset.x.fastCoerceIn(0f, size.width),
                                        offset.y.fastCoerceIn(0f, size.height)
                                    )
                                }
                                drawRect(
                                    ShaderBrush(interactiveHighlightShader),
                                    blendMode = BlendMode.Plus
                                )
                            } else {
                                drawRect(
                                    Color.White.copy(0.25f * progress),
                                    blendMode = BlendMode.Plus
                                )
                            }
                        }
                    }
                }
            )
            .let {
                if (enabled) {
                    it.clickable(
                        interactionSource = null,
                        indication = null ,
                        role = Role.Button,
                        onClick = onClick
                    )
                } else {
                    it
                }
            }
            .then(
                if (enabled) {
                    Modifier.pointerInput(animationScope) {
                        val progressAnimationSpec = spring(0.5f, 300f, 0.001f)
                        val offsetAnimationSpec = spring(1f, 300f, Offset.VisibilityThreshold)
                        val onDragStop: () -> Unit = {
                            animationScope.launch {
                                launch { progressAnimation.animateTo(0f, progressAnimationSpec) }
                                launch { offsetAnimation.animateTo(Offset.Zero, offsetAnimationSpec) }
                            }
                        }
                        inspectDragGestures(
                            onDragStart = { down ->
                                pressStartPosition = down.position
                                animationScope.launch {
                                    launch { progressAnimation.animateTo(1f, progressAnimationSpec) }
                                    launch { offsetAnimation.snapTo(Offset.Zero) }
                                }
                            },
                            onDragEnd = { onDragStop() },
                            onDragCancel = onDragStop
                        ) { _, dragAmount ->
                            animationScope.launch {
                                offsetAnimation.snapTo(offsetAnimation.value + dragAmount)
                            }
                        }
                    }
                } else {
                    Modifier
                }
            )
            .height(42f.dp)
            .padding(horizontal = innerPadding),
        horizontalArrangement = Arrangement.spacedBy(8f.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val c = LocalContentColor.current
        CompositionLocalProvider(
            LocalTextStyle provides textStyle,
            if(!enabled) {
                LocalContentColor provides Color.Gray.copy(.75f)
            } else {
                LocalContentColor provides c
            }
        ) {
            content()
        }
    }
}
