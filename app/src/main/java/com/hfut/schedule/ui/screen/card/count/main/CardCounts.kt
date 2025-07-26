package com.hfut.schedule.ui.screen.card.count.main

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.zjgd.BillMonth
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.text.BottomSheetTopBar
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.container.SmallCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.screen.card.count.drawLineChart
import com.hfut.schedule.ui.style.InnerPaddingHeight
import com.hfut.schedule.ui.style.bottomSheetRound
import com.hfut.schedule.viewmodel.network.NetWorkViewModel

private fun withoutMonthBills(originalList : List<BillMonth>) : List<BillMonth> {
    val list = originalList.toMutableList()
    val iterator = list.iterator()
    while (iterator.hasNext()) {
        val billMonth = iterator.next()
        if(DateTimeManager.Date_yyyy_MM == billMonth.date.substring(0,7)) {
            if(DateTimeManager.Date_MM_dd.replace("-","").toInt() < billMonth.date.substringAfter("-").replace("-","").toInt()) {
                iterator.remove() // 使用迭代器删除元素
            }
        }
    }
    return list
}


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthBillUI(vm : NetWorkViewModel, innerPadding : PaddingValues) {
    var month  by remember { mutableIntStateOf(DateTimeManager.Date_MM.toInt()) }
    var year by remember { mutableStateOf(DateTimeManager.Date_yyyy) }
    val uiState by vm.huiXinMonthBillResult.state.collectAsState()
    val refreshNetwork: suspend () -> Unit = {
        val auth = SharedPrefs.prefs.getString("auth","")
        val input = "$year-" + if(month.toInt() < 10) "0$month" else month.toString()
        vm.huiXinMonthBillResult.clear()
        vm.getMonthBills("bearer $auth", input)
    }

    LaunchedEffect(Unit,month,year) {
        refreshNetwork()
    }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val scrollstate = rememberLazyGridState()
    val shouldShowAddButton by remember { derivedStateOf{ scrollstate.firstVisibleItemScrollOffset == 0 } }
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            shape = bottomSheetRound(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    BottomSheetTopBar("查询 ${year}年${month}月")
                },
            ) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    LazyRow(modifier = Modifier.padding(horizontal = 10.dp)) {
                        items(7) { item ->
                            AssistChip(
                                modifier = Modifier.padding(horizontal = 5.dp),
                                onClick = { year = (DateTimeManager.Date_yyyy.toInt() + (item - 3)).toString() },
                                label = { Text(text = (DateTimeManager.Date_yyyy.toInt() + (item - 3)).toString()) })
                        }
                    }
                    LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.padding(horizontal = 10.dp)) {
                        items(12) { item ->
                            AssistChip(
                                onClick = { month = item+1 },
                                label = { Text(text = "${item + 1}月") },
                                modifier = Modifier.padding(horizontal = 5.dp)
                            )
                        }
                    }
                }
            }
        }
    }
    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        val list = (uiState as UiState.Success).data
        val filteredList = withoutMonthBills(list)
        //填充界面
        Box(modifier = Modifier.fillMaxSize()) {
            Column{
                InnerPaddingHeight(innerPadding,true)
                Spacer(modifier = Modifier.height(5.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = APP_HORIZONTAL_DP, vertical = CARD_NORMAL_DP),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = cardNormalColor())
                ){
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.padding(14.dp,6.dp)){
                        drawLineChart(list)
                    }
                }


                ///////////////////////////////////////
                val total by produceState(initialValue = 0.0) {
                    for (i in 0 until filteredList.size) {
                        var balance = filteredList[i].balance
                        balance = balance / 100
                        value += balance
                    }
                }


                LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP - CARD_NORMAL_DP), state = scrollstate){

                    item {
                        SmallCard(modifier = Modifier.padding(horizontal = CARD_NORMAL_DP, vertical = CARD_NORMAL_DP)) {
                            TransplantListItem(
                                headlineContent = { Text(text = "￥${formatDecimal(total,2)}") },
                                overlineContent = { Text(text = "支出总和")},
                                leadingContent = { Icon(painterResource(R.drawable.toll), contentDescription = "Localized description",) },
                                colors = MaterialTheme.colorScheme.primaryContainer,
                            )
                        }
                    }
                    item {
//                        val big = BigDecimal()
                        val sumFloat = formatDecimal((total / filteredList.size),2)
                        SmallCard(modifier = Modifier.padding(horizontal = CARD_NORMAL_DP, vertical = CARD_NORMAL_DP)) {
                            TransplantListItem(
                                headlineContent = { Text(text = "￥$sumFloat") },
                                overlineContent = { Text(text = "支出平均")},
                                leadingContent = { Icon(painterResource(R.drawable.filter_vintage), contentDescription = "Localized description",) },
                                colors =  MaterialTheme.colorScheme.primaryContainer,
                            )
                        }
                    }
                    items(filteredList.size) { item ->
                        var balance = filteredList[item].balance
                        balance /= 100
                        SmallCard(modifier = Modifier.padding(horizontal = CARD_NORMAL_DP, vertical = CARD_NORMAL_DP)){
                            TransplantListItem(
                                overlineContent = { Text(text = filteredList[item].date) },
                                headlineContent = { Text(text = "￥$balance") },
                                leadingContent = { Icon(painter = painterResource(id = R.drawable.paid), contentDescription = "")}
                            )
                        }
                    }
                    items(if(filteredList.size % 2 == 0) 1 else 2) { InnerPaddingHeight(innerPadding,false) }

                }
            }
            ExtendedFloatingActionButton(
                text = { Text(text = "$year 年 $month 月") },
                icon = { Icon(painter = painterResource(id = R.drawable.search), contentDescription = "") },
                onClick = { showBottomSheet = true },
                expanded = shouldShowAddButton,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(innerPadding)
                    .padding(APP_HORIZONTAL_DP)
            )
            AnimatedVisibility(
                visible = shouldShowAddButton,
                enter = scaleIn() ,
                exit = scaleOut(),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(innerPadding)
                    .padding(APP_HORIZONTAL_DP)
            ) {
                Row {
                    FloatingActionButton(
                        onClick = {
                            if(month.toInt() > 1)
                                month--
                            else showToast("请切换年份")
                        },
                    ) { Icon(Icons.Filled.ArrowBack, contentDescription = "") }
                    Spacer(modifier = Modifier.padding(horizontal =APP_HORIZONTAL_DP))
                    FloatingActionButton(
                        onClick = {
                            if(month.toInt() <= 11)
                                month++
                        },
                    ) { Icon(Icons.Filled.ArrowForward, contentDescription = "")}
                }
            }
        }
    }
}