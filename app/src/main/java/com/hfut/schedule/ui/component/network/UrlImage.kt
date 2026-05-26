package com.hfut.schedule.ui.component.network

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.ocr.TesseractUtils.recognizeCaptcha
import com.hfut.schedule.logic.util.ocr.preprocessCaptcha
import com.hfut.schedule.logic.util.other.rememberImageState
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.nav.window.ImagePreviewWindow
import com.sharednav.common.util.NoneRoundShape
import com.xah.container.component.base.sharedContainer
import com.xah.container.model.ContainerFilledStrategy
import com.xah.floating.util.LocalFloatingControllerSafely
import com.xah.navigation.util.LocalNavControllerSafely
import kotlinx.coroutines.launch

val DEFAULT_IMAGE_SIZE = 70.dp

@Composable
fun UrlImage(
    url: String,
    modifier: Modifier = Modifier,
    cookie: String? = null,
    enableClick : Boolean = true,
    shape: CornerBasedShape = NoneRoundShape,
    contentScale: ContentScale = ContentScale.Crop, // 决定是否裁剪
    placeholder: Painter = painterResource(R.drawable.ic_launcher_background),
    colorFilter: ColorFilter? = null,
    awaitTransition: Boolean = true,
) {
    val floatingController = LocalFloatingControllerSafely.current
    val navController = LocalNavControllerSafely.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 优化配合SharedNav，延迟加载
    var enableLoad by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        navController?.awaitTransition()
        enableLoad = true
    }

    val imageState =
        if (!awaitTransition || enableLoad) {
            rememberImageState(url, cookie = cookie)
        } else {
            null
        }

    val bitmap = imageState?.value?.asImageBitmap()

    if (bitmap != null) {
        val window = remember(bitmap) { ImagePreviewWindow(bitmap) }
        val interactionSource = remember { MutableInteractionSource() }

        Image(
            bitmap = bitmap,
            colorFilter = colorFilter,
            contentDescription = null,
            contentScale = contentScale,
            modifier = modifier
                .sharedContainer(
                    window.key,
                    containerFilledStrategy = ContainerFilledStrategy.Element,
                    shape = shape
                )
                .let {
                    if(enableClick) {
                        it.clickable(
                            interactionSource = interactionSource,
                            indication = null,
                        ) {
                            floatingController?.push(window)
                                ?: scope.launch {
                                    Starter.startWebUrlInner(context, url, "图片", cookie)
                                }
                        }
                    } else {
                        it
                    }
                }
        )
    } else {
        Image(
            painter = placeholder,
            contentDescription = null,
            contentScale = contentScale,
            modifier = modifier.clip(shape)
        )
    }
}
/*
@Composable
fun UrlImage(
    url : String,
    cookie : String? = null,
    roundSize  : Dp =7.dp,
    width : Dp =70.dp,
    height : Dp = 70.dp,
    useCut : Boolean = true
) {
    val floatingControllerSafely = LocalFloatingControllerSafely.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val modifierCut = if(useCut) {
        Modifier
            .clip(RoundedCornerShape(roundSize))
            .size(width = width,height= height)
            .aspectRatio(1f)
    } else {
        Modifier
            .clip(RoundedCornerShape(roundSize))
            .size(width = width,height= height)
    }
    Box(
        modifierCut
            .clip(RoundedCornerShape(roundSize))
            .size(width = width,height= height)
    ) {
        val imageState = rememberImageState(url, cookie = cookie)
        imageState.value?.let { bitmap ->
            val imageBitmap = bitmap.asImageBitmap()
            val window = ImagePreviewWindow(imageBitmap)
            Image(
                bitmap = imageBitmap,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .sharedContainer(window.key, containerFilledStrategy = ContainerFilledStrategy.Element, shape = RoundedCornerShape(roundSize))
                    .clickable {
                        if(floatingControllerSafely == null) {
                            scope.launch {
                                Starter.startWebView(context,url,"图片",cookie)
                            }
                        } else {
                            floatingControllerSafely.push(ImagePreviewWindow(imageBitmap))
                        }
                    }
                ,
                contentScale = ContentScale.Crop
            )
        } ?:
        Image(
            painterResource(R.drawable.ic_launcher_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun UrlImageNoCrop(
    url : String,
    modifier: Modifier = Modifier,
    cookie : String? = null,
) {
    val floatingControllerSafely = LocalFloatingControllerSafely.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val imageState = rememberImageState(url, cookie = cookie)
    imageState.value?.let { bitmap ->
        val imageBitmap = bitmap.asImageBitmap()
        val window = ImagePreviewWindow(imageBitmap)
        Image(
            bitmap = imageBitmap,
            contentDescription = null,
            modifier = modifier
                .sharedContainer(window.key, containerFilledStrategy = ContainerFilledStrategy.Element, shape = NoneRoundShape)
                .clickable {
                    if(floatingControllerSafely == null) {
                        scope.launch {
                            Starter.startWebView(context,url,"图片",cookie)
                        }
                    } else {
                        floatingControllerSafely.push(window)
                    }
                }
            ,
            contentScale = ContentScale.Fit
        )
    } ?:
    Image(
        painterResource(R.drawable.ic_launcher_background),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}
*/
@Composable
fun UrlImageWithAutoOcr(
    url : String,
    cookie : String? = null,
    roundSize  : Dp =7.dp,
    width : Dp =70.dp,
    height : Dp = 70.dp,
    onResult : (String) -> Unit
) {
    Box(
        Modifier
            .clip(RoundedCornerShape(roundSize))
            .size(width = width, height = height)
    ) {
        val imageState = rememberImageState(url, cookie = cookie)
        imageState.value?.let { bitmap ->
            val preProgressed = preprocessCaptcha(bitmap)
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            val switch_open = prefs.getBoolean("SWITCH_ML",false)
            if(switch_open) {
                onResult(recognizeCaptcha(preProgressed))
            }
        } ?:
        Image(
            painterResource(R.drawable.ic_launcher_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}


