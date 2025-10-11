package com.xah.uicommon.component.text

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import kotlinx.coroutines.delay

@Composable
fun ScrollText(
    text : String,
    modifier: Modifier = Modifier,
    textDecoration: TextDecoration? = null,
    style : TextStyle =  LocalTextStyle. current,
    color : Color = Color.Unspecified,
) {
    val scrollState = rememberScrollState()
    LaunchedEffect(key1 = text ) {
        delay(500L)
        scrollState.animateScrollTo(scrollState.maxValue)
        delay(4000L)
        scrollState.animateScrollTo(0)
    }

    Text(
        text = text,
        modifier = modifier.horizontalScroll(scrollState),
        textDecoration = textDecoration,
        style = style,
        color = color
    )
}