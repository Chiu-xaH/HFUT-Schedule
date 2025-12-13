package com.hfut.schedule.ui.component.screen.pager

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hfut.schedule.logic.util.sys.showToast
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import kotlinx.coroutines.launch

@Composable
fun rememberScrollDirection(state: LazyListState): MutableState<Boolean> {
    // true = 向上滚动，false = 向下滚动，null = 初始无方向
    val direction = remember { mutableStateOf<Boolean>(false) }

    var lastIndex by remember { mutableIntStateOf(state.firstVisibleItemIndex) }
    var lastScrollOffset by remember { mutableIntStateOf(state.firstVisibleItemScrollOffset) }

    LaunchedEffect(state) {
        snapshotFlow { state.firstVisibleItemIndex to state.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                direction.value = when {
                    index > lastIndex -> true   // 向上翻
                    index < lastIndex -> false  // 向下翻
                    offset > lastScrollOffset -> true   // 向上翻
                    offset < lastScrollOffset -> false  // 向下翻
                    else -> direction.value
                }
                lastIndex = index
                lastScrollOffset = offset
            }
    }

    return direction
}
// LazyListState当向下翻时为true，向下翻时为false
// 翻页器
@Composable
fun BoxScope.PageController(
    listState : LazyListState,
    currentPage : Int,
    onNextPage : (Int) -> Unit,
    onPreviousPage : (Int) -> Unit,
    modifier: Modifier = Modifier,
    paddingBottom : Boolean = true,
    paddingSafely : Boolean = true,
    gap : Int = 1,
    range : Pair<Int?,Int?> = Pair(1,null),
    resetPage : Int = range.first ?: 1,
    text : String = "第${currentPage}页",
) {
    // 如果列表无项目，不显示按钮
    val shouldShowButton by rememberScrollDirection(listState)
    BasePageController(
        shouldShowButton = shouldShowButton,
        onClick = {
            listState.animateScrollToItem(0)
        },
        currentPage  = currentPage,
        nextPage = onNextPage,
        previousPage = onPreviousPage,
        modifier = modifier,
        paddingBottom = paddingBottom,
        paddingSafely = paddingSafely,
        gap = gap,
        range = range,
        resetPage = resetPage,
        text = text
    )
}

// 翻页器
@Composable
fun BoxScope.PageController(
    scrollState : ScrollState,
    currentPage : Int,
    onNextPage : (Int) -> Unit,
    onPreviousPage : (Int) -> Unit,
    modifier: Modifier = Modifier,
    paddingBottom : Boolean = true,
    paddingSafely : Boolean = true,
    gap : Int = 1,
    range : Pair<Int?,Int?> = Pair(1,null),
    resetPage : Int = range.first ?: 1,
    text : String = "第${currentPage}页",
) {
    val shouldShowButton by remember {
        derivedStateOf {
            !(scrollState.value == 0 || scrollState.value == scrollState.maxValue)
        }
    }
    BasePageController(
        shouldShowButton = shouldShowButton,
        onClick = {
            scrollState.animateScrollTo(0)
        },
        currentPage  = currentPage,
        nextPage = onNextPage,
        previousPage = onPreviousPage,
        modifier = modifier,
        paddingBottom = paddingBottom,
        paddingSafely = paddingSafely,
        gap = gap,
        range = range,
        resetPage = resetPage,
        text = text
    )
}

@Composable
private fun BoxScope.BasePageController(
    shouldShowButton : Boolean,
    onClick : suspend () -> Unit,
    currentPage : Int,
    nextPage : (Int) -> Unit,
    previousPage : (Int) -> Unit,
    modifier: Modifier = Modifier,
    paddingBottom : Boolean = true,
    paddingSafely : Boolean = true,
    gap : Int = 1,
    range : Pair<Int?,Int?> = Pair(1,null),
    resetPage : Int = range.first ?: 1,
    text : String = "第${currentPage}页",
) {
    require(
        !((range.first != null && resetPage < range.first!!) || (range.second != null && resetPage > range.second!!))
    ) {
        "不合法的resetPage($resetPage),期望在range(${range.first}..${range.second})内"
    }

    val scope = rememberCoroutineScope()
    val angle by animateFloatAsState(
        if(shouldShowButton) -90f else 0f
    )

    val paddingModifier = modifier
        .zIndex(1f)
        .padding(horizontal = APP_HORIZONTAL_DP, vertical = if(paddingSafely) APP_HORIZONTAL_DP else 0.dp)
        .let { if(paddingBottom) it.navigationBarsPadding() else it }


    AnimatedVisibility(
        visible = shouldShowButton == false,
        modifier = paddingModifier.align(Alignment.BottomStart),
        enter = AppAnimationManager.hiddenRightAnimation.enter + AppAnimationManager.centerFadeAnimation.enter,
        exit = AppAnimationManager.hiddenRightAnimation.exit + AppAnimationManager.centerFadeAnimation.exit
    ){
        FloatingActionButton(
            elevation =  FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
            onClick = {
                val terminalPage = currentPage - gap
                if (range.first == null || terminalPage >= range.first!!) {
                    previousPage(terminalPage)
                }
            },
        ) { Icon(Icons.Filled.ArrowBack, "Add Button") }

    }
    AnimatedVisibility(
        visible = shouldShowButton == false,
        modifier = paddingModifier.align(Alignment.BottomCenter),
        enter =  AppAnimationManager.hiddenRightAnimation.enter + AppAnimationManager.centerFadeAnimation.enter,
        exit =  AppAnimationManager.hiddenRightAnimation.exit + AppAnimationManager.centerFadeAnimation.exit
    ){
        ExtendedFloatingActionButton(
            elevation =  FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
            onClick = {
                nextPage(resetPage)
            },
        ) { Text(text = text) }

    }
    FloatingActionButton(
        elevation =  FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
        onClick = {
            if(!shouldShowButton) {
                val terminalPage = currentPage + gap
                if (range.second == null || terminalPage <= range.second!!) {
                    nextPage(currentPage+gap)
                }
            } else {
                scope.launch {
                    onClick()
                }
            }
        },
        modifier = paddingModifier.align(Alignment.BottomEnd),
    ) {
        Icon(Icons.Filled.ArrowForward, "Add Button", modifier = Modifier.rotate(angle))
    }
}

@Composable
fun PaddingForPageControllerButton() = Spacer(Modifier.height(85.dp))

