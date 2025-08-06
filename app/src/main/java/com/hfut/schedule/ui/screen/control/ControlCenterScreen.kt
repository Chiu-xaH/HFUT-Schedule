package com.hfut.schedule.ui.screen.control

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitHorizontalDragOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.LargeButton
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.StyleCardListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.style.InnerPaddingHeight
import com.hfut.schedule.ui.style.bottomBarBlur
import com.hfut.schedule.ui.style.topBarTransplantColor
import com.hfut.schedule.ui.util.GlobalUIStateHolder
import com.hfut.schedule.ui.util.navigateForTransition
import com.xah.transition.util.currentRoute
import com.xah.transition.util.navigateAndClear
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

fun Modifier.limitDrawerSwipeArea(
    allowedArea: Rect,
): Modifier {
//    val overlayModifier = Modifier.drawWithContent {
//        drawContent()
//        drawRect(
//            color = Color.Red,
//            topLeft = Offset(allowedArea.left, allowedArea.top),
//            size = Size(allowedArea.width, allowedArea.height)
//        )
//    }

    return this
//        .then(overlayModifier)
        .pointerInput(allowedArea) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent()

                    // 检测第一根手指是否在允许区域
                    val position = event.changes.first().position
                    val inAllowedArea = allowedArea.contains(position)

                    if (!inAllowedArea) {
                        // 拦截并消费手势，不传递给 Drawer
//                        Log.d("手势","FALSE")
                        event.changes.forEach { it.consume() }
                    } else {
//                        Log.d("手势","TRUE")
                    }
                }
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlCenterScreen(
    navController: NavHostController,
    onExit : () -> Unit
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val queue = GlobalUIStateHolder.routeQueue
    val currentStack by navController.currentBackStack.collectAsState()
    val stack = currentStack.reversed()
    val color = MaterialTheme.colorScheme.surface
    val currentRoute = navController.currentRoute()
    val state = rememberScrollState()
    // 项目到达底部
    val isAtStart by remember { derivedStateOf { state.value == 0 } }
    Scaffold(
        modifier = Modifier,
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                modifier = Modifier.let {
                    if(isAtStart) it
                    else it.background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.0f to color.copy(alpha = 1f),
                                0.25f to color.copy(alpha = 0.95f),
                                0.50f to color.copy(alpha = 0.80f),
                                0.75f to color.copy(alpha = 0.65f),
                                1.0f to color.copy(alpha = 0f),
                            )
                        )
                    )
                },
                colors = topBarTransplantColor(),
                title = { Text("任务台") },
                navigationIcon = {
                    IconButton(
                        onClick = onExit
                    ) {
                        Icon(painterResource(R.drawable.menu),null, tint = MaterialTheme.colorScheme.primary)
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .hazeSource(hazeState)
                .verticalScroll(state)
                .fillMaxSize()
        ) {
            InnerPaddingHeight(innerPadding,true)
            if(queue.isNotEmpty()) {
                DividerTextExpandedWith("最近记录") {
                    for(index in queue.indices) {
                        val item = queue[index]
                        StyleCardListItem(
                            headlineContent = {
                                Text(item.app.label,fontWeight = if(currentRoute == item.route) FontWeight.Bold else FontWeight.Normal)
                            },
                            leadingContent = {
                                Icon(painterResource(item.app.icon),null)
                            },
                            trailingContent = {
                                FilledTonalIconButton (
                                    onClick = {
                                        queue.remove(item)
                                    },
                                ) {
                                    Icon(painterResource(R.drawable.delete),null)
                                }
                            },
                            modifier = Modifier.clickable {
                                if(currentRoute == item.route) {
                                    onExit()
                                } else {
                                    navController.navigateForTransition(item.app,item.route)
                                    onExit()
                                }
                            }
                        )
                    }
                }
            }
            if(stack.size > 2) {
                DividerTextExpandedWith("窗口栈") {
                    Row(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
                        LargeButton(
                            onClick = {
                                navController.navigate(AppNavRoute.Home.route){
                                    popUpTo(0) {
                                        inclusive = true
                                    }
                                }
                            },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(.75f),
                            contentColor = MaterialTheme.colorScheme.secondary,
                            icon = R.drawable.home,
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = "清空并回到主界面"
                        )
                    }
                    Spacer(Modifier.height(CARD_NORMAL_DP))

                    for(index in stack.indices) {
                        val item = stack[index]
                        val route = item.destination.route ?: continue
                        StyleCardListItem(
                            headlineContent = {
                                Text(route.substringBefore("?"), fontWeight = if(currentRoute == route) FontWeight.Bold else FontWeight.Normal)
                            },
                            trailingContent = if(index == 0 && stack.size > 2) {
                                {
                                    FilledTonalIconButton (
                                        onClick = { navController.popBackStack() },
                                    ) {
                                        Icon(painterResource(R.drawable.close),null)
                                    }
                                }
                            } else null,
                            leadingContent = {
                                Text("${stack.size - index - 1}")
                            },
                            color = cardNormalColor(),
                            modifier = Modifier.clickable {
                                if(currentRoute == route) {
                                    onExit()
                                } else {
                                    showToast("暂不支持越栈切换")
                                }
                            }
                        )
                    }
                    StyleCardListItem(
                        headlineContent = {
                            Text("并不会真正的占用后台，当退出时，其界面已经销毁")
                        },
                        leadingContent = {
                            Icon(painterResource(R.drawable.info),null)
                        },
                        cardModifier = Modifier.navigationBarsPadding()
                    )
                }
            }
            InnerPaddingHeight(innerPadding,false)
        }
    }
}