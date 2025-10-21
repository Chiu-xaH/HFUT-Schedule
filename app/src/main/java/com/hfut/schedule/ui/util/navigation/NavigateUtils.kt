package com.hfut.schedule.ui.util.navigation

import androidx.navigation.NavController
import com.hfut.schedule.logic.util.development.getKeyStackTrace
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.util.state.GlobalUIStateHolder
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

// 导航后 当前界面就是唯一界面 上一级被清除
fun NavController.navigateAndClear(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) { inclusive = true } // 清除所有历史记录
        launchSingleTop = true // 避免多次实例化相同的目的地
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