package com.hfut.schedule.ui.nav.window

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.PermissionSet
import com.hfut.schedule.ui.component.dialog.LittleDialog
import com.hfut.schedule.ui.nav.window.base.FloatingWindow
import com.hfut.schedule.ui.screen.xwx.saveImageToFile
import com.xah.common.ui.style.align.CenterScreen
import com.xah.common.ui.util.text
import com.xah.container.component.base.SharedContent
import com.xah.container.model.ContentStrategy
import com.sharednav.common.helper.NoneRoundShape
import com.xah.floating.util.LocalFloatingController
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

/** 图片预览器交互：
 * 1. 单击空白处收起预览
 * 2. 双指捏合图片缩放（从1x到5x）
 * 3. 当图片位于1x时，双击图片放大到2x
 * 4. 当图片不位于1x时，双击图片缩小到1x
 * 5. 长按图片弹窗菜单（保存）
 */
data class ImagePreviewWindow(
    val bitmap : ImageBitmap
) : FloatingWindow() {
    override val key = "${KEY}_${bitmap.hashCode()}"

    override val title = text("图片预览")

    companion object {
        const val KEY = "image_preview"
    }

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    override fun BoxScope.Content() {
        val floatingController = LocalFloatingController.current
        CenterScreen {
            SharedContent(
                key = key,
                contentStrategy = ContentStrategy.Shared(keepShowContainer = false),
                shape = NoneRoundShape,
            ) {
                ZoomableImage(bitmap) {
                    floatingController.pop()
                }
            }
        }
    }
}

private const val DOUBLE_TAP_SCALE = 2f
private const val MAX_SCALE = 5f

@Composable
private fun ZoomableImage(
    bitmap: ImageBitmap,
    modifier: Modifier = Modifier,
    onBlankTap: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    val animatedScale = remember { Animatable(1f) }
    val animatedOffset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    var baseImageSize by remember { mutableStateOf(IntSize.Zero) }
    val left = (containerSize.width - baseImageSize.width) / 2f + offset.x
    val top = (containerSize.height - baseImageSize.height) / 2f + offset.y

    val imageRect = Rect(
        Offset(left, top),
        baseImageSize.toSize()
    )

    var displayDialog by remember { mutableStateOf(false) }
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val activity = LocalActivity.current

    if(displayDialog) {
        LittleDialog(
            onDismissRequest = { displayDialog = false },
            onConfirmation = {
                scope.launch {
                    // 保存图片
                    activity?.let { PermissionSet.checkAndRequestStoragePermission(it) }
                    saveImageToFile(bitmap.asAndroidBitmap())
                    displayDialog = false
                }
            },
            hazeState = hazeState,
            dialogText = "是否保存此图片"
        )
    }

    Box(
        modifier = modifier
            .hazeSource(hazeState)
            .fillMaxSize()
            .onSizeChanged { containerSize = it }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { tap ->
                        if (!imageRect.contains(tap)) {
                            onBlankTap()
                        }
                    },
                    onDoubleTap = { tap ->
                        scope.launch {
                            if (scale == 1f) {
                                val targetScale = DOUBLE_TAP_SCALE
                                val center = Offset(
                                    containerSize.width / 2f,
                                    containerSize.height / 2f
                                )
                                val targetOffset = clampOffset(
                                    offset = (center - tap) * (targetScale - 1f),
                                    scale = targetScale,
                                    imageSize = baseImageSize,
                                    containerSize = containerSize
                                )
                                scale = targetScale
                                offset = targetOffset

                                launch {
                                    animatedScale.animateTo(targetScale)
                                }
                                launch {
                                    animatedOffset.animateTo(targetOffset)
                                }

                            } else {
                                scale = 1f
                                offset = Offset.Zero

                                launch {
                                    animatedScale.animateTo(1f)
                                }
                                launch {
                                    animatedOffset.animateTo(Offset.Zero)
                                }
                            }
                        }
                    },
                    onLongPress = { tap ->
                        if (imageRect.contains(tap)) {
                            displayDialog = true
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->

                    val newScale = (scale * zoom).coerceIn(1f, MAX_SCALE)
                    val scaleChange = newScale / scale

                    val scaledWidth = baseImageSize.width * newScale
                    val scaledHeight = baseImageSize.height * newScale

                    val canPanX = scaledWidth > containerSize.width
                    val canPanY = scaledHeight > containerSize.height

                    val filteredPan = Offset(
                        if (canPanX) pan.x else 0f,
                        if (canPanY) pan.y else 0f
                    )

                    val newOffset = offset * scaleChange + filteredPan

                    offset = clampOffset(
                        newOffset,
                        newScale,
                        baseImageSize,
                        containerSize
                    )
                    scale = newScale

                    scope.launch {
                        animatedScale.snapTo(scale)
                        animatedOffset.snapTo(offset)
                    }
                }
            }
    ) {
        val aspectRatio = bitmap.width.toFloat() / bitmap.height

        Image(
            bitmap = bitmap,
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio)
                .onSizeChanged {
                    baseImageSize = it
                }
//                .onGloballyPositioned {
//                    val pos = it.positionInParent()
//                    val size = it.size.toSize()
//                    imageRect = Rect(pos, size)
//                }
                .graphicsLayer {

                    val baseOffsetX =
                        (containerSize.width - baseImageSize.width) / 2f
                    val baseOffsetY =
                        (containerSize.height - baseImageSize.height) / 2f

                    scaleX = animatedScale.value
                    scaleY = animatedScale.value

                    translationX = baseOffsetX + animatedOffset.value.x
                    translationY = baseOffsetY + animatedOffset.value.y
                }
        )
    }
}

/**
 * 边界限制，防止拖出留白
 */
private fun clampOffset(
    offset: Offset,
    scale: Float,
    imageSize: IntSize,
    containerSize: IntSize
): Offset {

    val scaledWidth = imageSize.width * scale
    val scaledHeight = imageSize.height * scale

    val maxX = ((scaledWidth - containerSize.width) / 2f).coerceAtLeast(0f)
    val maxY = ((scaledHeight - containerSize.height) / 2f).coerceAtLeast(0f)

    return Offset(
        x = offset.x.coerceIn(-maxX, maxX),
        y = offset.y.coerceIn(-maxY, maxY)
    )
}