package com.hfut.schedule.ui.utils.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.logic.utils.ocr.TesseractUtils.recognizeCaptcha
import com.hfut.schedule.logic.utils.loadImage


@Composable
fun URLImage(
    url : String,
    cookie : String? = null,
    roundSize  : Dp =7.dp,
    width : Dp =70.dp,
    height : Dp = 70.dp,
    useCut : Boolean = true
) {
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
        val imageState = loadImage(url, cookie = cookie)
        imageState.value?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun URLImageWithOCR(
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
            .size(width = width,height= height)
    ) {
        val imageState = loadImage(url, cookie = cookie)
        imageState.value?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            val switch_open = prefs.getBoolean("SWITCH_ML",false)
            if(switch_open) {
                    val result = recognizeCaptcha(bitmap)
                    onResult(result)
            }
        }
    }
}


