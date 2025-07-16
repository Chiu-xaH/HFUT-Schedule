package com.hfut.schedule.ui.util

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

//fun NavController.navigateAndClear(route: String) {
//    navigate(route) {
//        popUpTo(graph.startDestinationId) { inclusive = true } // 清除所有历史记录
//        launchSingleTop = true // 避免多次实例化相同的目的地
//    }
//}

fun NavController.navigateAndSave(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}


//@Composable
//fun NavController.currentRoute() : String? = this.currentBackStackEntryAsState().value?.destination?.route
//@Composable
//fun NavController.isCurrentRoute(route: String) : Boolean = currentRoute() != route