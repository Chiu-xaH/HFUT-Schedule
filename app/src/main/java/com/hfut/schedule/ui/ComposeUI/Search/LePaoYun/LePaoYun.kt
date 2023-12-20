package com.hfut.schedule.ui.ComposeUI.Search.LePaoYun

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import com.google.gson.Gson
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.SharePrefs.Save
import com.hfut.schedule.logic.SharePrefs.prefs
import com.hfut.schedule.logic.datamodel.LePaoYunResponse
import com.hfut.schedule.ui.ComposeUI.LittleDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun Update(vm : LoginSuccessViewModel) {
    val token =  prefs.getString("Yuntoken","")?.trim()
    CoroutineScope(Job()).launch {
        async {vm.LePaoYunHome(token!!)}.await()
        async {
            delay(400)
            val json = prefs.getString("LePaoYun",MyApplication.NullLePao)
            val result = Gson().fromJson(json,LePaoYunResponse::class.java)
            val msg = result.msg
            if (msg.contains("成功")) {
                val distance = result.data.distance
                Save("distance",distance)
                Save("msg",msg)
            }
        }
    }
}
@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LePaoYun(vm : LoginSuccessViewModel) {

    Update(vm)

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val distance = prefs.getString("distance","")
    val msg = prefs.getString("msg","")

    ListItem(
        headlineContent = {
            if (msg != null) {
                if (msg.contains("成功")) Text(text = "校园跑  ${distance} km")
                else Text(text = "校园跑")
            }
                          },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.mode_of_travel), contentDescription = "")},
        modifier = Modifier.clickable {
            if (msg != null) {
                if (!msg.contains("成功")) showDialog = true
                else  showBottomSheet = true
            }
        }
    )
    
    if (showDialog) {
        LittleDialog(
            onDismissRequest = { showDialog = false },
            onConfirmation = { showDialog = false },
            dialogTitle = "提示",
            dialogText = "未检测到token,或token不正确,需前往 选项 配置信息",
            conformtext = "好",
            dismisstext = "取消"
        )
    }
    


    if (showBottomSheet) {
        ModalBottomSheet(onDismissRequest = { showBottomSheet = false },sheetState = sheetState) {
           LePaoYunUI()
        }
    }
}

@SuppressLint("ServiceCast")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LePaoYunUI() {
    val json = prefs.getString("LePaoYun",MyApplication.NullLePao)
//Log.d("j",json.toString())
    val result = Gson().fromJson(json,LePaoYunResponse::class.java)
    val msg = result.msg
    if (msg.contains("成功")) {
        val distance = result.data.distance
        val cralist = result.data.cralist[0]
        val points = cralist.points.split("|")
        val fences = cralist.fence.split("|")

        Save("distance",distance)
        Save("msg",msg)

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = { Text(cralist.raName) }
                )
            },) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    // .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                Column{
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp, vertical = 5.dp),
                        shape = MaterialTheme.shapes.medium
                    ){
                        ListItem(
                            headlineContent = { Text(text = "已跑 ${distance} 公里")},
                            leadingContent = { Icon(painterResource(id = R.drawable.directions_run), contentDescription = "")},
                            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        )
                    }

                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp, vertical = 5.dp),
                        shape = MaterialTheme.shapes.medium
                    ){

                        ListItem(
                            headlineContent = { Text(text = "单次要求")},
                            leadingContent = { Icon(painter = painterResource(id = R.drawable.flag), contentDescription = "")},
                        )

                        Row {
                            ListItem(
                                modifier = Modifier.width(100.dp),
                                headlineContent = { Text(text = "配速")},
                                supportingContent = { Text(text = "${cralist.raPaceMin} - ${cralist.raPaceMax}")},
                            )
                            ListItem(
                                modifier = Modifier.width(100.dp),
                                headlineContent = { Text(text = "公里")},
                                supportingContent = { Text(text = "${cralist.raSingleMileageMin} - ${cralist.raSingleMileageMax}")},
                            )

                            ListItem(
                                headlineContent = { Text(text = "步频")},
                                supportingContent = { Text(text = "${cralist.raCadenceMin} - ${cralist.raCadenceMax}")},
                            )

                        }
                    }

                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp, vertical = 5.dp),
                        shape = MaterialTheme.shapes.medium
                    ){
                        ListItem(
                            headlineContent = { Text(text = "日期")},
                            leadingContent = { Icon(painter = painterResource(id = R.drawable.calendar), contentDescription = "")},
                            supportingContent = { Text(text = "${cralist.raStartTime} - ${cralist.raEndTime}")},
                        )
                        ListItem(
                            headlineContent = { Text(text = "时间")},
                            leadingContent = { Icon(painter = painterResource(id = R.drawable.schedule), contentDescription = "")},
                            supportingContent = { Text(text = "${cralist.dayStartTime} - ${cralist.dayEndTime}")},
                        )
                    }
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(horizontal = 15.dp, vertical = 5.dp),
                        shape = MaterialTheme.shapes.medium
                    ){
                        LazyColumn{
                            item {
                                ListItem(
                                    headlineContent = { Text(text = "跑步点  ${points.size}个")},
                                    leadingContent = { Icon(painter = painterResource(id = R.drawable.near_me), contentDescription = "")},
                                )
                            }
                            items(points.size) { item ->
                                ListItem(headlineContent = { Text(text = points[item])}, modifier = Modifier.clickable {
                                })
                            }
                            item {
                                Divider()
                                ListItem(
                                    headlineContent = { Text(text = "边缘点  ${fences.size}个")},
                                    leadingContent = { Icon(painter = painterResource(id = R.drawable.near_me), contentDescription = "")},
                                )
                            }
                            items(fences.size) { item ->
                                ListItem(headlineContent = { Text(text = points[item])}, modifier = Modifier.clickable {
                                })
                            }
                        }
                    }
                }
            }
        }
    } else {
        Row(modifier = Modifier.fillMaxWidth().padding(15.dp), horizontalArrangement = Arrangement.Center){ Text(text = msg)
        }
        Spacer(modifier = Modifier.height(100.dp))
    }

}
