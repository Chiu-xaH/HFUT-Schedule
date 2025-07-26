package com.hfut.schedule.ui.style

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.other.AppVersion.CAN_HAZE_BLUR_BAR
import com.hfut.schedule.logic.util.other.AppVersion.HAZE_BLUR_FOR_S
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP

import com.hfut.schedule.ui.component.container.largeCardColor
import com.hfut.schedule.ui.util.AppAnimationManager
import com.xah.transition.state.TransitionState
import com.xah.transition.util.isCurrentRoute
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
fun Modifier.zIndexBlur(hazeState: HazeState,color : Color) : Modifier {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    return if(blur && CAN_HAZE_BLUR_BAR) {
        this.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                tint = HazeTint(color = surfaceColor.copy(0.35f)),
                backgroundColor = Color.Transparent,
                blurRadius = MyApplication.BLUR_RADIUS* 1.2f,
                noiseFactor = 0f
            )
        )
    } else {
        return this.background(color)
    }
}
@Composable
fun Modifier.bottomBarBlur(hazeState : HazeState) : Modifier {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    return if(
        blur
        && CAN_HAZE_BLUR_BAR
        && !HAZE_BLUR_FOR_S
        ) {
        this.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                tint = HazeTint(color = surfaceColor.copy(0.35f)),
                backgroundColor = Color.Transparent,
                blurRadius = MyApplication.BLUR_RADIUS* 1.2f,
                noiseFactor = 0f
            ),
            block = fun HazeEffectScope.() {
                progressive = HazeProgressive.verticalGradient(
                    // 适配安卓12
                    startIntensity =  0f,
                    endIntensity =  1f,
                )
            })
    } else {
        return this.background(Brush.verticalGradient(
            listOf(Color.Transparent, surfaceColor)
        ))
    }
}


@Composable
fun Modifier.topBarBlur(hazeState : HazeState) : Modifier {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    return if(blur && CAN_HAZE_BLUR_BAR) {
        this.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                tint = HazeTint(color = surfaceColor.copy(.35f)),
                backgroundColor = Color.Transparent,
                blurRadius = MyApplication.BLUR_RADIUS*1.2f,
                noiseFactor = 0f
            ),
            block = fun HazeEffectScope.() {
                progressive = HazeProgressive.verticalGradient(
                    // 适配安卓12不了一点儿，效果比较差，直接砍了
                    startIntensity = if(HAZE_BLUR_FOR_S)0.9f else 1f,
                    endIntensity = if(HAZE_BLUR_FOR_S) 0.1f else 0f
                )
            }
        )
    } else {
         this.background(Brush.verticalGradient(
             listOf(surfaceColor, Color.Transparent)
        ))
    }
}

@Composable
private fun Modifier.blurStyle(hazeState: HazeState,radius : Float = 1f,tint : Color = Color.Transparent) : Modifier {
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    return if(blur) {
        this.hazeEffect(state = hazeState, style = HazeStyle(
            tint = HazeTint(color =  tint),
            backgroundColor = Color.Transparent,
            blurRadius = MyApplication.BLUR_RADIUS*radius,
            noiseFactor = 0f
        ))
    } else {
        Modifier
    }
}

@Composable
fun Modifier.dialogBlur(hazeState: HazeState) : Modifier = blurStyle(hazeState,2.5f)

@Composable
fun Modifier.bottomSheetBlur(hazeState: HazeState) : Modifier = blurStyle(
    hazeState,
    3f,
    tint = MaterialTheme.colorScheme.surface.copy(0.3f))



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
        shape = bottomSheetRound(sheetState,autoShape)
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
        shape = bottomSheetRound(sheetState,autoShape)
    ) {
        content()
    }
}

// isExpanded=true时，下层背景进入高斯模糊，并用黑色压暗，伴随缩放，上层背景展开
@Composable
fun transitionBackground(isExpanded : Boolean) : Modifier {
    val motionBlur by DataStoreManager.motionBlurFlow.collectAsState(initial = AppVersion.CAN_MOTION_BLUR)
    val transition by DataStoreManager.transitionFlow.collectAsState(initial = false)
    // 稍微晚于运动结束
    val blurSize by animateDpAsState(
        targetValue = if (isExpanded && motionBlur) MyApplication.BLUR_RADIUS else 0.dp, label = ""
        ,animationSpec = tween(AppAnimationManager.ANIMATION_SPEED + AppAnimationManager.ANIMATION_SPEED/2, easing = FastOutSlowInEasing),
    )
    val scale = animateFloatAsState(
        targetValue = if (isExpanded) 0.825f else 1f, // 按下时为0.9，松开时为1
        animationSpec = tween(AppAnimationManager.ANIMATION_SPEED + AppAnimationManager.ANIMATION_SPEED/2, easing = FastOutSlowInEasing),
        label = "" // 使用弹簧动画
    )
    val backgroundColor by animateColorAsState(
        targetValue = if(isExpanded) Color.Black.copy(.25f) else Color.Transparent,
        animationSpec = tween(AppAnimationManager.ANIMATION_SPEED, easing = FastOutSlowInEasing),
        label = "",
    )
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    LaunchedEffect(isExpanded) {
        if(blur && transition) {
            DataStoreManager.saveHazeBlur(false)
            delay((AppAnimationManager.ANIMATION_SPEED + AppAnimationManager.ANIMATION_SPEED/2)*1L)
            DataStoreManager.saveHazeBlur(true)
        }
    }
    // 蒙版
    if(transition)
        Box(modifier = Modifier.fillMaxSize().background(backgroundColor).zIndex(2f))

    val transitionModifier = if(transition) Modifier.blur(blurSize).scale(scale.value) else Modifier
    return transitionModifier
}

@Composable
fun Modifier.transitionBackgroundF(
    navHostController: NavHostController,
    route : String,
) : Modifier = with(TransitionState.transitionBackgroundStyle) {
    val blur by produceState(initialValue = true) {
        value = DataStoreManager.hazeBlurFlow.first()
    }
    if(route == TransitionState.firstStartRoute && TransitionState.firstUse) {
        return Modifier
    }
    // 禁用刚冷启动第一个界面模糊缩放
    if(TransitionState.firstUse && TransitionState.firstTransition) {
        TransitionState.firstUse = false
        return Modifier
    } else if(TransitionState.firstTransition) {
        // 禁用刚冷启动第一次转场动画的增强效果
        TransitionState.firstTransition = false
        return Modifier
    }

    val transplantBackground = TransitionState.transplantBackground
    val isExpanded = !navHostController.isCurrentRoute(route)
    val speed = TransitionState.curveStyle.speedMs + TransitionState.curveStyle.speedMs/2
    // 稍微晚于运动结束
    val blurSize by animateDpAsState(
        targetValue = if (isExpanded && motionBlur) blurRadius else 0.dp, label = ""
        ,animationSpec = tween(speed, easing = FastOutSlowInEasing),
    )
    val scale = animateFloatAsState( //.875f
        targetValue = if (isExpanded) scaleValue else 1f,
        animationSpec = tween(speed , easing = FastOutSlowInEasing)
    )
    val backgroundColor by animateColorAsState(
        targetValue = if(isExpanded) backgroundColor else Color.Transparent,
        animationSpec = tween(TransitionState.curveStyle.speedMs, easing = FastOutSlowInEasing)
    )
    LaunchedEffect(isExpanded) {
        if(blur && TransitionState.transitionBackgroundStyle.forceTransition) {
            DataStoreManager.saveHazeBlur(false)
            delay(speed*1L)
            DataStoreManager.saveHazeBlur(true)
        }
    }
    // 蒙版 遮罩
    if(!transplantBackground
        && forceTransition
        )
        Box(modifier = Modifier.fillMaxSize().background(backgroundColor).zIndex(2f))

    val transitionModifier = if(forceTransition) this@transitionBackgroundF.scale(scale.value).blur(blurSize) else this@transitionBackgroundF

    transitionModifier
}

@Composable
fun appBlur(
    showBlur: Boolean,
    radius: Dp = 10.dp,
    tweenDuration: Int = AppAnimationManager.ANIMATION_SPEED / 2
): Modifier {
    val motionBlur by DataStoreManager.motionBlurFlow.collectAsState(initial = AppVersion.CAN_MOTION_BLUR)

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
