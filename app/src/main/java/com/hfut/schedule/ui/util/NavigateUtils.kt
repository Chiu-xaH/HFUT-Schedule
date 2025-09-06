package com.hfut.schedule.ui.util

import androidx.navigation.NavController
import com.hfut.schedule.logic.util.development.getKeyStackTrace
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.screen.AppNavRoute
import com.xah.transition.util.navigateAndSaveForTransition
import com.xah.transition.util.navigateWithSave

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

// 导航后 上一级永远是上个界面
fun NavController.navigateForTransition(app : AppNavRoute,route: String,transplantBackground : Boolean = false) = try {
    GlobalUIStateHolder.pushToFront(route,app)
    navigateAndSaveForTransition(route,transplantBackground)
} catch (e : Exception) {
    e.printStackTrace()
    showToast(getKeyStackTrace(e))
}

fun NavController.navigateForTransition(route: String,transplantBackground : Boolean = false) = try {
    navigateAndSaveForTransition(route,transplantBackground)
} catch (e : Exception) {
    e.printStackTrace()
    showToast(getKeyStackTrace(e))
}