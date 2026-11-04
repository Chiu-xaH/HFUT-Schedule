package com.hfut.schedule.ui.util.layout

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.hfut.schedule.ui.model.UiWidthSize

@Composable
fun currentUiWidthSize() : UiWidthSize {
    val sizeClass = currentWindowAdaptiveInfo().windowSizeClass
    return when {
        sizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
            UiWidthSize.LARGE
        }
        sizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
            UiWidthSize.MEDIUM
        }
        else -> {
            UiWidthSize.SMALL
        }
    }
}

@Composable
fun currentUiWidthSizeColumnsForCard() : Int = when (currentUiWidthSize()) {
    UiWidthSize.SMALL -> 1
    UiWidthSize.MEDIUM -> 1
    UiWidthSize.LARGE -> 2
}

@Composable
fun currentUiWidthSizeColumnsForSmallCard() : Int = when (currentUiWidthSize()) {
    UiWidthSize.SMALL -> 2
    UiWidthSize.MEDIUM -> 3
    UiWidthSize.LARGE -> 4
}