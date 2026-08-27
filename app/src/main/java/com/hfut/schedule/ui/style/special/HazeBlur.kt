package com.hfut.schedule.ui.style.special

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.other.AppVersion.CAN_HAZE_BLUR_BAR
import com.hfut.schedule.logic.util.other.AppVersion.HAZE_BLUR_FOR_S
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.largeCardColor
import com.hfut.schedule.ui.nav.destination.ControlCenterDestination
import com.hfut.schedule.ui.style.corner.bottomSheetRound
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.xah.shader.state.ShaderState
import com.hfut.schedule.ui.style.shader.GlassStyle
import com.hfut.schedule.ui.style.shader.glassLayer
import com.hfut.schedule.ui.style.shader.largeStyle
import com.xah.container.util.LocalSharedRegistrySafely
import com.xah.floating.util.LocalFloatingControllerSafely
import com.xah.navigation.util.LocalNavControllerSafely
import com.xah.shader.state.shaderSource
import dev.chrisbanes.haze.HazeEffectScope
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import kotlinx.coroutines.flow.first


private val progressiveBarBlur = 8.75.dp
//    MyApplication.BLUR_RADIUS.dp * 1.2f

@Composable
fun enableEffect() : Boolean {
    val navController = LocalNavControllerSafely.current
    val floatingController = LocalFloatingControllerSafely.current
    val registry = LocalSharedRegistrySafely.current
    if(navController?.currentDestination is ControlCenterDestination) {
        return false
    }
    if(navController?.isTransitioning == true) {
        return false
    }
    if(floatingController?.isRunning == true) {
        return false
    }
    if(registry?.isRunning == true) {
        return false
    }
    return true
    // fixme:转场时保证动画流畅，必须关闭Haze库的特效（即使画面不动，只要它还留存，就特别吃性能）
}

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun Modifier.containerBlur(hazeState: HazeState, color : Color) : Modifier = blurStyle(hazeState,1.5f,color,.5f, enabled = CAN_HAZE_BLUR_BAR)
@Composable
fun Modifier.bottomBarBlur(
    hazeState : HazeState,
    color : Color = MaterialTheme.colorScheme.surface,
) : Modifier {
    return if(
        canLayerBlur()
        && !HAZE_BLUR_FOR_S
        ) {
        this.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                tint = null,
//                tint = HazeTint(color = color.copy(0.4f)),
                backgroundColor = color,
                blurRadius = progressiveBarBlur,
                noiseFactor = 0f
            ),
            block = fun HazeEffectScope.() {
                progressive = HazeProgressive.verticalGradient(
//                     适配安卓12
                    startIntensity = 0f,
                    endIntensity = 1f,
                )
            }
        )
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to color.copy(alpha = 0f),
                        1.0f to color.copy(alpha = 0.9f),
                    )
                )
            )
    } else {
        return this.background(
            Brush.verticalGradient(
                colorStops = arrayOf(
                    0.0f to color.copy(alpha = 0f),
                    0.25f to color.copy(alpha = 0.65f),
                    0.50f to color.copy(alpha = 0.80f),
                    0.75f to color.copy(alpha = 0.95f),
                    1.0f to color.copy(alpha = 0.95f),
                )
            )
        )
    }
}

@Composable
fun Modifier.newBottomBarBlur(
    hazeState : HazeState,
    color : Color = MaterialTheme.colorScheme.surface,
) : Modifier {
    return if(
        canLayerBlur()
        && !HAZE_BLUR_FOR_S
    ) {
        this.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                tint = null,
//                tint = HazeTint(color = color.copy(0.4f)),
                backgroundColor = color,
                blurRadius = 5.dp,
                noiseFactor = 0f
            ),
            block = fun HazeEffectScope.() {
                progressive = HazeProgressive.verticalGradient(
//                     适配安卓12
                    startIntensity = 0f,
                    endIntensity = 1f,
                )
            }
        )
    } else {
        return this.background(
            Brush.verticalGradient(
                colorStops = arrayOf(
                    0.0f to color.copy(alpha = 0f),
                    0.25f to color.copy(alpha = 0.65f),
                    0.50f to color.copy(alpha = 0.80f),
                    0.75f to color.copy(alpha = 0.95f),
                    1.0f to color.copy(alpha = 0.95f),
                )
            )
        )
    }
}

@Composable
fun canLayerBlur(): Boolean {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    return enableEffect() && blur && CAN_HAZE_BLUR_BAR
}

@Composable
fun Modifier.topBarBlur(
    hazeState : HazeState,
    backgroundColor : Color = MaterialTheme.colorScheme.surface,
    color : Color = backgroundColor
) : Modifier {
    return if(canLayerBlur()) {
        this.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                tint = null,
//                tint = HazeTint(color = color.let { if(it == Color.Transparent) it else it.copy(.4f) }),
                backgroundColor = color,
                blurRadius = progressiveBarBlur,
                noiseFactor = 0f
            ),
            block = fun HazeEffectScope.() {
                progressive = HazeProgressive.verticalGradient(
                    // 适配安卓12不了一点儿，效果比较差，直接砍了
                    startIntensity = if(HAZE_BLUR_FOR_S) 0.9f else 1f,
                    endIntensity = if(HAZE_BLUR_FOR_S) 0.1f else 0f,
                )
            }
        )
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to color.copy(alpha = 0.9f),
                        1.0f to color.copy(alpha = 0f),
                    )
                )
            )
    } else {
        this.background(
            Brush.verticalGradient(
                colorStops = arrayOf(
                    0.0f to color.copy(alpha = 0.95f),
                    0.25f to color.copy(alpha = 0.95f),
                    0.50f to color.copy(alpha = 0.80f),
                    0.75f to color.copy(alpha = 0.65f),
                    1.0f to color.copy(alpha = 0f),
                )
            )
        )
    }
}


@Composable
fun Modifier.backDropSource(
    backdrop : ShaderState
): Modifier {
    val isTransitioning = LocalNavControllerSafely.current?.isTransitioning ?: false
    val enableEffect = enableEffect()
    val enableLiquidGlass by DataStoreManager.enableLiquidGlass.collectAsState(initial = AppVersion.CAN_SHADER)
    return if(enableLiquidGlass && enableEffect) {
        this.shaderSource(backdrop)
    } else {
        this
    }
}
@Composable
fun Modifier.backDropSource(
    backdrop : LayerBackdrop
): Modifier {
    val isTransitioning = LocalNavControllerSafely.current?.isTransitioning ?: false
    val enableEffect = enableEffect()
    val enableLiquidGlass by DataStoreManager.enableLiquidGlass.collectAsState(initial = AppVersion.CAN_SHADER)
    return if(enableLiquidGlass && enableEffect) {
        this.layerBackdrop(backdrop)
    } else {
        this
    }
}



@Composable
fun Modifier.normalTopBarBlur(
    hazeState: HazeState,
    backgroundColor : Color = MaterialTheme.colorScheme.surface,
    color : Color = Color.Transparent
) : Modifier {
    return if(canLayerBlur()) {
        this.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                tint = HazeTint(color = color.let { if(it == Color.Transparent) it else it.copy(.35f) }),
                backgroundColor = backgroundColor,
                blurRadius = MyApplication.BLUR_RADIUS.dp*1.2f,
                noiseFactor = 0f
            ),
        )
    } else {
        this.background(backgroundColor.copy(.925f))
    }
}

@Composable
private fun Modifier.blurStyle(
    hazeState: HazeState,
    radius : Float = 1f,
    tint : Color = MaterialTheme.colorScheme.surface,
    alpha : Float = .3f,
    enabled : Boolean = true
) : Modifier {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    return if(blur&& enabled) {
        this.hazeEffect(state = hazeState, style = HazeStyle(
            tint = HazeTint(color =  tint.copy(alpha)),
            backgroundColor = tint,
            blurRadius = MyApplication.BLUR_RADIUS.dp*radius,
            noiseFactor = 0f
        ))
    } else {
        this.background(tint.copy(.8f))
    }
}

@Composable
fun Modifier.dialogBlur(hazeState: HazeState) : Modifier = blurStyle(hazeState,2.5f, MaterialTheme.colorScheme.surface,0f,)

@Composable
fun Modifier.bottomSheetBlur(hazeState: HazeState) : Modifier = blurStyle(hazeState, 3f, MaterialTheme.colorScheme.surface,.3f,)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HazeBottomSheet(
    showBottomSheet : Boolean,
    onDismissRequest : () -> Unit,
    content : @Composable () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var height by remember { mutableStateOf<Int?>(null) }
    val density = LocalDensity.current
    val statusBarHeight = WindowInsets.statusBars.getTop(density)
    val navigationBarHeight = WindowInsets.navigationBars.getBottom(density)
    val screenHeight = with(density) { LocalConfiguration.current.screenHeightDp.dp.roundToPx() }
    val finalScreenHeight = screenHeight - statusBarHeight - navigationBarHeight
    val isFullScreen = height != null && height!! >= finalScreenHeight
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = bottomSheetRound(sheetState, isFullScreen)
    ) {
        Column(modifier = Modifier
            .onGloballyPositioned { coordinates ->
                height = coordinates.size.height
            }

//            .bottomSheetBlur(hazeState)
        ){
            if(!isFullScreen) {
                Spacer(Modifier.height(APP_HORIZONTAL_DP *1.5f))
            }
            content()
        }
    }
}

// 用于遮挡的blur
@Composable
fun Modifier.coverBlur(
    showBlur: Boolean,
    radius: Dp = MyApplication.BLUR_RADIUS.dp/2,
    tweenDuration: Int = AppAnimationManager.ANIMATION_SPEED / 2
): Modifier {
    val motionBlur by DataStoreManager.enableMotionBlur.collectAsState(initial = AppVersion.CAN_MOTION_BLUR)

    val blurRadius by animateDpAsState(
        targetValue = if (showBlur && motionBlur) radius else 0.dp,
        animationSpec = tween(tweenDuration, easing = LinearOutSlowInEasing),
        label = "BlurAnimation"
    )

    val overlayAlpha by animateFloatAsState(
        targetValue = if (showBlur && !motionBlur) 1f else 0f,
        animationSpec = tween(tweenDuration, easing = LinearOutSlowInEasing),
        label = "OverlayAlphaAnimation"
    )
    val color = largeCardColor()

    return this
        .then(if (motionBlur) this.blur(blurRadius) else this)
        .let {
            if(!motionBlur) {
                it.drawWithContent {
                    drawContent()
                    drawRect(
                        color = color.copy(alpha = overlayAlpha)
                    )
                }
            } else it
        }
}

fun Modifier.calendarSquareGlass(
    state : ShaderState,
    color : Color,
) : Modifier = this.layerGlass(
    state,
    style = GlassStyle(
        blur = 3.5.dp ,
        border = 30f,
        dispersion = 0f,
        distortFactor = 0f,
        stretchFactor = 0.4f,
        overlayColor = color
    )
)

fun Modifier.layerGlass(
    state: ShaderState,
    style: GlassStyle = largeStyle,
) : Modifier = composed {
    val enableLiquidGlass by DataStoreManager.enableLiquidGlass.collectAsState(initial = AppVersion.CAN_SHADER)
    val enableEffect = enableEffect()
    this.glassLayer(
        state,
        style = style,
        enabled = enableEffect && enableLiquidGlass
    )
}




