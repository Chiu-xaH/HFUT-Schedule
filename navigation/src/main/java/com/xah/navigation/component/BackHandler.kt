package com.xah.navigation.component

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import com.xah.navigation.utils.LocalNavController

@Composable
fun DefaultBackHandler() {
    // TODO 预测式返回
    val navController = LocalNavController.current
    BackHandler(enabled = navController.stack.size > 1) {
        navController.pop()
    }
}