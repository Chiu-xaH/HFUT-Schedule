package com.hfut.schedule.ui.component

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.palette.graphics.Palette
import com.hfut.schedule.App.MyApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.graphics.Color as AColor


fun parseColor(input: String): Long? {
    return try {
        val cleaned = input.removePrefix("0x").removePrefix("#")
        val colorLong = cleaned.toLong(16)
        colorLong
    } catch (e: Exception) {
        null
    }
}
fun longToHexColor(colorLong: Long): String {
    // 保证是 32 位 ARGB
    val colorInt = colorLong.toInt()
    return String.format("#%08X", colorInt)
}

// 从URI图片取色
suspend fun extractColor(uri: Uri): Long? {
    return withContext(Dispatchers.IO) {
        val inputStream = MyApplication.context.contentResolver.openInputStream(uri) ?: return@withContext null
        val bitmap = BitmapFactory.decodeStream(inputStream) ?: return@withContext null
        inputStream.close()

        val palette = Palette.from(bitmap).generate()
        palette.getDominantColor(Color.Gray.toArgb()).toLong() // 提取主色（可设置默认值）
    }
}


fun hsvToLong(hue: Float, saturation: Float = 1f, value: Float = 1f): Long {
    return Color.hsv(hue, saturation, value).toArgb().toLong()
}


fun longToHue(colorLong: Long): Float {
    val argb = colorLong.toInt()
    val r = AColor.red(argb)
    val g = AColor.green(argb)
    val b = AColor.blue(argb)

    val hsv = FloatArray(3)
    AColor.RGBToHSV(r, g, b, hsv)

    return hsv[0] // Hue
}