package com.hfut.schedule.ui.util.navigation

import androidx.navigation.NavController

// 导航后 上一级永远是firstRoute
fun NavController.navigateForBottomBar(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

// 导航后 当前界面就是唯一界面 上一级被清除
fun NavController.navigateAndClear(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) { inclusive = true } // 清除所有历史记录
        launchSingleTop = true // 避免多次实例化相同的目的地
    }
}

