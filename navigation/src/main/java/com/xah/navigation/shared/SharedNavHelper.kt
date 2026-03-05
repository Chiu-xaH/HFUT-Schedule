package com.xah.navigation.shared

import androidx.compose.runtime.snapshotFlow
import com.xah.container.controller.SharedContainerRegistry
import com.xah.navigation.anim.EffectLevel
import com.xah.navigation.controller.NavigationController
import com.xah.navigation.model.Destination
import com.xah.navigation.model.LaunchMode
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

object SharedNavHelper {
    fun push(
        destination: Destination,
        navigationController : NavigationController,
        registry : SharedContainerRegistry,
        launchMode: LaunchMode = LaunchMode. SINGLE_TOP,
    ) {
        if(navigationController.transitionLevel == EffectLevel.NONE || launchMode == LaunchMode.CLEAR_STACK || launchMode == LaunchMode.SINGLE_INSTANCE) {
            navigationController.push(destination,launchMode)
        } else {
            registry.push(
                destination.key,
                onAnimatedFinished = {
                    snapshotFlow { navigationController.isTransitioning }
                        .filter { !it }
                        .first()
                }
            ) {
                navigationController.push(destination,launchMode)
            }
        }
    }

    fun pop(navigationController : NavigationController, registry : SharedContainerRegistry) {
        if(navigationController.transitionLevel == EffectLevel.NONE) {
            navigationController.pop()
        } else {
            registry.pop(
                navigationController.stack.last().destination.key,
                onAnimatedFinished = {
                    snapshotFlow { navigationController.isTransitioning }
                        .filter { !it }
                        .first()
                }
            ) {
                navigationController.pop()
            }
        }
    }
}