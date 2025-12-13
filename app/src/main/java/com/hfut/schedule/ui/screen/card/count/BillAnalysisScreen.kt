package com.hfut.schedule.ui.screen.card.count

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.huixin.BillMonth
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.status.EmptyIcon
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.SmallCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.screen.card.bill.TodayCount
import com.hfut.schedule.ui.screen.card.bill.main.BillsInfo
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.ui.theme.greenColor
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.shared.model.BillRecordBean
import com.xah.uicommon.style.align.CenterScreen
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch

private const val TAB_ALL = 0
private const val TAB_DAY = 1
private const val TAB_MONTH = 2
private const val TAB_TERM = 3

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BillAnalysisScreen(innerPadding : PaddingValues, vm : NetWorkViewModel, pagerState : PagerState,hazeState: HazeState) =
    Column {
        HorizontalPager(state = pagerState) { page ->
            Scaffold { it->
                when(page) {
                    TAB_DAY -> TodayBillScreen(vm,innerPadding, hazeState)
                    TAB_MONTH -> MonthBillNewScreen(vm,innerPadding)
                    TAB_TERM -> YearBillNewScreen(vm,innerPadding)
                    TAB_ALL -> PredictedScreen(vm,innerPadding,pagerState)
                }
            }
        }
    }

@Composable
private fun TodayBillScreen(vm: NetWorkViewModel,innerPadding: PaddingValues,hazeState : HazeState) {
    val uiState by vm.huiXinBillResult.state.collectAsState()
    CommonNetworkScreen(uiState, onReload = null) {
        var infoNum by remember { mutableStateOf<BillRecordBean?>(null) }
        var showBottomSheet by remember { mutableStateOf(false) }

        if(showBottomSheet && infoNum != null) {
            HazeBottomSheet (
                onDismissRequest = { showBottomSheet = false },
                autoShape = false,
                showBottomSheet = showBottomSheet,
                hazeState = hazeState
            ){
                BillsInfo(infoNum!!)
            }
        }

        val list = (uiState as UiState.Success).data.records
        if(list.isEmpty()) {
            CenterScreen {
                EmptyIcon()
            }
        } else {
            LazyColumn() {
                item { InnerPaddingHeight(innerPadding,true) }
                items(list.size) { item ->
                    val bean = list[item]
                    TodayCount(bean) {
                        infoNum = bean
                        showBottomSheet = true
                    }
                }
                item { InnerPaddingHeight(innerPadding,false) }
            }
        }
    }
}

@Composable
private fun MonthBillNewScreen(vm : NetWorkViewModel,innerPadding: PaddingValues) {
    val uiState by vm.cardPredictedResponse.state.collectAsState()

    var refreshNetwork : suspend () -> Unit = rN@ {
        if(uiState is UiState.Success) {
            return@rN
        }
        val auth = prefs.getString("auth","")
        vm.cardPredictedResponse.clear()
        vm.getCardPredicted("bearer $auth")
    }
    LaunchedEffect(Unit) {
        refreshNetwork()
    }
    CommonNetworkScreen(uiState, onReload = refreshNetwork, loadingText = "正在拉取全部账单") {
        val data = (uiState as UiState.Success).data
        val day = data.day
        val dayList = day.analyzeData.statisticalData.toList()
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(horizontal = 11.dp),
        ) {
            items(2) {
                InnerPaddingHeight(innerPadding,true)
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding( bottom = CARD_NORMAL_DP),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = cardNormalColor())
                ){
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.padding(14.dp,6.dp)){
                        drawLineChart(
                            dayList.reversed().map { BillMonth(it.first,it.second) }
                        )
                    }
                }
            }
            items(dayList.size, key = { it }) { index ->
                val item = dayList[index]
                SmallCard (
                    modifier = Modifier.padding(CARD_NORMAL_DP),
                ){
                    TransplantListItem(
                        headlineContent = {
                            Text("￥${formatDecimal(item.second,2)}")
                        },
                        overlineContent = {
                            Text(item.first)
                        },
                    )
                }
            }
            items(2) {
                InnerPaddingHeight(innerPadding,false)
            }
        }
    }
}

@Composable
private fun YearBillNewScreen(vm : NetWorkViewModel,innerPadding: PaddingValues) {
    val uiState by vm.cardPredictedResponse.state.collectAsState()

    var refreshNetwork : suspend () -> Unit = rN@ {
        if(uiState is UiState.Success) {
            return@rN
        }
        val auth = prefs.getString("auth","")
        vm.cardPredictedResponse.clear()
        vm.getCardPredicted("bearer $auth")
    }
    LaunchedEffect(Unit) {
        refreshNetwork()
    }
    CommonNetworkScreen(uiState, onReload = refreshNetwork,loadingText = "正在拉取全部账单") {
        val data = (uiState as UiState.Success).data
        val month = data.month
        val monthList = month.analyzeData.statisticalData.toList()

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(horizontal = 11.dp),
        ) {
            items(2) {
                InnerPaddingHeight(innerPadding,true)
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding( bottom = CARD_NORMAL_DP),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = cardNormalColor())
                ){
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.padding(14.dp,6.dp)){
                        drawLineChart(
                            monthList.reversed().map { BillMonth(it.first,it.second) }
                        )
                    }
                }
            }
            items(monthList.size, key = { it }) { index ->
                val item = monthList[index]
                SmallCard(
                    modifier = Modifier.padding(CARD_NORMAL_DP),
                ) {
                    TransplantListItem(
                        headlineContent = {
                            Text("￥${formatDecimal(item.second,2)}")
                        },
                        overlineContent = {
                            Text(item.first)
                        }
                    )
                }
            }
            items(2) { InnerPaddingHeight(innerPadding,false) }
        }
    }
}

@Composable
private fun PredictedScreen(vm: NetWorkViewModel,innerPadding: PaddingValues,pagerState : PagerState) {
    val uiState by vm.cardPredictedResponse.state.collectAsState()
    var refreshNetwork : suspend () -> Unit = rN@ {
        if(uiState is UiState.Success) {
            return@rN
        }
        val auth = prefs.getString("auth","")
        vm.cardPredictedResponse.clear()
        vm.getCardPredicted("bearer $auth")
    }
    LaunchedEffect(Unit) {
        refreshNetwork()
    }
    val scope = rememberCoroutineScope()
    CommonNetworkScreen(uiState, onReload = refreshNetwork,loadingText = "正在拉取全部账单") {
        val data = (uiState as UiState.Success).data
        val day = data.day
        val month = data.month
        val dayList = day.analyzeData.statisticalData.toList()
        val monthList = month.analyzeData.statisticalData.toList()
        val dailyAvg = day.analyzeData.average
        val monthAvg = month.analyzeData.average
        val today = dayList.first().let {
            if(it.first != DateTimeManager.Date_yyyy_MM_dd) 0.0 else it.second
        }
        val difference = dailyAvg-today
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            InnerPaddingHeight(innerPadding,true)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = APP_HORIZONTAL_DP, vertical = CARD_NORMAL_DP),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = cardNormalColor())
            ){
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.padding(14.dp,6.dp)){
                    drawLineChart(
                        dayList.reversed().map { BillMonth(it.first,it.second) }
                    )
                }
            }
            DividerTextExpandedWith("日") {
                CustomCard(color = cardNormalColor()) {
                    if(today == 0.0) {
                        TransplantListItem(
                            overlineContent = {
                                Text("今日")
                            },
                            headlineContent = {
                                Text("尚未消费", fontWeight = FontWeight.Bold)
                            },
                            leadingContent = {
                                Icon(painterResource(R.drawable.send_money),null)
                            },
                            trailingContent = {
                                Button(
                                    onClick = {
                                        // 进入某一页
                                        scope.launch{ pagerState.animateScrollToPage(TAB_MONTH) }
                                    }
                                ) {
                                    Text("详情")
                                }
                            }
                        )
                    } else {
                        val t = if(difference < 0) {
                            "偏高￥${formatDecimal(today-dailyAvg,2)}"
                        } else if(difference > 0) {
                            "偏低￥${formatDecimal(dailyAvg-today,2)}"
                        } else {
                            ""
                        }
                        TransplantListItem(
                            overlineContent = {
                                Text("今日(${t})")
                            },
                            headlineContent = {
                                Text("￥${formatDecimal(today,2)}", fontWeight = FontWeight.Bold)
                            },
                            leadingContent = {
                                Icon(
                                    painterResource(
                                        if(difference < 0) {
                                            R.drawable.arrow_upward
                                        } else if(difference > 0) {
                                            R.drawable.arrow_downward
                                        } else {
                                            R.drawable.send_money
                                        }
                                    ),
                                    null,
                                    tint = if(difference < 0) {
                                        MaterialTheme.colorScheme.error
                                    } else if(difference > 0) {
                                        greenColor()
                                    } else {
                                        LocalContentColor.current
                                    }
                                )
                            },
                            trailingContent = {
                                Button(
                                    onClick = {
                                        // 进入某一页
                                        scope.launch{ pagerState.animateScrollToPage(TAB_MONTH) }
                                    }
                                ) {
                                    Text("详情")
                                }
                            }
                        )
                    }
                    Row {
                        TransplantListItem(
                            overlineContent = {
                                Text("日平均")
                            },
                            headlineContent = {
                                Text("￥${formatDecimal(dailyAvg,2)}")
                            },
                            leadingContent = {
                                Icon(painterResource(R.drawable.filter_vintage),null)
                            },
                            modifier = Modifier.weight(.5f)
                        )
                        TransplantListItem(
                            overlineContent = {
                                Text("明日预计")
                            },
                            headlineContent = {
                                Text("￥${formatDecimal(day.predictData.predict,2)}")
                            },
                            leadingContent = {
                                Icon(painterResource(R.drawable.azm),null)
                            },
                            modifier = Modifier.weight(.5f)
                        )
                    }
                }
            }
            DividerTextExpandedWith("月") {
                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(
                        overlineContent = {
                            Text("本月")
                        },
                        headlineContent = {
                            Text("￥${formatDecimal(monthList.first().second,2)}", fontWeight = FontWeight.Bold)
                        },
                        leadingContent = {
                            Icon(painterResource(R.drawable.send_money),null)
                        },
                        trailingContent = {
                            Button(
                                onClick = {
                                    // 进入某一页
                                    scope.launch{ pagerState.animateScrollToPage(TAB_TERM) }
                                }
                            ) {
                                Text("详情")
                            }
                        }
                    )
                    Row {
                        TransplantListItem(
                            overlineContent = {
                                Text("月平均")
                            },
                            headlineContent = {
                                Text("￥${formatDecimal(monthAvg,2)}")
                            },
                            leadingContent = {
                                Icon(painterResource(R.drawable.filter_vintage),null)
                            },
                            modifier = Modifier.weight(.5f)
                        )
                        TransplantListItem(
                            overlineContent = {
                                Text("下月预计")
                            },
                            headlineContent = {
                                Text("￥${formatDecimal(month.predictData.predict,2)}")
                            },
                            leadingContent = {
                                Icon(painterResource(R.drawable.azm),null)
                            },
                            modifier = Modifier.weight(.5f)
                        )
                    }
                }
            }
            InnerPaddingHeight(innerPadding,false)
        }
    }
}



