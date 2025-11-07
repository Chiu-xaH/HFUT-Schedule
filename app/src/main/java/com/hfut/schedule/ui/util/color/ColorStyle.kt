package com.hfut.schedule.ui.util.color

import com.materialkolor.PaletteStyle

enum class ColorStyle(val code : Int,val description: String,val style : PaletteStyle) {
    DEFAULT(2,"正常", PaletteStyle.TonalSpot),
    LIGHT(1,"淡雅", PaletteStyle.Neutral),
    DEEP(3,"艳丽", PaletteStyle.Vibrant),
    BLACK(0,"黑白", PaletteStyle.Monochrome),
}