package com.hfut.schedule.ui.model.choice

import com.xah.common.ui.model.BaseChoice
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.util.text
import com.xah.container.model.ContainerFilledStrategy

enum class SharedContainerFilledStrategy(
    override val label: UiText,
    override val code: Int,
    val strategy: ContainerFilledStrategy?
) : BaseChoice {
    DEFAULT(text("填充"),0, null),
    CLIP(text("裁切"),1, ContainerFilledStrategy.Clip),
}