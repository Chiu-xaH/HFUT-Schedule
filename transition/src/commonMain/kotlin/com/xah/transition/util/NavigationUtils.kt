package com.xah.transition.util

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.xah.transition.state.NavAction
import com.xah.transition.state.TransitionConfig

fun NavController.navigateWithSave(route: String) {
    navigate(route) {
        launchSingleTop = true
        restoreState = true
    }
}

fun NavController.navigateAndSaveForTransition(route: String,transplantBackground : Boolean = false) {
    TransitionConfig.transplantBackground = transplantBackground
    TransitionConfig.action = NavAction.Push
    navigateWithSave(route)
}

fun NavController.popBackStackForTransition() {
    if(this.canPopBack()) {
        TransitionConfig.action = NavAction.Pop
        this.popBackStack()
    }
}

@Composable
fun NavController.currentRouteWithArgWithoutValues() : String? = this.currentBackStackEntryAsState().value?.destination?.route

@Composable
fun NavController.currentRouteWithoutArgs() : String? =  currentRouteWithArgWithoutValues()?.substringBefore("?")

@Composable
fun NavController.isCurrentRouteWithoutArgs(route: String) : Boolean = currentRouteWithoutArgs() == route.substringBefore("?")

// 得到上一级
fun NavController.previousRouteWithArgWithoutValues(): String? = this.previousBackStackEntry?.destination?.route

@Composable
fun NavController.previousRouteWithoutArgs() : String? =  previousRouteWithArgWithoutValues()?.substringBefore("?")

// 所有
fun NavController.allRouteStack() : List<String> = this.currentBackStack.value.mapNotNull { it.destination.route }

// 所有
fun NavController.canPopBack(lastIndex : Int = 2) = this.allRouteStack().size >= lastIndex


// 界面是否在栈底倒数
fun NavController.isInBottom(route : String,lastIndex : Int = 2) : Boolean {
    val stack = this.allRouteStack()
    return stack.takeLast(lastIndex).contains(route)
}

