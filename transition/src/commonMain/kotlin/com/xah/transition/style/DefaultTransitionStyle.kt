package com.xah.transition.style

import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.ui.geometry.Rect

object DefaultTransitionStyle {
    const val DEFAULT_ANIMATION_SPEED = 400
    data class TransferAnimation(val remark : String,val enter : EnterTransition, val exit : ExitTransition)
    @OptIn(ExperimentalSharedTransitionApi::class)
    val defaultBoundsTransform = BoundsTransform { _, _ ->//StiffnessMediumLow
        defaultSpring
    }
    val defaultSpring = spring(
        dampingRatio = Spring.DampingRatioLowBouncy*1.1f,
        stiffness = Spring.StiffnessLow,
        visibilityThreshold = Rect.VisibilityThreshold
    )

    private const val SPRING = 0.875f
    private val enterAnimation2 = scaleIn(animationSpec =  tween(durationMillis = DEFAULT_ANIMATION_SPEED, easing = LinearOutSlowInEasing), initialScale = SPRING) + fadeIn(animationSpec = tween(durationMillis = DEFAULT_ANIMATION_SPEED/2))

    private val exitAnimation2 = scaleOut(animationSpec =  tween(durationMillis = DEFAULT_ANIMATION_SPEED,easing = LinearOutSlowInEasing), targetScale = SPRING) + fadeOut(animationSpec = tween(durationMillis = DEFAULT_ANIMATION_SPEED/2))

    val centerAnimation = TransferAnimation("向中心运动",enterAnimation2, exitAnimation2)

    private val enterAnimation5 = scaleIn(animationSpec =  tween(durationMillis = DEFAULT_ANIMATION_SPEED, easing = LinearOutSlowInEasing))

    private val exitAnimation5 = scaleOut(animationSpec =  tween(durationMillis = DEFAULT_ANIMATION_SPEED,easing = LinearOutSlowInEasing))

    val centerAllAnimation = TransferAnimation("向中心完全运动",enterAnimation5, exitAnimation5)

    private val enterAnimationFade = fadeIn(animationSpec = tween(durationMillis = DEFAULT_ANIMATION_SPEED))

    private val exitAnimationFade = fadeOut(animationSpec = tween(durationMillis = DEFAULT_ANIMATION_SPEED))

    val fadeAnimation = TransferAnimation("淡入淡出", enterAnimationFade, exitAnimationFade)

    private val enterAnimationNull = fadeIn(animationSpec = tween(durationMillis = 0))

    private val exitAnimationNull = fadeOut(animationSpec = tween(durationMillis = 0))

    val nullAnimation = TransferAnimation("无", enterAnimationNull, exitAnimationNull)
}

