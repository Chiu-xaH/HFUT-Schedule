package com.xah.common.logic.state

sealed class NetworkUiState<out T> {
    data object Loading : NetworkUiState<Nothing>()
    data class Success<T>(val data: T) : NetworkUiState<T>()
    data class Error<T>(val exception: Throwable?,val code: Int? = null) : NetworkUiState<T>()
    data object Prepare : NetworkUiState<Nothing>()
}