package com.xah.navigation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.xah.navigation.controller.NavigationController
import com.xah.navigation.model.Destination
import com.xah.navigation.model.Dependencies

// 根导航
val LocalNavController = staticCompositionLocalOf<NavigationController> {
    error("未提供根NavController")
}

val LocalNavDestination = staticCompositionLocalOf<Destination> {
    error("未提供根Destination")
}

val LocalNavDependencies = compositionLocalOf {
    Dependencies()
}

@Composable
fun rememberNavDependencies(
    vararg keys: Any?,
    builder: Dependencies.() -> Unit
): Dependencies {
    return remember(keys) {
        Dependencies().apply(builder)
    }
}