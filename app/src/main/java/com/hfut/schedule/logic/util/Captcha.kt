package com.hfut.schedule.logic.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get
import androidx.core.graphics.set

fun preprocessCaptcha(bitmap: Bitmap): Bitmap {
    try {
        val width = bitmap.width
        val height = bitmap.height
        val dst = createBitmap(width, height)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = bitmap[x, y]
                val r = Color.red(pixel)
                val g = Color.green(pixel)
                val b = Color.blue(pixel)

                // 灰度化
                val gray = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
                val newPixel = Color.rgb(gray, gray, gray)
                dst[x, y] = newPixel
            }
        }

        return dst
    } catch (e : Exception) {
        return bitmap
    }
}