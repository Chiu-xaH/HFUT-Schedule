package com.hfut.schedule.ui.component.icon

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.xah.common.ui.style.align.CenterScreen

@Composable
fun CircleProgressIcon(
    progress : Double,
    animate : Boolean,
    tintColor : Color = MaterialTheme.colorScheme.onSurfaceVariant,
    trackColor: Color = ProgressIndicatorDefaults.circularDeterminateTrackColor,
) = CircleProgressIcon(
    progress.toFloat(),
    animate,
    tintColor,
    trackColor
)

@Composable
fun CircleProgressIcon(
    progress : Float,
    animate : Boolean = true,
    tintColor : Color = MaterialTheme.colorScheme.onSurfaceVariant,
    trackColor: Color = ProgressIndicatorDefaults.circularDeterminateTrackColor,
) {
    val safeProgress = progress.coerceIn(0f, 1f)
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(safeProgress, animate) {
        if (animate) {
            animatedProgress.animateTo(
                targetValue = safeProgress,
                animationSpec = tween(durationMillis = AppAnimationManager.ANIMATION_SPEED*2)
            )
        } else {
            animatedProgress.snapTo(safeProgress)
        }
    }

    Row {
        // 按照LoadingIcon调的，几乎完全重合，这些参数别动
        val strokeWidth = remember { 2.25.dp }
        CircularProgressIndicator(
            gapSize = 0.dp,
            modifier = Modifier
                .size(24.dp-CARD_NORMAL_DP-0.5.dp)
                .padding(start = 1.5.dp)
                .padding(top = 0.5.dp)
            ,
            color = tintColor,
            trackColor = trackColor,
            strokeWidth = strokeWidth,
            progress = { animatedProgress.value }
        )
        Spacer(Modifier.width(strokeWidth+0.5.dp))
    }
}
/*
@Composable
fun LineProgressIcon(
    progress : Double,
    animate : Boolean = true,
) = LineProgressIcon(
    progress.toFloat(),
    animate
)

@Composable
fun LineProgressIcon(
    progress : Float,
    animate : Boolean = true,
) {

}
 */

@Preview
@Composable
fun A() {
    CenterScreen {
        Box {
            CardListItem(
                headlineContent = {
                    Text("hello",color = Color.Red)
                },
                leadingContent = {
                    CircleProgressIcon(
                        progress = 0.75f,
                    )
                },
                color = Color.Transparent
            )
            CardListItem(
                headlineContent = {
                    Text("hello")
                },
                leadingContent = {
                    LoadingIcon()
                },
                color = Color.Transparent
            )
        }
    }
}