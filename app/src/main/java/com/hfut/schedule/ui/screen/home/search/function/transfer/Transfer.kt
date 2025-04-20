package com.hfut.schedule.ui.screen.home.search.function.transfer

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
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.ui.component.AnimationCardListItem
import com.hfut.schedule.ui.component.appHorizontalDp
import com.hfut.schedule.ui.component.BottomSheetTopBar
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.LoadingUI
import com.hfut.schedule.ui.component.MyCustomCard
import com.hfut.schedule.ui.style.bottomSheetRound
import com.hfut.schedule.ui.component.ScrollText
import com.hfut.schedule.ui.component.StyleCardListItem
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.style.HazeBottomSheet
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


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
        HazeBottomSheet(
            onDismissRequest = { showBottomSheet_apply = false },
            hazeState = hazeState,
            isFullExpand = false,
            showBottomSheet = showBottomSheet_apply
//            sheetState = sheetState_apply,
//            shape = bottomSheetRound(sheetState_apply)
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
//                        .verticalScroll(rememberScrollState())
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
//            sheetState = sheetState,
//            shape = bottomSheetRound(sheetState)
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
