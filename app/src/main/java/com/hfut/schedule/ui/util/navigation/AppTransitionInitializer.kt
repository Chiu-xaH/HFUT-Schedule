package com.hfut.schedule.ui.util.navigation

import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.xah.transition.state.TransitionConfig
import com.xah.transition.style.TransitionLevel
import com.xah.transition.util.TransitionInitializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object AppTransitionInitializer : TransitionInitializer {
    override suspend fun init() {
        withContext(Dispatchers.IO) {
            launch {
                val transition = DataStoreManager.transitionLevel.first()
                TransitionConfig.transitionBackgroundStyle.level =
                    TransitionLevel.entries.find { it.code == transition } ?: TransitionLevel.NONE
            }
            launch {
                TransitionConfig.enableMirror = AppVersion.CAN_SHADER
            }
        }
    }
}