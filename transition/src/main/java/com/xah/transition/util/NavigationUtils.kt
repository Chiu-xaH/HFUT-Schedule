package com.xah.transition.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.xah.transition.state.TransitionState

fun NavController.navigateAndClear(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) { inclusive = true } // 清除所有历史记录
        launchSingleTop = true // 避免多次实例化相同的目的地
    }
}

fun NavController.navigateWithSave(route: String) {
    navigate(route) {
        launchSingleTop = true
        restoreState = true
    }
}

fun NavController.navigateAndSaveForTransition(route: String,transplantBackground : Boolean = false) {
    // 禁用背景透明
    TransitionState.transplantBackground = transplantBackground
//    println("READY GO $route | CURRENT" + this.allRouteStack())
    navigateWithSave(route)
//    println("GONE" + this.allRouteStack())
}

@Composable
fun NavController.currentRouteWithArgWithoutValues() : String? = this.currentBackStackEntryAsState().value?.destination?.route

@Composable
fun NavController.currentRouteWithoutArgs() : String? =  currentRouteWithArgWithoutValues()?.substringBefore("?")

@Composable
fun NavController.currentRouteWithArgsWithValues(): String? {
    val navBackStackEntry by currentBackStackEntryAsState()
    val routeTemplate = navBackStackEntry?.destination?.route?.substringBefore("?") ?: return null
    val args = navBackStackEntry?.arguments ?: return routeTemplate

    val paramStr = args.keySet()
        .filter { it != "android-support-nav:controller:deepLinkIntent" } // 排除系统参数
        .joinToString("&") { key ->
            val value = args.get(key)
            "$key=$value"
        }
    return if (paramStr.isNotEmpty()) "$routeTemplate?$paramStr" else routeTemplate
}

@Composable
fun NavController.isCurrentRouteWithoutArgs(route: String) : Boolean = currentRouteWithoutArgs() == route.substringBefore("?")

// 得到上一级
fun NavController.previousRouteWithArgWithoutValues(): String? = this.previousBackStackEntry?.destination?.route

@Composable
fun NavController.previousRouteWithoutArgs() : String? =  previousRouteWithArgWithoutValues()?.substringBefore("?")

@Composable
fun NavController.previousRouteWithArgsWithValues(): String? {
    val routeTemplate = previousBackStackEntry?.destination?.route?.substringBefore("?") ?: return null
    val args = previousBackStackEntry?.arguments ?: return routeTemplate

    val paramStr = args.keySet()
        .filter { it != "android-support-nav:controller:deepLinkIntent" } // 排除系统参数
        .joinToString("&") { key ->
            val value = args.get(key)
            "$key=$value"
        }
    return if (paramStr.isNotEmpty()) "$routeTemplate?$paramStr" else routeTemplate
}


// 所有
fun NavController.allRouteStack() : List<String> = this.currentBackStack.value.mapNotNull { it.destination.route }

// 界面是否在栈底倒数
fun NavController.isInBottom(route : String,lastIndex : Int = 2) : Boolean {
    val stack = this.allRouteStack()
    return stack.takeLast(lastIndex).contains(route)
}

