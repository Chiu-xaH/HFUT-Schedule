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

        // 红色阈值参数（可调）
        val minRed = 120       // 红色强度阈值
        val diff = 50          // 红色与其他通道的差值要求

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = bitmap[x, y]
                val r = Color.red(pixel)
                val g = Color.green(pixel)
                val b = Color.blue(pixel)

                // 判断是否为红色像素
                if (r > minRed && r > g + diff && r > b + diff) {
                    dst[x, y] = Color.BLACK // 保留为黑色
                } else {
                    dst[x, y] = Color.WHITE // 背景置白
                }
            }
        }

        return dst
    } catch (e : Exception) {
        return bitmap
    }
}