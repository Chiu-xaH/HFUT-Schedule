package com.hfut.schedule.ui.utils.components

import android.graphics.Bitmap
import android.icu.text.ListFormatter.Width
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.utils.CaptchaRecognizer
import com.hfut.schedule.logic.utils.loadImage

@Composable
fun URLImage(
    url : String,
    cookie : String? = null,
//    canRefresh : Boolean = false,
    roundSize  : Dp =7.dp,
    width : Dp =70.dp,
    height : Dp = 70.dp,
    useCut : Boolean = true
) {
    val modifierCut = if(useCut) Modifier.aspectRatio(1f) else Modifier

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
    var result = ""
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
            CaptchaRecognizer().recognizeCaptcha(bitmap = bitmap, onError = { } , onResult = onResult )
        }
    }
}


