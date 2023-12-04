package com.hfut.schedule.ui.theme

import android.app.Activity
import android.app.WallpaperManager
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.hfut.schedule.ui.MonetColor.LocalCustomPrimaryColor
import com.hfut.schedule.ui.MonetColor.LocalThemeName
import com.hfut.schedule.ui.MonetColor.ThemeNamePreference
import com.hfut.schedule.ui.MonetColor.toColorOrNull
import com.kyant.monet.LocalTonalPalettes
import com.kyant.monet.PaletteStyle
import com.kyant.monet.TonalPalettes
import com.kyant.monet.TonalPalettes.Companion.toTonalPalettes
import com.kyant.monet.dynamicColorScheme

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

@Composable
fun extractTonalPalettes(): Map<String, TonalPalettes> {
    return ThemeNamePreference.values.associate { it.name to it.keyColor.toTonalPalettes() }
        .toMutableMap().also { map ->
            val customPrimaryColor =
                LocalCustomPrimaryColor.current.toColorOrNull() ?: Color.Transparent
            map[ThemeNamePreference.CUSTOM_THEME_NAME] = customPrimaryColor.toTonalPalettes()
        }
}
@Composable
fun extractAllTonalPalettes(): Map<String, TonalPalettes> {
    return extractTonalPalettes() + extractTonalPalettesFromWallpaper()
}
@Composable
fun MonetColor(
    darkTheme: Int,
    content: @Composable () -> Unit
) {
    MonetColor(
      //  darkTheme = DarkModePreference.isInDark(darkTheme),
        content = content
    )
}

@Composable
fun MonetColor(
    wallpaperPalettes: Map<String, TonalPalettes> = extractAllTonalPalettes(),
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            //setSystemBarsColor(view, darkTheme)
        }
    }

    val tonalPalettes = wallpaperPalettes.getOrElse(LocalThemeName.current) {
        ThemeNamePreference.values[0].keyColor.toTonalPalettes(style = PaletteStyle.Content)
    }

    CompositionLocalProvider(
        LocalTonalPalettes provides tonalPalettes,
    ) {
        MaterialTheme(
            colorScheme = dynamicColorScheme(),
            typography = Typography,
            content = content
        )
    }
}
