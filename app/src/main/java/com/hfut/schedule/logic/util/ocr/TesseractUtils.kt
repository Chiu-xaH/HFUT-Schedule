package com.hfut.schedule.logic.util.ocr

import android.graphics.Bitmap
import android.os.Environment
import android.util.Log

import com.hfut.schedule.application.MyApplication
import com.xah.uicommon.util.LogUtil
import java.io.File

object TesseractUtils {
    const val filename = "eng.traineddata"
    @JvmStatic
    fun recognizeCaptcha(bitmap: Bitmap): String {
        try {
            val tesseractOCR = TesseractOCR()
            val result = tesseractOCR.recognizeText(bitmap)
            tesseractOCR.release()
            return result
        } catch (e: Exception) {
            return ""
        }
    }

    @JvmStatic
    fun isExistModule(): Boolean {
        val appFilesDir = MyApplication.context.getExternalFilesDir(null) // 获取 /Android/data/包名/files/
        val tessFile = File(appFilesDir, "tessdata/$filename") // 目标路径：Android/data/包名/files/tessdata/eng.traineddata
        return tessFile.exists()
    }
    @JvmStatic
    fun isModelInDownloadFolder(): Boolean {
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) // /storage/emulated/0/Download/
        val modelFile = File(downloadDir, filename)
        return modelFile.exists()
    }

    // 移动下载好的模型到目录
    @JvmStatic
    fun moveDownloadedModel() {
        val appFilesDir = MyApplication.context.getExternalFilesDir(null) // Android/data/包名/files/
        val tessDir = File(appFilesDir, "tessdata") // 目标目录：Android/data/包名/files/tessdata

        if (!tessDir.exists()) tessDir.mkdirs() // 确保 tessdata 目录存在

        val downloadedFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename)
        val destFile = File(tessDir, filename)

        if (downloadedFile.exists()) {
            try {
                downloadedFile.copyTo(destFile, overwrite = true) // 复制文件
                downloadedFile.delete() // 删除下载目录中的文件
//                Log.d("Tesseract", "模型移动成功: ${destFile.absolutePath}")
            } catch (e: Exception) {
                LogUtil.error(e)
//                Log.e("Tesseract", "模型移动失败", e)
            }
        } else {
            LogUtil.debug("Tesseract 下载的模型文件不存在: ${downloadedFile.absolutePath}")
        }
    }

    @JvmStatic
    fun deleteModel(): Boolean {
        val tessDir = File(MyApplication.context.getExternalFilesDir(null), "tessdata") // 获取 Android/data/包名/files/tessdata 目录
        val modelFile = File(tessDir, filename)

        return if (modelFile.exists()) {
            modelFile.delete()
        } else {
            false
        }
    }
}

