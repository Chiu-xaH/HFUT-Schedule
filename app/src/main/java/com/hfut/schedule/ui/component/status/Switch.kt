package com.hfut.schedule.ui.component.status

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private const val MAX_ANGLE = 90

// 依旧打滑
/** - 专用于深浅色切换的开关
 *  - [animationDurationMillis] 当且仅当 [expressiveMotionEnabled] 为 false 时可以准确控制时长, 因为回弹用了 `Spring` ， 这会导致时长难以被精确调控
 *  - 如果改了 [expressiveDampingRatio] 和 [expressiveStiffness] 这两个就需要去下方 `DrawScope.drawFancySwitch` 内改 `trackContentProgress` 和 `indicatorProgress`， 不过前提是往大了改， 应该说是往回弹力度小， 弹性高了改之后需要调整
 */
@Composable
fun FancySwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    width: Dp = FancySwitchDefaults.Width,
    height: Dp = FancySwitchDefaults.Height,
    expressiveMotionEnabled: Boolean = true, // 如果用了 motionScheme 可以换掉
    animationDurationMillis: Int = FancySwitchDefaults.AnimationDurationMillis,
    expressiveDampingRatio: Float = 0.5F,
    expressiveStiffness: Float = 70F,
    starAnimationSpeedMultiplier: Float = 1f,
    cloudAnimationSpeedMultiplier: Float = 1f,
    trackInnerShadowSize: Dp = FancySwitchDefaults.TrackInnerShadowSize,
    isDark: Boolean,
    colors: FancySwitchColors = FancySwitchDefaults.colors(isDark = isDark),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val animationSpec = remember(animationDurationMillis) {
        tween<Float>(
            durationMillis = animationDurationMillis.coerceAtLeast(0),
            easing = FastOutSlowInEasing
        )
    }
    val animatedProgress = remember { Animatable(if (checked) 1f else 0f) }
    var dragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableFloatStateOf(animatedProgress.value) }
    val travelPx = with(density) { (width - height).coerceAtLeast(1.dp).toPx() }

    LaunchedEffect(
        checked,
        animationSpec,
        dragging,
        expressiveMotionEnabled,
        expressiveDampingRatio,
        expressiveStiffness
    ) {
        if (!dragging) {
            val target = if (checked) 1f else 0f
            if (expressiveMotionEnabled) {
                animatedProgress.animateTo(
                    targetValue = target,
                    animationSpec = spring(
                        dampingRatio = expressiveDampingRatio,
                        stiffness = expressiveStiffness
                    )
                )
            } else {
                animatedProgress.animateTo(targetValue = target, animationSpec = animationSpec)
            }
        }
    }
    val progress = if (dragging) dragProgress else animatedProgress.value
    val cloudTransition = rememberInfiniteTransition(label = "FancySwitchClouds")
    val cloudPhase by cloudTransition.animateFloat(
        initialValue = 0f,
        targetValue = (PI * 2.0).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = animationPeriodMillis(
                    baseDurationMillis = FancySwitchDefaults.CloudAnimationDurationMillis,
                    speedMultiplier = cloudAnimationSpeedMultiplier
                ),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "FancySwitchCloudPhase"
    )
    val starTransition = rememberInfiniteTransition(label = "FancySwitchStars")
    val starPhase by starTransition.animateFloat(
        initialValue = 0f,
        targetValue = (PI * 2.0).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = animationPeriodMillis(
                    baseDurationMillis = FancySwitchDefaults.StarAnimationDurationMillis,
                    speedMultiplier = starAnimationSpeedMultiplier
                ),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "FancySwitchStarPhase"
    )

    val inputModifier = if (onCheckedChange != null) {
        Modifier
            .draggable(
                enabled = enabled,
                orientation = Orientation.Horizontal,
                interactionSource = interactionSource,
                state = rememberDraggableState { delta ->
                    dragProgress = (dragProgress + delta / travelPx).coerceIn(0f, 1f)
                },
                onDragStarted = {
                    dragging = true
                    dragProgress = animatedProgress.value.coerceIn(0f, 1f)
                },
                onDragStopped = {
                    val newChecked = dragProgress >= 0.5f
                    scope.launch {
                        animatedProgress.snapTo(dragProgress)
                        if (newChecked != checked) {
                            onCheckedChange(newChecked)
                        }
                        dragging = false
                    }
                }
            )
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Switch,
                interactionSource = interactionSource,
                indication = null,
                onValueChange = onCheckedChange
            )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .then(
                if (onCheckedChange != null) {
                    Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                } else {
                    Modifier
                }
            )
            .then(inputModifier)
            .wrapContentSize(Alignment.Center)
            .requiredSize(width, height),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.requiredSize(width, height)
        ) {
            drawFancySwitch(
                progress = progress,
                cloudPhase = cloudPhase,
                starPhase = starPhase,
                expressiveMotionEnabled = expressiveMotionEnabled,
                trackInnerShadowSize = trackInnerShadowSize,
                colors = colors,
                enabled = enabled
            )
        }
    }
}

private fun DrawScope.drawFancySwitch(
    progress: Float,
    cloudPhase: Float,
    starPhase: Float,
    expressiveMotionEnabled: Boolean,
    trackInnerShadowSize: Dp,
    colors: FancySwitchColors,
    enabled: Boolean
) {
    val safeProgress = progress.coerceIn(0f, 1f)
    val trackContentProgress = if (expressiveMotionEnabled) {
        progress.coerceIn(-0.1f, 1.1f)
    } else {
        safeProgress
    }
    val indicatorProgress = progress.coerceIn(-0.07f, 1.07f)
    val switchWidth = size.width
    val switchHeight = size.height
    if (switchWidth <= 0f || switchHeight <= 0f) return

    val left = (size.width - switchWidth) / 2f
    val top = (size.height - switchHeight) / 2f
    val trackRect = Rect(left, top, left + switchWidth, top + switchHeight)
    val trackRadius = switchHeight / 2f
    val center = trackRect.center
    val thumbRadiusBase = switchHeight * 0.75f
    val thumbRadius = (
            (switchHeight - FancySwitchDefaults.IndicatorInsetPx * 2f) / 2f
            ).coerceAtLeast(1f)
    val leftThumbCenter = Offset(left + trackRadius, center.y)
    val rightThumbCenter = Offset(trackRect.right - trackRadius, center.y)
    val thumbCenter = Offset(
        x = leftThumbCenter.x + (rightThumbCenter.x - leftThumbCenter.x) * indicatorProgress,
        y = center.y
    )
    val disabledAlpha = if (enabled) 1f else 0.38f
    val skyColor = lerp(colors.daySky, colors.nightSky, safeProgress)

    drawRoundRect(
        color = skyColor,
        topLeft = trackRect.topLeft,
        size = trackRect.size,
        cornerRadius = CornerRadius(trackRadius),
        alpha = disabledAlpha
    )

    val clipPath = Path().apply {
        addRoundRect(
            androidx.compose.ui.geometry.RoundRect(
                rect = trackRect,
                cornerRadius = CornerRadius(trackRadius)
            )
        )
    }
    clipPath(clipPath) {
        drawClouds(
            center = center,
            thumbRadius = thumbRadiusBase,
            switchHeight = switchHeight,
            progress = trackContentProgress,
            ambientPhase = cloudPhase,
            colors = colors,
            alpha = disabledAlpha
        )
        drawStars(
            left = left,
            top = top,
            switchWidth = switchWidth,
            switchHeight = switchHeight,
            thumbRadius = thumbRadiusBase,
            progress = trackContentProgress,
            ambientPhase = starPhase,
            color = colors.star,
            alpha = disabledAlpha
        )

        val haloColor = Color.White.copy(alpha = 0.10f * disabledAlpha)
        drawCircle(haloColor, thumbRadiusBase * 1.4f, thumbCenter)
        drawCircle(haloColor, thumbRadiusBase * 1.1f, thumbCenter)
        drawCircle(haloColor, thumbRadiusBase * 0.8f, thumbCenter)

        drawTrackInnerShadow(
            rect = trackRect,
            innerShadowSizePx = trackInnerShadowSize.toPx().coerceAtLeast(0f),
            colors = colors,
            alpha = disabledAlpha
        )

        val thumbColor = lerp(colors.sun, colors.moon, safeProgress)
        drawCircle(
            color = thumbColor,
            radius = thumbRadius,
            center = thumbCenter,
            alpha = disabledAlpha
        )
        drawCircle(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.52f * disabledAlpha),
                    Color(0xFF525E6E).copy(alpha = 0.52f * disabledAlpha)
                ),
                startY = thumbCenter.y - thumbRadius,
                endY = thumbCenter.y + thumbRadius
            ),
            radius = thumbRadius,
            center = thumbCenter,
            style = Stroke(width = (switchHeight / 40f).coerceAtLeast(1f))
        )

        drawMoonCraters(
            center = thumbCenter,
            thumbRadiusBase = thumbRadiusBase,
            progress = safeProgress,
            colors = colors,
            alpha = disabledAlpha
        )
    }
}

private fun DrawScope.drawClouds(
    center: Offset,
    thumbRadius: Float,
    switchHeight: Float,
    progress: Float,
    ambientPhase: Float,
    colors: FancySwitchColors,
    alpha: Float
) {
    val cloudAlpha = (1f - progress * 1.6f).coerceIn(0f, 1f) * alpha
    if (cloudAlpha <= 0f) return

    val frontDrift = Offset(
        x = sin(ambientPhase) * switchHeight * 0.025f,
        y = cos(ambientPhase * 2f + 0.4f) * switchHeight * 0.035f
    )
    val backDrift = Offset(
        x = sin(ambientPhase * 2f + 1.2f) * switchHeight * 0.018f,
        y = cos(ambientPhase + 0.8f) * switchHeight * 0.025f
    )
    val backBase = center + Offset(0f, switchHeight * 4f * progress) + backDrift
    val frontBase = center + Offset(0f, switchHeight * 2f * progress) + frontDrift
    val backClouds = listOf(
        Triple(1.4f, -0.1f, 1f / 1.5f),
        Triple(0.9f, 0.1f, 0.4f),
        Triple(0.5f, 0.5f, 0.6f),
        Triple(-0.2f, 0.6f, 0.4f),
        Triple(-1f, 1.1f, 1f)
    )
    val frontClouds = listOf(
        Triple(1.6f, 1f / 9f, 1f / 1.5f),
        Triple(1.5f, 0.9f, 1f),
        Triple(0.7f, 0.6f, 0.4f),
        Triple(0.5f, 1.1f, 0.75f),
        Triple(-0.2f, 0.8f, 0.4f),
        Triple(-1f, 1.3f, 1f)
    )
    backClouds.forEach { (x, y, radius) ->
        drawCircle(
            color = colors.backCloud,
            radius = thumbRadius * radius,
            center = backBase + Offset(thumbRadius * x, thumbRadius * y),
            alpha = cloudAlpha
        )
    }
    frontClouds.forEach { (x, y, radius) ->
        drawCircle(
            color = colors.frontCloud,
            radius = thumbRadius * radius,
            center = frontBase + Offset(thumbRadius * x, thumbRadius * y),
            alpha = cloudAlpha
        )
    }
}

private fun DrawScope.drawStars(
    left: Float,
    top: Float,
    switchWidth: Float,
    switchHeight: Float,
    thumbRadius: Float,
    progress: Float,
    ambientPhase: Float,
    color: Color,
    alpha: Float
) {
    val baseStarAlpha = progress.coerceIn(0f, 1f) * alpha
    if (baseStarAlpha <= 0f) return

    val positions = listOf(
        0.14f to 0.72f,
        0.22f to 0.24f,
        0.28f to 0.56f,
        0.36f to 0.82f,
        0.42f to 0.34f,
        0.49f to 0.67f,
        0.54f to 0.18f
    )
    val radiusFactors = listOf(
        1f / 9f,
        1f / 12f,
        1f / 13f,
        1f / 16f,
        1f / 18f,
        1f / 24f,
        1f / 28f
    )
    val twinkleHarmonics = intArrayOf(1, 2, 1, 3, 2, 3, 1)
    positions.forEachIndexed { index, (xFactor, yFactor) ->
        val center = Offset(
            x = left + switchWidth * xFactor,
            y = top + switchHeight * (yFactor - (1f - progress))
        )
        val twinkle = 0.48f + 0.52f * (
                (
                        sin(
                            ambientPhase * twinkleHarmonics[index] +
                                    index * 1.37f
                        ) + 1f
                        ) / 2f
                )
        val starAlpha = baseStarAlpha * twinkle
        val radius = thumbRadius * radiusFactors[index]
        drawFourPointStar(center, radius, color, starAlpha)
    }
}

private fun DrawScope.drawFourPointStar(
    center: Offset,
    radius: Float,
    color: Color,
    alpha: Float
) {
    val path = Path().apply {
        moveTo(center.x, center.y - radius)
        quadraticTo(center.x, center.y, center.x - radius, center.y)
        quadraticTo(center.x, center.y, center.x, center.y + radius)
        quadraticTo(center.x, center.y, center.x + radius, center.y)
        quadraticTo(center.x, center.y, center.x, center.y - radius)
        close()
    }
    drawPath(path = path, color = color, alpha = alpha)
}

private fun DrawScope.drawTrackInnerShadow(
    rect: Rect,
    innerShadowSizePx: Float,
    colors: FancySwitchColors,
    alpha: Float
) {
    var shadowInset = 0.5f
    while (shadowInset <= innerShadowSizePx) {
        val edgeDistance = shadowInset
        val targetWidth = rect.width - edgeDistance * 2f
        val targetHeight = rect.height - edgeDistance * 2f
        if (targetWidth <= 0f || targetHeight <= 0f) break

        val fraction = 1f - shadowInset / innerShadowSizePx.coerceAtLeast(1f)
        drawRoundRect(
            color = colors.innerShadow,
            topLeft = rect.topLeft + Offset(edgeDistance, edgeDistance),
            size = Size(targetWidth, targetHeight),
            cornerRadius = CornerRadius((rect.height / 2f - edgeDistance).coerceAtLeast(0f)),
            style = Stroke(width = 1f),
            alpha = alpha * fraction * fraction
        )
        shadowInset += 1f
    }
}

private fun DrawScope.drawMoonCraters(
    center: Offset,
    thumbRadiusBase: Float,
    progress: Float,
    colors: FancySwitchColors,
    alpha: Float
) {
    val craterAlpha = progress * alpha
    if (craterAlpha <= 0f) return

    val craters = listOf(
        center + Offset(-thumbRadiusBase / 4f, 0f) to thumbRadiusBase / 6f,
        center + Offset(thumbRadiusBase / 6f, thumbRadiusBase / 6f) to thumbRadiusBase / 8f,
        center + Offset(thumbRadiusBase / 12f, -thumbRadiusBase / 3.5f) to thumbRadiusBase / 12f
    )
    craters.forEach { (craterCenter, radius) ->
        drawCircle(
            color = colors.crater,
            radius = radius,
            center = craterCenter,
            alpha = craterAlpha
        )
        drawCircle(
            brush = Brush.verticalGradient(
                colors = listOf(
                    colors.rimDark.copy(alpha = 0.5f),
                    colors.rimLight.copy(alpha = 0.5f)
                ),
                startY = craterCenter.y - radius,
                endY = craterCenter.y + radius
            ),
            radius = radius,
            center = craterCenter,
            style = Stroke(width = (thumbRadiusBase / 40f).coerceAtLeast(0.5f)),
            alpha = craterAlpha
        )
    }
}

@Immutable
data class FancySwitchColors(
    val daySky: Color,
    val nightSky: Color,
    val sun: Color,
    val moon: Color,
    val crater: Color,
    val frontCloud: Color,
    val backCloud: Color,
    val star: Color,
    val rimLight: Color,
    val rimDark: Color,
    val innerShadow: Color
)

object FancySwitchDefaults {
    val Width = 64.dp
    val Height = 32.dp
    const val AnimationDurationMillis = 500
    // The former 3x speed is now the baseline represented by a multiplier of 1f.
    const val StarAnimationDurationMillis = 2167
    const val CloudAnimationDurationMillis = 2667
    const val IndicatorInsetPx = 16f
    val TrackInnerShadowSize = 4.dp

    fun colors(
        isDark: Boolean,
        daySky: Color = if (isDark) Color(0xFF2B5B84) else Color(0xFF45A4DE),
        nightSky: Color = if (isDark) Color(0xFF090D16) else Color(0xFF0E1621),
        sun: Color = if (isDark) Color(0xFFFFE054) else Color(0xFFFEE600),
        moon: Color = if (isDark) Color(0xFFD0E3EC) else Color(0xFFB5D5E2),
        crater: Color = if (isDark) Color(0xFF53637C) else Color(0xFF7487A5),
        frontCloud: Color = if (isDark) Color(0xFFC0D2E5) else Color(0xFFF6FAFF),
        backCloud: Color = if (isDark) Color(0xFF517D9E) else Color(0xFF91C9EC),
        star: Color = if (isDark) Color(0xFFFFF9DB) else Color(0xFFFFFFFF),
        rimLight: Color = if (isDark) Color(0xFF42566E) else Color(0xFFF6FAFF),
        rimDark: Color = if (isDark) Color(0xFF1B232E) else Color(0xFF525E6E),
        innerShadow: Color = Color.Black.copy(alpha = if (isDark) 0.34f else 0.22f)
    ) = FancySwitchColors(
        daySky = daySky,
        nightSky = nightSky,
        sun = sun,
        moon = moon,
        crater = crater,
        frontCloud = frontCloud,
        backCloud = backCloud,
        star = star,
        rimLight = rimLight,
        rimDark = rimDark,
        innerShadow = innerShadow
    )
}

private fun animationPeriodMillis(
    baseDurationMillis: Int,
    speedMultiplier: Float
): Int {
    return (baseDurationMillis / speedMultiplier.coerceAtLeast(0.1f))
        .toInt()
        .coerceAtLeast(1)
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CustomSwitch(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    enabled: Boolean = true,
) {
    val color by animateColorAsState(
        if(!checked) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.surface
    )
    val angle by animateIntAsState(
        if(checked) MAX_ANGLE else 0,tween(AppAnimationManager.ANIMATION_SPEED, easing = LinearOutSlowInEasing)
    )
    val animateEnded = angle == 0 ||angle == MAX_ANGLE

    Switch(
        modifier = modifier,
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        thumbContent = {
            AnimatedVisibility(
                visible = animateEnded,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Box(modifier = Modifier.fillMaxSize(1f).background(color, MaterialShapes.Circle.toShape()))
            }
            AnimatedVisibility(
                visible = !animateEnded,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Box(modifier = Modifier.fillMaxSize(1f).background(color, MaterialShapes.VerySunny.toShape(angle)))
            }
        },
        colors = SwitchDefaults.colors(uncheckedThumbColor = Color.Transparent, checkedThumbColor = Color.Transparent)
    )
}