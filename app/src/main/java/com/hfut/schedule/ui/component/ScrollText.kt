package com.hfut.schedule.ui.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.delay

@Composable
fun ScrollText(text : String) {
    val scrollState = rememberScrollState()
    LaunchedEffect(key1 = text ) {
        delay(500L)
        scrollState.animateScrollTo(scrollState.maxValue)
        delay(4000L)
        scrollState.animateScrollTo(0)
    }

    Text(
        text = text,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.horizontalScroll(scrollState))
}