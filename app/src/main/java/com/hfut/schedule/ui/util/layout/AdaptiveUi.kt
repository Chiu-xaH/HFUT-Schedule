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
            UiWidthSize.EXPANDED
        }
        sizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
            UiWidthSize.MEDIUM
        }
        else -> {
            UiWidthSize.COMPACT
        }
    }
}

@Composable
fun currentUiWidthSizeColumns() : Int = when (currentUiWidthSize()) {
    UiWidthSize.COMPACT -> 1
    UiWidthSize.MEDIUM -> 2
    UiWidthSize.EXPANDED -> 2
}