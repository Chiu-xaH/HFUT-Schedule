package com.hfut.schedule.ui.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.ui.style.TransparentSystemBars
import com.materialkolor.rememberDynamicColorScheme


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    // 自主取色
    val customColor by DataStoreManager.customColor.collectAsState(initial = -1L)
    val enableDynamicColor = customColor == -1L
    // 跟随系统深色模式
    val currentColorModeIndex by DataStoreManager.colorMode.collectAsState(initial = DataStoreManager.ColorMode.AUTO.code)
    val isInDark = when(currentColorModeIndex) {
        DataStoreManager.ColorMode.DARK.code -> true
        DataStoreManager.ColorMode.LIGHT.code -> false
        else -> isSystemInDarkTheme()
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
        val scheme = rememberDynamicColorScheme(seedColor = Color(customColor), isDark = isInDark)
        if(usePureBlack && isInDark) {
            scheme.copy(background = Color.Black, surface = Color.Black)
        } else {
            scheme
        }
    }

    MaterialExpressiveTheme (
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
    TransparentSystemBars(isInDark)
}

