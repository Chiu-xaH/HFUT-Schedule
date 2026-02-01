package com.xah.uicommon.util.language

import android.content.Context
import androidx.compose.runtime.Composable

sealed interface UiText {
    @Composable
    fun asString(): String

    fun asString(context: Context): String
}