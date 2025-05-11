package com.hfut.schedule.ui.screen.home.search.function.transfer

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.jxglstu.MyApplyModels
import com.hfut.schedule.logic.model.jxglstu.TransferData
import com.hfut.schedule.logic.model.jxglstu.courseType
import com.hfut.schedule.logic.util.network.SimpleUiState
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.component.BottomSheetTopBar
import com.hfut.schedule.ui.component.CommonNetworkScreen
import com.hfut.schedule.ui.component.DepartmentIcons
import com.hfut.schedule.ui.component.DividerTextExpandedWith
import com.hfut.schedule.ui.component.EmptyUI
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.LoadingLargeCard
import com.hfut.schedule.ui.component.ScrollText
import com.hfut.schedule.ui.component.StatusUI2
import com.hfut.schedule.ui.component.StyleCardListItem
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.onListenStateHolder
import com.hfut.schedule.ui.screen.home.search.function.life.countFunc
import com.hfut.schedule.ui.screen.home.search.function.person.getPersonInfo
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.bottomSheetRound
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApplyListUI(vm: NetWorkViewModel, batchId : String, hazeState: HazeState) {
    var indexs by remember { mutableIntStateOf(0) }
    val cookie = remember { if (!vm.webVpn) prefs.getString(
        "redirect",
        ""
    ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "") }

    var showBottomSheet_apply by remember { mutableStateOf(false) }

    if (showBottomSheet_apply) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_apply = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_apply,
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("申请详情")
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ) {
                    MyApply(vm,batchId,indexs)
                }
            }
        }
    }


    val refreshNetwork: suspend () -> Unit = {
        cookie?.let {
            vm.myApplyData.clear()
            vm.getMyApply(it,batchId)
        }
    }
    LaunchedEffect(Unit) {
        refreshNetwork()
    }
    val uiState by vm.myApplyData.state.collectAsState()

    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        var showBottomSheet by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState()
        val response = (uiState as SimpleUiState.Success).data
        val applyList = response.models
        if(showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState,
                shape = bottomSheetRound(sheetState)
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Transparent,
                    topBar = {
                        BottomSheetTopBar("结果")
                    },
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        countFunc = 0
                        TransferCancelStatusUI(vm,batchId, applyList[indexs].id)
                    }
                }
            }
        }


        if(applyList.isNotEmpty()) {
            LazyColumn {
                items(applyList.size, key = { it }) { index ->
                    val data = applyList[index]
                    val info = data.changeMajorSubmit
//                        MyCustomCard {
                    StyleCardListItem(
                        headlineContent = { Text(info.major.nameZh) },
                        leadingContent = { DepartmentIcons(info.department.nameZh) },
                        trailingContent = {
                            FilledTonalIconButton(
                                onClick = {
                                    showBottomSheet = true
                                }
                            ) {
                                Icon(Icons.Filled.Close,null)
                            }
                        },
                        modifier = Modifier.clickable {
                            indexs = index
                            showBottomSheet_apply = true
                        }
                    )
                }
            }
        } else {
            EmptyUI()
        }
    }
}

@Composable
fun MyApply(vm: NetWorkViewModel, batchId : String, indexs : Int) {

    val cookie = remember {
        if (!vm.webVpn) prefs.getString(
            "redirect",
            ""
        ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")
    }


    val uiState1 by vm.myApplyData.state.collectAsState()
    val refreshNetwork1 = suspend {
        cookie?.let {
            vm.myApplyData.clear()
            vm.getMyApply(it,batchId)
        }
    }
    LaunchedEffect(Unit) {
        refreshNetwork1()
    }

    val scope = rememberCoroutineScope()

    val uiState2 by vm.myApplyInfoData.state.collectAsState()

    var loading = uiState1 !is SimpleUiState.Success
    val refreshNetwork2 : suspend () -> Unit = {
        onListenStateHolder(vm.myApplyData) { data ->
            val list = data.models
            val id = if(list.isNotEmpty() == true) {
                list[indexs].id
            } else {
                null
            }
            cookie?.let { id?.let { i ->
                scope.launch {
                    vm.myApplyInfoData.clear()
                    vm.getMyApplyInfo(it, i)
                }
            } }
        }
    }
    LaunchedEffect(Unit) {
        refreshNetwork2()
    }

    var data by remember { mutableStateOf(TransferData(null,0, courseType(""), courseType(""),0,0)) }
    var list by remember { mutableStateOf<List<MyApplyModels>?>(null) }

    LaunchedEffect(uiState1) {
        if(uiState1 is SimpleUiState.Success) {
            val response = (uiState1 as SimpleUiState.Success).data
            list = response.models
            data = getMyTransfer(response.models,indexs)
        }
    }

    val isSuccessTransfer = remember { isSuccessTransfer() }
    DividerTextExpandedWith(text = "状态",false) {
        Box {
            LoadingLargeCard(
                title = if(isSuccessTransfer)"恭喜 已转入"
                else if(getApplyStatus(list,indexs) == true) "学籍尚未变更"
                else if(getApplyStatus(list,indexs) == false) "未申请或申请不通过"
                else "状态未知",
                loading = loading
            ) {
                if(isSuccessTransfer) {
                    TransplantListItem(
                        headlineContent = { getPersonInfo().major?.let { ScrollText(text = it) } },
                        overlineContent = { getPersonInfo().department?.let { ScrollText(text = it) } },
                        leadingContent = { getPersonInfo().department?.let { DepartmentIcons(it) } }
                    )
                } else {
                    Row {
                        TransplantListItem(
                            headlineContent = { getPersonInfo().major?.let { ScrollText(text = it) } },
                            overlineContent = { getPersonInfo().department?.let { ScrollText(text = it) } },
                            modifier = Modifier.weight(.4f)
                        )
                        TransplantListItem(
                            headlineContent = { ScrollText(text = data.major.nameZh) },
                            overlineContent = { ScrollText(text = data.department.nameZh) },
                            leadingContent = { Icon(Icons.Filled.ArrowForward, contentDescription = "") },
                            modifier = Modifier.weight(.6f)
                        )
                    }
                    TransplantListItem(
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.group), contentDescription = "") },
                        overlineContent = { ScrollText(text = "已申请/计划录取") },
                        headlineContent = { Text(text = "${data.applyStdCount} / ${data.preparedStdCount}", fontWeight = FontWeight.Bold ) },
                    )
                }
            }
        }
    }

    DividerTextExpandedWith("成绩") {
        CommonNetworkScreen(uiState2, isFullScreen = false, onReload = refreshNetwork2) {
            val bean = (uiState2 as SimpleUiState.Success).data

            val grade = bean.grade

            Column {
                if(!isSuccessTransfer) {
                    val examSchedule = bean.examSchedule
                    val meetSchedule = bean.meetSchedule

                    if(examSchedule != null) {
                        TransplantListItem(
                            headlineContent = { Text(examSchedule.place.replace("；","\n").replace("："," ").replace("。","")) },
                            supportingContent = { Text(examSchedule.time) },
                            overlineContent = { Text("笔试安排") }
                        )
                    }
                    if(meetSchedule != null) {
                        TransplantListItem(
                            headlineContent = { Text(meetSchedule.place.replace("；","\n").replace("："," ")) },
                            supportingContent = { Text(meetSchedule.time) },
                            overlineContent = { Text("面试安排") }
                        )
                    }
                }
                Row {
                    TransplantListItem(
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.award_star), contentDescription = "") },
                        overlineContent = { ScrollText(text = "绩点") },
                        headlineContent = { Text(text = "${grade.gpa.score}" ) },
                        supportingContent = {
                            Text("${grade.gpa.rank}/${data.applyStdCount} 名")
                        },
                        modifier = Modifier.weight(.5f)
                    )
                    TransplantListItem(
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.filter_vintage), contentDescription = "") },
                        overlineContent = { ScrollText(text = "加权均分") },
                        headlineContent = { Text(text = "${grade.weightAvg.score}" ) },
                        supportingContent = {
                            Text("${grade.weightAvg.rank}/${data.applyStdCount} 名")
                        },
                        modifier = Modifier.weight(.5f)
                    )
                }
                Row {
                    TransplantListItem(
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.award_star), contentDescription = "") },
                        overlineContent = { ScrollText(text = "转专业考核") },
                        headlineContent = { Text(text = "${grade.transferAvg.score}", fontWeight = FontWeight.Bold ) },
                        supportingContent = {
                            val rank = grade.transferAvg.rank
                            if(rank != null) {
                                Text("$rank/${data.applyStdCount} 名")
                            } else {
                                Text("教务无数据")
                            }
                        },
                        modifier = Modifier.weight(.5f)
                    )
                    TransplantListItem(
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.filter_vintage), contentDescription = "") },
                        overlineContent = { ScrollText(text = "算术均分") },
                        headlineContent = { Text(text = "${grade.operateAvg.score}") },
                        supportingContent = {
                            Text("${grade.operateAvg.rank}/${data.applyStdCount} 名")
                        },
                        modifier = Modifier.weight(.5f)
                    )
                }
            }
        }
    }
}


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun TransferCancelStatusUI(vm : NetWorkViewModel, batchId: String, id: Int) {
    val uiState by vm.cancelTransferResponse.state.collectAsState()
    val refreshNetwork: suspend () -> Unit = {
        var cookie = if (!vm.webVpn) prefs.getString(
            "redirect",
            ""
        ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")
        cookie?.let {
            vm.cancelTransferResponse.clear()
            vm.cancelTransfer(it,batchId,id.toString())
        }
    }
    LaunchedEffect(Unit) {
        refreshNetwork()
    }

    CommonNetworkScreen(uiState, onReload = refreshNetwork, isFullScreen = false) {
        val result = (uiState as SimpleUiState.Success).data
        var msg  by remember { mutableStateOf("结果") }
        msg = if(result) "成功"  else "未知错误"
        StatusUI2(painter = if(msg == "成功") Icons.Filled.Check else Icons.Filled.Close, text = msg)
    }
}
