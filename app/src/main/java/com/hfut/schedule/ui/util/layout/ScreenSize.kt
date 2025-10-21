package com.hfut.schedule.ui.util.layout

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity

@Composable
fun rememberDesiredWallpaperSize(
    baseWidthDp: Int,
    baseHeightDp: Int,
    over: Float = 1f // 清晰度余量
): Pair<Int, Int> {
    val density = LocalDensity.current.density
    val targetW = (baseWidthDp * density * over).toInt()
    val targetH = (baseHeightDp * density * over).toInt()
    return targetW to targetH
}