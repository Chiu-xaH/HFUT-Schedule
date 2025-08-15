package com.hfut.schedule.ui.component.screen

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.util.AppAnimationManager
import kotlinx.coroutines.launch

// 翻页器
@Composable
fun BoxScope.PagingController(
    listState : LazyListState,
    currentPage : Int,
    showUp : Boolean = false,
    nextPage : (Int) -> Unit,
    previousPage : (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // 如果列表无项目，不显示按钮
    val shouldShowButton by remember { derivedStateOf {
        listState.firstVisibleItemScrollOffset == 0
    } }
    AnimatedVisibility(
        visible = shouldShowButton,
        modifier = modifier.align(Alignment.BottomStart).padding(horizontal = APP_HORIZONTAL_DP, vertical = APP_HORIZONTAL_DP).navigationBarsPadding(),
        exit = AppAnimationManager.centerFadeAnimation.exit,
        enter = AppAnimationManager.centerFadeAnimation.enter
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
        visible = shouldShowButton,
        modifier = modifier.align(Alignment.BottomCenter).padding(horizontal = APP_HORIZONTAL_DP, vertical = APP_HORIZONTAL_DP).navigationBarsPadding(),
        exit = AppAnimationManager.centerFadeAnimation.exit,
        enter = AppAnimationManager.centerFadeAnimation.enter
    ){
        ExtendedFloatingActionButton(
            onClick = {
                nextPage(1)
            },
        ) { Text(text = "第${currentPage}页") }

    }
    AnimatedVisibility(
        visible = shouldShowButton,
        modifier = modifier.align(Alignment.BottomEnd).padding(horizontal = APP_HORIZONTAL_DP, vertical = APP_HORIZONTAL_DP).navigationBarsPadding(),
        exit = AppAnimationManager.centerFadeAnimation.exit,
        enter = AppAnimationManager.centerFadeAnimation.enter
    ){
        FloatingActionButton(
            onClick = {
                nextPage(currentPage+1)
            },
        ) { Icon(Icons.Filled.ArrowForward, "Add Button") }
    }
    if(showUp) {
        val scope = rememberCoroutineScope()
        AnimatedVisibility(
            visible = !shouldShowButton,
            modifier = modifier.align(Alignment.BottomEnd).padding(horizontal = APP_HORIZONTAL_DP, vertical = APP_HORIZONTAL_DP).navigationBarsPadding(),
            exit = AppAnimationManager.centerFadeAnimation.exit,
            enter = AppAnimationManager.centerFadeAnimation.enter
        ){
            FloatingActionButton(
                onClick = {
                    // 回到顶部
                    scope.launch { listState.animateScrollToItem(0) }
                },
            ) { Icon(painterResource(R.drawable.arrow_upward), "Add Button") }
        }
    }
}

@Composable
fun PaddingForPageControllerButton() = Spacer(Modifier.height(85.dp))