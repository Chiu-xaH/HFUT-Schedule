package com.hfut.schedule.ui.component.custom

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.TabRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.zIndex
import com.hfut.schedule.ui.component.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.EmptyUI
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Campus
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.getCampus
import kotlinx.coroutines.launch


@Composable
fun CustomTabRow(pagerState: PagerState, titles: List<String>
//                 ,padding : Dp = 10.dp
) {
    if(titles.size >= 5) {
        // 可滑动
//        CustomScrollTabRow(pagerState,titles)
        CustomScrollTabRow2(pagerState,titles)
    } else {
        CustomSlidingTabRow2(pagerState,titles)
//        CustomNormalTabRow(pagerState,titles)
    }
}
@Composable
private fun CustomNormalTabRow(pagerState: PagerState, titles: List<String>, padding : Dp = 10.dp) {
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.background(Color.Transparent).padding(horizontal = padding)) {
        SecondaryTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color.Transparent,
            contentColor = TabRowDefaults.primaryContentColor,
            indicator = {},
            divider = {}) {
            titles.forEachIndexed { index, title ->
                val selected = pagerState.currentPage == index
                Tab(
                    selected = selected,
                    text = {
                        Text(
                            text = title,
                            style = TextStyle(fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                        )
                    },
                    modifier = Modifier
                        .padding(5.dp)
                        .background(
                            color = if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                            shape = RoundedCornerShape(APP_HORIZONTAL_DP)
                        ),
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun CustomSlidingTabRow2(
    pagerState: PagerState,
    titles: List<String>,
    padding: Dp = 10.dp
) {
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.background(Color.Transparent).padding(horizontal = padding)) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = Color.Transparent,
            indicator = { tabPositions ->
                val progress = pagerState.currentPage + pagerState.currentPageOffsetFraction

                val left = lerp(
                    tabPositions.getOrNull(progress.toInt())?.left ?: 0.dp,
                    tabPositions.getOrNull(progress.toInt() + 1)?.left ?: 0.dp,
                    progress - progress.toInt()
                )
                val totalWidth = tabPositions.lastOrNull()?.right ?: 0.dp

                val right = lerp(
                    tabPositions.getOrNull(progress.toInt())?.right ?: 0.dp,
                    tabPositions.getOrNull(progress.toInt() + 1)?.right ?: 0.dp,
                    progress - progress.toInt()
                )

                Box(
                    modifier = Modifier.fillMaxWidth().padding(start = left + APP_HORIZONTAL_DP - padding, end = totalWidth - right + APP_HORIZONTAL_DP - padding)
                        .fillMaxHeight()
                        .padding(vertical = 4.5.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(APP_HORIZONTAL_DP)
                        )
                        .zIndex(0f)
                )
            },
            divider = {}
        ) {
            titles.forEachIndexed { index, title ->
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale = animateFloatAsState(
                    targetValue = if (isPressed) 0.8f else 1f, // 按下时为0.9，松开时为1
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = " " // 使用弹簧动画
                )
                val selected = pagerState.currentPage == index
                Tab(
                    selected = selected,
                    interactionSource = interactionSource,
                    text = {
                        Text(
                            text = title,
                            color = MaterialTheme.colorScheme.primary,
                            style = TextStyle(fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                        )
                    },
                    modifier = Modifier.scale(scale.value).padding(5.dp).zIndex(1f),
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                )
            }
        }

    }

}

@Composable
private fun CustomScrollTabRow(
    pagerState: PagerState,
    titles: List<String>,
    padding : Dp = 10.dp
) {
    var tabRowHeight by remember { mutableIntStateOf(0) }
    // px 转 dp（使用 LocalDensity）
    val density = LocalDensity.current
    val tabRowHeightDp = with(density) { tabRowHeight.toDp() }

    val scope = rememberCoroutineScope()
    Box(modifier = Modifier.fillMaxWidth()) {
        // TabRow 本体
        Column(modifier = Modifier.padding(horizontal = padding)) {
            SecondaryScrollableTabRow(
                modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                    tabRowHeight = layoutCoordinates.size.height  // 单位是像素 px
                },
                edgePadding = 0.dp,
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.Transparent,
                contentColor = TabRowDefaults.primaryContentColor,
                indicator = {},
                divider = {}
            ) {
                titles.forEachIndexed { index, title ->
                    val selected = pagerState.currentPage == index
                    Tab(
                        selected = selected,
                        text = {
                            Text(
                                text = title,
                                style = TextStyle(fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                            )
                        },
                        modifier = Modifier
                            .padding(5.dp)
                            .background(
                                color = if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                                shape = RoundedCornerShape(APP_HORIZONTAL_DP)
                            ),
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                    )
                }
            }
        }

        // 左侧渐变遮罩
        Box(
            Modifier
                .height(tabRowHeightDp)
                .width(APP_HORIZONTAL_DP*1.75f)
                .align(Alignment.CenterStart)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            Color.Transparent
                        )
                    )
                )
        )


        // 右侧渐变遮罩
        Box(
            Modifier
                .height(tabRowHeightDp)
                .width(APP_HORIZONTAL_DP*1.75f)
                .align(Alignment.CenterEnd)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        )

    }
}

@Composable
private fun CustomScrollTabRow2(
    pagerState: PagerState,
    titles: List<String>,
    padding : Dp = 10.dp
) {
    var tabRowHeight by remember { mutableIntStateOf(0) }
    // px 转 dp（使用 LocalDensity）
    val density = LocalDensity.current
    val tabRowHeightDp = with(density) { tabRowHeight.toDp() }

    val scope = rememberCoroutineScope()
    Box(modifier = Modifier.fillMaxWidth()) {
        // TabRow 本体
        Column(modifier = Modifier.padding(horizontal = padding)) {
            ScrollableTabRow(
                modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                    tabRowHeight = layoutCoordinates.size.height  // 单位是像素 px
                },
                edgePadding = 0.dp,
                selectedTabIndex = pagerState.currentPage,
                backgroundColor = Color.Transparent,
                contentColor = TabRowDefaults.primaryContentColor,
                indicator = { tabPositions ->
                    val progress = pagerState.currentPage + pagerState.currentPageOffsetFraction

                    val left = lerp(
                        tabPositions.getOrNull(progress.toInt())?.left ?: 0.dp,
                        tabPositions.getOrNull(progress.toInt() + 1)?.left ?: 0.dp,
                        progress - progress.toInt()
                    )
                    val totalWidth = tabPositions.lastOrNull()?.right ?: 0.dp

                    val right = lerp(
                        tabPositions.getOrNull(progress.toInt())?.right ?: 0.dp,
                        tabPositions.getOrNull(progress.toInt() + 1)?.right ?: 0.dp,
                        progress - progress.toInt()
                    )

                    Box(
                        modifier = Modifier.fillMaxWidth().padding(start = left + APP_HORIZONTAL_DP - padding, end = totalWidth - right + APP_HORIZONTAL_DP - padding)
                            .fillMaxHeight()
                            .padding(vertical = 4.5.dp)
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(APP_HORIZONTAL_DP)
                            )
                            .zIndex(0f)
                    )
                            },
                divider = {}
            ) {
                titles.forEachIndexed { index, title ->
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val scale = animateFloatAsState(
                        targetValue = if (isPressed) 0.8f else 1f, // 按下时为0.9，松开时为1
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = " " // 使用弹簧动画
                    )
                    val selected = pagerState.currentPage == index
                    Tab(
                        selected = selected,
                        interactionSource = interactionSource,
                        text = {
                            Text(
                                text = title,
                                color = MaterialTheme.colorScheme.primary,
                                style = TextStyle(fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                            )
                        },
                        modifier = Modifier.scale(scale.value).padding(5.dp).zIndex(1f),
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                    )
                }
            }
        }

        // 左侧渐变遮罩
        Box(
            Modifier
                .height(tabRowHeightDp)
                .width(APP_HORIZONTAL_DP*1.75f)
                .align(Alignment.CenterStart)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            Color.Transparent
                        )
                    )
                )
        )


        // 右侧渐变遮罩
        Box(
            Modifier
                .height(tabRowHeightDp)
                .width(APP_HORIZONTAL_DP*1.75f)
                .align(Alignment.CenterEnd)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        )

    }
}


private const val HEFEI_TAB = 0
private const val XUANCHENG_TAB = 1
@Composable
fun CampusSelectTab(xuanChengContent : @Composable () -> Unit,heFeiContent : @Composable () -> Unit = {
    EmptyUI("需要合肥校区在读生贡献数据源")
}) {
    val titles = remember { listOf("合肥","宣城") }

    val pagerState = rememberPagerState(pageCount = { titles.size }, initialPage =
        when(getCampus()) {
            Campus.XUANCHENG -> XUANCHENG_TAB
            Campus.HEFEI -> HEFEI_TAB
        }
    )

    CustomTabRow(pagerState,titles)
    HorizontalPager(state = pagerState) { page ->
        when(page) {
            HEFEI_TAB -> {
                heFeiContent()
            }
            XUANCHENG_TAB -> {
                xuanChengContent()
            }
        }
    }
}

