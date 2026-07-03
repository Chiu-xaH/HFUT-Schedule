package com.hfut.schedule.ui.util.state

import com.hfut.schedule.logic.model.AppStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * 全局事件放在这里，用Flow模拟事件总线
 *
 * 用于跨页面通知，不是用来存状态的，存状态去GlobalUiStateHolder或者ViewModel
 */
object GlobalEventHolder {
    class EventFlow<T> {
        private val _flow = MutableSharedFlow<T>(extraBufferCapacity = 1)
        val flow = _flow.asSharedFlow()

        fun emit(value: T): Boolean = _flow.tryEmit(value)
    }

    // 前台、后台
    val appStatusChanged = EventFlow<AppStatus>()
    val specialWorkDayChanged = EventFlow<Unit>()
    /**
     * //  发射
     * GlobalEventHolder.isLoginEvent.emit(true)
     *
     * //  收集
     * LaunchedEffect(Unit) {
     *     GlobalEventHolder.isLoginEvent.flow.collect { event ->
     *         XX
     *     }
     * }
     */
}