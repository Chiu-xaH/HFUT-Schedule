package com.hfut.schedule.ui.theme

import android.app.Activity
import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager.ColorMode
import com.xah.uicommon.style.color.TransparentSystemBars
import com.hfut.schedule.ui.util.deepen
import com.materialkolor.rememberDynamicColorScheme

private val list = DataStoreManager.ColorStyle.entries
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    // 自主取色
    val customColor by DataStoreManager.customColor.collectAsState(initial = -1L)
    val enableDynamicColor = customColor == -1L
    // 跟随系统深色模式
    val currentColorModeIndex by DataStoreManager.colorMode.collectAsState(initial = null)
    // Compose深色
    val isInDark = when(currentColorModeIndex) {
        ColorMode.DARK.code -> {
            true
        }
        ColorMode.LIGHT.code -> {
            false
        }
        else -> {
            isSystemInDarkTheme()
        }
    }
    // View体系跟随App深浅色
    LaunchedEffect(currentColorModeIndex) {
        when(currentColorModeIndex) {
            ColorMode.DARK.code -> {
                setNightMode(context,ColorMode.DARK)
            }
            ColorMode.LIGHT.code -> {
                setNightMode(context,ColorMode.LIGHT)
            }
            ColorMode.AUTO.code  -> {
                setNightMode(context, ColorMode.AUTO)
            }
        }
    }
    // OLED 纯黑
    val usePureBlack by DataStoreManager.enablePureDark.collectAsState(initial = false)

    val colorScheme = if(enableDynamicColor) {
        val context = LocalContext.current
        if(AppVersion.CAN_DYNAMIC_COLOR) {
            if(isInDark) {
                dynamicDarkColorScheme(context).let {
                    if(usePureBlack) {
                        it.copy(background = Color.Black, surface = Color.Black,)
                    } else {
                        it
                    }
                }
            } else {
                dynamicLightColorScheme(context)
            }
        } else {
            if(isInDark) {
                darkColorScheme().let {
                    if(usePureBlack) {
                        it.copy(background = Color.Black, surface = Color.Black,)
                    } else {
                        it
                    }
                }
            } else {
                lightColorScheme()
            }
        }
    } else {
        val style by DataStoreManager.customColorStyle.collectAsState(initial = DataStoreManager.ColorStyle.DEFAULT.code)
        val scheme = rememberDynamicColorScheme(
            seedColor = Color(customColor),
            isDark = isInDark,
            isAmoled = usePureBlack,
            style = list.find { it.code == style }?.style ?: DataStoreManager.ColorStyle.DEFAULT.style
        )
        scheme.let {
            if(style == DataStoreManager.ColorStyle.BLACK.code) {
                it.copy(
                    primaryContainer = it.secondaryContainer.deepen(if(isInDark) 0.3f else 0.2f,isInDark),
                    primary = it.primary.deepen(-0.2f,isInDark),
                    surfaceVariant = it.surface.deepen(0.1f,isInDark)
                )
            } else {
                it
            }
        }
    }

    MaterialExpressiveTheme (
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
    TransparentSystemBars(isInDark)
}


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
fun DefaultAppTheme(
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
    TransparentSystemBars(darkTheme)
}

@RequiresApi(Build.VERSION_CODES.S)
fun setNightMode(
    context: Context,
    mode: ColorMode
) {
    if (AppVersion.sdkInt >= Build.VERSION_CODES.S) {
        // Android 12+
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        uiModeManager.setApplicationNightMode(
            when(mode) {
                ColorMode.DARK -> UiModeManager.MODE_NIGHT_YES
                ColorMode.LIGHT -> UiModeManager.MODE_NIGHT_NO
                else -> UiModeManager.MODE_NIGHT_AUTO
            }
        )
    } else {
        // 低版本用
        AppCompatDelegate.setDefaultNightMode(
            when(mode) {
                ColorMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                ColorMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }
}
