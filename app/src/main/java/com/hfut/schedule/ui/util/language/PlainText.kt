package com.hfut.schedule.ui.util.language

import android.content.Context
import androidx.compose.runtime.Composable
import com.hfut.schedule.application.MyApplication

data class PlainText(
    val value: String
) : UiText {
    @Composable
    override fun asString(): String = value

    override fun asString(context: Context): String = value
}