package com.hfut.schedule.ui.util.language

import androidx.annotation.StringRes

fun res(@StringRes id: Int, vararg args: Any) = StringResText(id, args.toList())

fun text(value: String) = PlainText(value)
