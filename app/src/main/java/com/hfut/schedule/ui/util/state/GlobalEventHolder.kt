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

    // 应用前后台切换
    val appStatusChangeCallback = AutoEventFlow<AppStatus>()
    // 调休日发生修改
    val specialWorkDayChangeCallback = AutoEventFlow<Unit>()
    // 检查成绩单数目是否发现变化
    val gradeCountCheckCallback = ManualEventFlow<Int>()
    // 刷新图片验证码
    val captchaRefreshCallback = AutoEventFlow<Unit>()
    // 刷新教务成绩列表（评教完成后）
    val gradeRefreshCallback = AutoEventFlow<Unit>()

    /**
     * // 发射
     * GlobalEventHolder.gradeCountChanged.emit(0)
     *
     * // AutoEventFlow收集方法
     * LaunchedEffect(Unit) {
     *     GlobalEventHolder.gradeCountChanged.flow.collect { event ->
     *         XXX
     *     }
     * }
     * // ManualEventFlow收集方法
     * val gradeCount by GlobalEventHolder.gradeCountChanged.flow.collectAsState(initial = null)
     * if (gradeCount != null) {
     *     XXX
     * }
     */
}