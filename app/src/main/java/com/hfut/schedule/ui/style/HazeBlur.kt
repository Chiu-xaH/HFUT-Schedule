package com.hfut.schedule.ui.style

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
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
import com.hfut.schedule.ui.component.container.MyCustomCard
import com.hfut.schedule.ui.component.container.StyleCardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem

import com.hfut.schedule.ui.component.container.largeCardColor
import com.hfut.schedule.ui.util.AppAnimationManager
import com.xah.transition.state.TransitionState
import com.xah.transition.style.TransitionLevel
import com.xah.transition.style.transitionBackground
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
fun Modifier.containerBlur(hazeState: HazeState, color : Color) : Modifier = blurStyle(hazeState,2.5f,color,.5f)
@Composable
fun Modifier.bottomBarBlur(hazeState : HazeState,color : Color = MaterialTheme.colorScheme.surface) : Modifier {
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    return if(
        blur
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
fun Modifier.topBarBlur(hazeState : HazeState,color : Color = MaterialTheme.colorScheme.surface) : Modifier {
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    return if(blur && CAN_HAZE_BLUR_BAR) {
        this.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                tint = HazeTint(color = color.copy(.35f)),
                backgroundColor = color,
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
private fun Modifier.blurStyle(
    hazeState: HazeState,
    radius : Float = 1f,
    tint : Color = MaterialTheme.colorScheme.surface,
    alpha : Float = .3f
) : Modifier {
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    return if(blur) {
        this.hazeEffect(state = hazeState, style = HazeStyle(
            tint = HazeTint(color =  tint.copy(alpha)),
            backgroundColor = tint,
            blurRadius = MyApplication.BLUR_RADIUS*radius,
            noiseFactor = 0f
        ))
    } else {
        this.background(tint)
    }
}

@Composable
fun Modifier.dialogBlur(hazeState: HazeState) : Modifier = blurStyle(hazeState,2.5f, MaterialTheme.colorScheme.surface,0f)

@Composable
fun Modifier.bottomSheetBlur(hazeState: HazeState) : Modifier = blurStyle(hazeState, 3f, MaterialTheme.colorScheme.surface,.3f)


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
fun transitionBackground2(isExpanded : Boolean) : Modifier {
    val motionBlur by DataStoreManager.motionBlurFlow.collectAsState(initial = AppVersion.CAN_MOTION_BLUR)
    val transition by DataStoreManager.transitionFlow.collectAsState(initial = TransitionLevel.NONE.code)
    // 👍 NONE
    if(transition == TransitionLevel.NONE.code) {
        return Modifier
    }

    // 蒙版
    val backgroundColor by animateColorAsState(
        targetValue = if(isExpanded) Color.Black.copy(.25f) else Color.Transparent,
        animationSpec = tween(AppAnimationManager.ANIMATION_SPEED, easing = FastOutSlowInEasing),
        label = "",
    )
    if(transition >= TransitionLevel.LOW.code)
        Box(modifier = Modifier.fillMaxSize().background(backgroundColor).zIndex(2f))
    // 👍 LOW
    if(transition == TransitionLevel.LOW.code) {
        return Modifier
    }

    // 稍微晚于运动结束
    val blurSize by animateDpAsState(
        targetValue = if (isExpanded && motionBlur) MyApplication.BLUR_RADIUS else 0.dp, label = ""
        ,animationSpec = tween(AppAnimationManager.ANIMATION_SPEED + AppAnimationManager.ANIMATION_SPEED/2, easing = FastOutSlowInEasing),
    )
    // 👍 MEDIUM
    if(transition == TransitionLevel.MEDIUM.code) {
        return Modifier.blur(blurSize)
    }


    val scale = animateFloatAsState(
        targetValue = if (isExpanded) 0.825f else 1f, // 按下时为0.9，松开时为1
        animationSpec = tween(AppAnimationManager.ANIMATION_SPEED + AppAnimationManager.ANIMATION_SPEED/2, easing = FastOutSlowInEasing),
        label = "" // 使用弹簧动画
    )

    if(transition == TransitionLevel.HIGH.code) {
        val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
        LaunchedEffect(isExpanded) {
            if(blur && transition == TransitionLevel.HIGH.code) {
                DataStoreManager.saveHazeBlur(false)
                delay((AppAnimationManager.ANIMATION_SPEED + AppAnimationManager.ANIMATION_SPEED/2)*1L)
                DataStoreManager.saveHazeBlur(true)
            }
        }
    }

    // 👍 HIGH
    return Modifier.blur(blurSize).scale(scale.value)
}

@Composable
fun Modifier.transitionBackgroundF(
    navHostController: NavHostController,
    route : String,
) : Modifier = with(TransitionState.transitionBackgroundStyle) {
    val blur by produceState(initialValue = true) {
        value = DataStoreManager.hazeBlurFlow.first()
    }

    val isExpanded = !navHostController.isCurrentRoute(route)
    val speed = TransitionState.curveStyle.speedMs

    LaunchedEffect(isExpanded) {
        if(blur && TransitionState.transitionBackgroundStyle.level == TransitionLevel.HIGH) {
            DataStoreManager.saveHazeBlur(false)
            delay(speed*1L)
            DataStoreManager.saveHazeBlur(true)
        }
    }
    return transitionBackground(navHostController,route)
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


@Composable
@Preview
fun SpringAnimationTimer() {
    var startTime by remember { mutableStateOf(0L) }
    var trigger by remember { mutableStateOf(false) }
    var duration by remember { mutableStateOf(0L) }
    var dampingRatio by remember { mutableStateOf(Spring.DampingRatioLowBouncy * 1.1f) }
    var stiffness by remember { mutableStateOf(Spring.StiffnessLow) }
    val animatedValue by animateFloatAsState(
        targetValue = if (trigger) 350f else 75f,
        animationSpec = spring(
            dampingRatio = dampingRatio,
            stiffness = stiffness,
        ),
        finishedListener = {
            val endTime = System.currentTimeMillis()
            duration = endTime - startTime
            Log.d("Timer", "动画耗时: $duration ms")
        }
    )

    LaunchedEffect(trigger) {
        startTime = System.currentTimeMillis()
    }


    Column (Modifier.fillMaxSize()) {
        Spacer(Modifier.padding(APP_HORIZONTAL_DP*6).statusBarsPadding())
        RowHorizontal() {
            Box(
                Modifier
                    .size(animatedValue.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { trigger = !trigger }
            )
        }
        Spacer(Modifier.padding(APP_HORIZONTAL_DP*1).statusBarsPadding())

        Column(modifier = Modifier.navigationBarsPadding()) {
            StyleCardListItem(
                headlineContent = {
                    Text("时长 ${duration } ms")
                },
                color = MaterialTheme.colorScheme.surface
            )
            Spacer(Modifier.height(APP_HORIZONTAL_DP/3))
            MyCustomCard(containerColor = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = {
                        Text("dampingRatio $dampingRatio \n初始 ${Spring.DampingRatioLowBouncy * 1.1f}")
                    },
                    trailingContent = {
                        Button(onClick = {
                            dampingRatio = Spring.DampingRatioLowBouncy * 1.1f
                        }) {
                            Text("恢复")
                        }
                    }
                )
                Slider(
                    value = dampingRatio,
                    onValueChange = {
                        dampingRatio = it
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.secondary,
                        activeTrackColor = MaterialTheme.colorScheme.secondary,
                        inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    valueRange = 0f..2f,

                    modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)
                )
            }
            Spacer(Modifier.height(APP_HORIZONTAL_DP/3))
            MyCustomCard(containerColor = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = {
                        Text("stiffness $stiffness \n初始 ${Spring.StiffnessLow}")
                    },
                    trailingContent = {
                        Button(onClick = {
                            stiffness = Spring.StiffnessLow
                        }) {
                            Text("恢复")
                        }
                    }
                )
                Slider(
                    value = stiffness,
                    onValueChange = {
                        stiffness = it
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.secondary,
                        activeTrackColor = MaterialTheme.colorScheme.secondary,
                        inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    valueRange = 0f..400f,

                    modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)
                )
            }
        }
    }
}
