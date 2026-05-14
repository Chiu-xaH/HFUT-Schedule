package com.hfut.schedule.ui.util.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

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

// 所有
fun NavController.allRouteStack() : List<String> = this.currentBackStack.value.mapNotNull { it.destination.route }

// 所有
fun NavController.canPopBack(lastIndex : Int = 2) = this.allRouteStack().size >= lastIndex

fun NavController.popBackSafely() {
    if(this.canPopBack()) {
        this.popBackStack()
    }
}

// 得到上一级
fun NavController.previousRouteWithArgWithoutValues(): String? = this.previousBackStackEntry?.destination?.route

@Composable
fun NavController.previousRouteWithoutArgs() : String? =  previousRouteWithArgWithoutValues()?.substringBefore("?")

@Composable
fun NavController.currentRouteWithArgWithoutValues() : String? = this.currentBackStackEntryAsState().value?.destination?.route

@Composable
fun NavController.currentRouteWithoutArgs() : String? =  currentRouteWithArgWithoutValues()?.substringBefore("?")

@Composable
fun NavController.isCurrentRouteWithoutArgs(route: String) : Boolean = currentRouteWithoutArgs() == route.substringBefore("?")

