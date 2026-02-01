package com.hfut.schedule.ui.util.color

import com.hfut.schedule.R
import com.xah.uicommon.util.language.UiText
import com.xah.uicommon.util.language.res
import com.materialkolor.PaletteStyle

enum class ColorStyle(val code : Int,val description: UiText,val style : PaletteStyle) {
    DEFAULT(2, res(R.string.appearance_settings_choice_bright_normal), PaletteStyle.TonalSpot),
    LIGHT(1,res(R.string.appearance_settings_choice_bright_tinge), PaletteStyle.Neutral),
    DEEP(3,res(R.string.appearance_settings_choice_bright_heavy), PaletteStyle.Vibrant),
    BLACK(0,res(R.string.appearance_settings_choice_bright_monochrome), PaletteStyle.Monochrome),
}