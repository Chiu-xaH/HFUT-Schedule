package com.hfut.schedule.logic.util.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

//class EventFlow<T> {
//    private val _events = MutableSharedFlow<T>(replay = 0)
//    val events: SharedFlow<T> get() = _events
//
//    suspend fun emit(event: T) {
//        _events.emit(event)
//    }
//
//    fun tryEmit(event: T) {
//        _events.tryEmit(event)
//    }
//}
const val PARSE_ERROR_CODE = 1000


class SimpleStateHolder<T> {
    private val _state = MutableStateFlow<SimpleUiState<T>>(SimpleUiState.Loading)
    val state: StateFlow<SimpleUiState<T>> get() = _state

    fun emitData(data: T) {
        _state.value = SimpleUiState.Success(data)
    }

    fun emitError(e: Throwable?, code: Int? = null) {
        _state.value = SimpleUiState.Error(e, code)
    }


    fun setLoading() {
        _state.value = SimpleUiState.Loading
    }

    fun clear() {
        _state.value = SimpleUiState.Loading
    }

    fun emitPrepare() {
        _state.value = SimpleUiState.Prepare
    }
}

// 判断是否包含某个 key
fun <K, V> SimpleStateHolder<Map<K, V>>.containsKey(key: K): Boolean {
    val current = (state.value as? SimpleUiState.Success)?.data
    return current?.containsKey(key) == true
}

// 获取某个 key 的值
operator fun <K, V> SimpleStateHolder<Map<K, V>>.get(key: K): V? {
    val current = (state.value as? SimpleUiState.Success)?.data
    return current?.get(key)
}

// 设置某个 key 的值，会触发刷新
operator fun <K, V> SimpleStateHolder<Map<K, V>>.set(key: K, value: V) {
    val current = (state.value as? SimpleUiState.Success)?.data?.toMutableMap() ?: mutableMapOf()
    current[key] = value
    emitData(current)
}


class StateHolder<T, E> {
    private val _state = MutableStateFlow<UiState<T, E>>(UiState.Loading)
    val state: StateFlow<UiState<T, E>> get() = _state

    fun emitData(data: T?) {
        _state.value = if (data == null) UiState.Prepare else UiState.Success(data)
    }

    fun emitError(e: Throwable?, code: Int? = null, body: E?) {
        _state.value = UiState.Error(e, code, body)
    }

    fun setLoading() {
        _state.value = UiState.Loading
    }

    fun clear() {
        _state.value = UiState.Loading
    }
}
