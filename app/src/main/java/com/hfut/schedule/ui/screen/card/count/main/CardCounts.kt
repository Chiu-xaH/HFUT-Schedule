package com.hfut.schedule.ui.screen.card.count.main

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.CircularProgressIndicator
import com.hfut.schedule.ui.component.LoadingUI
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.logic.model.zjgd.BillMonth
import com.hfut.schedule.logic.model.zjgd.BillMonthResponse
import com.hfut.schedule.logic.util.sys.DateTimeUtils
import com.hfut.schedule.logic.util.storage.SharePrefs
import com.hfut.schedule.ui.screen.card.count.drawLineChart
import com.hfut.schedule.ui.component.appHorizontalDp
import com.hfut.schedule.ui.component.cardNormalColor
import com.hfut.schedule.ui.component.cardNormalDp
import com.hfut.schedule.ui.component.BottomSheetTopBar
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.component.SmallCard
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.style.bottomSheetRound
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

fun getbillmonth(vm : NetWorkViewModel, count : Boolean) : MutableList<BillMonth> {
    val json = vm.MonthData.value
    val lists = listOf(BillMonth("",0.0)).toMutableList()

    try {
        return if(json?.contains("操作成功") == true) {
            val data = Gson().fromJson(json, BillMonthResponse::class.java)
            val bill = data.data
            var list = bill.map { (date,balance) -> BillMonth(date, balance) }.toMutableList()
            val iterator = list.iterator()
            if(!count) {
                while (iterator.hasNext()) {
                    val billMonth = iterator.next()
                    if(DateTimeUtils.Date_yyyy_MM == billMonth.date.substring(0,7)) {
                        if(DateTimeUtils.Date_MM_dd.replace("-","").toInt() < billMonth.date.substringAfter("-").replace("-","").toInt()) {
                            iterator.remove() // 使用迭代器删除元素
                        }
                    }
                }
            }
            list
        } else lists
    } catch (e : Exception) {
        return lists
    }
}
@SuppressLint("SuspiciousIndentation")
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun monthCount(vm : NetWorkViewModel, innerPadding : PaddingValues) {

    var clicked by remember { mutableStateOf(false) }
    var loading2 by remember { mutableStateOf(true) }
    val auth = SharePrefs.prefs.getString("auth","")
    var input by remember { mutableStateOf("") }
  //  var sum by remember { mutableStateOf(0.0) }
  //  val big = BigDecimal(sum)
  //  var sum_float = big.toFloat()
    var Months  by remember { mutableStateOf(DateTimeUtils.Date_MM) }
    var Years by remember { mutableStateOf(DateTimeUtils.Date_yyyy) }
    input = "$Years-$Months"
    fun Click(Num : Int) {

        CoroutineScope(Job()).apply {
            launch {
                async {
                    Months = (Months.toInt() + Num).toString()
                    if(Months.toInt() < 10) Months = "0$Months"
                    input = "$Years-$Months"
                    clicked = true
                    loading2 = true
                    Handler(Looper.getMainLooper()).post{
                        vm.MonthData.value = "{}"
                    }
                    vm.getMonthBills("bearer $auth", input)
                }.await()
                async {
                    Handler(Looper.getMainLooper()).post{
                        vm.MonthData.observeForever { result ->
                            if (result != null) {
                                if(result.contains("操作成功")) {
                                    getbillmonth(vm,false)
                               //     sum = 0.0
                               //     for(i in 0 until getbillmonth(vm).size) {
                               //         var balance = getbillmonth(vm)[i].balance
                               //         balance /= 100
                               //         sum += balance
                              //      }
//
                                    loading2 = false
                                }
                            }
                        }
                    }
                }
            }
        }
    }


////////////////////////////////////////////////////布局///////////////////////////////////////////
    Box(modifier = Modifier
        .fillMaxSize()
    ) {

        // Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding()))

        var expanded by remember { mutableStateOf(true) }
        val sheetState = rememberModalBottomSheetState()
        var showBottomSheet by remember { mutableStateOf(false) }
        val scrollstate = rememberLazyGridState()
        val shouldShowAddButton = scrollstate.firstVisibleItemScrollOffset  == 0

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
                        BottomSheetTopBar("查询 ${Years}年${Months}月") {
                            FilledTonalIconButton(onClick = {
                                showBottomSheet = false
                                Click(0)
                            }) {
                                Icon(painter = painterResource(id = R.drawable.search), contentDescription = "")
                            }
                        }
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
                                    onClick = { Years = (DateTimeUtils.Date_yyyy.toInt() + (item - 3)).toString() },
                                    label = { Text(text = (DateTimeUtils.Date_yyyy.toInt() + (item - 3)).toString()) })
                            }
                        }
                        LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.padding(horizontal = 10.dp)) {
                            items(12) { item ->
                                AssistChip(
                                    onClick = { Months = if(item <= 10) "0${item + 1}" else "${item + 1}" },
                                    label = { Text(text = "${item + 1}月") },
                                    modifier = Modifier.padding(horizontal = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        if (clicked) {


            androidx.compose.animation.AnimatedVisibility(
                visible = loading2,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center)  {
                    LoadingUI()
                }
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = !loading2,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                //填充界面
                Column{

                        //   Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding()))
                        Spacer(modifier = Modifier.height(5.dp))


                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = appHorizontalDp(), vertical = cardNormalDp()),
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(containerColor = cardNormalColor())
                        ){
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.padding(14.dp,6.dp)){
                                drawLineChart(getbillmonth(vm,true))
                            }
                        }


                    ///////////////////////////////////////
                    var total = 0.0
                    for (i in 0 until getbillmonth(vm,false).size) {
                        var balance = getbillmonth(vm,false)[i].balance
                        balance = balance / 100
                        total += balance
                    }

                    val num = total.toString()
                    val bd = BigDecimal(num)
                    val str = bd.setScale(2, RoundingMode.HALF_UP).toString()

                    //Spacer(modifier = Modifier.height(50.dp))
                    val list = getbillmonth(vm,false)
                    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.padding(horizontal = appHorizontalDp() - cardNormalDp()), state = scrollstate){

                        item {
                            SmallCard(modifier = Modifier.padding(horizontal = cardNormalDp(), vertical = cardNormalDp())) {
                                    TransplantListItem(
                                        headlineContent = { Text(text = "￥$str") },
                                        overlineContent = { Text(text = "支出总和")},
                                        leadingContent = { Icon(painterResource(R.drawable.toll), contentDescription = "Localized description",) },
                                        colors = MaterialTheme.colorScheme.primaryContainer,
                                    )
                            }
                        }
                        item {
                            val big = BigDecimal((str.toFloat() / getbillmonth(vm,false).size).toString())
                            val sumFloat = big.setScale(2, RoundingMode.HALF_UP).toString()
                            SmallCard(modifier = Modifier.padding(horizontal = cardNormalDp(), vertical = cardNormalDp())) {
                                TransplantListItem(
                                    headlineContent = { Text(text = "￥$sumFloat") },
                                    overlineContent = { Text(text = "支出平均")},
                                    leadingContent = { Icon(painterResource(R.drawable.filter_vintage), contentDescription = "Localized description",) },
                                    colors =  MaterialTheme.colorScheme.primaryContainer,
                                )
                            }
                        }
                        items(list.size) { item ->
                            var balance = list[item].balance
                            balance /= 100
//                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center)
//                            {
                            SmallCard(modifier = Modifier.padding(horizontal = cardNormalDp(), vertical = cardNormalDp())){
                                    TransplantListItem(
                                        overlineContent = { Text(text = list[item].date) },
                                        headlineContent = { Text(text = "￥$balance") },
                                        leadingContent = { Icon(painter = painterResource(id = R.drawable.paid), contentDescription = "")}
                                    )
                                }
//                            }
                        }
                        items(if(list.size % 2 == 0) 1 else 2) { Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding())) }

                    }
                    //  LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.padding(horizontal = 5.dp), state = scrollstate) {
                    //  }
                }
            }
            ExtendedFloatingActionButton(
                text = { Text(text = "${Years} 年 ${Months} 月") },
                icon = { Icon(painter = painterResource(id = R.drawable.search), contentDescription = "") },
                onClick = { showBottomSheet = true },
                expanded = shouldShowAddButton,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                       .padding(innerPadding)
                    .padding(appHorizontalDp())
            )
            AnimatedVisibility(
                visible = shouldShowAddButton,
                enter = scaleIn() ,
                exit = scaleOut(),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                     .padding(innerPadding)
                    .padding(appHorizontalDp())
            ) {
                if (shouldShowAddButton) {
                    Row {
                        FloatingActionButton(
                            onClick = {
                                if(Months.toInt() > 1)
                                    Click(-1)
                                else showToast("请切换年份")
                            },
                        ) { Icon(Icons.Filled.ArrowBack, contentDescription = "") }
                        Spacer(modifier = Modifier.padding(horizontal =appHorizontalDp()))
                        FloatingActionButton(
                            onClick = {
                                if(Months.toInt() <= 11)
                                    Click(1)
                            },
                        ) { Icon(Icons.Filled.ArrowForward, contentDescription = "")}
                    }
                }
            }


        } else {
            ExtendedFloatingActionButton(
                text = { Text(text = "${Years} 年 ${Months} 月") },
                icon = { Icon(painter = painterResource(id = R.drawable.search), contentDescription = "") },
                onClick = { Click(0) },
                expanded = expanded,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(innerPadding)
                    .padding(appHorizontalDp())
            )
        }
    }
}