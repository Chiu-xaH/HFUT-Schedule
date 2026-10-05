package com.hfut.schedule.ui.util.navigation

import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.util.state.GlobalUiStateHolder
import com.xah.navigation.controller.NavigationController
import com.xah.navigation.model.action.ActionType
import com.xah.navigation.model.action.LaunchMode
import com.xah.navigation.model.anim.TransitionEffect
//
//fun NavigationController.pushProxy(
//    destination: NavDestination,
//    effect : TransitionEffect? = null,
//    launchMode: LaunchMode = LaunchMode.Push()
//) {
//    if(effect == null) {
//        this.push(destination,launchMode)
//    } else {
//        this.push(destination,launchMode,effect)
//    }
//    GlobalUiStateHolder.historyDestinations.add(
//        DestinationTrack(
//            destination,
//            ActionType.PUSH,
//            launchMode
//        )
//    )
//}
//
//fun NavigationController.popProxy() {
//    this.pop()
//    GlobalUiStateHolder.historyDestinations.add(
//        DestinationTrack(
//            destination,
//            ActionType.PUSH,
//            launchMode
//        )
//    )
//}



data class DestinationTrack(
    val destination: NavDestination,
    val action : ActionType,
    val launchMode : LaunchMode,
    val timestamp : Long = System.currentTimeMillis(),
)