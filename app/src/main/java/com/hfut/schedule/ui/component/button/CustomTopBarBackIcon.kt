package com.hfut.schedule.ui.component.button

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hfut.schedule.R
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.text.DIVIDER_TEXT_VERTICAL_PADDING
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.kyant.backdrop.Backdrop
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.navigation.model.action.LaunchMode
import com.xah.navigation.util.LocalNavController
import kotlinx.coroutines.launch


// APP的根导航
val LocalAppControlCenter = staticCompositionLocalOf<DrawerState> {
    error("未提供根DrawerState")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarNavigationIcon() {
    val activity = LocalActivity.current
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val queue = navController.stack.reversed()
    var displayDialog by remember { mutableStateOf(false) }
    val canPop = navController.canPop()

    if(displayDialog) {
        Dialog(
            onDismissRequest = { displayDialog = false }
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .clickable(
                    // 去掉水波纹
                    interactionSource = null,
                    indication = null
                ){
                    displayDialog = false
                }
            ) {
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(vertical = APP_HORIZONTAL_DP)
                        .align(Alignment.TopCenter)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f, fill = false),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        LazyColumn {
                            items(queue.size) { index ->
                                val item = queue[index]
                                val dest = item.destination as NavDestination
                                val desc = dest.description
                                val title = dest.title.asString()
                                val isCurrent = index == 0
                                TransplantListItem(
                                    headlineContent = {
                                        Text(title ,fontWeight = if(isCurrent) FontWeight.Bold else FontWeight.Normal)
                                    },
                                    supportingContent = {
                                        desc?.let {
                                            Text(it)
                                        }
                                    },
                                    leadingContent = {
                                        Icon(painterResource(dest.icon),null, tint = if(isCurrent) MaterialTheme.colorScheme.primary else  LocalContentColor. current)
                                    },

                                    trailingContent = {
                                        if(isCurrent) {
                                            Icon(painterResource(R.drawable.arrow_upward),null, modifier = Modifier.rotate(-90f))
                                        }
                                    },
                                    modifier = Modifier.clickable {
                                        if(isCurrent) {
                                            displayDialog = false
                                        } else {
                                            scope.launch {
                                                navController.push(item.destination, LaunchMode.PopToExisting())
                                                displayDialog = false
                                            }
                                        }
                                    }
                                )
                                if(index != queue.size-1) {
                                    PaddingHorizontalDivider()
                                }
                            }
                        }
                    }
//                    if(navController.stack.lastOrNull()?.destination != navController.startDestination) {
//                        LargeButton(
//                            containerColor = MaterialTheme.colorScheme.errorContainer,
//                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
//                            onClick = {
//                                scope.launch {
//                                    Starter.backToHome(navController)
//                                    showToast("已回到首页")
//                                    displayDialog = false
//                                }
//                            },
//                            modifier = Modifier
//                                .padding(top = APP_HORIZONTAL_DP)
//                                .fillMaxWidth()
//                            ,
//                            text = "回到首页",
//                            icon = R.drawable.home
//                        )
//                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .padding(horizontal = CARD_NORMAL_DP/2)
            .clip(CircleShape)
            .combinedClickable(
                enabled = true,
                onClick = {
                    if(canPop) {
                        navController.pop()
                    } else {
                        activity?.finish()
                    }
                },
                // TODO 预留唤出启动台
                onDoubleClick = null,
                onLongClick = if(canPop) {
                    { displayDialog = true }
                } else {
                    null
                }
            )
    ) {
        Box(
            modifier = Modifier.padding(DIVIDER_TEXT_VERTICAL_PADDING)
        ) {
            Icon(
                painterResource(
                    if(canPop) {
                        R.drawable.arrow_back
                    } else {
                        R.drawable.close
                    }
                ),
                contentDescription = null,
                tint =  MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun C() {
    Box(
        modifier = Modifier
            .padding(horizontal = CARD_NORMAL_DP/2)
            .clip(CircleShape)

    ) {
        Box(
            modifier = Modifier.padding(DIVIDER_TEXT_VERTICAL_PADDING)
        ) {
            Icon(
                painterResource(R.drawable.arrow_back),
                contentDescription = null,
                tint =  MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LiquidTopBarNavigateIcon(
    backdrop: Backdrop,
) {
    val activity = LocalActivity.current
    val navController = LocalNavController.current
    val canPop = navController.canPop()
    LiquidButton (
        onClick = {
            if(canPop) {
                navController.pop()
            } else {
                activity?.finish()
            }
        },
        backdrop = backdrop,
        isCircle = true,
        modifier = Modifier.padding(start = APP_HORIZONTAL_DP-2.5.dp, end = 9.dp)
    ) {
        Icon(
            painterResource(
                if(canPop) {
                    R.drawable.arrow_back
                } else {
                    R.drawable.close
                }
            ),
            contentDescription = null
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun A() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                title = { Text("测试标题") },
                colors = topBarTransplantColor(),
                navigationIcon = {
                    TopBarNavigationIcon()
                }
            )
        }
    ) { innerPadding ->
        LazyColumn {
            item { InnerPaddingHeight(innerPadding,true) }
            items(100) {
                CardListItem(
                    headlineContent = {
                        Text(" Item #$it")
                    }
                )
            }
            item { InnerPaddingHeight(innerPadding,false) }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun B() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    var dragging by remember { mutableStateOf(false) }
    var overlayOffset by remember { mutableStateOf(Offset.Zero) }

    Box(Modifier.fillMaxSize()) {

        Scaffold(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = { Text("测试标题") },
                    colors = topBarTransplantColor(),
                    navigationIcon = {

                        var iconGlobal by remember { mutableStateOf(Offset.Zero) }

                        Box(
                            modifier = Modifier
                                .onGloballyPositioned {
                                    iconGlobal = it.localToRoot(Offset.Zero)
                                }
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart = { offset ->
                                            dragging = true
                                            overlayOffset = iconGlobal + offset
                                        },
                                        onDragEnd = {
                                            dragging = false
                                        },
                                        onDragCancel = {
                                            dragging = false
                                        }
                                    ) { change, dragAmount ->
                                        change.consume()
                                        overlayOffset += dragAmount
                                    }
                                }
                        ) {

                            // ✅ 原组件（不改实现，只做隐藏）
                            Box(
                                Modifier.drawWithContent {
                                    if (!dragging) drawContent()
                                }
                            ) {
                                C()
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->

            LazyColumn {
                item { InnerPaddingHeight(innerPadding, true) }
                items(100) {
                    CardListItem(
                        headlineContent = {
                            Text(" Item #$it")
                        }
                    )
                }
                item { InnerPaddingHeight(innerPadding, false) }
            }
        }

        // ✅ 全局 overlay（用同一个组件再渲染一份）
        if (dragging) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                overlayOffset.x.toInt(),
                                overlayOffset.y.toInt()
                            )
                        }
                ) {
                    C()
                }
            }
        }
    }
}



