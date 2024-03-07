package com.hfut.schedule.ui.Activity.card.main.BottomBars

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.datamodel.zjgd.BillMonth
import com.hfut.schedule.logic.datamodel.zjgd.BillMonthResponse
import com.hfut.schedule.logic.utils.GetDate
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.ui.Activity.success.search.Search.SchoolCard.drawLineChart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

fun getbillmonth(vm : LoginSuccessViewModel) : List<BillMonth> {
    val json = vm.MonthData.value
    val lists = listOf(BillMonth("",0.0))

    return if(json?.contains("操作成功") == true) {
        val data = Gson().fromJson(json, BillMonthResponse::class.java)
        val bill = data.data
        val list = bill.map { (date,balance) -> BillMonth(date, balance) }
        list
    } else lists
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CardCounts(vm : LoginSuccessViewModel,innerPaddings : PaddingValues) {

    var clicked by remember { mutableStateOf(false) }
    var loading2 by remember { mutableStateOf(true) }
    val auth = SharePrefs.prefs.getString("auth","")
    var input by remember { mutableStateOf("") }

    var Months  by remember { mutableStateOf(GetDate.Date_MM) }
    var Years by remember { mutableStateOf(GetDate.Date_yyyy) }
    input = "$Years-$Months"
    var showitem_year by remember { mutableStateOf(false) }
    fun Click() {
        CoroutineScope(Job()).apply {
            launch {
                async {
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
                            if(result.contains("操作成功")) {
                                getbillmonth(vm)
                                loading2 = false
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

        Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding()))

        var expanded by remember { mutableStateOf(true) }
        val sheetState = rememberModalBottomSheetState()
        var showBottomSheet by remember { mutableStateOf(false) }
        val scrollstate = rememberLazyListState()
        val shouldShowAddButton = scrollstate.firstVisibleItemScrollOffset  == 0

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.mediumTopAppBarColors(
                                containerColor = Color.Transparent,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            title = { Text("查询 ${Years}年${Months}月") },
                            actions = {
                                FilledTonalIconButton(onClick = {
                                    showBottomSheet = false
                                    Click()
                                }) {
                                    Icon(painter = painterResource(id = R.drawable.search), contentDescription = "")
                                }
                            }
                        )
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
                                    onClick = { Years = (GetDate.Date_yyyy.toInt() + (item - 3)).toString() },
                                    label = { Text(text = (GetDate.Date_yyyy.toInt() + (item - 3)).toString()) })
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

                AnimatedVisibility(
                    visible = loading2,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center)  {
                        CircularProgressIndicator()
                    }
                }
                AnimatedVisibility(
                    visible = !loading2,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    //填充界面
                    Column{
                        //Spacer(modifier = Modifier.height(50.dp))
                        LazyColumn (state = scrollstate){
                            item {
                                Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding()))
                                Spacer(modifier = Modifier.height(5.dp))
                            }
                            item {
                                Card(
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 3.dp
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 15.dp, vertical = 5.dp),
                                    shape = MaterialTheme.shapes.medium
                                ){
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(modifier = Modifier.padding(14.dp,6.dp)){
                                        drawLineChart(getbillmonth(vm))
                                    }
                                }
                            }
                            item {
                                var total = 0.0
                                for (i in 0 until getbillmonth(vm).size) {
                                    var balance = getbillmonth(vm)[i].balance
                                    balance = balance / 100
                                    total += balance
                                }

                                val num = total.toString()
                                val bd = BigDecimal(num)
                                val str = bd.setScale(2, RoundingMode.HALF_UP).toString()

                                Card(
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 3.dp
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 15.dp, vertical = 5.dp),
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    ListItem(
                                        headlineContent = { Text(text = "月支出  $str 元") },
                                        leadingContent = {
                                            Icon(
                                                painterResource(R.drawable.credit_card),
                                                contentDescription = "Localized description",
                                            )
                                        },
                                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                    )
                                }
                            }
                            items(getbillmonth(vm).size) { item ->
                                var balance = getbillmonth(vm)[item].balance
                                balance /= 100
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center)
                                {
                                    Card(
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = 3.dp
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 15.dp, vertical = 5.dp),
                                        shape = MaterialTheme.shapes.medium
                                    ) {
                                        ListItem(
                                            headlineContent = { Text(text = getbillmonth(vm)[item].date) },
                                            supportingContent = { Text(text = "$balance 元") }
                                        )
                                    }
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(innerPaddings.calculateBottomPadding()))
                                Spacer(modifier = Modifier.height(5.dp))
                            }
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
                        .padding(innerPaddings)
                        .padding(15.dp)
                )
        } else {
            ExtendedFloatingActionButton(
                text = { Text(text = "${Years} 年 ${Months} 月") },
                icon = { Icon(painter = painterResource(id = R.drawable.search), contentDescription = "") },
                onClick = { Click() },
                expanded = expanded,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(innerPaddings)
                    .padding(15.dp)
            )
        }
    }
}