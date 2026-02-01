package com.xah.uicommon.util.language

import android.content.Context
import androidx.compose.runtime.Composable

data class PlainText(
    val value: String
) : UiText {
    @Composable
    override fun asString(): String = value

    override fun asString(context: Context): String = value
}