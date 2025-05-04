package com.hfut.schedule.ui.screen.home.search.function.transfer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.SimpleUiState
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.ui.component.AnimationCardListItem
import com.hfut.schedule.ui.component.CommonNetworkScreen
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.StyleCardListItem
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Transfer(ifSaved : Boolean, vm : NetWorkViewModel, hazeState: HazeState){
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    val sheetState_info = rememberModalBottomSheetState()
    var showBottomSheet_info by remember { mutableStateOf(false) }

    TransplantListItem(
        headlineContent = { Text(text = "转专业") },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.compare_arrows), contentDescription = "") },
        modifier = Modifier.clickable {
            if(ifSaved) refreshLogin() else
                showBottomSheet = true
        }
    )

    if (showBottomSheet_info) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_info = false },
            showBottomSheet =showBottomSheet_info,
            hazeState = hazeState,
            isFullExpand = false
//            sheetState = sheetState_info,
//            shape = bottomSheetRound(sheetState_info)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("说明", isPaddingStatusBar = false)
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
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet,
//            sheetState = sheetState,
//            shape = bottomSheetRound(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("转专业") {
                        FilledTonalIconButton(
                            onClick = { showBottomSheet_info = true },
//                            modifier = Modifier.padding(horizontal = AppHorizontalDp())
                        ) {
                            Icon(painterResource(R.drawable.info),null)
                        }
                    }
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
//                    TransferUI(vm,campusId)
                    TransferListUI(vm,hazeState)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransferListUI(vm: NetWorkViewModel, hazeState: HazeState) {


    var showBottomSheet by remember { mutableStateOf(false) }
    var batchId by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("转专业") }
    var showBottomSheet_apply by remember { mutableStateOf(false) }

    if (showBottomSheet_apply) {
        HazeBottomSheet(
            onDismissRequest = { showBottomSheet_apply = false },
            hazeState = hazeState,
            isFullExpand = false,
            showBottomSheet = showBottomSheet_apply
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("我的申请", isPaddingStatusBar = false)
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    MyApplyListUI(vm,batchId,hazeState)
                }
            }
        }
    }


    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar(title) {
                        FilledTonalButton(
                            onClick = { showBottomSheet_apply = true },
                        ) {
                            Text("我的申请")
                        }
                    }
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    TransferUI(vm,batchId,hazeState)
                }
            }
        }
    }
    val uiState by vm.transferListData.state.collectAsState()
    val refreshNetwork: suspend () -> Unit = {
        val cookie = if (!vm.webVpn) prefs.getString(
            "redirect",
            ""
        ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")
        cookie?.let {
            vm.transferListData.clear()
            vm.getTransferList(it)
        }
    }

    LaunchedEffect(Unit) {
        refreshNetwork()
    }

    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        val transferList = (uiState as SimpleUiState.Success).data ?: emptyList()
        LazyColumn {
            items(transferList.size) { index ->
                val data = transferList[index]
//                MyCustomCard {
                AnimationCardListItem(
                    headlineContent = { Text(data.title) },
                    supportingContent = { Text("申请日期 " + data.applicationDate + "\n转专业时期 " + data.admissionDate) },
                    trailingContent = { Icon(Icons.Filled.ArrowForward,null) },
                    modifier = Modifier.clickable {
                        title = data.title
                        batchId = data.batchId
                        showBottomSheet = true
                    },
                    index = index
                )
//                }
            }
        }
    }
}

@Composable
fun TransferTips() {
//    MyCustomCard {
    StyleCardListItem(
            headlineContent = { Text("具体要求") },
            supportingContent = { Text("请关注所在系QQ群或在 查询中心-新闻公告 检索已公示的转专业要求") },
        )
    StyleCardListItem(
            headlineContent = { Text("录取通知") },
            supportingContent = { Text("请关注所在转专业QQ群(上条要求会公示)或在 查询中心-新闻公告 检索已公示的面试/拟录取名单") },
        )
//    }
}
