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
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.other.AppVersion.CAN_HAZE_BLUR_BAR
import com.hfut.schedule.logic.util.other.AppVersion.HAZE_BLUR_FOR_S
import com.xah.uicommon.style.APP_HORIZONTAL_DP

import com.hfut.schedule.ui.component.container.largeCardColor
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.ui.style.corner.bottomSheetRound
import com.hfut.schedule.ui.util.AppAnimationManager
import com.xah.transition.state.TransitionState
import com.xah.transition.style.TransitionLevel
import com.xah.transition.style.transitionBackground
import com.xah.transition.util.isCurrentRouteWithoutArgs
import dev.chrisbanes.haze.HazeEffectScope
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first


@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun Modifier.containerBlur(hazeState: HazeState, color : Color) : Modifier = blurStyle(hazeState,1.5f,color,.5f, level = HazeBlurLevel.FULL, limit = CAN_HAZE_BLUR_BAR)
@Composable
fun Modifier.bottomBarBlur(hazeState : HazeState,color : Color = MaterialTheme.colorScheme.surface) : Modifier {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    return if(
        blur >= HazeBlurLevel.MID.code
        && CAN_HAZE_BLUR_BAR
        && !HAZE_BLUR_FOR_S
        ) {
        this.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                tint = HazeTint(color = color.copy(0.35f)),
                backgroundColor = color,
                blurRadius = MyApplication.BLUR_RADIUS* 1.2f,
                noiseFactor = 0f
            ),
            block = fun HazeEffectScope.() {
                progressive = HazeProgressive.verticalGradient(
                    // 适配安卓12
                    startIntensity = 0f,
                    endIntensity = 1f,
                )
            })
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
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    return if(blur >= HazeBlurLevel.MID.code && CAN_HAZE_BLUR_BAR) {
        this.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                tint = HazeTint(color = color.let { if(it == Color.Transparent) it else it.copy(.35f) }),
                backgroundColor = backgroundColor,
                blurRadius = MyApplication.BLUR_RADIUS*1.2f,
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
fun Modifier.normalTopBarBlur(
    hazeState: HazeState,
    backgroundColor : Color = MaterialTheme.colorScheme.surface,
    color : Color = Color.Transparent
) : Modifier {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    return if(blur >= HazeBlurLevel.MID.code && CAN_HAZE_BLUR_BAR) {
        this.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                tint = HazeTint(color = color.let { if(it == Color.Transparent) it else it.copy(.35f) }),
                backgroundColor = backgroundColor,
                blurRadius = MyApplication.BLUR_RADIUS*1.2f,
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
    level : HazeBlurLevel,
    limit : Boolean = true
) : Modifier {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    return if(blur >= level.code && limit) {
        this.hazeEffect(state = hazeState, style = HazeStyle(
            tint = HazeTint(color =  tint.copy(alpha)),
            backgroundColor = tint,
            blurRadius = MyApplication.BLUR_RADIUS*radius,
            noiseFactor = 0f
        ))
    } else {
        this.background(tint.copy(.8f))
    }
}

@Composable
fun Modifier.dialogBlur(hazeState: HazeState) : Modifier = blurStyle(hazeState,2.5f, MaterialTheme.colorScheme.surface,0f, level = HazeBlurLevel.MID)

@Composable
fun Modifier.bottomSheetBlur(hazeState: HazeState) : Modifier = blurStyle(hazeState, 3f, MaterialTheme.colorScheme.surface,.3f, level = HazeBlurLevel.MID)


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
        shape = bottomSheetRound(sheetState, autoShape)
    ) {
        content()
    }
}

// isExpanded=true时，下层背景进入高斯模糊，并用黑色压暗，伴随缩放，上层背景展开
@Composable
fun Modifier.transitionBackground2(isExpanded : Boolean) : Modifier {
    val motionBlur by DataStoreManager.enableMotionBlur.collectAsState(initial = AppVersion.CAN_MOTION_BLUR)
    val transition by DataStoreManager.transitionLevel.collectAsState(initial = TransitionLevel.NONE.code)
    // 👍 NONE
    if(transition == TransitionLevel.NONE.code) {
        return this
    }

    // 蒙版
    val backgroundColor by animateFloatAsState(
        targetValue = if(isExpanded) TransitionState.transitionBackgroundStyle.backgroundDark else 0f,
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
            with(TransitionState.transitionBackgroundStyle) {
                scale
            }
        } else 1f, // 按下时为0.9，松开时为1
        animationSpec = tween(AppAnimationManager.ANIMATION_SPEED + AppAnimationManager.ANIMATION_SPEED/2, easing = FastOutSlowInEasing),
        label = "" // 使用弹簧动画
    )

    if(transition >= TransitionLevel.MEDIUM.code) {
        val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
        LaunchedEffect(isExpanded) {
            val origin = blur
            if(blur >= HazeBlurLevel.MID.code && transition == TransitionLevel.HIGH.code) {
                DataStoreManager.saveHazeBlur(HazeBlurLevel.NONE)
                delay((AppAnimationManager.ANIMATION_SPEED + AppAnimationManager.ANIMATION_SPEED/2)*1L)
                DataStoreManager.saveHazeBlur(HazeBlurLevel.entries.find { it.code == origin } ?: HazeBlurLevel.MID)
            }
        }
    }

    // 👍 MEDIUM
    if(transition == TransitionLevel.MEDIUM.code) {
        return darkModifier.scale(scale.value)
    }

    // 稍微晚于运动结束
    val blurSize by animateDpAsState(
        targetValue = if (isExpanded && motionBlur) {
            with(TransitionState.transitionBackgroundStyle) {
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

@Composable
fun Modifier.transitionBackgroundF(
    navHostController: NavHostController,
    route : String,
) : Modifier = with(TransitionState.transitionBackgroundStyle) {
    val blur by produceState(initialValue = HazeBlurLevel.MID.code) {
        value = DataStoreManager.enableHazeBlur.first()
    }

    val isExpanded = !navHostController.isCurrentRouteWithoutArgs(route)
    val speed = TransitionState.curveStyle.speedMs

    LaunchedEffect(isExpanded) {
        if(blur == HazeBlurLevel.FULL.code && TransitionState.transitionBackgroundStyle.level >= TransitionLevel.MEDIUM) {
            val origin = blur
            DataStoreManager.saveHazeBlur(HazeBlurLevel.NONE)
            delay(speed*4/3*1L)
            DataStoreManager.saveHazeBlur(HazeBlurLevel.entries.find { it.code == origin } ?: HazeBlurLevel.MID)
        }
    }
    return transitionBackground(navHostController,route)
}

// 用于遮挡的blur
@Composable
fun coverBlur(
    showBlur: Boolean,
    radius: Dp = MyApplication.BLUR_RADIUS/2,
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

    return Modifier
        .then(if (motionBlur) Modifier.blur(blurRadius) else Modifier)
        .drawWithContent {
            drawContent()
            if (!motionBlur && overlayAlpha > 0f) {
                drawRect(
                    color = color.copy(alpha = overlayAlpha)
                )
            }
        }
}



