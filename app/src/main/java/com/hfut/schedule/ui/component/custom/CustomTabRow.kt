package com.hfut.schedule.ui.component.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hfut.schedule.ui.component.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.EmptyUI
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Campus
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.getCampus
import kotlinx.coroutines.launch


@Composable
fun CustomTabRow(pagerState: PagerState, titles: List<String>,padding : Dp = 10.dp) {
    if(titles.size >= 5) {
        // 可滑动
        CustomScrollTabRow(pagerState,titles,padding)
    } else {
        CustomNormalTabRow(pagerState,titles,padding)
    }
}
@Composable
private fun CustomNormalTabRow(pagerState: PagerState, titles: List<String>, padding : Dp = 10.dp) {
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.background(Color.Transparent).padding(padding)) {
        SecondaryTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color.Transparent,
            contentColor = TabRowDefaults.primaryContentColor,
            indicator = {},
            divider = {  }) {
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

