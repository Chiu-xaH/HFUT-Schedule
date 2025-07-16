package com.xah.transition.style

import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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

    private val enterAnimationFade = fadeIn(animationSpec = tween(durationMillis = DEFAULT_ANIMATION_SPEED))

    private val exitAnimationFade = fadeOut(animationSpec = tween(durationMillis = DEFAULT_ANIMATION_SPEED))

    val fadeAnimation = TransferAnimation("淡入淡出", enterAnimationFade, exitAnimationFade)
}

