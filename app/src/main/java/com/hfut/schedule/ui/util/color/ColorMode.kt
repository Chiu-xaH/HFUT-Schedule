package com.hfut.schedule.ui.util.color

import com.hfut.schedule.logic.util.storage.kv.DataStoreManager.BaseChoice
import com.hfut.schedule.ui.util.language.UiText
import com.hfut.schedule.ui.util.language.text

enum class ColorMode(
    override val label: UiText,
    override val code: Int
) : BaseChoice {
    LIGHT(text("浅色"),1),
    DARK(text("深色"),2),
    AUTO(text("跟随系统"),0)
}