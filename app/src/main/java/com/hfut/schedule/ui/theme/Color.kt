package com.hfut.schedule.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xCBFFFFFF)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

@Composable
fun greenColor() = if(isSystemInDarkTheme()) {
    Color(0xFF66BB6A)
} else {
    Color(0xFF2E7D32)
}

@Composable
fun warnColor() = if(isSystemInDarkTheme()) {
    Color(0xFFE0A350)
} else {
    Color(0xffba7f25)
}

@Composable
fun pureMaskColor() = if(!isSystemInDarkTheme()) {
    Color.Black
} else {
    Color.White
}

// 实际使用时用MaterialTheme.colorScheme.error
@Composable
fun errorColor() = MaterialTheme.colorScheme.error