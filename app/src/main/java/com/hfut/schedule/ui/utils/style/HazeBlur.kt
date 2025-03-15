package com.hfut.schedule.ui.utils.style

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.utils.VersionUtils
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.ui.activity.home.main.saved.MultiScheduleSettings
import com.hfut.schedule.ui.utils.components.AppHorizontalDp
import com.hfut.schedule.ui.utils.components.BottomSheetTopBar
import dev.chrisbanes.haze.HazeEffectScope
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect

@Composable
fun Modifier.bottomBarBlur(hazeState : HazeState) : Modifier {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val blur = prefs.getBoolean("SWITCHBLUR", VersionUtils.canBlur)
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
    val blur = prefs.getBoolean("SWITCHBLUR", VersionUtils.canBlur)
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
    val blur = prefs.getBoolean("SWITCHBLUR", VersionUtils.canBlur)
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
            Spacer(Modifier.height(AppHorizontalDp() *1.5f))
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


