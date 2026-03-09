package com.xah.container.model

import android.os.Build

sealed interface ContainerFilledStrategy {
    /**
     * 指定颜色填充底部
     */
    data class Color(val color : androidx.compose.ui.graphics.Color) : ContainerFilledStrategy

    /**
     * 竖屏取底部1像素做填充，横屏取右侧1像素做填充（类似OriginOS 6.0）
     * @param spareStrategy Require SDK33+ 若低版本使用此效果则降级为spareStrategy，推荐使用Color方案作为备用
     */
    data class Pixel(val spareStrategy : ContainerFilledStrategy = Clip) : ContainerFilledStrategy

    /**
     * 裁切放大（类似OriginOS 1.0）
     */
    data object Clip : ContainerFilledStrategy

    fun getFinalStrategy(enableShader : Boolean) : ContainerFilledStrategy = when(this) {
        is Pixel -> {
            if(enableShader && CAN_USE_SHADER) {
                this
            } else {
                this.spareStrategy
            }
        }
        else -> this
    }

    companion object {
        val CAN_USE_SHADER = Build.VERSION.SDK_INT >= 33
    }
}
