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
import com.hfut.schedule.logic.utils.loadImage

@Composable
fun URLImage(url : String, roundSize  : Dp =7.dp, size : Dp =70.dp) {
    Box(
        Modifier
            .clip(RoundedCornerShape(roundSize))
            .size(size)
            .aspectRatio(1f)
    ) {
        val imageState = loadImage(url)
        // 如果图片尚未加载，显示占位符
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
