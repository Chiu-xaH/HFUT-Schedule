package com.hfut.schedule.logic.utils

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class CaptchaRecognizer {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    /**
     * 识别验证码图片（异步回调）
     * @param bitmap 需要识别的验证码图片
     * @param onResult 识别成功回调
     * @param onError 识别失败回调
     */
    fun recognizeCaptcha(
        bitmap: Bitmap,
        onResult: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val image = InputImage.fromBitmap(bitmap, 0)

        recognizer.process(image)
            .addOnSuccessListener { result: Text ->
                val extractedText = result.text.trim()
//                Log.d("CaptchaRecognizer", "识别结果: $extractedText")
                onResult(extractedText)
            }
            .addOnFailureListener { e ->
//                Log.e("CaptchaRecognizer", "验证码识别失败", e)
                onError(e)
            }
    }
}

