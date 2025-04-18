package com.hfut.schedule.ui.screen.home.search.function.lepaoYun

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.AssistChip
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
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.logic.util.storage.SharePrefs.saveString
import com.hfut.schedule.logic.util.storage.SharePrefs.prefs
import com.hfut.schedule.logic.model.lepaoyun.LePaoYunHomeResponse
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.appHorizontalDp
import com.hfut.schedule.ui.component.cardNormalColor
import com.hfut.schedule.ui.component.BottomSheetTopBar
import com.hfut.schedule.ui.component.LittleDialog
import com.hfut.schedule.ui.component.MyCustomCard
import com.hfut.schedule.ui.component.StyleCardListItem
import com.hfut.schedule.ui.component.TransplantListItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun OpenLePao() {
    Starter.startLaunchAPK("com.yunzhi.tiyu","云运动")
}
fun Update(vm : NetWorkViewModel) {
    val token =  prefs.getString("Yuntoken","")?.trim()
   
    CoroutineScope(Job()).launch {
        async {vm.LePaoYunHome(token!!)}.await()
      //  async {vm.getRunRecord(token!!,RequestBody!!)}.await()
        async {
            delay(400)
            val json = prefs.getString("LePaoYun", "")
            try {
                val result = Gson().fromJson(json, LePaoYunHomeResponse::class.java)
                val msg = result.msg
                if (msg.contains("成功")) {
                    val distance = result.data.distance
                    saveString("distance",distance)
                    saveString("msg",msg)
                }
            } catch (_:Exception) { }
        }
    }
}
@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LePaoYun(vm : NetWorkViewModel) {

   // Update(vm)

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val distance = prefs.getString("distance","")
    val msg = prefs.getString("msg","")

    TransplantListItem(
        headlineContent = { Text(text = "云运动") },
//        overlineContent = {
//            if (msg != null) {
//                if (msg.contains("成功")) Text(text = "已跑 ${distance} km") else Text(text = "未获取里程")
//            } else Text(text = "未获取里程")
//        },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.mode_of_travel), contentDescription = "")},
        modifier = Modifier.clickable {
            if (msg != null) {
                if (!msg.contains("成功"))  OpenLePao()
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
            conformText = "好",
            dismissText = "取消"
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
    val sheetState_Record = rememberModalBottomSheetState()
    var showBottomSheet_Record by remember { mutableStateOf(false) }
    val json = prefs.getString("LePaoYun","")
    val result = Gson().fromJson(json, LePaoYunHomeResponse::class.java)
    val msg = result.msg
    if (msg.contains("成功")) {
        val distance = result.data.distance
        val cralist = result.data.cralist[0]
        val points = cralist.points.split("|")
        val fences = cralist.fence.split("|")

        saveString("distance",distance)
        saveString("msg",msg)


        if (showBottomSheet_Record) {
            ModalBottomSheet(onDismissRequest = { showBottomSheet_Record = false }, sheetState = sheetState_Record) {

            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                BottomSheetTopBar(cralist.raName)
            },) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    // .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                Column{

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = appHorizontalDp(), vertical = 0.dp), horizontalArrangement = Arrangement.Start){

                        AssistChip(
                            onClick = { OpenLePao() },
                            label = { Text(text = "打开云运动") },
                           // leadingIcon = { Icon(painter = painterResource(R.drawable.add), contentDescription = "description") }
                        )
                    }

//                    MyCustomCard{
                        StyleCardListItem(
                            headlineContent = { Text(text = "已跑 ${distance} 公里")},
                            leadingContent = { Icon(painterResource(id = R.drawable.directions_run), contentDescription = "")},
                            color = MaterialTheme.colorScheme.errorContainer
                        )
//                    }

                    MyCustomCard(hasElevation = false, containerColor = cardNormalColor()){

                        TransplantListItem(
                            headlineContent = { Text(text = "单次要求")},
                            leadingContent = { Icon(painter = painterResource(id = R.drawable.flag), contentDescription = "")},
                        )

                        Row {
                            TransplantListItem(
                                modifier = Modifier.width(100.dp),
                                headlineContent = { Text(text = "配速")},
                                supportingContent = { Text(text = "${cralist.raPaceMin} - ${cralist.raPaceMax}")},
                            )
                            TransplantListItem(
                                modifier = Modifier.width(100.dp),
                                headlineContent = { Text(text = "公里")},
                                supportingContent = { Text(text = "${cralist.raSingleMileageMin} - ${cralist.raSingleMileageMax}")},
                            )

                            TransplantListItem(
                                headlineContent = { Text(text = "步频")},
                                supportingContent = { Text(text = "${cralist.raCadenceMin} - ${cralist.raCadenceMax}")},
                            )

                        }
                    }

                    MyCustomCard(hasElevation = false, containerColor = cardNormalColor()){
                        TransplantListItem(
                            headlineContent = { Text(text = "日期")},
                            leadingContent = { Icon(painter = painterResource(id = R.drawable.calendar), contentDescription = "")},
                            supportingContent = { Text(text = "${cralist.raStartTime} - ${cralist.raEndTime}")},
                        )
                        TransplantListItem(
                            headlineContent = { Text(text = "时间")},
                            leadingContent = { Icon(painter = painterResource(id = R.drawable.schedule), contentDescription = "")},
                            supportingContent = { Text(text = "${cralist.dayStartTime} - ${cralist.dayEndTime}")},
                        )
                    }
                    MyCustomCard(hasElevation = false, containerColor = cardNormalColor()){
                        LazyColumn{
                            item {
                                TransplantListItem(
                                    headlineContent = { Text(text = "跑步点  ${points.size}个")},
                                    leadingContent = { Icon(painter = painterResource(id = R.drawable.near_me), contentDescription = "")},
                                )
                            }
                            items(points.size) { item ->
                                TransplantListItem(headlineContent = { Text(text = points[item])}, modifier = Modifier.clickable {
                                })
                            }
                            item {
                                Divider()
                                TransplantListItem(
                                    headlineContent = { Text(text = "边缘点  ${fences.size}个")},
                                    leadingContent = { Icon(painter = painterResource(id = R.drawable.near_me), contentDescription = "")},
                                )
                            }
                            items(fences.size) { item ->
                                TransplantListItem(headlineContent = { Text(text = points[item])}, modifier = Modifier.clickable {
                                })
                            }
                        }
                    }
                }
            }
        }
    } else {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(appHorizontalDp()), horizontalArrangement = Arrangement.Center){ Text(text = msg)
        }
        Spacer(modifier = Modifier.height(100.dp))
    }

}
