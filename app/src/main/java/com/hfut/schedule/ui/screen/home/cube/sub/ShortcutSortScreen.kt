package com.hfut.schedule.ui.screen.home.cube.sub

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.shortcut.AppShortcutManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.dialog.LittleDialog
import com.hfut.schedule.ui.component.divider.ScrollHorizontalTopDivider
import com.hfut.schedule.ui.component.network.UrlImage
import com.hfut.schedule.ui.component.screen.pager.PageIndicator
import com.hfut.schedule.ui.component.text.DividerText
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.nav.destination.SettingsShortcutEditDestination
import com.hfut.schedule.ui.screen.home.swap
import com.xah.common.ui.component.text.BottomTip
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.align.RowHorizontal
import com.xah.common.ui.style.color.topBarTransplantColor
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ShortcutSortScreen() {
    val hapticFeedback = LocalHapticFeedback.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val shortcutSort by DataStoreManager.shortcutSort.collectAsState(initial = null)
    val funcMaps = AppShortcutManager.getFinalList(shortcutSort).toMutableList()
    val state = rememberLazyListState()
    val reorderableLazyGridState = rememberReorderableLazyListState(state) { from, to ->
        // 交换
        funcMaps.swap(from.index, to.index)
        // 保存
        DataStoreManager.saveShortcutSort(funcMaps.map { it.id })
        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
    }
    var inEdit by remember { mutableStateOf(false) }

    val transition = rememberInfiniteTransition(label = "shake")
    val rotation by transition.animateFloat(
        initialValue = -.75f,
        targetValue = .75f,
        animationSpec = infiniteRepeatable(
            animation = tween(100, easing = LinearEasing), // 越短越快
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )
    val scope = rememberCoroutineScope()
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    var showDialog by remember { mutableStateOf(false) }
    if(showDialog) {
        LittleDialog(
            onDismissRequest = { showDialog = false },
            onConfirmation = {
                scope.launch {
                    DataStoreManager.saveShortcutSort(null)
                    showDialog = false
                    showToast("已恢复")
                }
            },
            dialogText = "恢复为初始顺序",
            hazeState = hazeState
        )
    }

    BackHandler(enabled = inEdit) {
        inEdit = false
    }
    Column (
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .fillMaxSize()
            .hazeSource(hazeState)
    ) {
        MediumTopAppBar(
            scrollBehavior = scrollBehavior,
            colors = topBarTransplantColor(),
            title = { Text(SettingsShortcutEditDestination.title.asString()) },
            navigationIcon = {
                TopBarNavigationIcon()
            },
            actions = {
                Row(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
                    FilledTonalIconButton(
                        onClick = {
                            showDialog = true
                        }
                    ) {
                        Icon(painterResource(R.drawable.rotate_right),null)
                    }
                    Spacer(Modifier.width(APP_HORIZONTAL_DP/5))
                    if(!inEdit) {
                        FilledTonalButton(
                            onClick = {
                                inEdit = true
                            }
                        ) {
                            Text("编辑")
                        }
                    } else {
                        FilledTonalButton(
                            onClick = {
                                inEdit = false
                            }
                        ) {
                            Text("完成")
                        }
                    }
                }
            }
        )
        val iconSize = remember { 29.dp }
        val pagerState = rememberPagerState(pageCount = { 2 })
        ScrollHorizontalTopDivider(state,startPadding = false,endPadding = false)
        LazyColumn(
            state = state,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            if(!inEdit) {
                item {
                    DividerText("排序")
                }
            }
            items(funcMaps.size,key = { funcMaps[it].id }) { index ->
                val item = funcMaps[index]
                val displayed = index < AppShortcutManager.MAX_SIZE
                ReorderableItem (reorderableLazyGridState, key = item.id, enabled = inEdit) { isDragging ->
                    val elevation by animateDpAsState(
                        targetValue = if (isDragging) APP_HORIZONTAL_DP else 0.dp,
                    )
                    val alpha by animateFloatAsState(
                        targetValue = if (displayed) 1f else 0.5f,
                    )
                    val overlay = MaterialTheme.colorScheme.surface.copy(alpha = alpha)

                    CardListItem(
                        headlineContent = {
                            Text(item.longLabel.asString(), modifier = Modifier.alpha(alpha))
                        },
                        leadingContent = {
                            if(item.icon == R.drawable.pdd_icon || item.icon == R.drawable.taobao_icon) {
                                Image(
                                    painterResource(item.icon),
                                    null,
                                    modifier = Modifier.size(iconSize).alpha(alpha).clip(RoundedCornerShape(6.5.dp))
                                )
                            } else {
                                Icon(
                                    painterResource(item.icon),
                                    null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(iconSize).alpha(alpha)
                                )
                            }
                        },
                        shadow = elevation,
                        color = overlay.compositeOver(MaterialTheme.colorScheme.surfaceContainer),
                        modifier = Modifier
                            .combinedClickable(
                                onClick = {
                                    if (!inEdit) {
                                        showToast("长按卡片开始编辑")
                                    } else {
                                        showToast("双击卡片结束编辑")
                                    }
                                },
                                onDoubleClick = {
                                    inEdit = false
                                },
                                onLongClick = {
                                    inEdit = true
                                }
                            )
                            .longPressDraggableHandle(
                                enabled = inEdit,
                                onDragStarted = {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                                },
                                onDragStopped = {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                },
                            ),
                        cardModifier = Modifier
                            .let {
                                if(inEdit) {
                                    if(isDragging) it else it.graphicsLayer { rotationZ = rotation }
                                } else {
                                    it
                                }
                            }
                            .padding(bottom = CARD_NORMAL_DP)
                    )
                }
            }
            item {
                BottomTip("由于系统限制，仅前${AppShortcutManager.MAX_SIZE}个选项可以在桌面长按图标快捷菜单中显示")
            }
            if(!inEdit) {
                item {
                    DividerTextExpandedWith("功能演示") {
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)
                        ) {
                            Column {
                                HorizontalPager(state = pagerState) { page ->
                                    when(page) {
                                        0 -> {
                                            UrlImage(
                                                "${Constant.GITHUB_RAW_RESOURCES_URL}image/tips_shortcut.jpg",
                                                contentScale = ContentScale.FillWidth,
                                                shape = MaterialTheme.shapes.medium,
                                            )
                                        }
                                        1 -> {
                                            UrlImage(
                                                "${Constant.GITHUB_RAW_RESOURCES_URL}image/tips_qs.jpg",
                                                contentScale = ContentScale.FillWidth,
                                                shape = MaterialTheme.shapes.medium,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        RowHorizontal {
                            PageIndicator(
                                pagerState,
                                modifier = Modifier.padding(vertical = CARD_NORMAL_DP*3)
                            )
                        }
                    }
                }
            }
            items(2) { Spacer(Modifier.navigationBarsPadding().height(APP_HORIZONTAL_DP)) }
        }
    }
}