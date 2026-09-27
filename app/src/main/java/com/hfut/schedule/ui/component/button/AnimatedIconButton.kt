package com.hfut.schedule.ui.component.button

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.hfut.schedule.R
import com.kyant.backdrop.Backdrop

@Composable
fun AnimatedExpandIconButton(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary,
    containerColor: Color = Color.Transparent,
    valueState: Boolean,
    onClick: (() -> Unit)? = null
) {
    val animatedImageVector: AnimatedImageVector = AnimatedImageVector.animatedVectorResource(id = R.drawable.ic_anim_expand) // 正向动画就行，compose 会自己计算反向的部分
    val painter = rememberAnimatedVectorPainter(animatedImageVector, valueState)

    IconButton(
        onClick = onClick ?: {},
        enabled = onClick != null, // 没传就是禁用
        modifier = modifier,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = containerColor
        ) // 可传入背景颜色
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = tint
        )
    }
}

@Composable
fun AnimatedExpandLiquidIconButton(
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    valueState: Boolean,
    onClick: (() -> Unit)? = null
) {
    val animatedImageVector: AnimatedImageVector = AnimatedImageVector.animatedVectorResource(id = R.drawable.ic_anim_expand)
    val painter = rememberAnimatedVectorPainter(animatedImageVector, valueState)

    LiquidButton(
        backdrop = backdrop,
        onClick = onClick ?: {},
        enabled = onClick != null,
        modifier = modifier,
        isCircle = true
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = tint
        )
    }
}
