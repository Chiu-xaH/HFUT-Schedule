package com.xah.container.utils

import androidx.compose.runtime.staticCompositionLocalOf
import com.xah.container.controller.SharedContainerRegistry


val LocalSharedContainerRegistry = staticCompositionLocalOf<SharedContainerRegistry> {
    error("未提供SharedContainerRegistry,请确认是否使用了本Library的SharedContainerRoot")
}