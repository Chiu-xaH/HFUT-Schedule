package com.hfut.schedule.ui.style.special

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.other.AppVersion.CAN_HAZE_BLUR_BAR
import com.hfut.schedule.logic.util.other.AppVersion.HAZE_BLUR_FOR_S
import com.xah.uicommon.style.APP_HORIZONTAL_DP

import com.hfut.schedule.ui.component.container.largeCardColor
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.ui.style.corner.bottomSheetRound
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.ui.util.state.GlobalUIStateHolder
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.refraction
import com.kyant.backdrop.effects.vibrancy
import com.xah.transition.state.TransitionConfig
import com.xah.transition.style.TransitionLevel
import com.xah.transition.style.transitionBackground
import com.xah.transition.style.transitionSkip
import com.xah.transition.util.isCurrentRouteWithoutArgs
import dev.chrisbanes.haze.HazeEffectScope
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import kotlinx.coroutines.delay


@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun Modifier.containerBlur(hazeState: HazeState, color : Color) : Modifier = blurStyle(hazeState,1.5f,color,.5f, limit = CAN_HAZE_BLUR_BAR)
@Composable
fun Modifier.bottomBarBlur(hazeState : HazeState,color : Color = MaterialTheme.colorScheme.surface) : Modifier {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    return if(
        !GlobalUIStateHolder.isTransiting &&
        blur
        && CAN_HAZE_BLUR_BAR
        && !HAZE_BLUR_FOR_S
        ) {
        this.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                tint = HazeTint(color = color.copy(0.4f)),
                backgroundColor = color,
                blurRadius = MyApplication.BLUR_RADIUS.dp* 1.2f,
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
                    1.0f to color.copy(alpha = 1f),
                )
            )
        )
    }
}

@Composable
fun Modifier.topBarBlur(
    hazeState : HazeState,
    backgroundColor : Color = MaterialTheme.colorScheme.surface,
    color : Color = backgroundColor
) : Modifier {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    return if(!GlobalUIStateHolder.isTransiting && blur && CAN_HAZE_BLUR_BAR) {
        this.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                tint = HazeTint(color = color.let { if(it == Color.Transparent) it else it.copy(.4f) }),
                backgroundColor = backgroundColor,
                blurRadius = MyApplication.BLUR_RADIUS.dp*1.2f,
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
    } else {
        this.background(
            Brush.verticalGradient(
                colorStops = arrayOf(
                    0.0f to color.copy(alpha = 1f),
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
    backdrop : LayerBackdrop
): Modifier {
    val enableLiquidGlass by DataStoreManager.enableLiquidGlass.collectAsState(initial = AppVersion.CAN_SHADER)
    return if(enableLiquidGlass) {
        this.layerBackdrop(backdrop)
    } else {
        this
    }
}

fun Modifier.containerBackDrop(
    backdrop: Backdrop,
    shape: Shape
) : Modifier {
    return this.drawBackdrop(
        highlight = null,
        backdrop = backdrop,
        shape = { shape },
        effects = {
            vibrancy()
            blur(7.5f.dp.toPx())
            refraction(15f.dp.toPx(), 25f.dp.toPx())
        },
        shadow = null,
    )
}
@Composable
fun Modifier.normalTopBarBlur(
    hazeState: HazeState,
    backgroundColor : Color = MaterialTheme.colorScheme.surface,
    color : Color = Color.Transparent
) : Modifier {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    return if(blur && CAN_HAZE_BLUR_BAR) {
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
    limit : Boolean = true
) : Modifier {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    return if(blur&& limit) {
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
    isFullExpand : Boolean = true,
    hazeState: HazeState,
    autoShape : Boolean = true,
    content : @Composable () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = isFullExpand)
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = bottomSheetRound(sheetState, autoShape)
    ) {
        Column(modifier = Modifier.bottomSheetBlur(hazeState)){
            Spacer(Modifier.height(APP_HORIZONTAL_DP *1.5f))
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheet(
    showBottomSheet : Boolean,
    onDismissRequest : () -> Unit,
    isFullExpand : Boolean = true,
    autoShape : Boolean = true,
    content : @Composable () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = isFullExpand)
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = bottomSheetRound(sheetState, autoShape)
    ) {
        content()
    }
}

// isExpanded=true时，下层背景进入高斯模糊，并用黑色压暗，伴随缩放，上层背景展开
@Composable
fun Modifier.transitionBackground2(isExpanded : Boolean) : Modifier {
    val motionBlur by DataStoreManager.enableMotionBlur.collectAsState(initial = AppVersion.CAN_MOTION_BLUR)
    val transition by DataStoreManager.transitionLevel.collectAsState(initial = TransitionLevel.MEDIUM.code)
    // 👍 NONE
    if(transition <= TransitionLevel.NONE.code) {
        return this
    }

    // 蒙版
    val backgroundColor by animateFloatAsState(
        targetValue = if(isExpanded) TransitionConfig.transitionBackgroundStyle.backgroundDark else 0f,
        animationSpec = tween(AppAnimationManager.ANIMATION_SPEED, easing = FastOutSlowInEasing),
    )

    val darkModifier = this.let {
        if(transition >= TransitionLevel.LOW.code) {
            it.drawWithContent {
                drawContent()
                drawRect(Color.Black.copy(alpha = backgroundColor))
            }
        } else it
    }
    // 👍 LOW
    if(transition == TransitionLevel.LOW.code) {
        return darkModifier
    }


    val scale = animateFloatAsState(
        targetValue = if (isExpanded) {
            with(TransitionConfig.transitionBackgroundStyle) {
                scale
            }
        } else 1f, // 按下时为0.9，松开时为1
        animationSpec = tween(AppAnimationManager.ANIMATION_SPEED + AppAnimationManager.ANIMATION_SPEED/2, easing = FastOutSlowInEasing),
        label = "" // 使用弹簧动画
    )

    if(transition >= TransitionLevel.MEDIUM.code) {
        LaunchedEffect(isExpanded) {
            GlobalUIStateHolder.isTransiting = true
            delay((AppAnimationManager.ANIMATION_SPEED + AppAnimationManager.ANIMATION_SPEED/2)*1L)
            GlobalUIStateHolder.isTransiting = false
        }
    }

    // 👍 MEDIUM
    if(transition == TransitionLevel.MEDIUM.code) {
        return darkModifier.scale(scale.value)
    }

    // 稍微晚于运动结束
    val blurSize by animateDpAsState(
        targetValue = if (isExpanded && motionBlur) {
            with(TransitionConfig.transitionBackgroundStyle) {
                blurRadius
            }
        } else 0.dp, label = ""
        ,animationSpec = tween(AppAnimationManager.ANIMATION_SPEED + AppAnimationManager.ANIMATION_SPEED/2, easing = FastOutSlowInEasing),
    )

    // 👍 HIGH
    return darkModifier
        .blur(blurSize)
        .scale(scale.value)
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



