package com.hfut.schedule.ui.utils

//import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController

object NavigateManager {
    data class TransferAnimation(val remark : String,val enter : EnterTransition, val exit : ExitTransition)

    fun turnTo(navController : NavHostController, route : String) {
        navController.navigate(route) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun turnToAndClear(navController: NavHostController, route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.startDestinationId) {
                inclusive = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    const val ANIMATION_SPEED = 400

    private val enterAnimation1 = scaleIn(animationSpec = tween(durationMillis = ANIMATION_SPEED)) +
            expandVertically(expandFrom = Alignment.Top,animationSpec = tween(durationMillis = ANIMATION_SPEED))
    private val exitAnimation1 = scaleOut(animationSpec = tween(durationMillis = ANIMATION_SPEED)) +
            shrinkVertically(shrinkTowards = Alignment.Top,animationSpec = tween(durationMillis = ANIMATION_SPEED))
    val upDownAnimation = TransferAnimation("底栏吸附",enterAnimation1, exitAnimation1)

    private val enterAnimation2 = scaleIn(animationSpec = tween(durationMillis = ANIMATION_SPEED)) +
            expandVertically(expandFrom = Alignment.CenterVertically,animationSpec = tween(durationMillis = ANIMATION_SPEED))
    private val exitAnimation2 = scaleOut(animationSpec = tween(durationMillis = ANIMATION_SPEED)) +
            shrinkVertically(shrinkTowards = Alignment.CenterVertically,animationSpec = tween(durationMillis = ANIMATION_SPEED))
    val centerAnimation = TransferAnimation("向中心运动",enterAnimation2, exitAnimation2)

    private val enterAnimationFade = fadeIn(animationSpec = tween(durationMillis = ANIMATION_SPEED))

    private val exitAnimationFade = fadeOut(animationSpec = tween(durationMillis = ANIMATION_SPEED))

    val fadeAnimation = TransferAnimation("淡入淡出", enterAnimationFade, exitAnimationFade)

    private val enterAnimationNull = fadeIn(animationSpec = tween(durationMillis = 0))

    private val exitAnimationNull = fadeOut(animationSpec = tween(durationMillis = 0))

    val nullAnimation = TransferAnimation("无", enterAnimationNull, exitAnimationNull)


    // 左右动画
    private val enterAnimationLeft = slideInHorizontally(
        initialOffsetX = { fullWidth -> -fullWidth },
        animationSpec = tween(durationMillis = ANIMATION_SPEED)
    )
//    + fadeIn(animationSpec = tween(durationMillis = ANIMATION_SPEED))

    private val exitAnimationLeft = slideOutHorizontally(
        targetOffsetX = { fullWidth -> -fullWidth },
        animationSpec = tween(durationMillis = ANIMATION_SPEED)
    )
//    + fadeOut(animationSpec = tween(durationMillis = ANIMATION_SPEED))

    private val leftToRightAnimation = TransferAnimation("从左向右运动", enterAnimationLeft, exitAnimationLeft)

    private val enterAnimationRight = slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(durationMillis = ANIMATION_SPEED)
    )
//    + fadeIn(animationSpec = tween(durationMillis = ANIMATION_SPEED))

    private val exitAnimationRight = slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(durationMillis = ANIMATION_SPEED)
    )
//    + fadeOut(animationSpec = tween(durationMillis = ANIMATION_SPEED))

    private val rightToLeftAnimation = TransferAnimation("从右向左运动", enterAnimationRight, exitAnimationRight)



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
            else -> fadeAnimation
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
