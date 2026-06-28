package com.hfut.schedule.ui.util.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

// 导航后 上一级永远是firstRoute
@Deprecated("Navigation2将被抛弃，请使用SharedNav")
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
@Deprecated("Navigation2将被抛弃，请使用SharedNav")
fun NavController.navigateAndClear(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) { inclusive = true } // 清除所有历史记录
        launchSingleTop = true // 避免多次实例化相同的目的地
    }
}

// 所有
@Deprecated("Navigation2将被抛弃，请使用SharedNav")
fun NavController.allRouteStack() : List<String> = this.currentBackStack.value.mapNotNull { it.destination.route }

// 所有
@Deprecated("Navigation2将被抛弃，请使用SharedNav")
fun NavController.canPopBack(lastIndex : Int = 2) = this.allRouteStack().size >= lastIndex

@Deprecated("Navigation2将被抛弃，请使用SharedNav")
fun NavController.popBackSafely() {
    if(this.canPopBack()) {
        this.popBackStack()
    }
}

// 得到上一级
@Deprecated("Navigation2将被抛弃，请使用SharedNav")
fun NavController.previousRouteWithArgWithoutValues(): String? = this.previousBackStackEntry?.destination?.route

@Composable
@Deprecated("Navigation2将被抛弃，请使用SharedNav")
fun NavController.previousRouteWithoutArgs() : String? =  previousRouteWithArgWithoutValues()?.substringBefore("?")

@Composable
@Deprecated("Navigation2将被抛弃，请使用SharedNav")
fun NavController.currentRouteWithArgWithoutValues() : String? = this.currentBackStackEntryAsState().value?.destination?.route

@Composable
@Deprecated("Navigation2将被抛弃，请使用SharedNav")
fun NavController.currentRouteWithoutArgs() : String? =  currentRouteWithArgWithoutValues()?.substringBefore("?")

@Composable
@Deprecated("Navigation2将被抛弃，请使用SharedNav")
fun NavController.isCurrentRouteWithoutArgs(route: String) : Boolean = currentRouteWithoutArgs() == route.substringBefore("?")

