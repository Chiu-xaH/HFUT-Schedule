package com.hfut.manage.ui

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import com.hfut.schedule.ui.utils.LoadingUI
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.google.gson.Gson
import com.hfut.manage.R
import com.hfut.manage.datamodel.data.APIResponse
import com.hfut.manage.datamodel.data.Schedule
import com.hfut.manage.datamodel.enums.DataType
import com.hfut.manage.ui.utils.EmptyUI
import com.hfut.manage.ui.utils.MyToast
import com.hfut.manage.viewmodel.NetViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun removeFocus(vm : NetViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }
    //if(refresh && loading) {
        CoroutineScope(Job()).launch{
            async{ vm.getData() }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.result.observeForever { result ->
                        if (result != null) {
                    //        Log.d("sssss",result)
                            if(result.contains("Schedule")) {
                                loading = false
                                refresh = false
                            }
                        }
                    }
                }
            }
        }
   // }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 5.dp),
        shape = MaterialTheme.shapes.medium,
    ){
        ListItem(
            headlineContent = {  Text(text = "删除聚焦卡片") },
            // overlineContent = { Text(text = "${item["日期时间"]}") },
            leadingContent = { Icon(painterResource(id = R.drawable.lightbulb), contentDescription = "Localized description") },
            trailingContent = { Icon(Icons.Filled.Close, contentDescription = "Localized description",) },
            modifier = Modifier.clickable { showDialog = true },
        )
    }

    if (showDialog) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("移除聚焦卡片") }
                    )
                },
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {

                    androidx.compose.animation.AnimatedVisibility(
                        visible = loading,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Spacer(modifier = Modifier.height(5.dp))
                            LoadingUI()
                        }
                    }////加载动画居中，3s后消失


                    AnimatedVisibility(
                        visible = !loading,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        if(getResult(vm,false).size + getResult(vm,true).size == 0) {
                            EmptyUI()
                        } else {
                            LazyColumn {
                                items(getResult(vm,true).size) {item ->
                                    val list = getResult(vm,true)[item]
                                    Card(
                                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 15.dp, vertical = 5.dp),
                                        shape = MaterialTheme.shapes.medium,
                                    ){
                                        ListItem(
                                            headlineContent = {  Text(text = list.title) },
                                            overlineContent = { Text(text = list.remark) },
                                            supportingContent = { Text(text = list.info)},
                                            leadingContent = { Icon(painterResource(id = R.drawable.calendar), contentDescription = "Localized description") },
                                            trailingContent = { Text(text = list.startTime + "\n" + list.endTime) },
                                            modifier = Modifier.combinedClickable(
                                                onClick = { MyToast("长按将会删除") },
                                                // onDoubleClick = null,
                                                onLongClick = {
                                                    vm.deleteData(DataType.Schedule.name,list.title)
                                                    refresh = true
                                                    MyToast("已删除")
                                                }
                                            ),
                                        )
                                    }
                                }
                                items(getResult(vm,false).size) {item ->
                                    val list = getResult(vm,false)[item]
                                    Card(
                                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 15.dp, vertical = 5.dp),
                                        shape = MaterialTheme.shapes.medium,
                                    ){
                                        ListItem(
                                            headlineContent = {  Text(text = list.title) },
                                            overlineContent = { Text(text = list.remark) },
                                            supportingContent = { Text(text = list.info)},
                                            leadingContent = { Icon(painterResource(id = R.drawable.net), contentDescription = "Localized description") },
                                            trailingContent = { Text(text = list.startTime + "\n" + list.endTime) },
                                            modifier = Modifier.combinedClickable(
                                                onClick = { MyToast("长按将会删除") },
                                                // onDoubleClick = null,
                                                onLongClick = {
                                                    vm.deleteData(DataType.Wangke.name,list.title)
                                                    refresh = true
                                                    MyToast("已删除")
                                                }
                                            )
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

//真为Schedule。否则Wangke
fun getResult(vm : NetViewModel,mode : Boolean): List<Schedule> {
    return try {
        val json = Gson().fromJson(vm.result.value,APIResponse::class.java)
        val schedule = json.Schedule
        val wangke = json.Wangke

        if(mode) schedule else wangke
    } catch (e : Exception) {
        emptyList()
        if(mode) listSchedule else listWangke
    }
}