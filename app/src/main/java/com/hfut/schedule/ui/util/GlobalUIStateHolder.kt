package com.hfut.schedule.ui.util

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.hfut.schedule.ui.screen.AppNavRoute

// 跨Activity的类似UIViewModel
object GlobalUIStateHolder {
    var isSupabaseRegistering = mutableStateOf(false)
    val routeQueue = mutableStateListOf<RouteQueueBean>()
    fun pushToFront(route: String,app : AppNavRoute) {
        routeQueue.removeAll { it.route == route }
        routeQueue.add(0, RouteQueueBean(route,app))
    }
}


data class RouteQueueBean(val route : String,val app : AppNavRoute)
