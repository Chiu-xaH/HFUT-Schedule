package com.xah.common.ui.style.padding

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun getNavigationBarHeight() : Dp {
    val density = LocalDensity.current
    return with(density) {
        WindowInsets.navigationBars.getBottom(this).toDp()
    }
}

@Composable
fun getStatusBarHeight() : Dp {
    val density = LocalDensity.current
    return with(density) {
        WindowInsets.statusBars.getTop(this).toDp()
    }
}