package com.xah.uicommon.util.language

import androidx.annotation.StringRes
import com.xah.uicommon.util.language.PlainText

fun res(@StringRes id: Int, vararg args: Any) = StringResText(id, args.toList())

fun text(value: String) = PlainText(value)
