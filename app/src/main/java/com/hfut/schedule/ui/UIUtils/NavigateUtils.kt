package com.hfut.schedule.ui.UIUtils

import androidx.navigation.NavHostController

object NavigateUtils {
    fun turnToBottomBar(navController : NavHostController, route : String) {
        navController.navigate(route) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}