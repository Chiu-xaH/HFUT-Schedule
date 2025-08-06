package com.hfut.schedule.ui.util

import androidx.navigation.NavController
import com.hfut.schedule.logic.util.development.getKeyStackTrace
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.screen.AppNavRoute
import com.xah.transition.util.navigateAndSaveForTransition
import com.xah.transition.util.navigateWithSave

fun NavController.navigateAndSave(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}


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