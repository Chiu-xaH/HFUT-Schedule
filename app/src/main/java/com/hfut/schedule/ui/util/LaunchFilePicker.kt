package com.hfut.schedule.ui.util

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.io.File


// 复制到缓存区 方便与C语言交互
private fun copyUriToCacheFile(context: Context, uri: Uri): String? {
    val inputStream = context.contentResolver.openInputStream(uri) ?: return null
    val file = File(context.cacheDir, "temp_file_${System.currentTimeMillis()}")

    file.outputStream().use { outputStream ->
        inputStream.copyTo(outputStream)
    }
    return file.absolutePath
}

// 文件选择器 onResult代表选择文件后的操作，记得跟随open = false关闭文件选择器
@Composable
fun LaunchFilePicker(open : Boolean,onResult: (String?) -> Unit) {
    val context = LocalContext.current
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                val newPath = copyUriToCacheFile(context,it)
                onResult(newPath)
            } ?: onResult(null)
        }
    )
    // 启动文件选择器
    if(open)
        filePickerLauncher.launch(arrayOf("*/*"))
}
