package com.hfut.schedule.logic.model.enumeration

import com.xah.common.ui.model.BaseChoice
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.util.text


enum class LocalEvent(override val code : Int,override val label: UiText): BaseChoice {
    SCHEDULE(0, text("日程")),
    DEADLINE(1, text("DeadLine")),
}