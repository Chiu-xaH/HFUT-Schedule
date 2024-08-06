package com.hfut.schedule.ui.Activity.card.counts

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.ui.Activity.card.bills.main.BillItem
import com.hfut.schedule.ui.Activity.card.counts.main.monthCount
import com.hfut.schedule.ui.Activity.card.bills.todayCount
import com.hfut.schedule.ui.UIUtils.DevelopingUI
import com.hfut.schedule.ui.UIUtils.EmptyUI
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardHome(innerPadding : PaddingValues,vm : LoginSuccessViewModel,blur : Boolean) {
    val TAB_DAY = 0
    val TAB_WEEK = 1
    val TAB_MONTH = 2
    val TAB_TERM = 3
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 4 })
    Column(modifier = Modifier.padding(innerPadding)) {
        //Spacer(modifier = Modifier.height(5.dp))
        var state by remember { mutableStateOf(TAB_DAY) }
        val titles = listOf("日","周","月","学期")
        Column {
            TabRow(selectedTabIndex = pagerState.currentPage) {
                titles.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { scope.launch {
                            pagerState.animateScrollToPage(index)
                        } },
                        text = { Text(text = title) },
                        modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = if(blur).5f else 1f))
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        HorizontalPager(state = pagerState) { page ->
            Scaffold { it->
                if(page == TAB_MONTH) {
                    monthCount(vm)
                } else {
                    LazyColumn() {
                        when (page) {
                            TAB_DAY ->  {
                                if(BillItem(vm).size == 0) item {EmptyUI()}
                                else { items(BillItem(vm).size) { item -> todayCount(vm, item) } }
                            }

                            TAB_WEEK ->  item { DevelopingUI() }

                            TAB_TERM -> item { DevelopingUI() }
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(it))
            }
        }
    }
}



