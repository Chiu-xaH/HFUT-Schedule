package com.hfut.schedule.ui.Activity.card.bills.main

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.ViewModel.UIViewModel
import com.hfut.schedule.logic.datamodel.zjgd.BillResponse
import com.hfut.schedule.logic.datamodel.zjgd.records
import com.hfut.schedule.logic.utils.GetDate
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.Activity.card.function.main.CardRow
import com.hfut.schedule.ui.Activity.card.BillsIcons
import com.hfut.schedule.ui.UIUtils.MyToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.math.BigDecimal


fun BillItem(vm : LoginSuccessViewModel) :MutableList<records> {
    val billjson = vm.BillsData.value
    var BillItems = mutableListOf<records>()
    if(billjson?.contains("操作成功") == true){
        val bill = Gson().fromJson(billjson, BillResponse::class.java)
        val data = bill.data.records
        val msg = bill.data.msg
        val totalpage = bill.data.pages
        SharePrefs.Save("totalpage",totalpage.toString())
        if (msg != null) {
            if (msg.contains("成功")) {
                val cardAccount = bill.data.records[0].fromAccount
                SharePrefs.Save("cardAccount", cardAccount)
            } else { Toast.makeText(MyApplication.context,msg,Toast.LENGTH_SHORT).show() }
        }
        data.forEach {  BillItems.add(it) }
    }
    return BillItems
}






@SuppressLint("SuspiciousIndentation")
@Composable
fun CardBills(vm : LoginSuccessViewModel,innerPaddings : PaddingValues,vmUI : UIViewModel) {
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
                                    if (result.contains("操作成功")) BillItem(vm)
                                    else {
                                        val ONE = prefs.getString("ONE", "")
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



    fun Updade() {
        CoroutineScope(Job()).apply {
            launch {
                async {
                    page = 1
                    loading = true
                    vm.CardGet("bearer " + auth,page)
                }.await()
                async {
                    Handler(Looper.getMainLooper()).post {
                        vm.libraryData.observeForever { result ->
                            loading = false
                            BillItem(vm)
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
                    CircularProgressIndicator()
                }
            }
        }
        AnimatedVisibility(
            visible = !loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LazyColumn() {
                item { Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding())) }
                if (page == 1)
                    item { CardRow(vm,false,vmUI) }
                items(BillItem(vm).size) { item ->
                    var num =( BillItem(vm)[item].tranamt ?: 1000 ).toString()
                    //优化0.0X元Bug
                    if(num.length == 1)
                        num = "00$num"

                    num = num.substring(0, num.length - 2) + "." + num.substring(num.length - 2)
                    val big = BigDecimal(num)
                    val num_float = big.toFloat()

                    var name = BillItem(vm)[item].resume
                    if (name.contains("有限公司")) name = name.replace("有限公司","")

                    var pay = "$num_float 元"
                    if (name.contains("充值") || name.contains("补助")) pay = "+$pay"
                    else pay = "-$pay"

                    val time = BillItem(vm)[item].effectdateStr
                    val getTime = time.substringBefore(" ")


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
                            supportingContent = { Text(text = pay) },
                            overlineContent = { Text(text = time) },
                            leadingContent = { BillsIcons(name) },
                            colors =
                            if(GetDate.Date_yyyy_MM_dd == getTime)
                                ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            else ListItemDefaults.colors()
                        )
                    }
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
                                                    BillItem(vm)
                                                }
                                            }
                                        }
                                    }
                                }
                            }) { Text(text = "上一页") }

                        Spacer(modifier = Modifier.width(15.dp))

                        OutlinedButton(
                            onClick = { Updade()}
                        ) { Text(text = "${page} / ${totalpage}") }

                        Spacer(modifier = Modifier.width(15.dp))

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
                                                        BillItem(vm)
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