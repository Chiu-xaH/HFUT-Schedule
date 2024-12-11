package com.hfut.schedule.ui.utils.monet

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import java.lang.Long

fun String.toColorOrNull(): Color? = try {
    Color(Long.parseLong(this, 16))
} catch (e: Exception) {
    null
}

val LocalThemeName = compositionLocalOf { ThemeNamePreference.default }
val LocalCustomPrimaryColor = compositionLocalOf { CustomPrimaryColorPreference.default }
val LocalCurrentStickerUuid = compositionLocalOf { CurrentStickerUuidPreference.default }
