package com.hfut.schedule.logic.util.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


const val PARSE_ERROR_CODE = 1000

class StateHolder<T> {
    private val _state = MutableStateFlow<UiState<T>>(UiState.Loading)
    val state: StateFlow<UiState<T>> get() = _state

    fun emitData(data: T) {
        _state.value = UiState.Success(data)
    }

    fun emitError(e: Throwable?, code: Int? = null) {
        _state.value = UiState.Error(e, code)
    }


    fun setLoading() {
        _state.value = UiState.Loading
    }

    fun clear() {
        _state.value = UiState.Loading
    }

    fun emitPrepare() {
        _state.value = UiState.Prepare
    }
}

// 判断是否包含某个 key
fun <K, V> StateHolder<Map<K, V>>.containsKey(key: K): Boolean {
    val current = (state.value as? UiState.Success)?.data
    return current?.containsKey(key) == true
}

// 获取某个 key 的值
operator fun <K, V> StateHolder<Map<K, V>>.get(key: K): V? {
    val current = (state.value as? UiState.Success)?.data
    return current?.get(key)
}

// 设置某个 key 的值，会触发刷新
operator fun <K, V> StateHolder<Map<K, V>>.set(key: K, value: V) {
    val current = (state.value as? UiState.Success)?.data?.toMutableMap() ?: mutableMapOf()
    current[key] = value
    emitData(current)
}