package com.hfut.schedule.ui.util.color

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.palette.graphics.Palette
import com.hfut.schedule.application.MyApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.graphics.Color as AColor
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import com.hfut.schedule.logic.util.other.AppVersion
import com.materialkolor.ktx.themeColors
import com.xah.uicommon.util.LogUtil
import kotlin.math.abs

fun parseColor(input: String): Long? {
    return try {
        val cleaned = input.removePrefix("0x").removePrefix("#")
        val colorLong = cleaned.toLong(16)
        colorLong
    } catch (e: Exception) {
        LogUtil.error(e)
        null
    }
}
fun longToHexColor(colorLong: Long): String {
    // 保证是 32 位 ARGB
    val colorInt = colorLong.toInt()
    return String.format("#%08X", colorInt)
}

@RequiresApi(Build.VERSION_CODES.P)
fun uriToImageBitmap(uri: Uri): ImageBitmap? {
    return try {
        val bitmap = if (AppVersion.sdkInt >= Build.VERSION_CODES.P) {
            // Android 9+
            val source = ImageDecoder.createSource(MyApplication.context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            // Android 8 及以下
            MediaStore.Images.Media.getBitmap(MyApplication.context.contentResolver, uri)
        }
        bitmap.asImageBitmap()
    } catch (e: Exception) {
        LogUtil.error(e)
        null
    }
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

suspend fun extractColor(context: Context,@DrawableRes resId: Int): Long? {
    return withContext(Dispatchers.IO) {
        val bitmap = resToBitmap(context,resId)
            ?: return@withContext null
        val palette = Palette.from(bitmap).generate()
        palette.getDominantColor(AColor.GRAY).toLong()
    }
}


fun resToBitmap(context: Context,@DrawableRes resId: Int) : Bitmap? = try {
    BitmapFactory.decodeResource(context.resources, resId)
} catch (e : Exception) {
    LogUtil.error(e)
    null
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

// 传入正数 变深
// 传入负数 变浅
fun Color.deepen(factor: Float = 0.1f, isInDark: Boolean): Color {
    if (factor == 0f) return this

    val f = abs(factor)

    return if (!isInDark) {
        // 亮色模式
        if (factor > 0) {
            // 变深：乘 (1 - factor)
            Color(
                red = (this.red * (1f - f)).coerceIn(0f, 1f),
                green = (this.green * (1f - f)).coerceIn(0f, 1f),
                blue = (this.blue * (1f - f)).coerceIn(0f, 1f),
                alpha = this.alpha
            )
        } else {
            // 变浅：往白色插值
            Color(
                red = (this.red + (1f - this.red) * f).coerceIn(0f, 1f),
                green = (this.green + (1f - this.green) * f).coerceIn(0f, 1f),
                blue = (this.blue + (1f - this.blue) * f).coerceIn(0f, 1f),
                alpha = this.alpha
            )
        }
    } else {
        // 深色模式
        if (factor > 0) {
            // 往白色插值
            Color(
                red = (this.red + (1f - this.red) * f).coerceIn(0f, 1f),
                green = (this.green + (1f - this.green) * f).coerceIn(0f, 1f),
                blue = (this.blue + (1f - this.blue) * f).coerceIn(0f, 1f),
                alpha = this.alpha
            )
        } else {
            // 往黑色插值
            Color(
                red = (this.red * (1f - f)).coerceIn(0f, 1f),
                green = (this.green * (1f - f)).coerceIn(0f, 1f),
                blue = (this.blue * (1f - f)).coerceIn(0f, 1f),
                alpha = this.alpha
            )
        }
    }
}

