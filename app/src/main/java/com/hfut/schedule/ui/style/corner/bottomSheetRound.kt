package com.hfut.schedule.ui.style.corner

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.xah.common.ScreenCornerHelper
import com.xah.uicommon.util.LogUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun bottomSheetRound(sheetState: SheetState, autoShape : Boolean = true) : Shape {
    return if(!autoShape) {
        BottomSheetDefaults.ExpandedShape
    } else {
        val progress = roundDp(sheetState)
        (BottomSheetDefaults.ExpandedShape as? CornerBasedShape)?.lerp(RoundedCornerShape(0.dp),progress) as? CornerBasedShape ?: BottomSheetDefaults.ExpandedShape
    }
}
@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun roundDp(sheetState : SheetState) : Float {
    val dpAnimation by animateFloatAsState(
        targetValue = if (sheetState.currentValue != SheetValue.Expanded) 0f else 1f, label = ""
        ,animationSpec = tween(AppAnimationManager.ANIMATION_SPEED / 2, easing = LinearOutSlowInEasing),
    )
    return dpAnimation
}