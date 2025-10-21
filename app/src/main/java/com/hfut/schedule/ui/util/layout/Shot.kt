package com.hfut.schedule.ui.util.layout

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.PixelCopy
import android.view.View
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream


// 截屏
@Composable
fun SaveComposeAsImage(value: MutableState<Int>, fileName:String, tabThumbFilePath:MutableState<String>) {
    val view = LocalView.current // 获取当前的 View
    val context = LocalContext.current // 获取当前的 Context
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            // 创建一个 Bitmap，大小为 View 的宽高
            try {
                // 创建一个 Canvas，并将 Bitmap 绘制到其中
                captureView(view = view, (context as Activity).window){ bitmap ->
                    scope.launch {
                        withContext(Dispatchers.IO){
                            try {
                                val parentFile = File(context.externalCacheDir, "tab_thumb")
                                if (!parentFile.exists()) {
                                    parentFile.mkdirs()
                                }

                                // 保存 Bitmap 到文件
                                val file = File(parentFile, "$fileName.png")
                                val outputStream = FileOutputStream(file)
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                                outputStream.close()

                                Log.i("保存TAB封面", file.absolutePath)
                                tabThumbFilePath.value = file.absolutePath
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            value.value = 0
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

fun captureView(view: View, window: Window, bitmapCallback: (Bitmap)->Unit) {
    val bitmap = createBitmap(view.width, view.height)
    val location = IntArray(2)
    view.getLocationInWindow(location)
    PixelCopy.request(window,
        Rect(location[0], location[1], location[0] + view.width, location[1] + view.height),
        bitmap,
        {
            if (it == PixelCopy.SUCCESS) {
                bitmapCallback.invoke(bitmap)
            }
        },
        Handler(Looper.getMainLooper()))
}

