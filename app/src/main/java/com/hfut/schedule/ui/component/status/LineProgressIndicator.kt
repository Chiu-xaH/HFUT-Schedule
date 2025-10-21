package com.hfut.schedule.ui.component.status

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.util.navigation.AppAnimationManager

@Composable
fun CustomLineProgressIndicator(
    value : Float,
    text : String? = "${formatDecimal((value * 100).toDouble(),1)}%",
    color : Color = ProgressIndicatorDefaults.linearColor,
    trackColor : Color = ProgressIndicatorDefaults. linearTrackColor,
    height : Dp = 20.dp,
    modifier: Modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP)
) {
    val animatedProgress by animateFloatAsState(
        targetValue = value,
        animationSpec = tween(durationMillis = AppAnimationManager.ANIMATION_SPEED*2)
    )
    val textColor = if (animatedProgress > 0.56f) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    Box(
        modifier = modifier
            .height(height)
            .clip(MaterialTheme.shapes.extraSmall)
    ) {
        LinearProgressIndicator(
            modifier = Modifier.matchParentSize(),
            color = color,
            trackColor = trackColor,
            strokeCap = StrokeCap.Butt,
            progress = { animatedProgress },
            drawStopIndicator = {},
            gapSize = 0.dp
        )
        // 文字覆盖在进度条中间
        text?.let {
            Text(
                text = it,
                color = textColor,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .align(
                        if(value in 0.44..0.56) {
                            Alignment.CenterEnd
                        } else {
                            Alignment.Center
                        }
                    ).padding(horizontal = APP_HORIZONTAL_DP)
            )
        }
    }
}