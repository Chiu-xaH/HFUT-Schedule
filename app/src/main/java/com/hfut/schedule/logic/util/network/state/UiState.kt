package com.hfut.schedule.logic.util.network.state

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error<T>(val exception: Throwable?,val code: Int? = null) : UiState<T>()
    data object Prepare : UiState<Nothing>()
}