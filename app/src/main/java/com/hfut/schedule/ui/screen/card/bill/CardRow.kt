package com.hfut.schedule.ui.screen.card.bill

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import com.hfut.schedule.logic.model.zjgd.records
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.container.AnimationCustomCard
import com.hfut.schedule.ui.component.icon.BillsIcons
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.text.ScrollText
import com.hfut.schedule.ui.component.container.StyleCardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.screen.card.bill.main.processTranamt
import com.hfut.schedule.ui.screen.card.function.main.loadTodayPay
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardRow(vm : NetWorkViewModel, vmUI : UIViewModel, hazeState: HazeState) {
    val cardValue by remember { derivedStateOf { vmUI.cardValue } }
    val now = prefs.getString("card_now","00")
    val settle = prefs.getString("card_settle","00")
    val str by loadTodayPay(vm)

    var showBottomSheet by remember { mutableStateOf(false) }

    if(showBottomSheet) {
        HazeBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            isFullExpand = false,
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
        ){
           TodayBills(vm)
        }
    }

    //添加间距
    Spacer(modifier = Modifier.height(5.dp))

    AnimationCustomCard(containerColor = MaterialTheme.colorScheme.errorContainer) {
        Row {
            TransplantListItem(
                headlineContent = { ScrollText(text = "余额 ￥${cardValue?.now ?: now}") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(.5f)
                    .clickable { Starter.startAppUrl(MyApplication.ALIPAY_CARD_URL) },
                overlineContent = { ScrollText(text = "待圈存 ￥${cardValue?.settle ?: settle}") },
                leadingContent = { Icon(painterResource(R.drawable.account_balance_wallet), contentDescription = "Localized description",) },
            )
            TransplantListItem(
                headlineContent = { ScrollText(text = "￥${str}") },
                overlineContent = { Text(text = " 今日消费") },
                leadingContent = { Icon(painterResource(R.drawable.send_money), contentDescription = "Localized description",) },
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
    val uiState by vm.huiXinBillResult.state.collectAsState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            HazeBottomSheetTopBar("今日消费")
        },
    ) {innerPadding ->
        if(uiState is UiState.Success) {
            val list = (uiState as UiState.Success).data.records
            LazyColumn(modifier = Modifier.padding(innerPadding)) {
                items(list.size) { item ->
                    TodayCount(list[item] )
                }
            }
        }
    }
}
@Composable
fun TodayCount(item : records) = with(item) {
    var name = resume
    if (name.contains("有限公司")) name = name.replace("有限公司","")

    val time = effectdateStr
    val getTime = time.substringBefore(" ")

    if(DateTimeManager.Date_yyyy_MM_dd == getTime) {
        StyleCardListItem(
            headlineContent = { Text(text = name) },
            supportingContent = { Text(text = processTranamt(item)) },
            overlineContent = { Text(text = time) },
            leadingContent = { BillsIcons(name) },
            modifier = Modifier.clickable {

            }
        )
    }
}