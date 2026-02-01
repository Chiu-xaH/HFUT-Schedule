package com.hfut.schedule.ui.util.color

import com.hfut.schedule.R
import com.xah.uicommon.util.language.BaseChoice
import com.xah.uicommon.util.language.UiText
import com.xah.uicommon.util.language.res

enum class ColorMode(
    override val label: UiText,
    override val code: Int
) : BaseChoice {
    LIGHT(res(R.string.appearance_settings_choice_theme_light),1),
    DARK(res(R.string.appearance_settings_choice_theme_dark),2),
    AUTO(res(R.string.appearance_settings_choice_theme_auto),0)
}