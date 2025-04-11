package com.hfut.schedule.ui.theme

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.ui.style.TransparentSystemBars

private val DarkColorScheme = darkColorScheme()

private val LightColorScheme = lightColorScheme()

private val OLEDColorScheme = darkColorScheme(
    background = Color.Black, // 纯黑背景
    surface = Color.Black, // 纯黑表面
)



@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AppTheme(
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // 跟随系统深色模式
    val currentColorModeIndex by DataStoreManager.colorModeFlow.collectAsState(initial = DataStoreManager.ColorMode.AUTO.code)
    val darkTheme = when(currentColorModeIndex) {
        DataStoreManager.ColorMode.DARK.code -> true
        DataStoreManager.ColorMode.LIGHT.code -> false
        else -> isSystemInDarkTheme()
    }
    // 是否使用 OLED 纯黑
    val usePureBlack by DataStoreManager.pureDarkFlow.collectAsState(initial = false)

    val colorScheme = when {
        dynamicColor && AppVersion.canMonet -> {
            val context = LocalContext.current
            val defaultDynamicScheme = if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }

            if (darkTheme && usePureBlack) {
                // 复制动态取色方案，并将背景设为纯黑
                defaultDynamicScheme.copy(
                    background = Color.Black,
                    surface = Color.Black,
                )
            } else {
                defaultDynamicScheme
            }
        }

        darkTheme -> if (usePureBlack) OLEDColorScheme else DarkColorScheme
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
    TransparentSystemBars(!darkTheme)
}

