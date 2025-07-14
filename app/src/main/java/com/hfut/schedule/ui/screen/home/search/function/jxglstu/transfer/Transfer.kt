package com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.AnimationCardListItem
import com.hfut.schedule.ui.component.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.CommonNetworkScreen
import com.hfut.schedule.ui.component.custom.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.StyleCardListItem
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.custom.CustomTextField
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.flow.first


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
//                            modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)
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
    var isHidden by remember { mutableStateOf(false) }


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
                    TransferUI(vm,batchId,hazeState,isHidden)
                }
            }
        }
    }
    val uiState by vm.transferListData.state.collectAsState()

    val refreshNetwork: suspend () -> Unit = {
        val cookie = getJxglstuCookie(vm)
        cookie?.let {
            vm.transferListData.clear()
            vm.getTransferList(it)
        }
    }

    LaunchedEffect(Unit) {
        refreshNetwork()
    }
    var input by remember { mutableStateOf("") }

    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        val transferList = (uiState as UiState.Success).data
        LazyColumn {
            items(transferList.size) { index ->
                val data = transferList[index]
//                MyCustomCard {
                AnimationCardListItem(
                    headlineContent = { Text(data.title) },
                    supportingContent = { Text("申请日期 " + data.applicationDate + "\n转专业时期 " + data.admissionDate) },
//                    trailingContent = { Icon(Icons.Filled.ArrowForward,null) },
                    trailingContent = {
                        Text("代号 " + data.batchId)
                    },
                    modifier = Modifier.clickable {
                        isHidden = false
                        title = data.title
                        batchId = data.batchId
                        showBottomSheet = true
                    },
                    index = index
                )
//                }
            }
            item {
                AnimationCardListItem(
                    index = transferList.size,
                    headlineContent = {
                        Text("手动输入代号查看被隐藏掉的转专业入口")
                    },
                    supportingContent = {
                        Column {
                            Text("合肥校区和宣城校区之间转专业入口互相不可见，但可以通过输入代号进入，代号位于右上角\n免责声明：只供看，不要报异地校区的志愿，后果自负\n示例：1,3,21,42,43,61,101等...")
                            Spacer(Modifier.height(APP_HORIZONTAL_DP/2))
                            Row {
                                TextField(
                                    modifier = Modifier
                                        .weight(1f),
                                    value = input,
                                    onValueChange = { input = it },
                                    label = { Text("输入数字代号") },
                                    trailingIcon = {
                                        IconButton(onClick = {
                                            if(input.toIntOrNull() != null) {
                                                isHidden = true
                                                title = "${input}入口"
                                                batchId = input
                                                showBottomSheet = true
                                            } else {
                                                showToast("必须为数字")
                                            }
                                        }) {
                                            Icon(Icons.Default.ArrowForward,null)
                                        }
                                    },
                                    shape = MaterialTheme.shapes.medium,
                                    colors = textFiledTransplant(),
                                )
                            }
                        }
                    }
                )
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
