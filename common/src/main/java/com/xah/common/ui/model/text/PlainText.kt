package com.xah.common.ui.model.text

import android.content.Context
import androidx.compose.runtime.Composable
import com.xah.common.ui.model.text.UiText

data class PlainText(
    val value: String
) : UiText {
    @Composable
    override fun asString(): String = value

    override fun asString(context: Context): String = value
}