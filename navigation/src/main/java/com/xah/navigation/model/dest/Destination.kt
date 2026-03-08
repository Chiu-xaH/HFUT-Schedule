package com.xah.navigation.model.dest

import androidx.compose.runtime.Composable

abstract class Destination {
    /**
     * 也作为共享容器Key
     */
    abstract val key: String
    // ...可扩展

    @Composable
    abstract fun Screen()
}