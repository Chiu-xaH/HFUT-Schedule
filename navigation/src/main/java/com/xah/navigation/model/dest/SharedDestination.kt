package com.xah.navigation.model.dest

import androidx.compose.runtime.Composable
import com.xah.container.container.SharedContent
import com.xah.navigation.model.dest.Destination

abstract class SharedDestination : Destination() {

    @Composable
    abstract fun Content()

    @Composable
    override fun Screen() {
        SharedContent(key) {
            Content()
        }
    }
}