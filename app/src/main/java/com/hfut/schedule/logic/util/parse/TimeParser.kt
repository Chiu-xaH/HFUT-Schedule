package com.hfut.schedule.logic.util.parse

import java.util.Locale

fun parseJxglstuIntTime(time: Int): String {
    val hour = time / 100
    val minute = time % 100
    return "%02d:%02d".format(Locale.ROOT, hour, minute)
}
