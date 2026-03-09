package com.xah.navigation.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xah.common.ScreenCornerHelper
import com.xah.container.overlay.SharedContainerRoot
import com.xah.container.utils.LocalSharedRegistry
import com.xah.navigation.anim.EffectLevel
import com.xah.navigation.anim.PageEffect
import com.xah.navigation.controller.NavigationController
import com.xah.navigation.controller.NavigationViewModel
import com.xah.navigation.model.Dependencies
import com.xah.navigation.model.action.ActionType
import com.xah.navigation.model.dest.Destination
import com.xah.navigation.utils.LocalNavController
import com.xah.navigation.utils.LocalNavControllerSafely
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.navigation.utils.scaleMirror
import com.xah.navigation.utils.touchEvent
import com.xah.uicommon.util.LogUtil


@Composable
fun rememberNavController(
    startDestination : Destination,
): NavigationController {
    val scope = rememberCoroutineScope()
    val navViewModel: NavigationViewModel = viewModel(factory = NavigationViewModel.Factory())
    val navController = remember(navViewModel) {
        NavigationController(scope, startDestination, navViewModel.stack,null)
    }
    return navController
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun SharedNavHost(
    navController: NavigationController,
    modifier: Modifier = Modifier,
    dependencies: Dependencies = Dependencies(),
    customBackHandler: (@Composable () -> Unit) = { DefaultBackHandler() },
) {
    SharedContainerRoot {
        NavHost(
            navController,
            modifier,
            dependencies,
            customBackHandler
        )
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun SharedNavHost(
    startDestination: Destination,
    modifier: Modifier = Modifier,
    dependencies: Dependencies = Dependencies(),
    customBackHandler: (@Composable () -> Unit) = { DefaultBackHandler() },
) {
    val navController = rememberNavController(startDestination)
    SharedNavHost(navController, modifier, dependencies, customBackHandler)
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NavHost(
    navController: NavigationController,
    modifier: Modifier = Modifier,
    dependencies: Dependencies = Dependencies(),
    customBackHandler: (@Composable () -> Unit) = { DefaultBackHandler() },
) {
    val registry = LocalSharedRegistry.current
    val saveableStateHolder = rememberSaveableStateHolder()
    LaunchedEffect(registry) {
        navController.sharedRegistry = registry
    }

    CompositionLocalProvider(
        LocalNavControllerSafely provides navController,
        LocalNavController provides navController,
        LocalNavDependencies provides dependencies
    ) {
        customBackHandler()

        val transition = navController.navTransition
        val progress = navController.transitionProgress

        // 当 transition 变化时启动动画
        LaunchedEffect(transition,registry.isRunning) {
            navController.animate()
        }

        val visibleEntries = remember(transition) {
            when (transition?.type) {
                ActionType.POP -> listOf(transition.to, transition.from)
                ActionType.PUSH -> listOf(transition.from, transition.to)
                else -> listOf(navController.stack.last())
            }
        }

        val level = navController.transitionLevel
        val enableBlur = navController.enableBlur
        val enableShader = navController.enableShader

        Box(modifier = modifier.fillMaxSize()) {
            visibleEntries.forEach { entry ->
                key(entry.id) {
                    saveableStateHolder.SaveableStateProvider(entry.id) {
                        val isFrom = transition?.from == entry
                        val isTo = transition?.to == entry

                        val animatedProgress = progress.value
                        val underEffect = remember(animatedProgress,level,enableBlur,enableShader) { BackgroundEffect(animatedProgress,level,enableBlur,enableShader) }
                        val upEffect = remember(animatedProgress,level,enableBlur) { ForegroundEffect(animatedProgress,level,enableBlur) }

                        Box(
                            Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    if (transition != null) {
                                        when (transition.type) {
                                            ActionType.PUSH -> {
                                                if (isFrom) { }
                                                if(isTo) { }
                                            }
                                            ActionType.POP -> {
                                                if(isFrom) {
                                                    // 有共享元素
                                                    if(registry.isRunning) {
                                                        alpha = 0f
                                                    }
                                                }
                                                if (isTo) { }
                                            }
                                        }
                                    }
                                }
                                .let {
                                    if (transition != null) {
                                        when (transition.type) {
                                            ActionType.PUSH -> {
                                                if (isFrom) {
                                                    // 背景
                                                    return@let with(underEffect) {
                                                        it.effect()
                                                    }
                                                }
                                                if (isTo) {
                                                    // 目标屏幕
                                                    if(!registry.isRunning) {
                                                        return@let with(upEffect) {
                                                            it.effect()
                                                        }
                                                    }
                                                }
                                            }
                                            ActionType.POP -> {
                                                if (isTo) {
                                                    // 背景
                                                    return@let with(underEffect) {
                                                        it.effect()
                                                    }
                                                }
                                                if (isFrom) {
                                                    // 退出屏幕
                                                    if(!registry.isRunning) {
                                                        return@let with(upEffect) {
                                                            it.effect()
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    return@let it
                                }
                                .touchEvent(
                                    transition == null
                                    // 当返回时，禁用前景；当前进时，禁用背景；当非动画态，启用
                                    // TODO 暂时一刀切，未适配并行动画
//                                    when {
//                                        transition == null -> true
//                                        transition.type == ActionType.POP && isTo -> true
//                                        transition.type == ActionType.POP && isFrom -> false
//                                        transition.type == ActionType.PUSH && isTo -> true
//                                        transition.type == ActionType.PUSH && isFrom -> false
//                                        else -> false
//                                    }
                                )
                        ) {
                            entry.destination.Screen()
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NavHost(
    startDestination: Destination,
    modifier: Modifier = Modifier,
    dependencies: Dependencies = Dependencies(),
    customBackHandler: (@Composable () -> Unit) = { DefaultBackHandler() },
) {
    val navController = rememberNavController(startDestination)

    NavHost(
        navController,
        modifier,
        dependencies,
        customBackHandler
    )
}


// scaleRadio放慢scale的速度
private class BackgroundEffect(animatedProgress : Float,val level: EffectLevel,val enableBlur : Boolean,val enableShader : Boolean) {
    private val effect = PageEffect(
        scale = lerp(
            PageEffect.Full.scale,
            PageEffect.Background.scale,
            animatedProgress
        ),
        blur = lerp(
            PageEffect.Full.blur,
            PageEffect.Background.blur,
            animatedProgress
        ),
        mask = lerp(
            PageEffect.Full.mask,
            PageEffect.Background.mask,
            animatedProgress
        ),
        alpha = lerp(
            PageEffect.Full.alpha,
            PageEffect.Background.alpha,
            animatedProgress
        ),
        corner = lerp(
            PageEffect.Full.corner,
            PageEffect.Background.corner,
            animatedProgress
        )
    )

    private fun Modifier.mask() : Modifier {
        return this.drawWithCache {
            onDrawWithContent {
                drawContent()
                if (effect.mask > 0f) {
                    drawRect(
                        Color.Black.copy(alpha = effect.mask)
                    )
                }
            }
        }
    }

    private fun Modifier.blur() : Modifier {
        return if(enableBlur) {
            this.blur(effect.blur)
        } else {
            this
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun Modifier.scale() : Modifier {
        return if(enableShader) {
            this.scaleMirror(effect.scale)
        } else {
            this.graphicsLayer {
                scaleX = effect.scale
                scaleY = effect.scale
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun Modifier.effect() : Modifier {
        return when(level) {
            EffectLevel.FULL -> {
                this.mask().blur().scale()
            }
            EffectLevel.NO_BLUR -> {
                this.mask().scale()
            }
            EffectLevel.NO_SCALE -> {
                this.mask()
            }
            EffectLevel.NONE -> {
                this.mask()
            }
        }
    }
}

private class ForegroundEffect(animatedProgress : Float,val level: EffectLevel,val enableBlur : Boolean)  {

    private val effect = PageEffect(
        scale = lerp(
            if(level == EffectLevel.NONE) PageEffect.Background.scale else PageEffect.None.scale,
            PageEffect.Full.scale,
            animatedProgress
        ),
        blur = lerp(
            PageEffect.None.blur,
            PageEffect.Full.blur,
            animatedProgress
        ),
        mask = lerp(
            PageEffect.None.mask,
            PageEffect.Full.mask,
            animatedProgress
        ),
        alpha = lerp(
            if(level == EffectLevel.NONE) 0f else PageEffect.None.alpha,
            PageEffect.Full.alpha,
            animatedProgress
        ),
        corner = lerp(
            if(level == EffectLevel.NONE) RoundedCornerShape(ScreenCornerHelper.corner) else RoundedCornerShape(ScreenCornerHelper.corner*2),
            RoundedCornerShape(ScreenCornerHelper.corner),
            animatedProgress
        )
    )


    private fun Modifier.blur() : Modifier {
        return if(enableBlur) {
            this.blur(effect.blur)
        } else {
            this
        }
    }
    private fun Modifier.corner() : Modifier {
        return this.clip(effect.corner)
    }

    private fun Modifier.scale() : Modifier {
        return this.graphicsLayer {
            scaleX = effect.scale
            scaleY = effect.scale
            alpha = effect.alpha
            transformOrigin = TransformOrigin(0.5f,0.275f)
        }
    }

    private fun Modifier.tinyScale() : Modifier {
        return this.graphicsLayer {
            scaleX = effect.scale
            scaleY = effect.scale
            alpha = effect.alpha
        }
    }

    private fun Modifier.tinyCorner() : Modifier {
        return this.clip(RoundedCornerShape(ScreenCornerHelper.corner))
    }

    fun Modifier.effect() : Modifier {
        return when(level) {
            EffectLevel.FULL -> {
                this.blur().scale().corner()
            }
            EffectLevel.NO_BLUR -> {
                this.scale().corner()
            }
            EffectLevel.NO_SCALE -> {
                this.scale().corner()
            }
            EffectLevel.NONE -> {
                this.tinyScale().tinyCorner()
            }
        }
    }
}

// TODO 共享元素过渡时，背景模糊
private class BackgroundEffectWithSharedElement(animatedProgress : Float,val level: EffectLevel) {
    private val effect = PageEffect(
        scale = lerp(
            PageEffect.Full.scale,
            PageEffect.Full.scale,
            animatedProgress
        ),
        blur = lerp(
            PageEffect.Full.blur,
            PageEffect.Background.blur,
            animatedProgress
        ),
        mask = lerp(
            PageEffect.Full.mask,
            PageEffect.Full.mask,
            animatedProgress
        ),
        alpha = lerp(
            PageEffect.Full.alpha,
            PageEffect.Background.alpha,
            animatedProgress
        ),
        corner = lerp(
            PageEffect.Full.corner,
            PageEffect.Background.corner,
            animatedProgress
        )
    )

    private fun Modifier.blur() : Modifier {
        return this.blur(effect.blur)
    }


    fun Modifier.effect() : Modifier {
        return when(level) {
            EffectLevel.FULL -> {
                this.blur()
            }
            EffectLevel.NO_BLUR -> {
                this
            }
            EffectLevel.NO_SCALE -> {
                this
            }
            EffectLevel.NONE -> {
                this
            }
        }
    }
}
// TODO 共享元素过渡时，前景模糊、透明度淡入
private class ForegroundEffectWithSharedElement(animatedProgress : Float,val level: EffectLevel)  {

    private val effect = PageEffect(
        scale = lerp(
            PageEffect.Full.scale,
            PageEffect.Full.scale,
            animatedProgress
        ),
        blur = lerp(
            PageEffect.None.blur,
            PageEffect.Full.blur,
            animatedProgress
        ),
        mask = lerp(
            PageEffect.None.mask,
            PageEffect.Full.mask,
            animatedProgress
        ),
        alpha = lerp(
            0f,
            PageEffect.Full.alpha,
            animatedProgress
        ),
        corner = lerp(
            if(level != EffectLevel.NONE) RoundedCornerShape(ScreenCornerHelper.corner) else RoundedCornerShape(0.dp),
            if(level != EffectLevel.NONE) RoundedCornerShape(ScreenCornerHelper.corner) else RoundedCornerShape(0.dp),
            animatedProgress
        )
    )


    private fun Modifier.blur() : Modifier {
        return this.blur(effect.blur)
    }

    private fun Modifier.corner() : Modifier {
        return this.clip(effect.corner)
    }

    private fun Modifier.alpha() : Modifier {
        return this.graphicsLayer {
            alpha = effect.alpha
        }
    }

    fun Modifier.effect() : Modifier {
        return when(level) {
            EffectLevel.FULL -> {
                this.blur().alpha().corner()
            }
            EffectLevel.NO_BLUR -> {
                this.alpha().corner()
            }
            EffectLevel.NO_SCALE -> {
                this
            }
            EffectLevel.NONE -> {
                this
            }
        }
    }
}


fun lerp(start: CornerBasedShape, stop: CornerBasedShape, fraction: Float): CornerBasedShape = start.lerp(stop,fraction) as CornerBasedShape

//abstract class OnTransition(
//    private val animatedProgress: Float
//) {
//    abstract fun Modifier.onPushFrom() : Modifier
//    abstract fun Modifier.onPushTo() : Modifier
//    abstract fun Modifier.onPopFrom() : Modifier
//    abstract fun Modifier.onPopTo() : Modifier
//}