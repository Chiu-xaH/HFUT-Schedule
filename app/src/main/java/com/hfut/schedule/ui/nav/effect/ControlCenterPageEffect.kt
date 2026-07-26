package com.hfut.schedule.ui.nav.effect

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.other.AppVersion
import com.sharednav.common.helper.NoneRoundShape
import com.xah.navigation.controller.NavigationController
import com.xah.navigation.model.anim.EffectLevel
import com.xah.navigation.model.anim.TransitionEffect
import com.xah.navigation.model.anim.effect.BackgroundPageEffectState
import com.xah.navigation.model.anim.effect.EffectValue
import com.xah.navigation.model.anim.effect.ForegroundPageEffectState
import com.xah.navigation.model.anim.effect.PageEffect
import com.xah.navigation.model.anim.effect.PageEffectFrame
import com.xah.navigation.model.anim.effect.PageEffects

const val CONTROL_CENTER_ALPHA = 0.125f
private const val CONTROL_CENTER_SCALE = 0.85f
private const val CONTROL_CENTER_TWEEN = 550

data class ControlCenterTransitionEffect(
    val compositeOverColor : Color? = null,
    override val pageEffect : PageEffects = ControlCenterEffects(compositeOverColor),
    override val predictiveMinValue: Float = CONTROL_CENTER_SCALE,
    override val pushAnimation: AnimationSpec<Float> = tween(CONTROL_CENTER_TWEEN),
    override val popAnimation: AnimationSpec<Float> = tween(CONTROL_CENTER_TWEEN)
) : TransitionEffect

@Composable
fun rememberControlCenterEffects(): PageEffects = ControlCenterEffects(MaterialTheme.colorScheme.background)

fun ControlCenterEffects(
    compositeOverColor : Color? = null
) : PageEffects {
    val fgScale = 0.9f
    val blur = 42.5.dp
    return object : PageEffects(
        backgroundEffect = BackgroundPageEffectState(
            enableMirror = true,
            effect = PageEffect(
                corner = EffectValue.const(NoneRoundShape),
                scale = EffectValue(
                    start = 1f,
                    end = CONTROL_CENTER_SCALE
                ),
                maskLight = EffectValue(
                    start = Color.Transparent,
                    end = Color.Black.copy(CONTROL_CENTER_ALPHA),
                ),
                maskDark = EffectValue(
                    start = Color.Transparent,
                    end = Color.White.copy(CONTROL_CENTER_ALPHA),
                ),
                blur = EffectValue(
                    start = 0.dp,
                    end = blur
                )
            )
        ),
        foregroundEffect = ForegroundPageEffectState(
            enableMirror = false,
            effect = PageEffect(
                corner = EffectValue.const(NoneRoundShape),
                scale = EffectValue(
                    start = fgScale,
                    end = 1f
                ),
                blur = EffectValue(
                    start = blur,
                    end = 0.dp
                ),
                alpha = EffectValue(
                    start = 0f,
                    end = 1f
                ),
                translationPercent = EffectValue(
                    start = Offset(0f,fgScale-1f),
                    end = Offset(0f,0f)
                )
            )
        )
    ) {
        override fun background(progress: Float, level: EffectLevel): PageEffectFrame {
            return when {
                AppVersion.CAN_MOTION_BLUR -> backgroundEffect.lerp(progress)
                else -> {
                    // 蒙层完全盖住 带缩放
                    backgroundEffect
                        .lerp(progress)
                        .copy(
                            blur = 0.dp,
                            innerBlur = 0.dp,
                            maskLight = lerp(Color.Transparent,Color.Black.copy(CONTROL_CENTER_ALPHA).compositeOver(compositeOverColor ?: Color.White),progress),
                            maskDark = lerp(Color.Transparent,Color.White.copy(CONTROL_CENTER_ALPHA).compositeOver(compositeOverColor ?: Color.Black),progress)
                        )
                }
            }
        }
        override fun foreground(progress: Float, level: EffectLevel): PageEffectFrame {
            return when {
                AppVersion.CAN_MOTION_BLUR -> foregroundEffect.lerp(progress)
                else -> {
                    // 蒙层完全盖住 带缩放
                    foregroundEffect
                        .lerp(progress)
                        .copy(blur = 0.dp, innerBlur = 0.dp)
                }
            }
        }
    }
}

