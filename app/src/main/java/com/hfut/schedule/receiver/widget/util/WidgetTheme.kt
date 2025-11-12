package com.hfut.schedule.receiver.widget.util

import androidx.compose.runtime.Composable
import androidx.glance.GlanceTheme

@Composable
fun WidgetTheme(
    content: @Composable () -> Unit
) {
    GlanceTheme (
        content = content
    )
}