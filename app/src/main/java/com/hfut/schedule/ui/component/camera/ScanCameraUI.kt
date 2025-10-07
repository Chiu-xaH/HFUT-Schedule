package com.hfut.schedule.ui.component.camera

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.hfut.schedule.logic.util.other.QRCodeAnalyzer
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.xah.uicommon.component.status.LoadingScreen


@Composable
fun ScanQrCode(
    modifier: Modifier = Modifier,
    onResult : (com.google.zxing.Result) -> Unit,
) {
    val enableCameraDynamicRecord by DataStoreManager.enableCameraDynamicRecord.collectAsState(initial = null)

    when(enableCameraDynamicRecord) {
        true -> ScanQrCodeView2(modifier,onResult)
        false -> ScanQrCodeView(modifier,onResult)
        null -> LoadingScreen()
    }
}
@Composable
fun ScanQrCodeView(
    modifier: Modifier = Modifier,
    onResult : (com.google.zxing.Result) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val imageAnalysis =  remember {
        ImageAnalysis.Builder()
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
            .build()
            .also {
                it.setAnalyzer(ContextCompat.getMainExecutor(context), QRCodeAnalyzer { result ->
                    result?.let { onResult(it) }
                })
            }
    }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageAnalysis
                )
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = modifier
    )
    DisposableEffect(Unit) {
        onDispose {
            // 避免 analyzer 持有回调
            imageAnalysis.clearAnalyzer()
            val cameraProvider = ProcessCameraProvider.getInstance(context).get()
            cameraProvider.unbindAll()
        }
    }
}

fun decode(bitmap: Bitmap): com.google.zxing.Result? {
    val intArray = IntArray(bitmap.width * bitmap.height)
    bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
    val source = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
    val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
    return try {
        MultiFormatReader().decode(binaryBitmap)
    } catch (e: Exception) { null }
}

@Composable
fun ScanQrCodeView2(
    modifier: Modifier = Modifier,
    onResult: (com.google.zxing.Result) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    // ImageAnalysis 用于获取每帧
    val imageAnalysis = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
            .build()
    }

    DisposableEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val listener = Runnable {
            val cameraProvider = cameraProviderFuture.get()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context)) { image ->
                try {
                    // 转 Bitmap 并旋转
                    val bmp = image.toBitmap().rotate(image.imageInfo.rotationDegrees.toFloat())
                    bmp.let { bitmap = it.asImageBitmap() }

                    // 扫码
                    decode(image.toBitmap())?.let { result ->
                        onResult(result)
                    }
                } catch (_: Exception) {
                } finally {
                    image.close()
                }
            }

            // 绑定 Camera
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                imageAnalysis
            )
        }

        cameraProviderFuture.addListener(listener, ContextCompat.getMainExecutor(context))

        onDispose {
            imageAnalysis.clearAnalyzer()
            cameraProviderFuture.get().unbindAll()
        }
    }

    Box(modifier) {
        bitmap?.let { img ->
            Image(
                bitmap = img,
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}


// --- Bitmap 扩展函数 ---
fun Bitmap.rotate(degrees: Float): Bitmap {
    if (degrees == 0f) return this
    val matrix = Matrix()
    matrix.postRotate(degrees)
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}
