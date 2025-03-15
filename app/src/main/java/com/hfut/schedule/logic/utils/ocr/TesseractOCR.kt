package com.hfut.schedule.logic.utils.ocr

import android.graphics.Bitmap
import com.googlecode.tesseract.android.TessBaseAPI
import com.hfut.schedule.App.MyApplication
import java.io.File

class TesseractOCR {
    private val tessBaseAPI: TessBaseAPI = TessBaseAPI()

    init {
        val tessDataPath = getTessDataPath()
        tessBaseAPI.init(tessDataPath, "eng") // 选择语言
        tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE)
    }

    // 识别图片中的文字
    fun recognizeText(bitmap: Bitmap): String {
        tessBaseAPI.setImage(bitmap)
        return tessBaseAPI.utF8Text.trim()
    }

    // 释放资源
    fun release() {
        tessBaseAPI.stop()
//        tessBaseAPI.end()
    }

    // 获取 Tesseract 数据路径
    private fun getTessDataPath(): String {
        val tessDataDir = File(MyApplication.context.getExternalFilesDir(null), "tessdata")
        if (!tessDataDir.exists()) {
            tessDataDir.mkdirs()
        }
        return MyApplication.context.getExternalFilesDir(null)!!.absolutePath
    }
}