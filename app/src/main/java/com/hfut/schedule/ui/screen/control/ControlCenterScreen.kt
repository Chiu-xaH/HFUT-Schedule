package com.hfut.schedule.ui.screen.control

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.ui.style.topBarTransplantColor
import com.hfut.schedule.ui.util.GlobalUIStateHolder
import com.hfut.schedule.ui.util.navigateForTransition
import com.xah.transition.util.currentRouteWithArgWithoutValues
import com.xah.transition.util.isCurrentRouteWithoutArgs
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

fun isAllChinese(str: String): Boolean = str.isNotEmpty() && str.all { it.isChinese() }

fun Char.isChinese(): Boolean = this in '\u4e00'..'\u9fff'


fun containsChinese(str: String): Boolean = str.any { it.isChinese() }



fun getLabel(route : String) : String? {
    val str = route.substringAfter("?")
    val isFirstCharUpperCase = str.firstOrNull()?.isUpperCase() == true
    if(isFirstCharUpperCase) {
        return null
    }
    // 以&分割 类似 key=value&key=value
    val list = str.split("&")
    list.forEach { item ->
        val key = item.substringBefore("=")
        val value = item.substringAfter("=")
        if((key.contains("title") || key.contains("name") || (key.contains("type")  && containsChinese(value)))) {
            return value
        }
    }
    return null
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
    val currentRoute = navController.currentRouteWithArgWithoutValues()?.substringBefore("?")
    val state = rememberScrollState()
    var input by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }
    // 项目到达底部
    val isAtStart by remember { derivedStateOf { state.value == 0 } }
//    PredictiveBackHandler { progress: Flow<BackEventCompat> ->
//        // code for gesture back started
//        try {
//            progress.collect { backEvent ->
//                // code for progress
////                onScale(1f - (targetScale * backEvent.progress))
//            }
//            // code for completion
//            onExit()
//        } catch (e: CancellationException) {
//            // code for cancellation
//
//        }
//    }
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
                title = { Text("启动台") },
                actions = {
                    IconButton(
                        onClick = {
                            showSearch = !showSearch
                        }
                    ) {
                        Icon(painterResource(if(!showSearch) R.drawable.search else R.drawable.flash_on),null, tint = MaterialTheme.colorScheme.primary)
                    }
                },
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
            if(navController.isCurrentRouteWithoutArgs(AppNavRoute.WebView.route)) {
                StyleCardListItem(
                    headlineContent = { Text("位于网页界面时暂时禁用手势轻扫呼出，可点击左上角退出")},
                    leadingContent = {
                        Icon(painterResource(R.drawable.info),null)
                    }
                )
                Spacer(Modifier.height(CARD_NORMAL_DP))
            }
            Box() {
                androidx.compose.animation.AnimatedVisibility(
                    visible = showSearch,
                    enter = fadeIn(),
                    exit = fadeOut()
                ){
                    Column {
                        TextField(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP),
                            value = input,
                            onValueChange = {
                                input = it
                            },
                            label = { Text("搜索 查询中心功能") },
                            leadingIcon = { Icon(painterResource(R.drawable.search),null) },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            colors = textFiledTransplant(true,false),
                        )
                    }
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = !showSearch,
                    enter = fadeIn(),
                    exit = fadeOut()
                ){
                    Column {
                        if(queue.isNotEmpty()) {
                            DividerTextExpandedWith("最近记录") {
                                LazyRow {
                                    item { Spacer(Modifier.width(APP_HORIZONTAL_DP)) }
                                    items(queue.size) { index ->
                                        val item = queue[index]
                                        if(currentRoute == item.app.route && index == 0) {
                                            FilledTonalButton(
                                                onClick = {
                                                    onExit()
                                                },
                                            ) {
                                                Text(item.app.label)
                                            }
                                            Spacer(Modifier.width(4.dp))
                                        } else {
                                            FilledTonalIconButton(
                                                onClick = {
                                                    navController.navigateForTransition(item.app,item.route)
                                                    onExit()
                                                },
                                            ) {
                                                Icon(painterResource(item.app.icon),null)
                                            }
                                        }
                                    }
                                    item { Spacer(Modifier.width(APP_HORIZONTAL_DP)) }
                                }
                                Spacer(Modifier.height(CARD_NORMAL_DP))
                                for(index in queue.indices) {
                                    val item = queue[index]
                                    val label = getLabel(item.route)
                                    val isCurrent = currentRoute == item.app.route && index == 0
                                    StyleCardListItem(
                                        headlineContent = {
                                            Text(item.app.label ,fontWeight = if(isCurrent) FontWeight.Bold else FontWeight.Normal)
                                        },
                                        supportingContent = {
                                            label?.let {
                                                Text(it)
                                            }
                                        },
                                        leadingContent = {
                                            Icon(painterResource(item.app.icon),null, tint = if(isCurrent) MaterialTheme.colorScheme.primary else  LocalContentColor. current)
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
                                            if(isCurrent) {
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
                                    val route = item.destination.route?.substringBefore("?") ?: continue
                                    StyleCardListItem(
                                        headlineContent = {
                                            Text(route, fontWeight = if(currentRoute == route) FontWeight.Bold else FontWeight.Normal)
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
                    }
                }
            }
            InnerPaddingHeight(innerPadding,false)
        }
    }
}