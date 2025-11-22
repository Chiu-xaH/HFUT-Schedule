package com.hfut.schedule.ui.component.button

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.hfut.schedule.R

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun AnimatedIconButton(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary,
    valueState: Boolean,
    contentDescription: String = "",
    onClick: (() -> Unit)? = null
) {
    val animatedImageVector: AnimatedImageVector =
        AnimatedImageVector.animatedVectorResource(id = R.drawable.avd1) // 正向动画就行，compose 会自己计算反向的部分

    val painter = rememberAnimatedVectorPainter(animatedImageVector, valueState)

    IconButton(
        onClick = onClick ?: {},
        enabled = onClick != null, // 没传就是禁用
        modifier = modifier
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            tint = tint
        )
    }
}
