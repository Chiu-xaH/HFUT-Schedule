package com.hfut.schedule.ui.component.screen.pager

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.showToast
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.ui.util.toBottomExit
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
    paddingBottom : Boolean = true,
    nextPage : (Int) -> Unit,
    previousPage : (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // 如果列表无项目，不显示按钮
    val shouldShowButton by rememberScrollDirection(listState)
    val scope = rememberCoroutineScope()
    val angle by animateFloatAsState(
        if(shouldShowButton) -90f else 0f
    )
    AnimatedVisibility(
        visible = shouldShowButton == false,
        modifier = modifier.align(Alignment.BottomStart).padding(horizontal = APP_HORIZONTAL_DP, vertical = APP_HORIZONTAL_DP).let { if(paddingBottom) it.navigationBarsPadding() else it },
        enter = AppAnimationManager.hiddenRightAnimation.enter + AppAnimationManager.centerFadeAnimation.enter,
        exit = AppAnimationManager.hiddenRightAnimation.exit + AppAnimationManager.centerFadeAnimation.exit
        ){
        FloatingActionButton(
            onClick = {
                if (currentPage > 1) {
                    previousPage(currentPage-1)
                } else {
                    showToast("第一页")
                }
            },
        ) { Icon(Icons.Filled.ArrowBack, "Add Button") }

    }
    AnimatedVisibility(
        visible = shouldShowButton == false,
        modifier = modifier.align(Alignment.BottomCenter).padding(horizontal = APP_HORIZONTAL_DP, vertical = APP_HORIZONTAL_DP).let { if(paddingBottom) it.navigationBarsPadding() else it },
        enter =  AppAnimationManager.hiddenRightAnimation.enter + AppAnimationManager.centerFadeAnimation.enter,
        exit =  AppAnimationManager.hiddenRightAnimation.exit + AppAnimationManager.centerFadeAnimation.exit
    ){
        ExtendedFloatingActionButton(
            onClick = {
                nextPage(1)
            },
        ) { Text(text = "第${currentPage}页") }

    }
    FloatingActionButton(
        onClick = {
            if(!shouldShowButton) {
                nextPage(currentPage+1)
            } else {
                scope.launch { listState.animateScrollToItem(0) }
            }
        },
        modifier = modifier.align(Alignment.BottomEnd).padding(horizontal = APP_HORIZONTAL_DP, vertical = APP_HORIZONTAL_DP).let { if(paddingBottom) it.navigationBarsPadding() else it },
        ) {
        Icon(Icons.Filled.ArrowForward, "Add Button", modifier = Modifier.rotate(angle))
    }
}

@Composable
fun PaddingForPageControllerButton() = Spacer(Modifier.height(85.dp))

