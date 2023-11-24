package com.hfut.schedule.ViewModel

import android.app.Application
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.hfut.schedule.ui.PrefHelper
import com.hfut.schedule.ui.PrefHelper.get
import com.hfut.schedule.ui.PrefHelper.operation
import com.hfut.schedule.ui.PrefHelper.put
import com.hfut.schedule.ui.theme.Pink40
import com.hfut.schedule.ui.theme.Pink80
import com.hfut.schedule.ui.theme.Purple40
import com.hfut.schedule.ui.theme.Purple80
import com.hfut.schedule.ui.theme.PurpleGrey40
import com.hfut.schedule.ui.theme.PurpleGrey80
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
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

)

val DEFAULT_THEME = CatalogTheme.BLUE_THEME.name

enum class CatalogTheme(val paletteName: String, val lightColorScheme: ColorScheme, val darkColorScheme: ColorScheme) {

    BLUE_THEME("蓝色", BlueLightColors, BlueDarkColors),
    RED_THEME("红色", RedLightColors, RedDarkColors),
     TEAL_THEME("绿色", TealLightColors, TealDarkColors),
    DEEP_ORANGE_THEME("橙色", DeepOrangeLightColors, DeepOrangeDarkColors),
    GREEN_THEME("绿色", GreenLightColors, GreenDarkColors)
}


@HiltViewModel
class MainViewModel  @Inject constructor(private val app: Application) : ViewModel() {

    val currentTheme = mutableStateOf(
        PrefHelper.prefs(app.applicationContext)
            .get(PrefHelper.KEY_CURRENT_THEME, DEFAULT_THEME) as String
    )

    fun setCurrentTheme(theme: String) {
        currentTheme.value = theme
        PrefHelper.prefs(app.applicationContext).operation {
            it.put(Pair(PrefHelper.KEY_CURRENT_THEME, theme))
        }
    }
}