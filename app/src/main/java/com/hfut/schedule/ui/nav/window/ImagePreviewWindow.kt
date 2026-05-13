package com.hfut.schedule.ui.nav.window

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.hfut.schedule.ui.nav.window.base.FloatingWindow
import com.xah.common.ui.style.align.CenterScreen
import com.xah.common.ui.util.text
import com.xah.container.component.base.SharedContent
import com.xah.container.model.ContentStrategy
import com.xah.container.util.NoneRoundShape
import com.xah.floating.util.LocalFloatingController
import kotlinx.coroutines.launch

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
                contentStrategy = ContentStrategy.Layer(isFloating = true),
                shape = NoneRoundShape,
            ) {
                ZoomableImage(bitmap) {
                    floatingController.pop()
                }
            }
        }
    }
}

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
    var imageRect by remember { mutableStateOf<Rect?>(null) }

    var baseImageSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { containerSize = it }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { tap ->
                        imageRect?.let {
                            if (!it.contains(tap)) onBlankTap()
                        }
                    },
                    onDoubleTap = {
                        scope.launch {
                            launch { animatedScale.animateTo(1f) }
                            launch { animatedOffset.animateTo(Offset.Zero) }
                            scale = 1f
                            offset = Offset.Zero
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->

                    val newScale = (scale * zoom).coerceIn(1f, 5f)
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
                .onGloballyPositioned {
                    val pos = it.positionInParent()
                    val size = it.size.toSize()
                    imageRect = Rect(pos, size)
                }
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
//@Composable
//private fun ZoomableImage(
//    bitmap: ImageBitmap,
//    modifier: Modifier = Modifier,
//    onBlankTap: () -> Unit = {}
//) {
//    val scope = rememberCoroutineScope()
//
//    // 用 Animatable 作为唯一真值，手势时 snapTo，松手/双击时 animateTo
//    val animatedScale  = remember { Animatable(1f) }
//    val animatedOffset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
//
//    // 给手势计算用的"快照"引用，避免每帧读 Animatable.value
//    var scale  by remember { mutableStateOf(1f) }
//    var offset by remember { mutableStateOf(Offset.Zero) }
//
//    var imageRect     by remember { mutableStateOf<Rect?>(null) }
//    var containerSize by remember { mutableStateOf(IntSize.Zero) }
//    var displayedSize by remember { mutableStateOf(IntSize.Zero) } // Fit 后的实际渲染尺寸
//
//    // ContentScale.Fit 的实际渲染尺寸：取宽高比中较小的缩放倍率
//    LaunchedEffect(bitmap, containerSize) {
//        if (containerSize.width == 0 || containerSize.height == 0) return@LaunchedEffect
//        val fitScale = minOf(
//            containerSize.width.toFloat()  / bitmap.width,
//            containerSize.height.toFloat() / bitmap.height
//        )
//        displayedSize = IntSize(
//            (bitmap.width  * fitScale).toInt(),
//            (bitmap.height * fitScale).toInt()
//        )
//    }
//
//    Box(
//        modifier = modifier
//            .fillMaxSize()
//            .onSizeChanged { containerSize = it }
//            .pointerInput(Unit) {
//                detectTapGestures(
//                    onTap = { tapOffset ->
//                        val rect = imageRect
//                        if (rect != null && !rect.contains(tapOffset)) onBlankTap()
//                    },
//                    onDoubleTap = {
//                        scope.launch {
//                            // 并发动画，真正有视觉效果
//                            launch { animatedScale.animateTo(1f) }
//                            launch { animatedOffset.animateTo(Offset.Zero) }
//                            scale  = 1f
//                            offset = Offset.Zero
//                        }
//                    }
//                )
//            }
//            .pointerInput(Unit) {
//                detectTransformGestures { _, pan, zoom, _ ->
//                    val newScale = (scale * zoom).coerceIn(1f, 5f)
//                    val scaleChange = newScale / scale
//
//                    // 用展示尺寸判断是否可拖动
//                    val canPanX = displayedSize.width  * newScale > containerSize.width
//                    val canPanY = displayedSize.height * newScale > containerSize.height
//
//                    val filteredPan = Offset(
//                        x = if (canPanX) pan.x else 0f,
//                        y = if (canPanY) pan.y else 0f
//                    )
//
//                    val newOffset = offset * scaleChange + filteredPan
//
//                    offset = clampOffset(newOffset, newScale, displayedSize, containerSize)
//                    scale  = newScale
//
//                    // 手势期间 snap，保持动画值同步
//                    scope.launch {
//                        animatedScale.snapTo(scale)
//                        animatedOffset.snapTo(offset)
//                    }
//                }
//            }
//    ) {
//        Image(
//            bitmap = bitmap,
//            contentDescription = null,
//            contentScale = ContentScale.Fit,
//            modifier = Modifier
//                .onGloballyPositioned { coordinates ->
//                    val position = coordinates.positionInParent()
//                    val size = coordinates.size.toSize()
//                    imageRect = Rect(position, size)
//                }
//                .graphicsLayer {
//                    val baseOffsetX = (containerSize.width  - displayedSize.width)  / 2f
//                    val baseOffsetY = (containerSize.height - displayedSize.height) / 2f
//
//                    scaleX = animatedScale.value   // ← 读 Animatable，双击动画才生效
//                    scaleY = animatedScale.value
//                    translationX = baseOffsetX + animatedOffset.value.x
//                    translationY = baseOffsetY + animatedOffset.value.y
//                }
//        )
//    }
//}

//@Composable
//private fun ZoomableImage(
//    bitmap: ImageBitmap,
//    modifier: Modifier = Modifier,
//    onBlankTap: () -> Unit = {}
//) {
//    val scope = rememberCoroutineScope()
//
//    var scale by remember { mutableStateOf(1f) }
//    var offset by remember { mutableStateOf(Offset.Zero) }
//
//
//    val animatedScale = remember { Animatable(1f) }
//    val animatedOffset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
//
//    var imageRect by remember { mutableStateOf<Rect?>(null) }
//    var containerSize by remember { mutableStateOf(IntSize.Zero) }
//
//    val imageSize = IntSize(bitmap.width,bitmap.height)
//
//    Box(
//        modifier = modifier
//            .fillMaxSize()
//            .onSizeChanged {
//                containerSize = it
//            }
//            .pointerInput(Unit) {
//                detectTapGestures(
//                    onTap = { tapOffset ->
//                        val rect = imageRect
//                        if (rect != null && !rect.contains(tapOffset)) {
//                            onBlankTap()
//                        }
//                    },
//                    onDoubleTap = {
//                        scope.launch {
//                            animatedScale.animateTo(1f)
//                            animatedOffset.animateTo(Offset.Zero)
//                            scale = 1f
//                            offset = Offset.Zero
//                        }
//                    }
//                )
//            }
//            .pointerInput(Unit) {
//                detectTransformGestures { _, pan, zoom, _ ->
//
//                    val newScale = (scale * zoom).coerceIn(1f, 5f)
//                    val scaleChange = newScale / scale
//
//                    // 当前缩放后的尺寸
//                    val scaledWidth = imageSize.width * newScale
//                    val scaledHeight = imageSize.height * newScale
//
//                    // 是否允许拖动
//                    val canPanX = scaledWidth > containerSize.width
//                    val canPanY = scaledHeight > containerSize.height
//
//                    val filteredPan = Offset(
//                        x = if (canPanX) pan.x else 0f,
//                        y = if (canPanY) pan.y else 0f
//                    )
//
//                    val newOffset = (offset + filteredPan) * scaleChange
//
//                    offset = clampOffset(
//                        newOffset,
//                        newScale,
//                        imageSize,
//                        containerSize
//                    )
//
//                    scale = newScale
//                }
//            }
//    ) {
//        var imageSize by remember { mutableStateOf(IntSize.Zero) }
//
//        Image(
//            bitmap = bitmap,
//            contentDescription = null,
//            contentScale = ContentScale.Fit,
//            modifier = Modifier
//                .onGloballyPositioned { coordinates ->
//                    val position = coordinates.positionInParent()
//                    val size = coordinates.size.toSize()
//                    imageRect = Rect(position, size)
//                }
//                .onSizeChanged {
//                    imageSize = it
//                }
//                .graphicsLayer {
//
//                    val baseOffsetX =
//                        (containerSize.width - imageSize.width) / 2f
//                    val baseOffsetY =
//                        (containerSize.height - imageSize.height) / 2f
//
//                    scaleX = scale
//                    scaleY = scale
//
//                    translationX = baseOffsetX + offset.x
//                    translationY = baseOffsetY + offset.y
//                }
//        )
//    }
//}

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