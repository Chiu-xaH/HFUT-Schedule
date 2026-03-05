package com.xah.container.anim

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.util.lerp

typealias RectInterpolator = (progress: Float, from: Rect, to: Rect) -> Rect

val LinearRectInterpolator: RectInterpolator = { t, from, to ->
    Rect(
        left = lerp(from.left, to.left, t),
        top = lerp(from.top, to.top, t),
        right = lerp(from.right, to.right, t),
        bottom = lerp(from.bottom, to.bottom, t)
    )
}

fun QuadraticBezierRectInterpolator(
    screenHeight: Float,
    screenWidth: Float,
    maxVerticalArc: Float = screenHeight / 2f,
    maxHorizontalArc: Float = screenWidth / 3f
): RectInterpolator = { t, from, to ->

    val startCenter = Offset(
        from.left + from.width / 2f,
        from.top + from.height / 2f
    )

    val endCenter = Offset(
        to.left + to.width / 2f,
        to.top + to.height / 2f
    )

    val avgY = (startCenter.y + endCenter.y) / 2f
    val normalizedY = ((avgY / screenHeight) - 0.5f) * 2f
    val verticalArc = -normalizedY * maxVerticalArc

    val avgX = (startCenter.x + endCenter.x) / 2f
    val normalizedX = ((avgX / screenWidth) - 0.5f) * 2f
    val horizontalArc = -normalizedX * maxHorizontalArc

    val control = Offset(
        x = (startCenter.x + endCenter.x) / 2f + horizontalArc,
        y = (startCenter.y + endCenter.y) / 2f + verticalArc
    )

    val oneMinusT = 1f - t

    val center = Offset(
        x = oneMinusT * oneMinusT * startCenter.x +
                2 * oneMinusT * t * control.x +
                t * t * endCenter.x,
        y = oneMinusT * oneMinusT * startCenter.y +
                2 * oneMinusT * t * control.y +
                t * t * endCenter.y
    )

    val width = lerp(from.width, to.width, t)
    val height = lerp(from.height, to.height, t)

    Rect(
        left = center.x - width / 2f,
        top = center.y - height / 2f,
        right = center.x + width / 2f,
        bottom = center.y + height / 2f
    )
}