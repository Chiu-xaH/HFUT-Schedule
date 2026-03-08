package com.xah.navigation.anim

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xah.common.ScreenCornerHelper

/** lerp
 * 1. 预测式返回时：
 * 背景从 Background->PredictiveBackground 按predictiveProgress定进度
 * 主体从 Full->PredictiveSelf 按predictiveProgress定进度
 *
 * 2. 正常情况返回时：
 * 原来的背景从 Background->Full 按transition()动画播放
 * 原来的主体从 Full->None 按transition()动画播放
 *
 * 3. 正常情况前进时：
 * 原来的主体变背景，从Full->Background 按transition()动画播放
 * 主体从 None->Full 按transition()动画播放
 */
@Immutable
data class PageEffect(
    val scale: Float,
    val blur: Dp,
    val mask: Float,
    val corner : CornerBasedShape,
    val alpha : Float
) {
    companion object {
        // 上层页面完全展开或背景完全清晰
        val Full = PageEffect(
            scale = 1f,
            blur = 0.dp,
            mask = 0f,
            corner = ScreenCornerHelper.shape,
            alpha = 1f,
        )
        // 上层页面回缩
        val None = PageEffect(
            scale = 0f,
            blur = 20.dp,
            mask = 0f,
            corner = ScreenCornerHelper.shape,
            alpha = 1f
        )
        // 背景 下层页面
        val Background = PageEffect(
            scale = 0.875f,
            blur = 25.dp,
            mask = 0.25f,
            corner = RoundedCornerShape(0.dp),
            alpha = 1f
        )
        val BackgroundWithoutBlur = PageEffect(
            scale = 0.875f,
            blur = 0.dp,
            mask = 0.25f,
            corner = RoundedCornerShape(0.dp),
            alpha = 1f
        )
        val BackgroundWithoutScale = PageEffect(
            scale = 1f,
            blur = 0.dp,
            mask = 0.25f,
            corner = RoundedCornerShape(0.dp),
            alpha = 1f
        )
        // 预测式时的背景 不完全清晰 从 Background->PredictiveBackground scale、blur、dim略减小
        val PredictiveBackground = PageEffect(
            scale = 0.875f,
            blur = 12.5.dp,
            mask = 0.1f,
            corner = RoundedCornerShape(0.dp),
            alpha = 1f
        )
        // 预测式时的前景 不完全变小，露出下层一些背景即可
        val PredictiveSelf = PageEffect(
            scale = 0.875f,
            blur = 0.dp,
            mask = 0f,
            corner = RoundedCornerShape(25.dp),
            alpha = 1f
        )
    }
}