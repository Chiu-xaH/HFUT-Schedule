package com.hfut.schedule.ui.screen.card.bill.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.zjgd.records
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.ui.component.container.AnimationCardListItem
import com.hfut.schedule.ui.component.icon.BillsIcons
import com.hfut.schedule.ui.component.text.BottomTip
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.container.MyCustomCard
import com.hfut.schedule.ui.component.screen.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.PagingController
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.navigationBarHeightPadding
import com.hfut.schedule.ui.component.screen.RefreshIndicator
import com.hfut.schedule.ui.screen.card.bill.CardRow
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.InnerPaddingHeight
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CardBills(vm : NetWorkViewModel, innerPaddings : PaddingValues, vmUI : UIViewModel, hazeState : HazeState) {
    val auth = prefs.getString("auth","")
    var page by remember { mutableIntStateOf(1) }
    val uiState by vm.huiXinBillResult.state.collectAsState()
    val refreshNetwork : suspend () -> Unit = refreshNetwork@ {
        // 有缓存
        vm.huiXinBillResult.clear()
        vm.getCardBill("bearer $auth",page)
    }
    val refreshing = uiState is UiState.Loading
    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        scope.launch {
            refreshNetwork()
        }
    })


    LaunchedEffect(page) {
        refreshNetwork()
    }


    var showBottomSheet by remember { mutableStateOf(false) }
    var infoNum by remember { mutableIntStateOf(0) }

    if(showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            autoShape = false,
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
        ){
            BillsInfo(vm,infoNum)
        }
    }

    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        val data = (uiState as UiState.Success).data
        val list = data.records
        val listState = rememberLazyListState()

        Box(modifier = Modifier.fillMaxSize().pullRefresh(pullRefreshState)) {
            RefreshIndicator(refreshing, pullRefreshState, Modifier.align(Alignment.TopCenter).padding(innerPaddings).zIndex(1f))
            LazyColumn(state = listState) {
                item { InnerPaddingHeight(innerPaddings,true) }
                if (page == 1)
                    item { CardRow(vm,vmUI, hazeState) }
                items(list.size) { item ->
                    val bills = list[item]
                    var name = bills.resume
                    if (name.contains("有限公司")) name = name.replace("有限公司","")

                    val time =bills.effectdateStr
                    val getTime = time.substringBefore(" ")
                    AnimationCardListItem(
                        headlineContent = { Text(text = name) },
                        supportingContent = { Text(text = processTranamt(bills)) },
                        overlineContent = { Text(text = time) },
                        leadingContent = { BillsIcons(name) },
                        color =
                            if(DateTimeManager.Date_yyyy_MM_dd == getTime)
                                MaterialTheme.colorScheme.secondaryContainer
                            else null,
                        modifier = Modifier.clickable {
                            infoNum = item
                            showBottomSheet = true
                        },
                        index = item
                    )
                }
                item {
                    BottomTip("总 ${data.total} 条")
                }
                item { InnerPaddingHeight(innerPaddings,false) }
                item { PaddingForPageControllerButton() }
            }
            PagingController(
                listState,
                page,
                nextPage = { page = it },
                previousPage = { page = it },
                modifier = Modifier.padding(bottom = innerPaddings.calculateBottomPadding()-navigationBarHeightPadding),
                paddingBottom = false
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillsInfo(vm : NetWorkViewModel, Infonum : Int) {
    val uiState by vm.huiXinBillResult.state.collectAsState()
    val data = (uiState as UiState.Success).data.records
    val bills = data[Infonum]
    Column {
        HazeBottomSheetTopBar("详情", isPaddingStatusBar = false)
        MyCustomCard(hasElevation = false, containerColor = cardNormalColor()){
            TransplantListItem(
                headlineContent = { Text( bills.resume.substringBefore("-") ) },
                leadingContent = {
                    BillsIcons(bills.resume)
                },
                overlineContent = { Text(text = "商家")}
            )
            TransplantListItem(
                headlineContent = { Text( bills.resume.substringAfter("-")) },
                leadingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.credit_card),
                        contentDescription = "Localized description",
                    )
                },
                overlineContent = { Text(text = "类型 ${Text( bills.turnoverType)}")}
            )
            TransplantListItem(
                headlineContent = { Text(  "出账 "+ bills.jndatetimeStr+ "\n入账 " + bills.effectdateStr ) },
                leadingContent = {
                    Icon(
                        painterResource(id = R.drawable.schedule),
                        contentDescription = "Localized description",
                    )
                },
                overlineContent = { Text(text = "时间")}
            )

            TransplantListItem(
                headlineContent = { Text( processTranamt(bills)) },
                leadingContent = {
                    Icon(
                        painterResource(id = R.drawable.paid),
                        contentDescription = "Localized description",
                    )
                },
                overlineContent = { Text(text = "金额")}
            )
        }
        Spacer(Modifier.height(10.dp))
    }
}


fun processTranamt(bills : records) : String {
    var num =( bills.tranamt ?: 1000 ).toString()
    //优化0.0X元Bug
    if(num.length == 1)
        num = "00$num"
    num = num.substring(0, num.length - 2) + "." + num.substring(num.length - 2)
    val big = BigDecimal(num)
    val numFloat = big.toFloat()
    var pay = "$numFloat 元"
    pay = if (bills.resume.contains("充值") || bills.resume.contains("补助")) "+ $pay"
    else "- $pay"
    return pay
}