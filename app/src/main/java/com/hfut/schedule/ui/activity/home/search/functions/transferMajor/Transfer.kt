package com.hfut.schedule.ui.activity.home.search.functions.transferMajor

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.utils.Starter.refreshLogin
import com.hfut.schedule.ui.utils.components.LoadingUI
import com.hfut.schedule.ui.utils.components.MyCustomCard
import com.hfut.schedule.ui.utils.style.Round
import com.hfut.schedule.ui.utils.components.ScrollText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Transfer(ifSaved : Boolean,vm : NetWorkViewModel){
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    val sheetState_info = rememberModalBottomSheetState()
    var showBottomSheet_info by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text(text = "转专业") },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.compare_arrows), contentDescription = "") },
        modifier = Modifier.clickable {
            if(ifSaved) refreshLogin() else
                showBottomSheet = true
        }
    )

    if (showBottomSheet_info) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_info = false },
            sheetState = sheetState_info,
            shape = Round(sheetState_info)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("说明") },
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    TransferTips()
                }
            }
        }
    }


    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
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
                        title = { ScrollText("转专业") },
                        actions = {
                            FilledTonalIconButton(
                                onClick = { showBottomSheet_info = true },
                                modifier = Modifier.padding(horizontal = 15.dp)
                            ) {
                                Icon(painterResource(R.drawable.info),null)
                            }
                        }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
//                    TransferUI(vm,campusId)
                    TransferListUI(vm)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferListUI(vm: NetWorkViewModel) {


    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    var batchId by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("转专业") }
    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }
    val cookie = if (!vm.webVpn) prefs.getString(
        "redirect",
        ""
    ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")

    val sheetState_apply = rememberModalBottomSheetState()
    var showBottomSheet_apply by remember { mutableStateOf(false) }

    if (showBottomSheet_apply) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_apply = false },
            sheetState = sheetState_apply,
            shape = Round(sheetState_apply)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("我的申请") },
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
//                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ) {
                    MyApplyListUI(vm,batchId)
                }
            }
        }
    }


    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
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
                        title = { ScrollText(title) },
                        actions = {
                            FilledTonalButton(
                                onClick = { showBottomSheet_apply = true },
                                modifier = Modifier.padding(horizontal = 15.dp)
                            ) {
                                Text("我的申请")
                            }
                        }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    TransferUI(vm,batchId)
                }
            }
        }
    }

    if(refresh) {
        loading = true
        CoroutineScope(Job()).launch{
            async{ cookie?.let { vm.getTransferList(it)} }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.transferListData.observeForever { result ->
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
    if(loading) {
        LoadingUI()
    } else {
        val transferList = getTransferList(vm)
        LazyColumn {
            items(transferList.size) { index ->
                val data = transferList[index]
                MyCustomCard {
                    ListItem(
                        headlineContent = { Text(data.title) },
                        supportingContent = { Text("申请日期 " + data.applicationDate + "\n转专业时期 " + data.admissionDate) },
                        trailingContent = { Icon(Icons.Filled.ArrowForward,null) },
                        modifier = Modifier.clickable {
                            title = data.title
                            batchId = data.batchId
                            showBottomSheet = true
                        }
                    )
                }
            }
        }
    }

}

@Composable
fun TransferTips() {
    MyCustomCard {
        ListItem(
            headlineContent = { Text("具体要求") },
            supportingContent = { Text("请关注所在系QQ群或在 查询中心-新闻公告 检索已公示的转专业要求") },
        )
        ListItem(
            headlineContent = { Text("录取通知") },
            supportingContent = { Text("请关注所在转专业QQ群(上条要求会公示)或在 查询中心-新闻公告 检索已公示的面试/拟录取名单") },
        )
    }
}
