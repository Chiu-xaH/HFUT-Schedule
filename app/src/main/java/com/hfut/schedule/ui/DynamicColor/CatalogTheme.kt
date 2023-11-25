package com.hfut.schedule.ui.DynamicColor

import androidx.compose.material3.ColorScheme
import com.hfut.schedule.ui.theme.blue.BlueDarkColors
import com.hfut.schedule.ui.theme.blue.BlueLightColors
import com.hfut.schedule.ui.theme.deeporange.DeepOrangeDarkColors
import com.hfut.schedule.ui.theme.deeporange.DeepOrangeLightColors
import com.hfut.schedule.ui.theme.green.GreenDarkColors
import com.hfut.schedule.ui.theme.green.GreenLightColors
import com.hfut.schedule.ui.theme.red.RedDarkColors
import com.hfut.schedule.ui.theme.red.RedLightColors
import com.hfut.schedule.ui.theme.teal.TealDarkColors
import com.hfut.schedule.ui.theme.teal.TealLightColors

enum class CatalogTheme(val paletteName: String, val lightColorScheme: ColorScheme, val darkColorScheme: ColorScheme) {

    BLUE_THEME("蓝色", BlueLightColors, BlueDarkColors),
    RED_THEME("红色", RedLightColors, RedDarkColors),
    TEAL_THEME("绿色", TealLightColors, TealDarkColors),
    DEEP_ORANGE_THEME("橙色", DeepOrangeLightColors, DeepOrangeDarkColors),
    GREEN_THEME("绿色", GreenLightColors, GreenDarkColors)
}
