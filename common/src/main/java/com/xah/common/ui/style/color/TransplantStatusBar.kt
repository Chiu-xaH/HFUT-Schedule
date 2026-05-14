package com.xah.common.ui.style.color

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.google.accompanist.systemuicontroller.rememberSystemUiController

// isDark = true表示状态栏颜色为黑色，此时背景应该为浅色  即浅色主题时isDark应该为true
// isDark = false表示状态栏颜色为白色，此时背景应该为深色 即深色主题时isDark应该为false
@Composable
fun TransparentSystemBars(isBackgroundLight : Boolean) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = !isBackgroundLight,
            isNavigationBarContrastEnforced = false
        )
    }
}

// 子页面手动控制状态栏反色
@Composable
fun TransparentSystemBars2(backgroundColor: Color? = null) {
    if(backgroundColor == null) {
        return
    }
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = backgroundColor.luminance() > 0.5f,
            isNavigationBarContrastEnforced = false
        )
    }
}

@Composable
fun TransparentSystemBars(backgroundColor: Color? = null) {
    val systemUiController = rememberSystemUiController()
    val inDark = isSystemInDarkTheme()
    DisposableEffect(backgroundColor) {
        if(backgroundColor != null) {
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = backgroundColor.luminance() > 0.5f,
                isNavigationBarContrastEnforced = false
            )
        }
        onDispose {
            // 复原
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = !inDark,
                isNavigationBarContrastEnforced = false
            )
        }
    }
}