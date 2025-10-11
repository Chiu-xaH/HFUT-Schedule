package com.xah.transition.style

import androidx.compose.ui.Modifier


// 绘制自身
expect fun Modifier.scaleMirror(
    scale: Float = 1f,
): Modifier
