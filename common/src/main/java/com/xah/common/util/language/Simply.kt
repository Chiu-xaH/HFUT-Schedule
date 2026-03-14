package com.xah.common.util.language

import androidx.annotation.StringRes
import com.xah.common.util.language.PlainText

fun res(@StringRes id: Int, vararg args: Any) = StringResText(id, args.toList())

fun text(value: String) = PlainText(value)
