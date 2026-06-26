package com.xah.common.logic.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private interface IStateHolder<in T> {
    fun emitData(data : T)
    fun emitError(e: Throwable?, code: Int? = null)
    fun clear()
    fun setLoading()
}

class UiStateHolder<T> : IStateHolder<T> {
    private val _state = MutableStateFlow<NetworkUiState<T>>(NetworkUiState.Loading)
    val state: StateFlow<NetworkUiState<T>> get() = _state

    override fun emitData(data: T) {
        _state.value = NetworkUiState.Success(data)
    }

    override fun emitError(e: Throwable?, code: Int?) {
        _state.value = NetworkUiState.Error(e, code)
    }


    override fun setLoading() {
        _state.value = NetworkUiState.Loading
    }

    override fun clear() {
        _state.value = NetworkUiState.Loading
    }

    fun emitPrepare() {
        _state.value = NetworkUiState.Prepare
    }
}

// 判断是否包含某个 key
fun <K, V> UiStateHolder<Map<K, V>>.containsKey(key: K): Boolean {
    val current = (state.value as? NetworkUiState.Success)?.data
    return current?.containsKey(key) == true
}

// 获取某个 key 的值
operator fun <K, V> UiStateHolder<Map<K, V>>.get(key: K): V? {
    val current = (state.value as? NetworkUiState.Success)?.data
    return current?.get(key)
}

// 设置某个 key 的值，会触发刷新
operator fun <K, V> UiStateHolder<Map<K, V>>.set(key: K, value: V) {
    val current = (state.value as? NetworkUiState.Success)?.data?.toMutableMap() ?: mutableMapOf()
    current[key] = value
    emitData(current)
}