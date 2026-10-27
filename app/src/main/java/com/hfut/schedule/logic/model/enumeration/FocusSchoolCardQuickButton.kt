package com.hfut.schedule.logic.model.enumeration

import com.xah.common.ui.model.BaseChoice
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.util.text

enum class FocusSchoolCardQuickButton(
    override val label: UiText,
    override val code: Int
) : BaseChoice {
    NONE(text("无"), 0),
    ADD(text("充值"), 1),
    PAY(text("付款码"), 2)
}