package com.xah.uicommon.util.language

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

data class StringResText(
    @StringRes val resId: Int,
    val args: List<Any> = emptyList()
) : UiText {
    @Composable
    override fun asString(): String = stringResource(resId, *args.toTypedArray())

    override fun asString(context: Context): String = context.getString(resId, *args.toTypedArray())

}