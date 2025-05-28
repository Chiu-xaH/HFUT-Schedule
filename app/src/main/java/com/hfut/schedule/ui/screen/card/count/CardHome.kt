package com.hfut.schedule.ui.screen.card.count

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.hfut.schedule.logic.util.network.UiState
import com.hfut.schedule.ui.component.DevelopingUI
import com.hfut.schedule.ui.component.EmptyUI
import com.hfut.schedule.ui.screen.card.bill.TodayCount
import com.hfut.schedule.ui.screen.card.count.main.MonthBillUI
import com.hfut.schedule.viewmodel.network.NetWorkViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardHome(innerPadding : PaddingValues, vm : NetWorkViewModel, pagerState : PagerState) {
    val TAB_DAY = 0
   // val TAB_WEEK = 1
    val TAB_MONTH = 1
    val TAB_TERM = 2
  //  val scope = rememberCoroutineScope()
  //  val pagerState = rememberPagerState(pageCount = { 4 })
    Column {
        Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
     //   var state by remember { mutableStateOf(TAB_DAY) }

//        Column {
//            TabRow(selectedTabIndex = pagerState.currentPage) {
//                titles.forEachIndexed { index, title ->
//                    Tab(
//                        selected = pagerState.currentPage == index,
//                        onClick = { scope.launch {
//                            pagerState.animateScrollToPage(index)
//                        } },
//                        text = { Text(text = title) },
//                        modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = if(blur) 0f else 1f))
//                    )
//                }
//            }
//        }
//        Spacer(modifier = Modifier.height(5.dp))
        val uiState by vm.huiXinBillResult.state.collectAsState()

        HorizontalPager(state = pagerState) { page ->
            Scaffold { it->
                if(page == TAB_MONTH) {
                    MonthBillUI(vm,innerPadding)
                } else {
                    LazyColumn() {
                        when (page) {
                            TAB_DAY ->  {
                                if(uiState is UiState.Success) {
                                    val list = (uiState as UiState.Success).data.data.records
                                    if(list.isEmpty()) item { EmptyUI() }
                                    else { items(list.size) { item -> TodayCount(list[item]) } }
                                }
                            }
                            TAB_TERM -> item { DevelopingUI() }
                        }
                        item { Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding())) }
                    }
                }
            }
        }
    }
}



