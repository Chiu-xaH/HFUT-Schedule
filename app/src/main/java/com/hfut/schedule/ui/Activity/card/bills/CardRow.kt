package com.hfut.schedule.ui.Activity.card.bills

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.ViewModel.UIViewModel
import com.hfut.schedule.logic.utils.GetDate
import com.hfut.schedule.logic.utils.StartApp
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.Activity.card.BillsIcons
import com.hfut.schedule.ui.Activity.card.bills.main.BillItem
import com.hfut.schedule.ui.Activity.card.bills.main.processTranamt
import com.hfut.schedule.ui.UIUtils.Round
import com.hfut.schedule.ui.UIUtils.ScrollText
import java.math.BigDecimal
import java.math.RoundingMode
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardRow(vm : LoginSuccessViewModel,vmUI : UIViewModel) {
    var todaypay = 0.0
    var date = GetDate.Date_yyyy_MM_dd

    for (item in 0 until BillItem(vm).size) {
        val get = BillItem(vm)[item].effectdateStr
        val name = BillItem(vm)[item].resume
        val todaydate = get?.substringBefore(" ")
        var num = BillItem(vm)[item].tranamt.toString()

        //优化0.0X元Bug
        if(num.length == 1)
            num = "00$num"


        num = num.substring(0, num.length - 2) + "." + num.substring(num.length - 2)

        val num_float = num.toFloat()

        if (date == todaydate) {
            if (!name.contains("充值")) todaypay += num_float
        }

    }
    val now = prefs.getString("card_now","00")
    val settle = prefs.getString("card_settle","00")
    val num = todaypay.toString()
    val bd = BigDecimal(num)
    val str = bd.setScale(2, RoundingMode.HALF_UP).toString()

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()


    if(showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            shape = Round(sheetState)
        ){
           TodayBills(vm)
        }
    }

    //添加间距
    Spacer(modifier = Modifier.height(5.dp))


    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 15.dp,
                vertical = 5.dp
            ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row {
            ListItem(
                headlineContent = { ScrollText(text = "余额 ￥${vmUI.CardValue.value?.now ?: now}") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(.5f)
                    .clickable { StartApp.openAlipay(MyApplication.AlipayCardURL) },
                overlineContent = { ScrollText(text = "待圈存 ￥${vmUI.CardValue.value?.settle ?: settle}") },
                leadingContent = { Icon(painterResource(R.drawable.account_balance_wallet), contentDescription = "Localized description",) },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.errorContainer)
            )
            ListItem(
                headlineContent = { ScrollText(text = "￥${str}") },
                overlineContent = { Text(text = " 今日消费") },
                leadingContent = { Icon(painterResource(R.drawable.send_money), contentDescription = "Localized description",) },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier
                    .clickable { showBottomSheet = true }
                    .weight(.5f)
            )
        }
    }
}
@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayBills(vm: LoginSuccessViewModel) {
    val bills = BillItem(vm)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                TopAppBar(
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = { Text("今日消费") },
                )
            }
        },
    ) {innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(BillItem(vm).size) { item ->
                todayCount(vm, item )
            }
        }
    }
}
@Composable
fun todayCount(vm  : LoginSuccessViewModel,item : Int) {
    val bills = BillItem(vm)[item]
    var name = bills.resume
    if (name.contains("有限公司")) name = name.replace("有限公司","")

    val time =bills.effectdateStr
    val getTime = time.substringBefore(" ")

    if(GetDate.Date_yyyy_MM_dd == getTime)
        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 15.dp,
                    vertical = 5.dp
                ),
            shape = MaterialTheme.shapes.medium
        ) {
            ListItem(
                headlineContent = { Text(text = name) },
                supportingContent = { Text(text = processTranamt(bills)) },
                overlineContent = { Text(text = time) },
                leadingContent = { BillsIcons(name) },
                modifier = Modifier.clickable {

                }
            )
        }
}