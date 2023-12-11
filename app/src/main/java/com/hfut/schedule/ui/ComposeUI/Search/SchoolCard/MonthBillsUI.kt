package com.hfut.schedule.ui.ComposeUI.Search.SchoolCard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.GetDate
import com.hfut.schedule.logic.SharePrefs.prefs
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
        val json = prefs.getString("monthbalance", MyApplication.NullSearch)
        val data = Gson().fromJson(json, BillMonthResponse::class.java)
        val bill = data.data
        val list = bill.map { (date,balance) -> BillMonth(date, balance) }
        return list
    }

    var clicked by remember { mutableStateOf(false) }
    var loading2 by remember { mutableStateOf(true) }
    var expand by remember { mutableStateOf(false) }
    val auth = prefs.getString("auth","")

    val date = GetDate.Date_yyyy_MM
    var input by remember { mutableStateOf(date) }

    fun Click() {
        CoroutineScope(Job()).apply {
            launch {
                async {
                    clicked = true
                    loading2 = true
                    vm.getMonthBills("bearer " + auth, input)
                }.await()
                async {
                    delay(500)
                    getbillmonth()
                    loading2 = false
                }
            }
        }
    }

   // DropdownMenu(expanded = expand, onDismissRequest = { expand = false }) {
       // LazyColumn{
       //     items(12){item ->
         //       DropdownMenuItem(text = { Text(text = "${item}月") }, onClick = { /*TODO*/ })
       //     }
       // }
   // }

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

            TextField(
                //  modifier = Modifier.size(width = 170.dp, height = 70.dp).padding(horizontal = 15.dp, vertical = 5.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                value = input,
                onValueChange = {
                    input = it
                    clicked = false
                },
                label = { Text("输入年月" ) },
                singleLine = true,
                trailingIcon = {
                    IconButton(
                        onClick = { Click() }) {
                        Icon(painter = painterResource(R.drawable.search), contentDescription = "description")
                    }
                },
                shape = MaterialTheme.shapes.medium,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
                    unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
                ),
            )
            Spacer(modifier = Modifier.height(500.dp))

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
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                                    TextField(
                                        //  modifier = Modifier.size(width = 170.dp, height = 70.dp).padding(horizontal = 15.dp, vertical = 5.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 15.dp, vertical = 5.dp),
                                        value = input,
                                        onValueChange = {
                                            input = it
                                            clicked = false
                                        },
                                        label = { Text("输入年月") },
                                        singleLine = true,
                                        trailingIcon = {
                                            IconButton(
                                                onClick = { Click() }) {
                                                Icon(painter = painterResource(R.drawable.search), contentDescription = "description")
                                            }
                                        },
                                        shape = MaterialTheme.shapes.medium,
                                        colors = TextFieldDefaults.textFieldColors(
                                            focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
                                            unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
                                        ),
                                    )
                                }
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