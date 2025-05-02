package com.hfut.schedule.logic.util.network

//sealed class UiState<out T> {
//    data object Loading : UiState<Nothing>()
//    data class Success<T>(val data: T?) : UiState<T>()
//    data class Error<T>(val exception: Throwable?,val code: Int? = null,val body : T?) : UiState<T>()
//    data object Empty : UiState<Nothing>()
//}

sealed class SimpleUiState<out T> {
    data object Loading : SimpleUiState<Nothing>()
    data class Success<T>(val data: T?) : SimpleUiState<T>()
    data class Error<T>(val exception: Throwable?,val code: Int? = null) : SimpleUiState<T>()
    data object Empty : SimpleUiState<Nothing>()
}
//typealias SimpleUiState<T> = UiState<T, Nothing>
//
sealed class UiState<out T, out E> {
    data object Loading : UiState<Nothing, Nothing>()
    data class Success<T>(val data: T?) : UiState<T, Nothing>()
    data class Error<E>(
        val exception: Throwable?,
        val code: Int? = null,
        val errorBody: E? = null
    ) : UiState<Nothing, E>()
    data object Empty : UiState<Nothing, Nothing>()
}
