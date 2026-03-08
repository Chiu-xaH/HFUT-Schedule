package com.xah.container.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import com.xah.container.container.pixelExtension
import com.xah.container.model.ContainerFilledStrategy
import com.xah.container.utils.LocalSharedRegistry
import kotlin.math.roundToInt

@Composable
fun SharedContainerOverlay() {
    val registry = LocalSharedRegistry.current
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    val extensionDouble = registry.extensionDouble

    registry.runningStates.forEach { state ->
        val progress = state.animation.value

        if (state.containerRect != null && state.contentRect != null) {
            val container = state.containerRect!!
            val content = state.contentRect!!

            val safelyProgress =  (progress * registry.speedUpRadio).coerceIn(0f,1f)

            val parent = registry.rectInterpolator(progress, container, content)
            val contentAlpha = lerp(0f,1f,safelyProgress)
            val corner = lerp(state.containerCorner,state.contentCorner,safelyProgress)

            val containerFilledStrategy = state.containerFilledStrategy.getFinalStrategy(registry.enableShader)

            /**150,90 2340,1080
             * container.width/content.width 12
             * container.height/content.height 15.6
             */
            val heightW = container.height/content.height
            val widthW = container.width/content.width
            val isHorizontal = if(heightW > widthW) {
                // 左右填充
                true
            } else if(heightW < widthW) {
                // 上下填充
                false
            } else {
                isLandscape
            }

            Box(
                modifier = Modifier
                    .offset { IntOffset(parent.left.roundToInt(), parent.top.roundToInt()) }
                    .size(
                        with(density) { parent.width.toDp() },
                        with(density) { parent.height.toDp() }
                    )
                    .clip(corner)
                    .background(
                        when(containerFilledStrategy) {
                            // 如果出现了黑色边界，说明SharedContainer里面的Content可能不是0圆角的矩形，导致取像素、裁切出现空缺，请把圆角裁剪挪到SharedContainer的corner参数中，里面的内容不要裁切任何圆角！
                            is ContainerFilledStrategy.Pixel -> Color.Black
                            is ContainerFilledStrategy.Clip -> Color.Black
                            is ContainerFilledStrategy.Color -> containerFilledStrategy.color
                        }
                    )
            ) {
                // 容器
                Box {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.align(
                            if(isHorizontal) {
                                if(containerFilledStrategy is ContainerFilledStrategy.Clip) {
                                    Alignment.CenterStart
                                } else {
                                    if(!extensionDouble) {
                                        Alignment.TopStart
                                    } else {
                                        Alignment.TopCenter
                                    }
                                }
                            } else {
                                if(containerFilledStrategy is ContainerFilledStrategy.Clip) {
                                    Alignment.TopCenter
                                } else {
                                    if(!extensionDouble) {
                                        Alignment.TopCenter
                                    } else {
                                        Alignment.CenterStart
                                    }
                                }
                            }
                        )) {
                            when(containerFilledStrategy) {
                                is ContainerFilledStrategy.Clip -> {
                                    // 对state.containerLayout竖直裁切填满父容器
                                    Box(modifier = Modifier
                                        .drawWithCache {
                                            onDrawWithContent {
                                                val layer = state.containerLayer ?: return@onDrawWithContent
                                                val scale = if(isHorizontal) {
                                                    parent.width / container.width
                                                } else {
                                                    parent.height / container.height
                                                }
                                                withTransform({
                                                    scale(scale, scale)
                                                    if(isHorizontal) {
                                                        translate(left = 0f , top = -container.height/2f)
                                                    } else {
                                                        translate(left = -container.width/2f , top = 0f)
                                                    }
                                                }) {
                                                    drawLayer(layer)
                                                }
                                            }
                                        }
                                    )
                                }
                                else -> {
                                    // 填充
                                    Box(modifier = Modifier
                                        .drawWithCache {
                                            onDrawWithContent {
                                                val layer = state.containerLayer ?: return@onDrawWithContent
                                                val scale = if(!isHorizontal) {
                                                    parent.width / container.width
                                                } else {
                                                    parent.height / container.height
                                                }
                                                withTransform({
                                                    scale(scale, scale)
                                                    if(!extensionDouble) {
                                                        if(isHorizontal) {
                                                            translate(left = 0f , top = 0f)
                                                        } else {
                                                            translate(left = -container.width/2f , top = 0f)
                                                        }
                                                    } else {
                                                        if(isHorizontal) {
                                                            translate(left = -container.width/2 , top = 0f)
                                                        } else {
                                                            translate(left = 0f , top = -container.height/2f)
                                                        }
                                                    }
                                                }) {
                                                    drawLayer(layer)
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                    // 使用延展填充
                    if(containerFilledStrategy is ContainerFilledStrategy.Pixel) {
                        val layer = state.containerLayerForPixel
                        layer?.let {
                            Box(
                                modifier = Modifier
                                    .zIndex(-1f)
                                    .graphicsLayer {
                                        val scale = if(!isHorizontal) {
                                            parent.width / container.width
                                        } else {
                                            parent.height / container.height
                                        }
                                        scaleX = scale
                                        scaleY = scale
                                    }
                                    .pixelExtension(it,container,isHorizontal,extensionDouble)
                            )
                        }
                    }
                }
                // 内容始终透明度淡入淡出
                Box(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.align(
                        if(extensionDouble) {
                            Alignment.Center
                        } else {
                            Alignment.TopStart
                        }
                    )) {
                        Box(
                            modifier = Modifier.drawWithContent {
                                val layer = state.contentLayer ?: return@drawWithContent
                                val scale = if(isHorizontal) {
                                    parent.height / content.height
                                } else {
                                    parent.width / content.width
                                }

                                withTransform({
                                    scale(scale, scale)
                                    if(extensionDouble) {
                                        translate(left = -content.width/2f , top = -content.height/2f)
                                    } else {
                                        translate(left = 0f, top = 0f)
                                    }
                                }) {
                                    layer.alpha = contentAlpha
                                    drawLayer(layer)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}


fun lerp(start: CornerBasedShape, stop: CornerBasedShape, fraction: Float): CornerBasedShape = start.lerp(stop,fraction) as CornerBasedShape
