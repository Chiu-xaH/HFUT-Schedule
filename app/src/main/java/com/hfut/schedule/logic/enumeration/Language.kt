package com.hfut.schedule.logic.enumeration

import com.hfut.schedule.R
import com.xah.common.ui.model.BaseChoice
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.util.res

enum class Language(
    override val label: UiText,
    override val code: Int
) : BaseChoice {
    CHINESE_SIMPLY(res(R.string.app_settings_language_choice_simplified_chinese), 0),
    ENGLISH(res(R.string.app_settings_language_choice_english), 1),
    AUTO(res(R.string.app_settings_language_choice_auto), 2)
}