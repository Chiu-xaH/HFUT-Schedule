package com.hfut.schedule.ui.util

//import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.xah.transition.state.TransitionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

object AppAnimationManager {
    data class ControlCenterBackgroundStyle(
        val blurRadius : Dp,
        val scale : Float,
        val alpha : Float
    )
    val blurControlCenterBackgroundStyle = ControlCenterBackgroundStyle(
        blurRadius = 37.5.dp,
        scale = 0.875f,
        alpha = 0.35f
    )
    val noBlurControlCenterBackgroundStyle = ControlCenterBackgroundStyle(
        blurRadius = 0.dp,
        scale = blurControlCenterBackgroundStyle.scale - 0.025f,
        alpha = 0.925f
    )
    fun getControlCenterBackgroundStyle(motionBlur : Boolean) : ControlCenterBackgroundStyle =
        if(motionBlur) blurControlCenterBackgroundStyle else noBlurControlCenterBackgroundStyle
    data class TransferAnimation(val remark : String,val enter : EnterTransition, val exit : ExitTransition)
    // 全局动画速度 毫秒
    var ANIMATION_SPEED = 400
    const val CONTROL_CENTER_ANIMATION_SPEED = 550
//    const val CONTROL_CENTER_BLUR_RADIUS = 37.5

    private suspend fun setAnimationSpeed() : Int {
        val speedCode = DataStoreManager.animationSpeedType.first()
        val speed = DataStoreManager.AnimationSpeed.entries.find { it.code == speedCode }?.speed ?: ANIMATION_SPEED
        return speed
    }
    @OptIn(ExperimentalSharedTransitionApi::class)
    suspend fun updateAnimationSpeed() = withContext(Dispatchers.IO) {
        ANIMATION_SPEED = setAnimationSpeed()
//        if(ANIMATION_SPEED == DataStoreManager.AnimationSpeed.NORMAL.code) {
//            TransitionState.curveStyle.speedMs = DefaultTransitionStyle.DEFAULT_ANIMATION_SPEED
//            TransitionState.curveStyle.boundsTransform = DefaultTransitionStyle.defaultBoundsTransform
//        } else {
//            TransitionState.curveStyle.speedMs = ANIMATION_SPEED
//            TransitionState.curveStyle.boundsTransform = getCenterBoundsTransform()
//        }
    }


//    @OptIn(ExperimentalSharedTransitionApi::class)
//    fun getCenterBoundsTransform() = BoundsTransform { _, _ ->//FastOutSlowInEasing
//        tween(durationMillis = TransitionState.curveStyle.speedMs, easing = FastOutSlowInEasing)
//    }

    private val enterAnimation1 =
        scaleIn(animationSpec = tween(durationMillis = ANIMATION_SPEED)) +
            expandVertically(expandFrom = Alignment.Top,animationSpec = tween(durationMillis = ANIMATION_SPEED))
    private val exitAnimation1 =
        scaleOut(animationSpec = tween(durationMillis = ANIMATION_SPEED)) +
            shrinkVertically(shrinkTowards = Alignment.Top,animationSpec = tween(durationMillis = ANIMATION_SPEED))
    val upDownAnimation = TransferAnimation("底栏吸附",enterAnimation1, exitAnimation1)

    private val enterAnimation11 = scaleIn(animationSpec = tween(durationMillis = ANIMATION_SPEED)) +
            expandVertically(expandFrom = Alignment.Bottom,animationSpec = tween(durationMillis = ANIMATION_SPEED))
    private val exitAnimation11 = scaleOut(animationSpec = tween(durationMillis = ANIMATION_SPEED)) +
            shrinkVertically(shrinkTowards = Alignment.Bottom,animationSpec = tween(durationMillis = ANIMATION_SPEED))
    val downUpAnimation = TransferAnimation("向上吸附",enterAnimation11, exitAnimation11)

    private const val SPRING = 0.875f
    private val enterAnimation2 = scaleIn(animationSpec =  tween(durationMillis = ANIMATION_SPEED, easing = LinearOutSlowInEasing), initialScale = SPRING) + fadeIn(animationSpec = tween(durationMillis = ANIMATION_SPEED/2))

    private val exitAnimation2 = scaleOut(animationSpec =  tween(durationMillis = ANIMATION_SPEED,easing = LinearOutSlowInEasing), targetScale = SPRING) + fadeOut(animationSpec = tween(durationMillis = ANIMATION_SPEED/2))

    val centerAnimation = TransferAnimation("向中心运动",enterAnimation2, exitAnimation2)

    private val enterAnimation5 = scaleIn(animationSpec =  tween(durationMillis = ANIMATION_SPEED, easing = LinearOutSlowInEasing))

    private val exitAnimation5 = scaleOut(animationSpec =  tween(durationMillis = ANIMATION_SPEED,easing = LinearOutSlowInEasing))

    val centerFadeAnimation = TransferAnimation("向中心完全运动",enterAnimation5, exitAnimation5)

    private val enterAnimationFade = fadeIn(animationSpec = tween(durationMillis = ANIMATION_SPEED))

    private val exitAnimationFade = fadeOut(animationSpec = tween(durationMillis = ANIMATION_SPEED))

    val fadeAnimation = TransferAnimation("淡入淡出", enterAnimationFade, exitAnimationFade)

    private val enterAnimationNull = fadeIn(animationSpec = tween(durationMillis = 0))

    private val exitAnimationNull = fadeOut(animationSpec = tween(durationMillis = 0))

    val nullAnimation = TransferAnimation("无", enterAnimationNull, exitAnimationNull)

    private val enterRightAnimation = slideInHorizontally(
        initialOffsetX = { it }, // 从右侧进入
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    )
    private val exitRightAnimation = slideOutHorizontally(
        targetOffsetX = { it }, // 向右侧隐藏
        animationSpec = tween(durationMillis = ANIMATION_SPEED)
    )
    val hiddenRightAnimation = TransferAnimation("向右侧边隐藏", enterRightAnimation, exitRightAnimation)

    private val enterLeftAnimation = slideInHorizontally(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
    private val exitLeftAnimation = slideOutHorizontally(animationSpec = tween(durationMillis = ANIMATION_SPEED))
    val hiddenLeftAnimation = TransferAnimation("向左侧边隐藏", enterRightAnimation, exitRightAnimation)



    // 左右动画
    private val enterAnimationLeft = slideInHorizontally(
        initialOffsetX = { fullWidth -> -fullWidth },
        animationSpec = tween(durationMillis = ANIMATION_SPEED)
    )
    private val exitAnimationLeft = slideOutHorizontally(
        targetOffsetX = { fullWidth -> -fullWidth },
        animationSpec = tween(durationMillis = ANIMATION_SPEED)
    )

    private val leftToRightAnimation = TransferAnimation("从左向右运动", enterAnimationLeft, exitAnimationLeft)

    private val enterAnimationRight = slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(durationMillis = ANIMATION_SPEED)
    )

    private val exitAnimationRight = slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(durationMillis = ANIMATION_SPEED)
    )

    private val rightToLeftAnimation = TransferAnimation("从右向左运动", enterAnimationRight, exitAnimationRight)


    private val enterAnimationBottom = slideInVertically(
        initialOffsetY = { fullWidth -> fullWidth },
        animationSpec = tween(durationMillis = ANIMATION_SPEED)
    )

    private val exitAnimationBottom = slideOutVertically(
        targetOffsetY = { fullWidth -> fullWidth },
        animationSpec = tween(durationMillis = ANIMATION_SPEED)
    )

    val toBottomAnimation = TransferAnimation("向下运动", enterAnimationBottom, exitAnimationBottom)

    private val enterAnimationTop = slideInVertically(
        initialOffsetY = { fullWidth -> -fullWidth },
        animationSpec = tween(durationMillis = ANIMATION_SPEED)
    )

    private val exitAnimationTop = slideOutVertically(
        targetOffsetY = { fullWidth -> -fullWidth },
        animationSpec = tween(durationMillis = ANIMATION_SPEED)
    )

    val toTopAnimation = TransferAnimation("向上运动", enterAnimationTop, exitAnimationTop)





    var currentPage = 0 // 默认值
    /*
    用法
    var targetPage by remember { mutableStateOf(first) }
    // 保存上一页页码 用于决定左右动画
    LaunchedEffect(targetPage) {
        currentPage = targetPage.page
    }
    onclick按钮赋予点击：
    when(item) {
                                    items[0] -> targetPage = COURSES
                                    items[1] -> targetPage = FOCUS
                                    items[2] -> targetPage = SEARCH
                                    items[3] -> targetPage = SETTINGS
                                }
                                if (!selected) {
                                    turnToAndClear(navController,route)
                                }
     */

    fun getLeftRightAnimation(targetPage : Int) : TransferAnimation {
        val enterAnimation3 = if (currentPage > targetPage) {
            leftToRightAnimation.enter
        } else {
            rightToLeftAnimation.enter
        }
        val exitAnimation3 = if (currentPage > targetPage) {
            rightToLeftAnimation.exit
        } else {
            leftToRightAnimation.exit
        }
        return TransferAnimation("左右运动",enterAnimation3, exitAnimation3)
    }

    fun getAnimationType(index : Int,targetPage: Int = 0) : TransferAnimation {
        return when(index) {
            AnimationTypes.UpDownAnimation.code -> upDownAnimation
            AnimationTypes.CenterAnimation.code -> centerAnimation
            AnimationTypes.LeftRightAnimation.code -> getLeftRightAnimation(targetPage)
            AnimationTypes.FadeAnimation.code -> fadeAnimation
            AnimationTypes.NullAnimation.code -> nullAnimation
            else -> centerAnimation
        }
    }

    enum class AnimationTypes(val code : Int) {
        UpDownAnimation(0),
        CenterAnimation(1),
        LeftRightAnimation(2),
        FadeAnimation(3),
        NullAnimation(4)
    }
}
