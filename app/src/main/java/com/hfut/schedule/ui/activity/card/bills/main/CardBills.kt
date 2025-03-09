package com.hfut.schedule.ui.activity.card.bills.main

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.widget.Toast
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
import androidx.compose.material3.CircularProgressIndicator
import com.hfut.schedule.ui.utils.components.LoadingUI
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.viewmodel.UIViewModel
import com.hfut.schedule.logic.beans.zjgd.BillResponse
import com.hfut.schedule.logic.beans.zjgd.records
import com.hfut.schedule.logic.utils.DateTimeUtils
import com.hfut.schedule.logic.utils.data.JxglstuParseUtils
import com.hfut.schedule.logic.utils.data.SharePrefs
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.logic.utils.data.reEmptyLiveDta
import com.hfut.schedule.ui.activity.card.bills.CardRow
import com.hfut.schedule.ui.utils.components.AnimationCardListItem
import com.hfut.schedule.ui.utils.components.AppHorizontalDp
import com.hfut.schedule.ui.utils.components.BillsIcons
import com.hfut.schedule.ui.utils.components.CardNormalColor
import com.hfut.schedule.ui.utils.components.CustomTopBar
import com.hfut.schedule.ui.utils.components.LoadingUI
import com.hfut.schedule.ui.utils.components.MyCustomCard
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.components.StyleCardListItem
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.style.Round
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.math.BigDecimal


fun getBills(vm : NetWorkViewModel) : List<records> {
    val billjson = vm.BillsData.value
//    var BillItems = mutableListOf<records>()
    if(billjson?.contains("操作成功") == true){
        val bill = Gson().fromJson(billjson, BillResponse::class.java)
        val data = bill.data.records
        val msg = bill.data.msg
        val totalpage = bill.data.pages
        SharePrefs.saveString("totalpage",totalpage.toString())
        if (msg != null) {
            if (msg.contains("成功")) {
                val cardAccount = bill.data.records[0].fromAccount
                SharePrefs.saveString("cardAccount", cardAccount)
            } else {
                MyToast(msg)
            }
        }
        return data
//        data.forEach {  BillItems.add(it) }
    }
    return emptyList()
}






@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun CardBills(vm : NetWorkViewModel, innerPaddings : PaddingValues, vmUI : UIViewModel) {
    var loading by remember { mutableStateOf(true) }
    var page by remember { mutableStateOf(1) }
    var counter by remember { mutableStateOf(1) }
    val auth = prefs.getString("auth","")

    if(counter == 1) {
        vm.CardGet("bearer $auth",page)
        counter++
    }
   // page = 1


        CoroutineScope(Job()).apply {
            launch {
                async {
                    //  delay(1000)
                    Handler(Looper.getMainLooper()).post {
                        vm.BillsData.observeForever { result ->
                            if (result != null) {
                                if (result.contains("操作成功")) {
                                    loading = false
                                    if (result.contains("操作成功")) getBills(vm)
                                    else {
                                        val ONE = JxglstuParseUtils.casCookies
                                        val TGC = prefs.getString("TGC", "")
                                        vm.OneGotoCard("$ONE;$TGC")
                                        MyToast("空数据,请再次尝试或登录")
                                    }
                                }
                            }
                        }
                    }
                }.await()
            }
        }



    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var Infonum by remember { mutableStateOf(0) }

    if(showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
        ){
            BillsInfo(vm,Infonum)
        }
    }

    fun Updade() {
        CoroutineScope(Job()).apply {
            launch {
                async {
                    page = 1
                    loading = true
                    vm.CardGet("bearer $auth",page)
                }.await()
                async { reEmptyLiveDta(vm.BillsData) }.await()
                async {
                    Handler(Looper.getMainLooper()).post {
                        vm.BillsData.observeForever { result ->
                            if (result != null) {
                                if(result.contains("{")) {
                                    loading = false
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
   Box(){
        AnimatedVisibility(
            visible = loading,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .padding(innerPaddings)
                .align(Alignment.Center)
        ) {
            Column {
                Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding()))
                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center)  {
                    LoadingUI()
                }
            }
        }
        AnimatedVisibility(
            visible = !loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val list = getBills(vm)
            LazyColumn() {
                item { Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding())) }
                if (page == 1)
                    item { CardRow(vm,vmUI) }
                items(list.size) { item ->
                    val bills = list[item]
                    var name = bills.resume
                    if (name.contains("有限公司")) name = name.replace("有限公司","")

                    val time =bills.effectdateStr
                    val getTime = time.substringBefore(" ")


//                    MyCustomCard{
                        AnimationCardListItem(
                            headlineContent = { Text(text = name) },
                            supportingContent = { Text(text = processTranamt(bills)) },
                            overlineContent = { Text(text = time) },
                            leadingContent = { BillsIcons(name) },
                            color =
                            if(DateTimeUtils.Date_yyyy_MM_dd == getTime)
                                MaterialTheme.colorScheme.primaryContainer
                            else null,
                            modifier = Modifier.clickable {
                                Infonum = item
                                showBottomSheet = true
                            },
                            index = item
                        )
//                    }
                }
                item {
                    val totalpage = prefs.getString("totalpage","1")
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {

                        OutlinedButton(
                            onClick = {
                                CoroutineScope(Job()).apply {
                                    launch {
                                        async {
                                            if(page > 1) {
                                                page--
                                                loading = true
                                                vm.CardGet("bearer $auth",page)
                                            }
                                        }.await()
                                        async {
                                            Handler(Looper.getMainLooper()).post{
                                                vm.libraryData.observeForever { result ->
                                                    loading = false
                                                    getBills(vm)
                                                }
                                            }
                                        }
                                    }
                                }
                            }) { Text(text = "上一页") }

                        Spacer(modifier = Modifier.width(AppHorizontalDp()))

                        OutlinedButton(
                            onClick = { Updade()}
                        ) { Text(text = "${page} / ${totalpage}") }

                        Spacer(modifier = Modifier.width(AppHorizontalDp()))

                        OutlinedButton(
                            onClick = {
                                CoroutineScope(Job()).apply {
                                    launch {
                                        async {
                                            if ( page < totalpage?.toInt() ?: 1) {
                                                page++
                                                loading = true
                                                vm.CardGet("bearer " + auth,page)
                                            }
                                        }.await()
                                        async {
                                            async {
                                                Handler(Looper.getMainLooper()).post {
                                                    vm.libraryData.observeForever { result ->
                                                        loading = false
                                                        getBills(vm)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }) { Text(text = "下一页") }
                    }
                    Spacer(modifier = Modifier.height(innerPaddings.calculateBottomPadding()))
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillsInfo(vm : NetWorkViewModel, Infonum : Int) {
    val bills = getBills(vm)[Infonum]
    Column {
        CustomTopBar("详情")
        MyCustomCard(hasElevation = false, containerColor = CardNormalColor()){
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
    val num_float = big.toFloat()
    var pay = "$num_float 元"
    if (bills.resume.contains("充值") || bills.resume.contains("补助")) pay = "+ $pay"
    else pay = "- $pay"
    return pay
}