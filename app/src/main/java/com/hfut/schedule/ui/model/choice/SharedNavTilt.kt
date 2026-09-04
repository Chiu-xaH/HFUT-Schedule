package com.hfut.schedule.ui.model.choice

import com.xah.common.ui.model.BaseChoice
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.util.text
import com.xah.container.model.TiltEffect
import com.xah.navigation.anim.effect.PushTransitionEffect
import com.xah.navigation.anim.effect.ScaleTransitionEffect
import com.xah.navigation.model.anim.TransitionEffect

enum class SharedNavTilt(
    override val label: UiText,
    override val code: Int,
    val effect: TiltEffect?
) : BaseChoice {
    ROTATION(text("倾斜"),0, TiltEffect.ROTATION),
    SHADER(text("扭曲"),1, TiltEffect.SHADER_2),
    NONE(text("无"),2, TiltEffect.NONE),
}