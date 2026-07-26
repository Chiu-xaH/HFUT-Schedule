package com.hfut.schedule.ui.model.choice

import com.xah.common.ui.model.BaseChoice
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.util.text

enum class GradeAutoCheckMode(
    override val label: UiText,
    override val code: Int
) : BaseChoice {
    ONLY_VACATION(text("仅寒暑假"),1),
    ALWAYS(text("打开"),2),
    NEVER(text("关闭"),0)
}