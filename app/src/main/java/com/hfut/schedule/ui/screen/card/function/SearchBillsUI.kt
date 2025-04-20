package com.hfut.schedule.ui.screen.card.function

import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
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
import androidx.compose.material3.CircularProgressIndicator
import com.hfut.schedule.ui.component.LoadingUI
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.model.zjgd.BillResponse
import com.hfut.schedule.logic.model.zjgd.records
import com.hfut.schedule.ui.screen.card.bill.main.getBills
import com.hfut.schedule.ui.screen.card.bill.main.processTranamt
import com.hfut.schedule.ui.component.appHorizontalDp
import com.hfut.schedule.ui.component.BillsIcons
import com.hfut.schedule.ui.component.BottomSheetTopBar
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.MyCustomCard
import com.hfut.schedule.ui.component.StyleCardListItem
import com.hfut.schedule.ui.style.textFiledTransplant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


private fun Items(vm: NetWorkViewModel) : List<records> {
    val result = vm.SearchBillsData.value
    if(result?.contains("操作成功") == true) {
        try {
            val data = Gson().fromJson(result,BillResponse::class.java)
            val records = data.data.records
            val totalpage = data.data.pages
            if(prefs.getInt("totalsearch",0) != totalpage)
                SharedPrefs.saveInt("totalsearch", totalpage)
            return records
        } catch (_:Exception) {
            return emptyList()
        }
    }
    return emptyList()
}
@Composable
fun SearchBillsUI(vm : NetWorkViewModel) {

    var clicked by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    val auth = SharedPrefs.prefs.getString("auth","")

    var input by remember { mutableStateOf("") }
    var page by remember { mutableStateOf(1) }


    val Click =  {
        CoroutineScope(Job()).apply {
            launch {
                async {
                    clicked = true
                    loading = true
                    Handler(Looper.getMainLooper()).post{
                        vm.SearchBillsData.value = "{}"
                    }
                    vm.searchBills("bearer $auth",input,page) }.await()
                async {
                    Handler(Looper.getMainLooper()).post{
                        vm.SearchBillsData.observeForever { result ->
                            loading = false
                        }
                    }
                }
            }
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            HazeBottomSheetTopBar("流水搜索")
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ){
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

                TextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = appHorizontalDp(), vertical = 5.dp),
                    value = input,
                    onValueChange = {
                        input = it
                        clicked = false
                    },
                    label = { Text("输入关键字检索" ) },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                Click()
                            }) {
                            Icon(painter = painterResource(R.drawable.search), contentDescription = "description")
                        }
                    },
                    shape = MaterialTheme.shapes.medium,
                    colors = textFiledTransplant(),
                )
                Spacer(modifier = Modifier.height(500.dp))

                if (clicked) {

                    AnimatedVisibility(
                        visible = loading,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center)  {
                            Spacer(modifier = Modifier.height(5.dp))
                            LoadingUI()
                            Spacer(modifier = Modifier.height(50.dp))
                        }
                    }


                    AnimatedVisibility(
                        visible = !loading,
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
                                            //  modifier = Modifier.size(width = 170.dp, height = 70.dp).padding(horizontal = AppHorizontalDp(), vertical = 5.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(horizontal = appHorizontalDp(), vertical = 5.dp),
                                            value = input,
                                            onValueChange = {
                                                input = it
                                                clicked = false
                                            },
                                            label = { Text("输入关键字检索") },
                                            singleLine = true,
                                            trailingIcon = {
                                                IconButton(
                                                    onClick = {Click()}) {
                                                    Icon(painter = painterResource(R.drawable.search), contentDescription = "description")
                                                }
                                            },
                                            shape = MaterialTheme.shapes.medium,
                                            colors = textFiledTransplant(),
                                        )
                                    }

                                }
                                val list = Items(vm)

                                items(list.size) { index ->
                                    val item = list[index]
                                    var name = item.resume
                                    if (name.contains("有限公司")) name = name.replace("有限公司","")
                                    StyleCardListItem(
                                        headlineContent = { Text(text = name) },
                                        supportingContent = {Text(text = processTranamt(getBills(vm)[index]))},
                                        overlineContent = {Text(text = item.effectdateStr)},
                                        leadingContent = { BillsIcons(name) }
                                    )
                                }
                                item {
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
                                                                vm.searchBills("bearer $auth",input,page)
                                                            }
                                                        }.await()
                                                        async {
                                                            Handler(Looper.getMainLooper()).post{
                                                                vm.SearchBillsData.observeForever { result ->
                                                                    loading = false
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                            }) { Text(text = "上一页") }

                                        Spacer(modifier = Modifier.width(appHorizontalDp()))

                                        OutlinedButton(
                                            onClick = {
                                                CoroutineScope(Job()).launch {
                                                    async {
                                                        page = 1
                                                        loading = true
                                                        vm.searchBills("bearer $auth",input,page)
                                                    }.await()
                                                    async {
                                                        Handler(Looper.getMainLooper()).post{
                                                            vm.SearchBillsData.observeForever { result ->
                                                                loading = false
                                                            }
                                                        }
                                                    }
                                                }

                                            }
                                        ) { Text(text = "$page / ${prefs.getInt("totalsearch",1)}") }

                                        Spacer(modifier = Modifier.width(appHorizontalDp()))

                                        OutlinedButton(
                                            onClick = {
                                                CoroutineScope(Job()).apply {
                                                    launch {
                                                        async {
                                                            if ( page < prefs.getInt("totalsearch",1)) {
                                                                page++
                                                                loading = true
                                                                vm.searchBills("bearer $auth",input,page)
                                                            }

                                                        }.await()
                                                        async {
                                                            Handler(Looper.getMainLooper()).post{
                                                                vm.SearchBillsData.observeForever { result ->
                                                                    loading = false
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                            }) { Text(text = "下一页") }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}