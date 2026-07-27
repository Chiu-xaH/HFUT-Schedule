package com.hfut.schedule.ui.util.state

import com.hfut.schedule.logic.model.enumeration.AppStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 全局事件放在这里，用Flow模拟事件总线，用于跨页面通知，GlobalEventHolder强调事件驱动
 */
object GlobalEventHolder {
    /**
     * 事件发送后，被消费后即清除
     */
    class AutoEventFlow<T> {
        private val _flow = MutableSharedFlow<T>(extraBufferCapacity = 1)
        val flow = _flow.asSharedFlow()

        fun emit(value: T): Boolean = _flow.tryEmit(value)
    }

    /**
     * 事件发送后，即使被消费也不会被清除，直到手动clear
     */
    class ManualEventFlow<T> {
        private val _flow = MutableStateFlow<T?>(null)
        val flow = _flow.asStateFlow()

        fun emit(value: T) {
            _flow.value = value
        }

        fun clear() {
            _flow.value = null
        }

        val value: T?
            get() = _flow.value
    }

    // 前台、后台
    val appStatusChanged = AutoEventFlow<AppStatus>()
    // 调休日设置
    val specialWorkDayChanged = AutoEventFlow<Unit>()
    val gradeCountChanged = ManualEventFlow<Int>()

    /**
     * // 发射
     * GlobalEventHolder.gradeCountChanged.emit(0)
     *
     * // 收集
     * LaunchedEffect(Unit) {
     *     GlobalEventHolder.gradeCountChanged.flow.collect { event ->
     *         XXX
     *     }
     * }
     * // 收集
     * val gradeCount by GlobalEventHolder.gradeCountChanged.flow.collectAsState(initial = null)
     * if (gradeCount != null) {
     *     XXX
     * }
     */
}