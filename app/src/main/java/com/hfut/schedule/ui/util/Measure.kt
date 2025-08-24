package com.hfut.schedule.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun Modifier.measureDpSize(
    onSize: (Dp, Dp) -> Unit
): Modifier {
    val density = LocalDensity.current
    val currentCallback = rememberUpdatedState(onSize)

    return this.then(
        Modifier.onSizeChanged { size ->
            with(density) {
                currentCallback.value(size.width.toDp(), size.height.toDp())
            }
        }
    )
}
