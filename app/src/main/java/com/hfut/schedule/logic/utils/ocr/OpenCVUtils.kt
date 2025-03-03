package com.hfut.schedule.logic.utils.ocr

import android.graphics.Bitmap
import android.util.Log
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

object OpenCVUtils {
    private fun init() = OpenCVLoader.initDebug()

    fun processBitmap(bitmap: Bitmap): Bitmap? {
        if(!init()) {
            Log.d("OpenCV","未初始化")
            return null
        }
        val src = Mat()
        Utils.bitmapToMat(bitmap, src) // Bitmap 转 Mat
        val processedMat = processImage(src) // 处理 Mat
        return matToBitmap(processedMat) // Mat 转 Bitmap
    }

    private fun processImage(src: Mat): Mat {
        val gray = Mat()
        val blurred = Mat()
        val thresh = Mat()
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY)
        Imgproc.GaussianBlur(gray, blurred, Size(5.0, 5.0), 0.0)
        Imgproc.threshold(blurred, thresh, 0.0, 255.0, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU)
        return thresh
    }

    private fun matToBitmap(src: Mat): Bitmap {
        val bitmap = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(src, bitmap) // Mat 转 Bitmap
        return bitmap
    }
}