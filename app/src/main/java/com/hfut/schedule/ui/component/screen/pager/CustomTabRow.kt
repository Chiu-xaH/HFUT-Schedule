package com.hfut.schedule.ui.component.screen.pager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.TabRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.zIndex
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import kotlinx.coroutines.launch


@Composable
fun CustomTabRow(pagerState: PagerState, titles: List<String>) {
    if(titles.size <= 4 || titles.toString().length <= 25) {
        CustomSlidingTabRow(pagerState,titles)
    } else {
        CustomScrollTabRow(pagerState,titles)
    }
}

@Composable
private fun CustomSlidingTabRow(
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
                val selected = pagerState.currentPage == index
                Tab(
                    selected = selected,
                    text = {
                        Text(
                            text = title,
                            color = MaterialTheme.colorScheme.primary,
                            style = TextStyle(fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                        )
                    },
                    modifier = Modifier.padding(5.dp).zIndex(1f),
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
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier) {
        ScrollableTabRow(
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


                Row {
                    if(pagerState.currentPage == 0) {
                        Spacer(Modifier.width(APP_HORIZONTAL_DP - padding))
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(start = left + APP_HORIZONTAL_DP - padding, end = totalWidth - right + APP_HORIZONTAL_DP - padding)
                            .fillMaxHeight()
                            .padding(vertical = 5.dp)
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(APP_HORIZONTAL_DP)
                            )
                            .zIndex(0f)
                    )
                    if(pagerState.currentPage == titles.size-1) {
                        Spacer(Modifier.width(APP_HORIZONTAL_DP - padding))
                    }
                }
            },
            divider = {}
        ) {
            titles.forEachIndexed { index, title ->
                val selected = pagerState.currentPage == index
                Tab(
                    selected = selected,
                    text = {
                        Text(
                            text = title,
                            color = MaterialTheme.colorScheme.primary,
                            style = TextStyle(fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                        )
                    },
                    modifier = Modifier.padding(5.dp).zIndex(1f).padding(start = if(pagerState.currentPage == 0) APP_HORIZONTAL_DP-padding else 0.dp),
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