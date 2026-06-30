package com.hfut.schedule.ui.util.state

import com.hfut.schedule.logic.model.AppStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * 全局事件放在这里，用Flow模拟事件总线
 */
object GlobalEventHolder {
    class EventFlow<T> {
        private val _flow = MutableStateFlow<T?>(null)
        val flow: Flow<T?> = _flow.asSharedFlow()
        fun emit(event: T) {
            _flow.tryEmit(event)
        }
    }

    // 前台、后台
    val appStatus = EventFlow<AppStatus>()
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