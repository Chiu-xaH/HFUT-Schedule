package com.hfut.schedule.ui.util.language

import android.content.Context
import androidx.compose.runtime.Composable
import com.hfut.schedule.application.MyApplication

sealed interface UiText {
    @Composable
    fun asString(): String

    fun asString(context: Context = MyApplication.context): String
}