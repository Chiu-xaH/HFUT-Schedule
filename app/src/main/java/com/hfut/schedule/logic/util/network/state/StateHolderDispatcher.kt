package com.hfut.schedule.logic.util.network.state

fun <T> StateHolder<T>.dispatchLoading() {
    setLoading()
}

fun <T> StateHolder<T>.dispatchData(data: T) {
    emitData(data)
}

fun <T> StateHolder<T>.dispatchError(e: Throwable?, code: Int? = null) {
    emitError(e, code)
}
