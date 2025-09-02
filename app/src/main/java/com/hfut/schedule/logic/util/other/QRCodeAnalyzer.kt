package com.hfut.schedule.logic.util.other

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.hfut.schedule.application.MyApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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



suspend fun parseQRCode(uri: Uri): String? = withContext(Dispatchers.IO) {
    try {
        // 从 Uri 获取 Bitmap
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(MyApplication.context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.isMutableRequired = true
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE // 避免 HARDWARE
            }
        } else {
            MediaStore.Images.Media.getBitmap(MyApplication.context.contentResolver, uri)
        }

        // 转成 ZXing 需要的 BinaryBitmap
        val intArray = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        val source = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

        // 解码
        val reader = MultiFormatReader().apply {
            setHints(mapOf(DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE)))
        }
        val result = reader.decode(binaryBitmap)

        return@withContext result.text
    } catch (e: Exception) {
        e.printStackTrace()
        return@withContext null
    }
}