package com.hfut.schedule.ui.component

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun CameraScan(imageAnalysis: ImageAnalysis, modifier: Modifier = Modifier.fillMaxSize()) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    DisposableEffect(lifecycleOwner) {
        val cameraProvider = cameraProviderFuture.get()

        onDispose {
            // 解绑所有绑定的 UseCase
            cameraProvider.unbindAll()
        }
    }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
//            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
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
}

/*用法
val imageAnalysis = ImageAnalysis.Builder() .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888).build().also {
        it.setAnalyzer(ContextCompat.getMainExecutor(context), QRCodeAnalyzer { result ->
            result?.let {
                // 处理扫描结果
                if(result.text.contains("XXX")) {
                    try {

                        show = false
                    } catch (e:Exception) {
                        println(e)
                        MyToast("解析出错")
                    }
                }
            }
        })
    }

if(show)
    CameraPreview(imageAnalysis)
 */