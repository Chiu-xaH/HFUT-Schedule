package com.hfut.schedule.ui.screen.card.count

import android.annotation.SuppressLint
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.zjgd.BillMonth
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.status.EmptyUI
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.SmallCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.screen.card.bill.TodayCount
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.ui.theme.greenColor
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import kotlinx.coroutines.launch

private const val TAB_ALL = 0
private const val TAB_DAY = 1
private const val TAB_MONTH = 2
private const val TAB_TERM = 3

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class)

@Composable
fun CardHome(innerPadding : PaddingValues, vm : NetWorkViewModel, pagerState : PagerState) {
    val uiState by vm.huiXinBillResult.state.collectAsState()
    Column {
        HorizontalPager(state = pagerState) { page ->
            Scaffold { it->
                when(page) {
                    TAB_DAY -> {
                        LazyColumn() {
                            item { InnerPaddingHeight(innerPadding,true) }
                            if(uiState is UiState.Success) {
                                val list = (uiState as UiState.Success).data.records
                                if(list.isEmpty()) item { EmptyUI() }
                                else { items(list.size) { item -> TodayCount(list[item]) } }
                            }
                            item { InnerPaddingHeight(innerPadding,false) }
                        }
                    }
                    TAB_MONTH -> {
//                        MonthBillUI(vm,innerPadding)
                        MonthBillNewScreen(vm,innerPadding)
                    }
                    TAB_TERM -> {
                        YearBillNewScreen(vm,innerPadding)
                    }
                    TAB_ALL -> {
                        PredictedScreen(vm,innerPadding,pagerState)
                    }
                }
            }
        }
    }
}

@Composable
fun MonthBillNewScreen(vm : NetWorkViewModel,innerPadding: PaddingValues) {
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
    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        val data = (uiState as UiState.Success).data
        val day = data.day
        val dayList = day.statisticalData.reversed()
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(11.dp),
        ) {
            items(2) {
                InnerPaddingHeight(innerPadding,true)
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding( vertical = CARD_NORMAL_DP),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = cardNormalColor())
                ){
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.padding(14.dp,6.dp)){
                        drawLineChart(
                            dayList.reversed().map { BillMonth(it.date,it.amount) }
                        )
                    }
                }
            }
            items(dayList.size, key = { dayList[it].date }) { index ->
                with(dayList[index]) {
                    SmallCard (
                        modifier = Modifier.padding(CARD_NORMAL_DP),
                    ){
                        TransplantListItem(
                            headlineContent = {
                                Text("￥${amount}")
                            },
                            overlineContent = {
                                Text(date)
                            },
                        )
                    }
                }
            }
            items(2) {
                InnerPaddingHeight(innerPadding,false)
            }
        }
    }
}

@Composable
fun YearBillNewScreen(vm : NetWorkViewModel,innerPadding: PaddingValues) {
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
    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        val data = (uiState as UiState.Success).data
        val month = data.month
        val monthList = month.statisticalData

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(11.dp),
        ) {
            items(2) {
                InnerPaddingHeight(innerPadding,true)
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding( vertical = CARD_NORMAL_DP),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = cardNormalColor())
                ){
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.padding(14.dp,6.dp)){
                        drawLineChart(
                            monthList.reversed().map { BillMonth(it.date,it.amount) }
                        )
                    }
                }
            }
            items(monthList.size, key = { monthList[it].date }) { index ->
                with(monthList[index]) {
                    SmallCard(
                        modifier = Modifier.padding(CARD_NORMAL_DP),
                    ) {
                        TransplantListItem(
                            headlineContent = {
                                Text("￥${amount}")
                            },
                            overlineContent = {
                                Text(date)
                            }
                        )
                    }
                }
            }
            items(2) { InnerPaddingHeight(innerPadding,false) }
        }
    }
}

@Composable
fun PredictedScreen(vm: NetWorkViewModel,innerPadding: PaddingValues,pagerState : PagerState) {
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
    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        val data = (uiState as UiState.Success).data
        val day = data.day
        val month = data.month
        val dayList = day.statisticalData.reversed()
        val monthList = month.statisticalData
        val dailyAvg = day.averageData
        val today = dayList.first().let {
            if(it.date != DateTimeManager.Date_yyyy_MM_dd) 0.0 else it.amount
        }
        val difference = dailyAvg-today
        Column {
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
                        dayList.reversed().map { BillMonth(it.date,it.amount) }
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
                                Text("￥${today}", fontWeight = FontWeight.Bold)
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
                                Text("￥${dailyAvg}")
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
                                Text("￥${day.predictedData}")
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
                            Text("￥${monthList.first().amount}", fontWeight = FontWeight.Bold)
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
                                Text("￥${month.averageData}")
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
                                Text("￥${month.predictedData}")
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



