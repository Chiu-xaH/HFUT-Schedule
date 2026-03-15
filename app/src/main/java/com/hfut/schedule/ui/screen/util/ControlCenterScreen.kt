package com.hfut.schedule.ui.screen.util

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.divider.ScrollHorizontalTopDivider
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.destination.HomeDestination
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.cube.screen.SharedAppearanceSettingsScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.ui.util.layout.measureDpSize
import com.hfut.schedule.ui.util.state.GlobalUIStateHolder
import com.xah.container.utils.LocalSharedRegistry
import com.xah.navigation.controller.NavigationController
import com.xah.navigation.model.action.ActionType
import com.xah.navigation.model.action.LaunchMode
import com.xah.navigation.utils.LocalNavController
import com.xah.navigation.utils.LocalNavControllerSafely
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.color.topBarTransplantColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

fun Modifier.limitDrawerSwipeArea(
    allowedArea: Rect,
): Modifier = this
    .pointerInput(allowedArea) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()

                // 检测第一根手指是否在允许区域
                val position = event.changes.first().position
                val inAllowedArea = allowedArea.contains(position)

                if (!inAllowedArea) {
                    // 拦截并消费手势，不传递给 Drawer
                    event.changes.forEach { it.consume() }
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
private const val TAB_STACK = 0
private const val TAB_SETTINGS = 1
private const val TAB_SEARCH = 2



@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun ControlCenterScreen(
    navController: NavigationController,
    onExit : () -> Unit
) {
    val globalColor = MaterialTheme.colorScheme.surface.copy(1- MyApplication.CONTROL_CENTER_BACKGROUND_MASK_ALPHA)
    val state = rememberScrollState()
    // 项目到达底部
    val currentRoute = navController.current()
    var tab by remember { mutableIntStateOf(TAB_STACK) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scope = rememberCoroutineScope()
    val contentColor = MaterialTheme.colorScheme.onSurface

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = Color.Transparent,
        topBar = {
            Column(
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor().copy(
                        titleContentColor =  contentColor
                    ),
                    title = {
                        Text(
                            when(tab) {
                                TAB_STACK -> "启动台"
                                TAB_SEARCH -> "搜索"
                                TAB_SETTINGS -> "外观设置"
                                else -> "启动台"
                            }
                        )
                    },
                    actions = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            var height by remember { mutableStateOf(0.dp) }
                            if(tab == TAB_STACK && currentRoute?.destination != navController.startDestination){
                                IconButton (
                                    onClick = {
                                        scope.launch {
                                            navController.push(navController.startDestination, LaunchMode.Single(reuse = true, actionType = ActionType.POP))
                                            showToast("已回到首页")
                                            snapshotFlow { navController.isTransitioning }
                                                .filter { !it }
                                                .first()
                                            onExit()
                                        }
                                    },
                                    modifier = Modifier.measureDpSize { _,h -> height = h },
                                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor =  MaterialTheme.colorScheme.errorContainer.copy(.75f))
                                ) {
                                    Icon(
                                        painterResource(R.drawable.home),
                                        null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(25.5.dp)
                                    )
                                }
                                VerticalDivider(modifier = Modifier.height(height/2))
                            }
                            IconButton(
                                onClick = { tab = TAB_STACK },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = if(tab == TAB_STACK) {
                                        globalColor.copy(.85f)
                                    } else {
                                        Color.Unspecified
                                    }
                                ),
                            ) {
                                Icon(painterResource(R.drawable.flash_on),null, tint = contentColor, modifier = Modifier.size(25.5.dp))
                            }
                            IconButton(
                                onClick = { tab = TAB_SETTINGS },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = if(tab == TAB_SETTINGS) {
                                        globalColor.copy(.85f)
                                    } else {
                                        Color.Unspecified
                                    }
                                ),
                            ) {
                                Icon(painterResource(R.drawable.format_paint),null, tint = contentColor)
                            }
                            IconButton(
                                onClick = { tab = TAB_SEARCH },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = if(tab == TAB_SEARCH) {
                                        globalColor.copy(.85f)
                                    } else {
                                        Color.Unspecified
                                    }
                                ),
                            ) {
                                Icon(painterResource(R.drawable.category_search),null, tint = contentColor)
                            }
                            Spacer(Modifier.width(APP_HORIZONTAL_DP-8.dp))
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onExit
                        ) {
                            Icon(painterResource(R.drawable.arrow_back),null, tint = contentColor)
                        }
                    },
                )
                if(tab == TAB_SEARCH) {
//                    TextField(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = APP_HORIZONTAL_DP),
//                        value = input,
//                        onValueChange = {
//                            showToast("正在开发")
//                            input = it
//                        },
//                        label = { Text("全局搜索") },
//                        leadingIcon = { Icon(painterResource(R.drawable.search),null) },
//                        singleLine = true,
//                        shape = MaterialTheme.shapes.medium,
//                        colors = textFiledTransplant(),
//                    )
//                    val height by animateDpAsState(
//                        targetValue = if(isAtStart) {
//                            CARD_NORMAL_DP
//                        } else {
//                            APP_HORIZONTAL_DP
//                        }
//                    )
//                    Spacer(Modifier.height(height -DividerDefaults.Thickness))
                }
                ScrollHorizontalTopDivider(state, startPadding = true,endPadding = true)
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(state)
                .fillMaxSize()
        ) {
            Box() {
                androidx.compose.animation.AnimatedVisibility(
                    visible = tab == TAB_SEARCH,
                    enter = fadeIn(),
                    exit = fadeOut()
                ){
//                    val list = GlobalUIStateHolder.funcMaps.filter { it.name.contains(input,ignoreCase = true) }
//                    Column(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP-3.dp)) {
//                        for(i in list.indices step 2) {
//                            val item = list[i]
//                            Row() {
//                                SmallCard(modifier = Modifier
//                                    .padding(horizontal = 3.dp, vertical = 3.dp)
//                                    .weight(.5f)) {
//                                    TransplantListItem(
//                                        headlineContent = { ScrollText(item.name) },
//                                        leadingContent = {
//                                            Icon(painterResource(item.icon),null)
//                                        },
//                                        modifier = Modifier.clickable {
//                                            showToast("正在开发")
//                                        }
//                                    )
//                                }
//                                if(i + 1 < list.size) {
//                                    val item2 = list[i+1]
//                                    SmallCard(modifier = Modifier
//                                        .padding(horizontal = 3.dp, vertical = 3.dp)
//                                        .weight(.5f)) {
//                                        TransplantListItem(
//                                            headlineContent = { ScrollText(item2.name) },
//                                            leadingContent = {
//                                                Icon(painterResource(item2.icon),null)
//                                            },
//                                            modifier = Modifier.clickable {
//                                                showToast("正在开发")
//                                            }
//                                        )
//                                    }
//                                } else {
//                                    Spacer(modifier = Modifier.weight(.5f))
//                                }
//                            }
//                        }
//                    }
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = tab == TAB_SETTINGS,
                    enter = fadeIn(),
                    exit = fadeOut()
                ){
                    Column {
                        SharedAppearanceSettingsScreen(
                            innerPaddings = innerPadding,
                            isControlCenter = true,
                            navController = navController
                        )
                    }
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = tab == TAB_STACK,
                    enter = fadeIn(),
                    exit = fadeOut()
                ){
                    RecentlyStackUI(navController,contentColor,globalColor,onExit)
                }
            }
//            InnerPaddingHeight(innerPadding,false)
        }
    }
}

@Composable
fun RecentlyStackUI(
    navController: NavigationController,
    contentColor : Color,
    globalColor : Color,
    onExit : () -> Unit
) {
    val scope = rememberCoroutineScope()
    val queue = navController.stack.reversed()
    val currentRoute = navController.stack.lastOrNull()
    Column {
//                        DividerTextExpandedWith("固定项目") {

//                        }
        if(queue.isNotEmpty()) {
            DividerTextExpandedWith("最近使用",contentColor=contentColor) {
                LazyRow {
                    item { Spacer(Modifier.width(APP_HORIZONTAL_DP-3.dp)) }
                    items(queue.size) { index ->
                        val item = queue[index]
                        val dest = item.destination as NavDestination
                        if(currentRoute == item ) {
                            FilledTonalButton(
                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = globalColor),
                                onClick = {
                                    onExit()
                                },
                            ) {
                                Text(
                                    (item.destination as NavDestination).title.asString()
                                )
                            }
                            Spacer(Modifier.width(4.dp))
                        } else {
                            FilledTonalIconButton(
                                colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = globalColor),
                                onClick = {
                                    scope.launch {
                                        navController.push(item.destination, LaunchMode.PopToExisting)
                                        snapshotFlow { navController.isTransitioning }
                                            .filter { !it }
                                            .first()
                                        onExit()
                                    }
                                },
                            ) {
//                                Icon(painterResource(R.drawable.stacks),null, tint = contentColor)
                                Icon(painterResource(dest.icon),null, tint = contentColor)
                            }
                        }
                    }
                    item { Spacer(Modifier.width(APP_HORIZONTAL_DP)) }
                }
                Spacer(Modifier.height(CARD_NORMAL_DP))
                for(index in queue.indices) {
                    val item = queue[index]
                    val dest = item.destination as NavDestination
                    val desc = dest.description
                    val title = dest.title
                    val isCurrent = currentRoute == item
                    CardListItem(
                        headlineContent = {
                            Text(title.asString(),fontWeight = if(isCurrent) FontWeight.Bold else FontWeight.Normal)
                        },
                        supportingContent = {
                            desc?.let {
                                Text(it)
                            }
                        },
                        leadingContent = {
//                            Icon(painterResource(R.drawable.stacks),null, tint = contentColor)
                            Icon(painterResource(dest.icon),null, tint = if(isCurrent) MaterialTheme.colorScheme.primary else  LocalContentColor. current)
                        },
                        trailingContent = {
                            if(index == 0 && item.destination != navController.startDestination) {
                                FilledTonalIconButton (
                                    onClick = {
                                        navController.pop()
                                    },
                                ) {
                                    Icon(painterResource(R.drawable.delete),null)
                                }
                            }
                        },
                        color = globalColor,
                        modifier = Modifier.clickable {
                            if(isCurrent) {
                                onExit()
                            } else {
                                scope.launch {
                                    navController.push(item.destination, LaunchMode.PopToExisting)
                                    snapshotFlow { navController.isTransitioning }
                                        .filter { !it }
                                        .first()
                                    onExit()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}