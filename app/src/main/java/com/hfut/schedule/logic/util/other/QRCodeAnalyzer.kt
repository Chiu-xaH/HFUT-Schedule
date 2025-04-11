package com.hfut.schedule.logic.util.other

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer

class QRCodeAnalyzer(private val listener: (com.google.zxing.Result?) -> Unit) : ImageAnalysis.Analyzer {
    private val reader = MultiFormatReader().apply {
        setHints(mapOf(DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE)))
    }

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            // 获取YUV数据并转换为ByteArray
            val yBuffer = mediaImage.planes[0].buffer
            val uBuffer = mediaImage.planes[1].buffer
            val vBuffer = mediaImage.planes[2].buffer

            val ySize = yBuffer.remaining()
            val uSize = uBuffer.remaining()
            val vSize = vBuffer.remaining()

            val byteArray = ByteArray(ySize + uSize + vSize)
            yBuffer.get(byteArray, 0, ySize)
            vBuffer.get(byteArray, ySize, vSize)
            uBuffer.get(byteArray, ySize + vSize, uSize)

            val source = PlanarYUVLuminanceSource(
                byteArray, mediaImage.width, mediaImage.height,
                0, 0, mediaImage.width, mediaImage.height, false
            )
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

            try {
                val result = reader.decode(binaryBitmap)
                listener(result)
            } catch (e: Exception) {
                listener(null)
            } finally {
                imageProxy.close()
            }
        } else {
            imageProxy.close()
        }
    }
}
