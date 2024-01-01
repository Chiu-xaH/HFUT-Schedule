package com.hfut.schedule.ui.ComposeUI.Search.SchoolCard

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.utils.GetDate
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.datamodel.zjgd.BillMonth
import com.hfut.schedule.logic.datamodel.zjgd.BillMonthResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthBillsUI(vm : LoginSuccessViewModel) {


    fun getbillmonth() : List<BillMonth> {
        val json = vm.MonthData.value
        var lists = listOf<BillMonth>(BillMonth("",0.0))

        if(json?.contains("操作成功") == true) {
            val data = Gson().fromJson(json, BillMonthResponse::class.java)
            val bill = data.data
            val list = bill.map { (date,balance) -> BillMonth(date, balance) }
            return list
        } else return lists
    }

    var clicked by remember { mutableStateOf(false) }
    var loading2 by remember { mutableStateOf(true) }
    val auth = prefs.getString("auth","")

    var input by remember { mutableStateOf("") }

    var Months  by remember { mutableStateOf(GetDate.Date_MM) }
    var Years by remember { mutableStateOf(GetDate.Date_yyyy) }

    input = "$Years-$Months"


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
                    vm.getMonthBills("bearer " + auth, input)
                }.await()
                async {
                    Handler(Looper.getMainLooper()).post{
                        vm.MonthData.observeForever { result ->
                            if(result.contains("操作成功")) {
                                getbillmonth()
                                loading2 = false
                            }
                        }
                    }
                }
            }
        }
    }


    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

            Spacer(modifier = Modifier.height(500.dp))
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("月份查询") }
                    )
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    var showitem by remember { mutableStateOf(false) }
                    DropdownMenu(expanded = showitem, onDismissRequest = { showitem = false }, offset = DpOffset(103.dp,0.dp)) {
                        DropdownMenuItem(text = { Text(text = "一月") }, onClick = { Months =  "01"
                            showitem = false})
                        DropdownMenuItem(text = { Text(text = "二月") }, onClick = {  Months =  "02"
                            showitem = false})
                        DropdownMenuItem(text = { Text(text = "三月") }, onClick = {  Months =  "03"
                            showitem = false})
                        DropdownMenuItem(text = { Text(text = "四月") }, onClick = {  Months =  "04"
                            showitem = false})
                        DropdownMenuItem(text = { Text(text = "五月") }, onClick = {  Months =  "05"
                            showitem = false})
                        DropdownMenuItem(text = { Text(text = "六月") }, onClick = {  Months =  "06"
                            showitem = false})
                        DropdownMenuItem(text = { Text(text = "七月") }, onClick = {  Months =  "07"
                            showitem = false})
                        DropdownMenuItem(text = { Text(text = "八月") }, onClick = {  Months =  "08"
                            showitem = false})
                        DropdownMenuItem(text = { Text(text = "九月") }, onClick = {  Months =  "09"
                            showitem = false})
                        DropdownMenuItem(text = { Text(text = "十月") }, onClick = {  Months =  "10"
                            showitem = false})
                        DropdownMenuItem(text = { Text(text = "十一月") }, onClick = {  Months = "11"
                            showitem = false})
                        DropdownMenuItem(text = { Text(text = "十二月") }, onClick = {  Months = "12"
                            showitem = false})
                    }


                    var showitem_year by remember { mutableStateOf(false) }
                    DropdownMenu(expanded = showitem_year, onDismissRequest = { showitem_year = false }, offset = DpOffset(15.dp,0.dp)) {
                        DropdownMenuItem(text = { Text(text =(GetDate.Date_yyyy.toInt() - 3).toString()) }, onClick = {  Years = (GetDate.Date_yyyy.toInt() - 3).toString()
                            showitem_year = false})
                        DropdownMenuItem(text = { Text(text = (GetDate.Date_yyyy.toInt() - 2).toString()) }, onClick = {  Years =  (GetDate.Date_yyyy.toInt() - 2).toString()
                            showitem_year = false})
                        DropdownMenuItem(text = { Text(text = (GetDate.Date_yyyy.toInt() - 1).toString()) }, onClick = {  Years =  (GetDate.Date_yyyy.toInt() - 1).toString()
                            showitem_year = false})
                        DropdownMenuItem(text = { Text(text = GetDate.Date_yyyy.toInt().toString()) }, onClick = {  Years =  GetDate.Date_yyyy.toInt().toString()
                            showitem_year = false})
                        DropdownMenuItem(text = { Text(text = (GetDate.Date_yyyy.toInt() + 1).toString()) }, onClick = {  Years =  (GetDate.Date_yyyy.toInt() + 1).toString()
                            showitem_year = false})
                        DropdownMenuItem(text = { Text(text = (GetDate.Date_yyyy.toInt() + 2).toString()) }, onClick = {  Years =  (GetDate.Date_yyyy.toInt() + 2).toString()
                            showitem_year = false})
                        DropdownMenuItem(text = { Text(text = (GetDate.Date_yyyy.toInt() + 3).toString()) }, onClick = { Years =  (GetDate.Date_yyyy.toInt() + 3).toString()
                            showitem_year = false})
                    }


                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 0.dp), horizontalArrangement = Arrangement.Start){


                        AssistChip(
                            onClick = { showitem_year = true },
                            label = { Text(text = "${Years} 年") },
                        )

                        Spacer(modifier = Modifier.width(10.dp))


                        AssistChip(
                            onClick = {showitem = true},
                            label = { Text(text = "${Months} 月") },
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        AssistChip(
                            onClick = {Click()},
                            label = { Text(text = "搜索") },
                            leadingIcon = {Icon(painter = painterResource(R.drawable.search), contentDescription = "description")},
                        )
                    }

                    if (clicked) {

                        AnimatedVisibility(
                            visible = loading2,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center)  {
                                Spacer(modifier = Modifier.height(5.dp))
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(50.dp))
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
                                LazyColumn {
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
                                                drawLineChart(getbillmonth())
                                            }

                                        }

                                    }

                                    item {
                                        var total = 0.0
                                        for (i in 0 until getbillmonth().size) {
                                            var balance = getbillmonth()[i].balance
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
                                                headlineContent = { Text(text = "月支出  ${str} 元") },
                                                leadingContent = {
                                                    Icon(
                                                        painterResource(R.drawable.credit_card),
                                                        contentDescription = "Localized description",
                                                    )
                                                },
                                                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.errorContainer)
                                            )
                                        }
                                    }

                                    items(getbillmonth().size) { item ->
                                        var balance = getbillmonth()[item].balance
                                        balance = balance / 100
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
                                                    headlineContent = { Text(text = getbillmonth()[item].date) },
                                                    supportingContent = { Text(text = balance.toString() + " 元") },
                                                    leadingContent = {
                                                        Icon(
                                                            painterResource(R.drawable.paid),
                                                            contentDescription = "Localized description",
                                                        )
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}