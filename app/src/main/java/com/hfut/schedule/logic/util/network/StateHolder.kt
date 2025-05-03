package com.hfut.schedule.logic.util.network

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
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


class SimpleStateHolder<T> {
    private val _state = MutableStateFlow<SimpleUiState<T>>(SimpleUiState.Loading)
    val state: StateFlow<SimpleUiState<T>> get() = _state

    fun emitData(data: T?) {
        _state.value = if (data == null) SimpleUiState.Empty else SimpleUiState.Success(data)
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

    fun emitEmpty() {
        _state.value = SimpleUiState.Empty
    }

}

class StateHolder<T, E> {
    private val _state = MutableStateFlow<UiState<T, E>>(UiState.Loading)
    val state: StateFlow<UiState<T, E>> get() = _state

    fun emitData(data: T?) {
        _state.value = if (data == null) UiState.Empty else UiState.Success(data)
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
