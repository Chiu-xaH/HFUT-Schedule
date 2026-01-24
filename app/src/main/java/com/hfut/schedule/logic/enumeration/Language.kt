package com.hfut.schedule.logic.enumeration

import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager.BaseChoice
import com.hfut.schedule.ui.util.language.UiText
import com.hfut.schedule.ui.util.language.res

enum class Language(
    override val label: UiText,
    override val code: Int
) : BaseChoice {
    CHINESE_SIMPLY(res(R.string.app_settings_language_choice_simplified_chinese), 0),
    ENGLISH(res(R.string.app_settings_language_choice_english), 1),
    AUTO(res(R.string.app_settings_language_choice_auto), 2)
}