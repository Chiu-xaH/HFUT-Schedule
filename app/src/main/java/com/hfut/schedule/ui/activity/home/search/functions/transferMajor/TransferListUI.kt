package com.hfut.schedule.ui.activity.home.search.functions.transferMajor

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import com.hfut.schedule.ui.utils.components.LoadingUI
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.hfut.schedule.R
import com.hfut.schedule.logic.beans.community.LoginCommunityResponse
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.beans.jxglstu.TransferData
import com.hfut.schedule.logic.utils.ClipBoard
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.utils.reEmptyLiveDta
import com.hfut.schedule.ui.activity.home.cube.items.subitems.getUserInfo
import com.hfut.schedule.ui.activity.home.search.functions.life.countFunc
import com.hfut.schedule.ui.activity.home.search.functions.person.getPersonInfo
import com.hfut.schedule.ui.activity.shower.home.function.ShowerStatusUI
import com.hfut.schedule.ui.activity.shower.home.function.StatusMsgResponse
import com.hfut.schedule.ui.utils.components.AppHorizontalDp
import com.hfut.schedule.ui.utils.components.CardNormalDp
import com.hfut.schedule.ui.utils.components.MyCustomCard
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.components.ScrollText
import com.hfut.schedule.ui.utils.components.DepartmentIcons
import com.hfut.schedule.ui.utils.components.StyleCardListItem
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.components.statusUI2
import com.hfut.schedule.ui.utils.style.Round
import com.hfut.schedule.ui.utils.style.textFiledTransplant
import com.hfut.schedule.viewmodel.GuaGuaViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferUI(vm: NetWorkViewModel, batchId: String) {
    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState_select = rememberModalBottomSheetState()
    var showBottomSheet_select by remember { mutableStateOf(false) }
    var telephone by remember { mutableStateOf("") }
    val cookie = if (!vm.webVpn) prefs.getString(
        "redirect",
        ""
    ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")
    var id by remember { mutableStateOf(0) }
   // val campus =

    LaunchedEffect(key1 = batchId) {
        loading = true
        reEmptyLiveDta(vm.transferData)
        refresh = true
    }
    if(refresh) {
        loading = true
        CoroutineScope(Job()).launch{
            async{ cookie?.let { vm.getTransfer(it,batchId)} }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.transferData.observeForever { result ->
                        if (result != null) {
                            if(result.contains("转专业")) {
                                loading = false
                                refresh = false
                            }
                        }
                    }
                }
            }
        }
    }

    if(showBottomSheet) {
        countFunc = 0
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState,
            shape = Round(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("结果") },
                    )
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
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet_select = false
            },
            sheetState = sheetState_select,
            shape = Round(sheetState_select)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("手机号 (教务要求)") },
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ) {
                    val personInfo = getPersonInfo()
//                    var input by remember { mutableStateOf("") }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextField(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = AppHorizontalDp()),
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
//                            MyCustomCard {
                            StyleCardListItem(
                                    headlineContent = { Text(it) },
                                    overlineContent = { Text("教务系统预留手机号") },
                                    modifier = Modifier.clickable {
                                        telephone = it
                                        showBottomSheet_select = false
                                        showBottomSheet = true
                                    }
                                )
//                            }
                        }
                    }
                    personInfo.phone?.let {
                        if(it.isNotEmpty()) {
//                            MyCustomCard {
                                StyleCardListItem(
                                    headlineContent = { Text(it) },
                                    overlineContent = { Text("教务系统预留电话号") },
                                    modifier = Modifier.clickable {
                                        telephone = it
                                        showBottomSheet_select = false
                                        showBottomSheet = true
                                    }
                                )
//                            }

                        }
                    }
                    prefs.getString("PHONENUM","")?.let {
                        if(it.isNotEmpty()) {
//                            MyCustomCard  {
                                StyleCardListItem(
                                    headlineContent = { Text(it) },
                                    overlineContent = { Text("呱呱物联登录手机号") },
                                    modifier = Modifier.clickable {
                                        telephone = it
                                        showBottomSheet_select = false
                                        showBottomSheet = true
                                    }
                                )
//                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    Box {
        AnimatedVisibility(
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
        }


        AnimatedVisibility(
            visible = !loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            var input by remember { mutableStateOf("") }
            Column {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextField(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = AppHorizontalDp()),
                        value = input,
                        onValueChange = {
                            input = it
                        },
                        label = { Text("搜索学院或专业" ) },
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
                val list = getTransfer(vm)
                val searchList = mutableListOf<TransferData>()
                list.forEach { item->
                    if(item.department.nameZh.contains(input) || item.major.nameZh.contains(input)) {
                        searchList.add(item)
                    }
                }
                Spacer(modifier = Modifier.height(CardNormalDp()))

                LazyColumn {
                    items(searchList.size) {item ->
                        val dataItem = searchList[item]
                        var department = dataItem.department.nameZh
                        if(department.contains("（")) department = department.substringBefore("（")
                        if(department.contains("(")) department = department.substringBefore("(")
                        val count = dataItem.applyStdCount
                        val limit = dataItem.preparedStdCount
                        val isFull = count > limit
                        StyleCardListItem(
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
                            }
                        )
                    }
                }
            }
        }
    }
}
data class TransferPostResponse(val result : Boolean,val errors : List<ErrorText>)
data class ErrorText(val textZh : String)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun TransferStatusUI(vm : NetWorkViewModel,batchId: String,id: Int,phoneNumber : String) {

    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }

    var msg  by remember { mutableStateOf("") }

    var cookie = if (!vm.webVpn) prefs.getString(
        "redirect",
        ""
    ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")

    val cookieObserver = Observer<String?> { result ->
        if (result != null) {
            if(countFunc == 0) {
                ("$cookie;$result").let { vm.postTransfer(it,batchId,id.toString(),phoneNumber) }
                countFunc++
            }
        }
    }
    val postObserver = Observer<String?> { result ->
        if (result != null) {
            if(result.contains("result")) {
                val data =  Gson().fromJson(result,TransferPostResponse::class.java)
                if(data.result) {
                    msg = "成功"
                } else {
                    val errors = data.errors
                    errors.forEach { item ->
                        msg += item.textZh + " "
                    }
                }
                refresh = false
                loading = false
            }
        }
    }

    if(refresh && countFunc == 0) {

        loading = true
        Handler(Looper.getMainLooper()).post {
            vm.formCookie.observeForever(cookieObserver)
            vm.postTransferResponse.observeForever(postObserver)
        }
        CoroutineScope(Job()).launch {
            async {
                reEmptyLiveDta(vm.formCookie)
                reEmptyLiveDta(vm.postTransferResponse)
            }.await()
            async {
                if(countFunc == 0)
                cookie?.let { vm.getFormCookie(it,batchId,id.toString()) }
            }.await()
        }
    }


    Box {
        AnimatedVisibility(
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
        }


        AnimatedVisibility(
            visible = !loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            statusUI2(painter =
            if(msg == "成功" ) Icons.Filled.Check
            else Icons.Filled.Close
                , text = msg)
        }
    }

}
