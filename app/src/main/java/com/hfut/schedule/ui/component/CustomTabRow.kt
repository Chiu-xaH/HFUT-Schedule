package com.hfut.schedule.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Campus
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.getCampus
import com.hfut.schedule.ui.style.RowHorizontal
import kotlinx.coroutines.launch

@Composable
fun CustomTabRow(pagerState: PagerState, titles: List<String>,modifier: Modifier = Modifier.padding(horizontal = 10.dp)) {
    val scope = rememberCoroutineScope()
    Column(modifier = modifier.background(Color.Transparent)) {
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

private const val HEFEI_TAB = 0
private const val XUANCHENG_TAB = 1
@Composable
fun CampusSelectTab(xuanChengContent : @Composable () -> Unit,heFeiContent : @Composable () -> Unit = {
    StatusUI(R.drawable.manga,"需要合肥校区在读生贡献数据源")
}) {
    val pagerState = rememberPagerState(pageCount = { 2 }, initialPage =
        when(getCampus()) {
            Campus.XUANCHENG -> XUANCHENG_TAB
            Campus.HEFEI -> HEFEI_TAB
        }
    )
    val titles = remember { listOf("合肥","宣城") }

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

