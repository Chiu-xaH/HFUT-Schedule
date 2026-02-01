package com.hfut.schedule.logic.enumeration

import com.hfut.schedule.R
import com.xah.uicommon.util.language.BaseChoice
import com.xah.uicommon.util.language.UiText
import com.xah.uicommon.util.language.res

enum class Language(
    override val label: UiText,
    override val code: Int
) : BaseChoice {
    CHINESE_SIMPLY(res(R.string.app_settings_language_choice_simplified_chinese), 0),
    ENGLISH(res(R.string.app_settings_language_choice_english), 1),
    AUTO(res(R.string.app_settings_language_choice_auto), 2)
}