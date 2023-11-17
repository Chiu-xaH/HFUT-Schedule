package com.hfut.schedule.ui.ComposeUI

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.dynamicColorScheme


@Composable
fun MyTheme(
    seedColor: Color,
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = dynamicColorScheme(seedColor, useDarkTheme)

    MaterialTheme(
    //    colorScheme = ,
        content = content
    )
}