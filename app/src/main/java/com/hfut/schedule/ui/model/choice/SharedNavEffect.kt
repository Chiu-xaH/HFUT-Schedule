package com.hfut.schedule.ui.model.choice

import com.xah.common.ui.model.BaseChoice
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.util.text
import com.xah.navigation.anim.effect.PushTransitionEffect
import com.xah.navigation.anim.effect.TinyScaleTransitionEffect
import com.xah.navigation.model.anim.TransitionEffect

enum class SharedNavEffect(
    override val label: UiText,
    override val code: Int,
    val effect: TransitionEffect?
) : BaseChoice {
    DEFAULT(text("默认"),0, null),
    TINY_SCALE(text("缩放"),1, TinyScaleTransitionEffect(false, true)),
    PUSH_OFF(text("推入"),2, PushTransitionEffect()),
}