package com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.jxglstu.TransferData
import com.hfut.schedule.logic.util.network.UiState
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.component.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.AnimationCardListItem
import com.hfut.schedule.ui.component.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.CommonNetworkScreen
import com.hfut.schedule.ui.component.DepartmentIcons
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.ScrollText
import com.hfut.schedule.ui.component.StatusUI2
import com.hfut.schedule.ui.component.StyleCardListItem
 
  
import com.hfut.schedule.ui.screen.home.search.function.other.life.countFunc
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferUI(vm: NetWorkViewModel, batchId: String, hazeState: HazeState) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var showBottomSheet_select by remember { mutableStateOf(false) }
    var telephone by remember { mutableStateOf("") }

    var id by remember { mutableIntStateOf(0) }
    val uiState by vm.transferData.state.collectAsState()
    val refreshNetwork: suspend () -> Unit = {
        val cookie = if (!vm.webVpn) prefs.getString(
            "redirect",
            ""
        ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")
        cookie?.let {
            vm.transferData.clear()
            vm.getTransfer(it,batchId)
        }
    }

    LaunchedEffect(batchId) {
        refreshNetwork()
    }

    if(showBottomSheet) {
        countFunc = 0
        HazeBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet,
            isFullExpand = false
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("结果", isPaddingStatusBar = false)
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    TransferStatusUI(vm,batchId,id,telephone)
                }
            }
        }
    }

    if(showBottomSheet_select) {
        HazeBottomSheet (
            onDismissRequest = {
                showBottomSheet_select = false
            },
            hazeState = hazeState,
            isFullExpand = false,
            showBottomSheet = showBottomSheet_select
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("手机号 (教务要求)", isPaddingStatusBar = false)
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ) {
                    val personInfo = getPersonInfo()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextField(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = APP_HORIZONTAL_DP),
                            value = telephone,
                            onValueChange = {
                                telephone = it
                            },
                            label = { Text("自行输入手机号" ) },
                            singleLine = true,
                            trailingIcon = {
                               IconButton(
                                   onClick = {
                                       showBottomSheet_select = false
                                       showBottomSheet = true
                                   }
                               ) {
                                   Icon(Icons.Filled.Check, null)
                               }
                            },
                            shape = MaterialTheme.shapes.medium,
                            colors = textFiledTransplant(),
                        )
                    }
                    Spacer(Modifier.height(5.dp))
                    personInfo.mobile?.let {
                        if(it.isNotEmpty()) {
                            StyleCardListItem(
                                    headlineContent = { Text(it) },
                                    overlineContent = { Text("教务系统预留手机号") },
                                    modifier = Modifier.clickable {
                                        telephone = it
                                        showBottomSheet_select = false
                                        showBottomSheet = true
                                    }
                                )
                        }
                    }
                    personInfo.phone?.let {
                        if(it.isNotEmpty()) {
                                StyleCardListItem(
                                    headlineContent = { Text(it) },
                                    overlineContent = { Text("教务系统预留电话号") },
                                    modifier = Modifier.clickable {
                                        telephone = it
                                        showBottomSheet_select = false
                                        showBottomSheet = true
                                    }
                                )
                        }
                    }
                    prefs.getString("PHONENUM","")?.let {
                        if(it.isNotEmpty()) {
                                StyleCardListItem(
                                    headlineContent = { Text(it) },
                                    overlineContent = { Text("呱呱物联登录手机号") },
                                    modifier = Modifier.clickable {
                                        telephone = it
                                        showBottomSheet_select = false
                                        showBottomSheet = true
                                    }
                                )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
    var input by remember { mutableStateOf("") }
    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        val response = (uiState as UiState.Success).data
        val list = response.data
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = APP_HORIZONTAL_DP),
                    value = input,
                    onValueChange = {
                        input = it
                    },
                    label = { Text("搜索学院或专业") },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            onClick = {}) {
                            Icon(painter = painterResource(R.drawable.search), contentDescription = "description")
                        }
                    },
                    shape = MaterialTheme.shapes.medium,
                    colors = textFiledTransplant(),
                )
            }
            val searchList = mutableListOf<TransferData>()
            list.forEach { item->
                if(item.department.nameZh.contains(input) || item.major.nameZh.contains(input)) {
                    searchList.add(item)
                }
            }
            Spacer(modifier = Modifier.height(CARD_NORMAL_DP))

            LazyColumn {
                items(searchList.size, key = { it }) {item ->
                    val dataItem = searchList[item]
                    var department = dataItem.department.nameZh
                    if(department.contains("（")) department = department.substringBefore("（")
                    if(department.contains("(")) department = department.substringBefore("(")
                    val count = dataItem.applyStdCount
                    val limit = dataItem.preparedStdCount
                    val isFull = count > limit
                    AnimationCardListItem(
                        headlineContent = { Text(text = dataItem.major.nameZh, fontWeight = FontWeight.Bold) },
                        supportingContent = { dataItem.registrationConditions?.let { Text(text = it) } },
                        overlineContent = { ScrollText(text = "已申请 $count / $limit $department") },
                        leadingContent = { DepartmentIcons(dataItem.department.nameZh) },
                        trailingContent = {  FilledTonalIconButton(onClick = {
                            id = dataItem.id
                            showBottomSheet_select = true
                        },
                            colors = if(!isFull) IconButtonDefaults.filledTonalIconButtonColors() else IconButtonDefaults.filledTonalIconButtonColors(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                        ) { Icon(painter = painterResource(id = R.drawable.add_2), contentDescription = "") } },
                        color = if(isFull) {
                            MaterialTheme.colorScheme.errorContainer
                        } else {
                            null
                        },
                        index = item
                    )
                }
            }
        }
    }
}
data class TransferPostResponse(val result : Boolean,val errors : List<ErrorText>)
data class ErrorText(val textZh : String)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun TransferStatusUI(vm : NetWorkViewModel, batchId: String, id: Int, phoneNumber : String) {

//    var loading by remember { mutableStateOf(true) }
//
//    var msg  by remember { mutableStateOf("") }
    var cookie = remember {
        if (!vm.webVpn) prefs.getString(
            "redirect",
            ""
        ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")
    }
    val refreshNetwork : suspend () -> Unit = {
        cookie?.let {
            vm.postTransferResponse.clear()
            vm.fromCookie.clear()
            vm.getFormCookie(it,batchId,id.toString())
            val preferCookie = (vm.fromCookie.state.value as? UiState.Success)?.data ?: return@let
            vm.postTransfer("$cookie;$preferCookie",batchId,id.toString(),phoneNumber)
        }
    }
//    val uiStateCookie by vm.fromCookie.state.collectAsState()
    val uiState by vm.postTransferResponse.state.collectAsState()
    LaunchedEffect(Unit) {
        refreshNetwork()
    }



//    val cookieObserver = Observer<String?> { result ->
//        if (result != null) {
//            if(countFunc == 0) {
//                vm.postTransfer("$cookie;$result",batchId,id.toString(),phoneNumber)
//                countFunc++
//            }
//        }
//    }
//    val postObserver = Observer<String?> { result ->
//        if (result != null) {
//            if(result.contains("result")) {
//                try {
//                    val data =  Gson().fromJson(result,TransferPostResponse::class.java)
//                    if(data.result) {
//                        msg = "成功"
//                    } else {
//                        val errors = data.errors
//                        errors.forEach { item ->
//                            msg += item.textZh + " "
//                        }
//                    }
//                } catch (_: Exception) {
//                    msg = "错误"
//                }
//
//                refresh = false
//                loading = false
//            }
//        }
//    }

//    if(refresh && countFunc == 0) {
//
//        loading = true
//        Handler(Looper.getMainLooper()).post {
//            vm.fromCookie.observeForever(cookieObserver)
//            vm.postTransferResponse.observeForever(postObserver)
//        }
//        CoroutineScope(Job()).launch {
//            async {
//                reEmptyLiveDta(vm.fromCookie)
//                reEmptyLiveDta(vm.postTransferResponse)
//            }.await()
//            async {
//                if(countFunc == 0)
//                cookie?.let { vm.getFormCookie(it,batchId,id.toString()) }
//            }.await()
//        }
//    }
//

    CommonNetworkScreen(uiState, isFullScreen = false , onReload = refreshNetwork) {
        val msg = (uiState as UiState.Success).data
        StatusUI2(painter = if(msg == "成功" ) Icons.Filled.Check else Icons.Filled.Close, text = msg)
    }

//    Box {
//        AnimatedVisibility(
//            visible = loading,
//            enter = fadeIn(),
//            exit = fadeOut()
//        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.Center
//            ) {
//                Spacer(modifier = Modifier.height(5.dp))
//                LoadingUI()
//            }
//        }
//
//
//        AnimatedVisibility(
//            visible = !loading,
//            enter = fadeIn(),
//            exit = fadeOut()
//        ) {
//
//        }
//    }

}
