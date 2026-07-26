package com.hfut.schedule.ui.util.state

import com.hfut.schedule.logic.model.enumeration.AppStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 全局事件放在这里，用Flow模拟事件总线
 *
 * 用于跨页面通知，不是用来存状态的，存状态去GlobalUiStateHolder或者ViewModel
 */
object GlobalEventHolder {
    /**
     * 普通事件，发送后不会保存，没人接收就丢失
     */
    class EventFlow<T> {
        private val _flow = MutableSharedFlow<T>(extraBufferCapacity = 1)
        val flow = _flow.asSharedFlow()

        fun emit(value: T): Boolean = _flow.tryEmit(value)
    }

    /**
     * 可消费事件，事件会一直保存，直到手动消费
     */
    class SavedEventFlow<T> {
        private val _flow = MutableStateFlow<T?>(null)
        val flow = _flow.asStateFlow()

        fun emit(value: T) {
            _flow.value = value
        }

        fun consume(block: (T) -> Unit) {
            _flow.value?.let {
                block(it)
                _flow.value = null
            }
        }

        fun clear() {
            _flow.value = null
        }

        val value: T?
            get() = _flow.value
    }

    // 前台、后台
    val appStatusChanged = EventFlow<AppStatus>()
    // 调休日设置
    val specialWorkDayChanged = EventFlow<Unit>()
    val gradeCountChanged = SavedEventFlow<Int>()
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