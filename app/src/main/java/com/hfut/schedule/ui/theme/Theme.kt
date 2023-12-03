package com.hfut.schedule.ui.theme

import android.app.Activity
import android.app.WallpaperManager
import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.hfut.schedule.MyApplication.Companion.DEFAULT_THEME
import com.hfut.schedule.ui.DynamicColor.PrefHelper
import com.hfut.schedule.ui.DynamicColor.CatalogTheme
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
import com.kyant.monet.TonalPalettes
import com.kyant.monet.TonalPalettes.Companion.toTonalPalettes

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

)

@Composable
fun 肥工课程表Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}




@Composable
fun DynamicColr(
    context: Context,
    currentTheme: String? = PrefHelper.prefs(context).getString(PrefHelper.KEY_CURRENT_THEME, DEFAULT_THEME),
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {


    val colorScheme = if(dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }else{
        when(currentTheme){
            CatalogTheme.BLUE_THEME.name->{ if(darkTheme) BlueDarkColors else BlueLightColors }
            CatalogTheme.RED_THEME.name->{ if(darkTheme) RedDarkColors else RedLightColors }
            CatalogTheme.TEAL_THEME.name->{ if(darkTheme) TealDarkColors else TealLightColors }
            CatalogTheme.DEEP_ORANGE_THEME.name->{ if(darkTheme) DeepOrangeDarkColors else DeepOrangeLightColors}
            CatalogTheme.GREEN_THEME.name-> { if(darkTheme) GreenDarkColors else GreenLightColors}
            else->{ if(darkTheme) DarkColorScheme else LightColorScheme }
        }
    }
    /*    val view = LocalView.current
        if (!view.isInEditMode) {
            SideEffect {
                (view.context as Activity).window.statusBarColor = colorScheme.primary.toArgb()
                ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = darkTheme
            }
        }*/

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}


@Composable
fun extractTonalPalettesFromWallpaper(): Map<String, TonalPalettes> {
    val context = LocalContext.current
    val preset = mutableMapOf<String, TonalPalettes>()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 && !LocalView.current.isInEditMode) {
        val colors = WallpaperManager.getInstance(context)
            .getWallpaperColors(WallpaperManager.FLAG_SYSTEM)
        val primary = colors?.primaryColor?.toArgb()
        val secondary = colors?.secondaryColor?.toArgb()
        val tertiary = colors?.tertiaryColor?.toArgb()
        if (primary != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                preset["WallpaperPrimary"] = context.getSystemTonalPalettes()
            } else {
                preset["WallpaperPrimary"] = (Color(primary).toTonalPalettes())
            }
        }
        if (secondary != null) preset["WallpaperSecondary"] = Color(secondary).toTonalPalettes()
        if (tertiary != null) preset["WallpaperTertiary"] = Color(tertiary).toTonalPalettes()
    }
    return preset
}