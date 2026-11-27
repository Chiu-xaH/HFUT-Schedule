package com.hfut.schedule.ui.component.button

import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastCoerceAtMost
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.lerp
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.style.shader.largeStyle
import com.hfut.schedule.ui.style.special.enableEffect
import com.hfut.schedule.ui.style.special.layerGlass
import com.hfut.schedule.ui.util.isThemeDark
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.highlight.Highlight
import com.sharednav.common.helper.NoneRoundShape
import com.xah.common.logic.util.safeDiv
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.container.component.base.SharedContainer
import com.xah.shader.state.ShaderState
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

val BUTTON_END_PADDING = APP_HORIZONTAL_DP- CARD_NORMAL_DP-1.dp

val BUTTON_HEIGHT = 42.dp

@Composable
fun LiquidButton(
    onClick: () -> Unit,
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    enabled : Boolean = true,
    isCircle : Boolean = false,
    innerPadding : Dp = if(!isCircle) 20.dp else 9.5.dp,
    surfaceColor : Color = MaterialTheme.colorScheme.surfaceContainer,
    content: @Composable RowScope.() -> Unit
) {
    val surfaceColor = surfaceColor.copy(
        if(enabled).5f else .9f
    )
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
    val isTransiting = enableEffect()
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)

    Row(
        modifier
            .drawBackdrop(
                highlight = {
                    Highlight.Default.copy(width = 0.25.dp)
                },
                backdrop = if (isTransiting) backdrop else rememberLayerBackdrop(),
                shape = { CircleShape },
                effects =  {
                    vibrancy()
                    if(blur) {
                        blur(1.5f.dp.toPx())
                    }
                    lens(12f.dp.toPx(), 24.dp.toPx())
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
                        translationX = maxOffset * tanh(initialDerivative * offset.x safeDiv maxOffset)
                        translationY = maxOffset * tanh(initialDerivative * offset.y safeDiv maxOffset)

                        val maxDragScale = 0.1f
                        val offsetAngle = atan2(offset.y, offset.x)
                        scaleX =
                            scale +
                                    maxDragScale * abs(cos(offsetAngle) * offset.x safeDiv size.maxDimension) *
                                    (width safeDiv height).fastCoerceAtMost(1f)
                        scaleY =
                            scale +
                                    maxDragScale * abs(sin(offsetAngle) * offset.y safeDiv size.maxDimension) *
                                    (height safeDiv width).fastCoerceAtMost(1f)
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
                                    setColorUniform(
                                        "color",
                                        Color.White.copy(0.15f * progress).toArgb()
                                    )
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
                        indication = null,
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
                                launch {
                                    offsetAnimation.animateTo(
                                        Offset.Zero,
                                        offsetAnimationSpec
                                    )
                                }
                            }
                        }
                        inspectDragGestures(
                            onDragStart = { down ->
                                pressStartPosition = down.position
                                animationScope.launch {
                                    launch {
                                        progressAnimation.animateTo(
                                            1f,
                                            progressAnimationSpec
                                        )
                                    }
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
            .height(BUTTON_HEIGHT)
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

@Composable
fun Modifier.containerBackDrop(
    backdrop: Backdrop,
    shape: Shape,
    enabled : Boolean = true,
    surfaceColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(if(enabled).3f else .7f),
) : Modifier {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val isTransiting = enableEffect()

    return this.drawBackdrop(
        highlight = {
            Highlight.Default.copy(width = 0.25.dp)
        },
        backdrop = if (isTransiting) backdrop else rememberLayerBackdrop(),
        shape = { shape },
        effects = {
            vibrancy()
            if(blur) {
                blur(4f.dp.toPx())
            }
            lens(15.dp.toPx(), 24.dp.toPx())
        },
        shadow = null,
        onDrawSurface = {
            drawRect(surfaceColor)
        }
    )
}


@Composable
fun Modifier.containerBackDrop(
    backdrop: ShaderState,
    shape: Shape,
    surfaceColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(.7f),
) : Modifier {
    val isTransiting = enableEffect()

    return this
        .clip(shape)
        .let {
            if(isTransiting) {
                it.layerGlass(
                    backdrop,
                    style = largeStyle.copy(
                        overlayColor = surfaceColor
                    ),
                )
            } else {
                it
            }
        }
}

@Composable
fun LiquidButton(
    onClick: () -> Unit,
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    enabled : Boolean = true,
    isCircle : Boolean = false,
    shape: Shape,
    content: @Composable () -> Unit
) {
    val isRunning = enableEffect()

//    if(!isRunning) {
//        LiquidButton(
//            onClick = onClick,
//            backdrop = backdrop,
//            modifier = modifier,
//            enabled = enabled,
//            isCircle = isCircle,
//        ) {
//            content()
//        }
//    } else {
        val color = MaterialTheme.colorScheme.surfaceVariant.copy(
            if(isRunning) 0.65f else 1f
        )
        NoPadding {
            if(isCircle) {
                FilledTonalIconButton(
                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = color,),
                    onClick = onClick,
                    modifier = modifier,
                    enabled = enabled,
                    content = content,
                    shape = shape,
                )
            } else {
                FilledTonalButton(
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = color),
                    shape = shape,
                    onClick = onClick,
                    modifier = modifier,
                    enabled = enabled,
                ) {
                    content()
                }
            }
        }
//    }
}

@Composable
fun NoPadding(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalMinimumInteractiveComponentSize provides 0.dp
    ) {
        content()
    }
}

@Composable
fun Modifier.bottomBarBackDrop(
    backdrop: Backdrop,
    color : Color = MaterialTheme.colorScheme.surface,
    shape : RoundedCornerShape = CircleShape,
) : Modifier {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val finalColor = MaterialTheme.colorScheme.surfaceContainerLow.copy(.6f)
//   .275f
    val isTransiting = enableEffect()
    val isThemeDark = isThemeDark()

    return this.drawBackdrop(
        highlight = {
            if(isThemeDark) {
                Highlight.Default.copy(width = 0.25.dp)
            } else {
                Highlight.Plain
            }
        },
        backdrop = if (isTransiting) backdrop else rememberLayerBackdrop(),
        shape = { shape },
        effects = {
            vibrancy()
            if(blur) {
                blur(1.75f.dp.toPx())
            }
            lens(17.5.dp.toPx(), 24.dp.toPx())
        },
        shadow = null,
        onDrawSurface = {
            drawRect(finalColor)
        }
    )
}


enum class CalendarSquareBackDropSource {
    BUTTON,BOTTOM,SQUARE,CONTAINER
}

@Composable
fun Modifier.calendarSquareBackDrop(
    backdrop: Backdrop,
    source : CalendarSquareBackDropSource,
    shape: Shape = CircleShape,
    overlayColor : Color = MaterialTheme.colorScheme.surface,
) : Modifier {
    val customBackgroundAlpha by DataStoreManager.customCalendarSquareAlpha.collectAsState(initial = MyApplication.CALENDAR_SQUARE_ALPHA)
//    val customBackgroundBlur by DataStoreManager.customCalendarSquareBlur.collectAsState(initial = MyApplication.CALENDAR_SQUARE_BLUR)
    val color = overlayColor.copy(customBackgroundAlpha)
    val isTransiting = enableEffect()
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)

    return this.drawBackdrop(
        highlight = {
            Highlight.Default.copy(width = 0.25.dp)
        },
        backdrop = if (isTransiting) backdrop else rememberLayerBackdrop(),
        shape = { shape },
        effects = {
            vibrancy()
            // 统一调配
            if(blur) {
//            blur(10f.dp.toPx() * customBackgroundBlur)
              blur(1.5.dp.toPx())
            }
            when(source) {
                CalendarSquareBackDropSource.CONTAINER -> {
                    lens(15.dp.toPx(), 24.dp.toPx())
                }
                CalendarSquareBackDropSource.BOTTOM -> {
                    lens(17.5.dp.toPx(), 24.dp.toPx())
                }
                CalendarSquareBackDropSource.BUTTON -> {
                    lens(12.dp.toPx(), 24.dp.toPx())
                }
                CalendarSquareBackDropSource.SQUARE -> {
                    // 圆角太小了，特殊处理
                    lens(5.5.dp.toPx(), 24.dp.toPx(),true)
                }
            }
        },
        shadow = null,
        onDrawSurface = {
            drawRect(color)
        }
    )
}

@Composable
fun LiquidButtonWithSharedContainer(
    key: String?,
    backdrop: Backdrop,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isCircle: Boolean = false,
    content: @Composable (() -> Unit)
) {
    val enableEffect = enableEffect()
    SharedContainer(
        key,
        CircleShape,
        MaterialTheme.colorScheme.surfaceVariant,
        modifier
    ) {
        if(!enableEffect) {
            LiquidButton(
                onClick = onClick,
                isCircle = isCircle,
                shape = NoneRoundShape,
                backdrop = backdrop,
                enabled = enabled,
            ) {
                content()
            }
        } else {
            LiquidButton(
                onClick = onClick,
                isCircle = isCircle,
                backdrop = backdrop,
                enabled = enabled,
            ) {
                content()
            }
        }
    }
}