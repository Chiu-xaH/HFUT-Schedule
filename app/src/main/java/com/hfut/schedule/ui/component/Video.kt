package com.hfut.schedule.ui.component

import android.content.Context
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.view.Surface
import android.view.TextureView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.hfut.schedule.R
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.util.webview.isThemeDark
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.mirror.shader.scaleMirror
import com.xah.mirror.style.mask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


@Composable
fun SimpleVideo(
    filePath: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true,
    mute: Boolean = true,
    loop: Boolean = true,
    aspectRatio: Float? = null
) {
    val mediaPlayer = remember { MediaPlayer() }
    // 释放
    DisposableEffect(filePath) {
        mediaPlayer.setDataSource(filePath)
        mediaPlayer.isLooping = loop
        mediaPlayer.setOnPreparedListener { mp ->
            if (mute) mp.setVolume(0f, 0f)
            if (autoPlay) mp.start()
        }
        mediaPlayer.prepareAsync()

        onDispose {
            try {
                mediaPlayer.stop()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mediaPlayer.release()
        }
    }

    var showButton by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(autoPlay) }
    val blur by animateDpAsState(
        if(!isPlaying) 10.dp else 0.dp
    )
    val scale by animateFloatAsState(
        if(!isPlaying) 0.8f else 1f
    )
    LaunchedEffect(showButton,isPlaying) {
        if(showButton && isPlaying) {
            delay(5000L)
            showButton = false
        } else if(!isPlaying && !showButton) {
            showButton = true
        }
    }

    val backdrop = rememberLayerBackdrop()
    Box(
        modifier = modifier.clickable {
            showButton = !showButton
        }
    ) {
        AnimatedVisibility(
            visible = showButton,
            enter = scaleIn(initialScale = 1.5f) + fadeIn(),
            exit = fadeOut(targetAlpha = 1.5f) + fadeOut(),
            modifier = Modifier
                .align(Alignment.Center)
                .zIndex(2f)
        ) {
            LiquidButton(
                onClick = {
                    if(isPlaying) {
                        mediaPlayer.pause()
                    } else {
                        mediaPlayer.start()
                        showButton = false
                    }
                    isPlaying = mediaPlayer.isPlaying
                },
                surfaceColor = MaterialTheme.colorScheme.surface.copy(.45f),
                backdrop = backdrop,
                isCircle = true,
            ) {
                Icon(painterResource(
                    if(!isPlaying)
                        R.drawable.play_arrow
                    else
                        R.drawable.pause
                ),null)
            }
        }
        AndroidView(
            factory = { ctx ->
                TextureView(ctx).apply {
                    surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                        override fun onSurfaceTextureAvailable(
                            surfaceTexture: SurfaceTexture,
                            width: Int,
                            height: Int
                        ) {
                            mediaPlayer.setSurface(Surface(surfaceTexture))
                        }

                        override fun onSurfaceTextureSizeChanged(
                            surface: SurfaceTexture,
                            width: Int,
                            height: Int
                        ) = Unit

                        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                            mediaPlayer.setSurface(null)
                            return true
                        }

                        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) = Unit
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .let { m ->
                    aspectRatio?.let { ratio -> m.aspectRatio(ratio) } ?: m
                }
                .backDropSource(backdrop)
                // 深色模式压暗
                .mask(
                    color = Color.Black,
                    targetAlpha = 0.3f,
                    show = isThemeDark()
                )
                .blur(blur)
                .scaleMirror(scale)
        )
    }
}


/**
 * 检查视频文件是否存在，不存在则自动下载。
 *
 * @param context 上下文
 * @param fileName 文件名
 * @param downloadUrl 下载链接
 * @return 本地视频路径或 null（下载失败）
 */
suspend fun checkOrDownloadVideo(
    context: Context,
    fileName: String,
    downloadUrl: String
): String? = withContext(Dispatchers.IO) {
    val dir = File(context.getExternalFilesDir(null), "videos")
    if (!dir.exists()) dir.mkdirs()

    val videoFile = File(dir, fileName)

    // 已存在 直接返回路径
    if (videoFile.exists()) return@withContext videoFile.absolutePath

    val client = OkHttpClient()
    val request = Request.Builder().url(downloadUrl).build()

    try {
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return@withContext null

            response.body?.byteStream()?.use { input ->
                FileOutputStream(videoFile).use { output ->
                    input.copyTo(output)
                }
            }

            // 下载完成 返回路径
            return@withContext videoFile.absolutePath
        }
    } catch (e: IOException) {
        e.printStackTrace()
        videoFile.delete() // 删除半下载文件
        return@withContext null
    }
}
