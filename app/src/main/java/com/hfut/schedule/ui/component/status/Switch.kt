package com.hfut.schedule.ui.component.status

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.hfut.schedule.ui.util.navigation.AppAnimationManager

private const val MAX_ANGLE = 90


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CustomSwitch(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    enabled: Boolean = true,
) {
    val color by animateColorAsState(
        if(!checked) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.surface
    )
    val angle by animateIntAsState(
        if(checked) MAX_ANGLE else 0,tween(AppAnimationManager.ANIMATION_SPEED, easing = LinearOutSlowInEasing)
    )
    val animateEnded = angle == 0 ||angle == MAX_ANGLE

    Switch(
        modifier = modifier,
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        thumbContent = {
            AnimatedVisibility(
                visible = animateEnded,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Box(modifier = Modifier.fillMaxSize(1f).background(color, MaterialShapes.Circle.toShape()))
            }
            AnimatedVisibility(
                visible = !animateEnded,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Box(modifier = Modifier.fillMaxSize(1f).background(color, MaterialShapes.VerySunny.toShape(angle)))
            }
        },
        colors = SwitchDefaults.colors(uncheckedThumbColor = Color.Transparent, checkedThumbColor = Color.Transparent)
    )
}