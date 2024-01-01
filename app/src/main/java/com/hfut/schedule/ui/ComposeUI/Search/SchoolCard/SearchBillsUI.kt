package com.hfut.schedule.ui.ComposeUI.Search.SchoolCard

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.datamodel.zjgd.BillResponse
import com.hfut.schedule.logic.datamodel.zjgd.records
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBillsUI(vm : LoginSuccessViewModel) {

    var clicked by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    val auth = SharePrefs.prefs.getString("auth","")

    var input by remember { mutableStateOf("") }
    var page by remember { mutableStateOf(1) }

  fun Items() : MutableList<records> {
    val result = vm.SearchBillsData.value
      var BillItems = mutableListOf<records>()
      if(result?.contains("操作成功") == true) {
          val data = Gson().fromJson(result,BillResponse::class.java)
          val records = data.data.records
          val totalpage = data.data.pages
          val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
          if(sp.getInt("totalsearch",0) != totalpage){ sp.edit().putInt("totalsearch", totalpage).apply() }
          records.forEach {  BillItems.add(it) }
      }
    return BillItems
  }

    fun get()  {
        val result = prefs.getString("searchbills","")
        if (result != null) {
            if (result.contains("操作成功")) { Items() }
        }
    }


    fun Click() {
        CoroutineScope(Job()).apply {
            launch {
                async {
                    clicked = true
                    loading = true
                    Handler(Looper.getMainLooper()).post{
                        vm.SearchBillsData.value = "{}"
                    }
                    vm.searchBills("bearer " + auth,input,page) }.await()
                async {
                    Handler(Looper.getMainLooper()).post{
                        vm.SearchBillsData.observeForever { result ->
                            loading = false
                            get()
                        }
                    }
                }
            }
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("流水搜索") }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ){
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
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
                        unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
                    ),
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
                            CircularProgressIndicator()
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
                                            //  modifier = Modifier.size(width = 170.dp, height = 70.dp).padding(horizontal = 15.dp, vertical = 5.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(horizontal = 15.dp, vertical = 5.dp),
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
                                            colors = TextFieldDefaults.textFieldColors(
                                                focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
                                                unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
                                            ),
                                        )
                                    }

                                }

                                items(Items().size) { item ->
                                    var num = Items()[item].tranamt.toString()
                                    num = num.substring(0, num.length - 2) + "." + num.substring(num.length - 2)
                                    val big = BigDecimal(num)
                                    val num_float = big.toFloat()

                                    var name = Items()[item].resume
                                    if (name.contains("有限公司")) name = name.replace("有限公司","")

                                    var pay = "$num_float 元"
                                    if (name.contains("充值") || name.contains("补助")) pay = "+" + pay
                                    else pay = "-" + pay

                                    Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center)
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
                                                headlineContent = { Text(text = name) },
                                                supportingContent = {Text(text = pay)},
                                                overlineContent = {Text(text = Items()[item].effectdateStr)},
                                                leadingContent = { BillsIcons(name) }
                                            )



                                        }
                                    }

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
                                                                vm.searchBills("bearer " + auth,input,page)
                                                            }
                                                        }.await()
                                                        async {
                                                            Handler(Looper.getMainLooper()).post{
                                                                vm.SearchBillsData.observeForever { result ->
                                                                    loading = false
                                                                    get()
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                            }) { Text(text = "上一页") }

                                        Spacer(modifier = Modifier.width(15.dp))

                                        OutlinedButton(
                                            onClick = {
                                                CoroutineScope(Job()).launch {
                                                    async {
                                                        page = 1
                                                        loading = true
                                                        vm.searchBills("bearer " + auth,input,page)
                                                    }.await()
                                                    async {
                                                        Handler(Looper.getMainLooper()).post{
                                                            vm.SearchBillsData.observeForever { result ->
                                                                loading = false
                                                                get()
                                                            }
                                                        }
                                                    }
                                                }

                                            }
                                        ) { Text(text = "${page} / ${prefs.getInt("totalsearch",1)}") }

                                        Spacer(modifier = Modifier.width(15.dp))

                                        OutlinedButton(
                                            onClick = {
                                                CoroutineScope(Job()).apply {
                                                    launch {
                                                        async {
                                                            if ( page < prefs.getInt("totalsearch",1)) {
                                                                page++
                                                                loading = true
                                                                vm.searchBills("bearer " + auth,input,page)
                                                            }

                                                        }.await()
                                                        async {
                                                            Handler(Looper.getMainLooper()).post{
                                                                vm.SearchBillsData.observeForever { result ->
                                                                    loading = false
                                                                    get()
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