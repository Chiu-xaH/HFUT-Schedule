package com.hfut.schedule.ui.activity.card.bills

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
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.viewmodel.UIViewModel
import com.hfut.schedule.logic.utils.DateTimeUtils
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.ui.activity.card.bills.main.getBills
import com.hfut.schedule.ui.activity.card.bills.main.processTranamt
import com.hfut.schedule.ui.utils.components.AnimationCustomCard
import com.hfut.schedule.ui.utils.components.BillsIcons
import com.hfut.schedule.ui.utils.components.CustomTopBar
import com.hfut.schedule.ui.utils.components.MyCustomCard
import com.hfut.schedule.ui.utils.style.Round
import com.hfut.schedule.ui.utils.components.ScrollText
import com.hfut.schedule.ui.utils.components.StyleCardListItem
import com.hfut.schedule.ui.utils.components.TransplantListItem
import java.math.BigDecimal
import java.math.RoundingMode
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardRow(vm : NetWorkViewModel, vmUI : UIViewModel) {
    var todaypay = 0.0
    var date = DateTimeUtils.Date_yyyy_MM_dd

    for (item in 0 until getBills(vm).size) {
        val get = getBills(vm)[item].effectdateStr
        val name = getBills(vm)[item].resume
        val todaydate = get?.substringBefore(" ")
        var num = getBills(vm)[item].tranamt.toString()

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


    AnimationCustomCard(containerColor = MaterialTheme.colorScheme.errorContainer) {
        Row {
            TransplantListItem(
                headlineContent = { ScrollText(text = "余额 ￥${vmUI.CardValue.value?.now ?: now}") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(.5f)
                    .clickable { Starter.startAppUrl(MyApplication.ALIPAY_CARD_URL) },
                overlineContent = { ScrollText(text = "待圈存 ￥${vmUI.CardValue.value?.settle ?: settle}") },
                leadingContent = { Icon(painterResource(R.drawable.account_balance_wallet), contentDescription = "Localized description",) },
//                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.errorContainer)
            )
            TransplantListItem(
                headlineContent = { ScrollText(text = "￥${str}") },
                overlineContent = { Text(text = " 今日消费") },
                leadingContent = { Icon(painterResource(R.drawable.send_money), contentDescription = "Localized description",) },
//                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.errorContainer),
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
fun TodayBills(vm: NetWorkViewModel) {
//    val bills = getBills(vm)
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            CustomTopBar("今日消费")
        },
    ) {innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(getBills(vm).size) { item ->
                TodayCount(vm, item )
            }
        }
    }
}
@Composable
fun TodayCount(vm  : NetWorkViewModel, item : Int) {
    val bills = getBills(vm)[item]
    var name = bills.resume
    if (name.contains("有限公司")) name = name.replace("有限公司","")

    val time =bills.effectdateStr
    val getTime = time.substringBefore(" ")

    if(DateTimeUtils.Date_yyyy_MM_dd == getTime) {
        StyleCardListItem(
            headlineContent = { Text(text = name) },
            supportingContent = { Text(text = processTranamt(bills)) },
            overlineContent = { Text(text = time) },
            leadingContent = { BillsIcons(name) },
            modifier = Modifier.clickable {

            }
        )
    }
//        MyCustomCard(hasElevation = false, containerColor = ) {

//        }
}