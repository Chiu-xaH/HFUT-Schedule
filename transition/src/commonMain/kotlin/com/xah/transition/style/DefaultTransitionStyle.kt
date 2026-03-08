package com.xah.transition.style

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut

object DefaultTransitionStyle {
    const val DEFAULT_ANIMATION_SPEED = 400
    data class TransferAnimation(val remark : String,val enter : EnterTransition, val exit : ExitTransition)


    private val enterAnimation5 = scaleIn(animationSpec =  tween(durationMillis = DEFAULT_ANIMATION_SPEED, easing = LinearOutSlowInEasing))

    private val exitAnimation5 = scaleOut(animationSpec =  tween(durationMillis = DEFAULT_ANIMATION_SPEED,easing = LinearOutSlowInEasing))

    val centerAllAnimation = TransferAnimation("向中心完全运动",enterAnimation5, exitAnimation5)


}

