package com.hfut.schedule.ui.style

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.screen.home.calendar.multi.MultiScheduleSettings
import com.hfut.schedule.ui.component.appHorizontalDp
import com.hfut.schedule.ui.component.BottomSheetTopBar
import dev.chrisbanes.haze.HazeEffectScope
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import kotlinx.coroutines.delay

@Composable
fun Modifier.bottomBarBlur(hazeState : HazeState) : Modifier {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = AppVersion.canBlur)
    return if(blur) {
        this.hazeEffect(state = hazeState,
            style = HazeStyle(
                tint = HazeTint(color = surfaceColor),
                backgroundColor = Color.Transparent,
                blurRadius = MyApplication.BLUR_RADIUS,
                noiseFactor = 0f
            ),
            block = fun HazeEffectScope.() {
                    progressive = HazeProgressive.verticalGradient(
                        startIntensity = 0f,
                        endIntensity = .75f,
                        endY = Float.POSITIVE_INFINITY
                    )
            })
    } else {
        return this.background(Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                surfaceColor,
            )
        ))
    }
}


@Composable
fun Modifier.topBarBlur(hazeState : HazeState) : Modifier {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = AppVersion.canBlur)
    return if(blur) {
         this.hazeEffect(state = hazeState,
            style = HazeStyle(
                tint = HazeTint(color = surfaceColor),
                backgroundColor = Color.Transparent,
                blurRadius = MyApplication.BLUR_RADIUS,
                noiseFactor = 0f
            ),
            block = fun HazeEffectScope.() {
                    progressive = HazeProgressive.verticalGradient(startIntensity = .75f, endIntensity = 0f)
            })
    } else {
         this.background(Brush.verticalGradient(
            colors = listOf(
                surfaceColor,
                Color.Transparent,
            )
        ))
    }
}

@Composable
private fun Modifier.blurStyle(hazeState: HazeState,radius : Float = 1f,tint : Color = Color.Transparent) : Modifier {
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = AppVersion.canBlur)
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
            Spacer(Modifier.height(appHorizontalDp() *1.5f))
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
    val motionBlur by DataStoreManager.motionBlurFlow.collectAsState(initial = true)
    val transition by DataStoreManager.transitionFlow.collectAsState(initial = false)
    // 稍微晚于运动结束
    val blurSize by animateDpAsState(
        targetValue = if (isExpanded && motionBlur) 12.dp else 0.dp, label = ""
        ,animationSpec = tween(MyApplication.ANIMATION_SPEED + 100, easing = LinearOutSlowInEasing),
    )
    val scale = animateFloatAsState(
        targetValue = if (isExpanded) 0.8f else 1f, // 按下时为0.9，松开时为1
        animationSpec = tween(MyApplication.ANIMATION_SPEED + 100, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )
    val backgroundColor by animateColorAsState(
        targetValue = if(isExpanded) Color.Black.copy(.3f) else Color.Transparent,
        animationSpec = tween(MyApplication.ANIMATION_SPEED, easing = LinearOutSlowInEasing),
        label = "",
    )
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = AppVersion.canBlur)
    LaunchedEffect(isExpanded) {
        if(blur && transition) {
            DataStoreManager.saveHazeBlur(false)
            delay((MyApplication.ANIMATION_SPEED + 100)*1L)
            DataStoreManager.saveHazeBlur(true)
        }
    }
    // 蒙版
    if(transition)
        Box(modifier = Modifier.fillMaxSize().background(backgroundColor).zIndex(2f))

    val transitionModifier = if(transition) Modifier.blur(blurSize).scale(scale.value) else Modifier
    return transitionModifier
}


