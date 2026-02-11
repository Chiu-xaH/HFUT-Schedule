package com.xah.uicommon.util


infix fun Float.safeDiv(denominator: Float): Float {
    if (denominator == 0f) return 0f
    val result = this / denominator
    return if (result.isFinite()) result else 0f
}

infix fun Double.safeDiv(denominator: Double): Double {
    if (denominator == 0.0) return 0.0
    val result = this / denominator
    return if (result.isFinite()) result else 0.0
}

infix fun Int.safeDiv(denominator: Float): Float {
    if (denominator == 0f) return 0f
    val result = this / denominator
    return if (result.isFinite()) result else 0f
}

infix fun Int.safeDiv(denominator: Double): Double {
    if (denominator == 0.0) return 0.0
    val result = this / denominator
    return if (result.isFinite()) result else 0.0
}

infix fun Float.safeDiv(denominator: Int): Float {
    if (denominator == 0) return 0f
    val result = this / denominator
    return if (result.isFinite()) result else 0f
}

infix fun Double.safeDiv(denominator: Int): Double {
    if (denominator == 0) return 0.0
    val result = this / denominator
    return if (result.isFinite()) result else 0.0
}

