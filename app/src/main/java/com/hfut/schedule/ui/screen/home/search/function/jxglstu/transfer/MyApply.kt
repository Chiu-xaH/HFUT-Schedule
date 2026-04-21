package com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.jxglstu.MyApplyModels
import com.hfut.schedule.logic.model.jxglstu.TransferData
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.LoadingLargeCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.dialog.LittleDialog
import com.hfut.schedule.ui.component.icon.DepartmentIcons
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.network.onListenStateHolder
import com.hfut.schedule.ui.component.screen.pager.CustomTabRow
import com.hfut.schedule.ui.component.status.EmptyIcon
import com.hfut.schedule.ui.component.status.StatusIcon
import com.hfut.schedule.ui.component.text.BottomSheetTopBar
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.nav.destination.HaiLeWashingDestination
import com.hfut.schedule.ui.nav.destination.TransferMajorAppliedDetailDestination
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.style.corner.bottomSheetRound
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.component.text.ScrollText
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.align.CenterScreen
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.common.ui.util.text
import com.xah.container.component.base.sharedContainer
import com.xah.navigation.util.LocalNavController
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApplyListUI(vm: NetWorkViewModel, batchId : String,title : String) {

    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val navController = LocalNavController.current

    val refreshNetwork: suspend () -> Unit = {
        val cookie = getJxglstuCookie()
        cookie?.let {
            vm.myApplyData.clear()
            vm.getMyApply(it,batchId)
        }
    }
    LaunchedEffect(Unit) {
        refreshNetwork()
    }
    val uiState by vm.myApplyData.state.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }

    var displayWarningDialog by remember { mutableStateOf(false) }

    if(displayWarningDialog) {
        LittleDialog(
            onDismissRequest = { displayWarningDialog = false },
            onConfirmation = {
                displayWarningDialog = false
                showBottomSheet = true
            },
            dialogText = "二次确认"
        )
    }

    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column(
                modifier = Modifier.topBarBlur(hazeState),
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text("我的申请") },
                    navigationIcon = {
                        TopBarNavigationIcon()
                    }
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .hazeSource(hazeState)
                .fillMaxSize()
        ) {
            CommonNetworkScreen(uiState, onReload = refreshNetwork) {
                val response = (uiState as UiState.Success).data
                val applyList = response.models
                var idx by remember { mutableIntStateOf(0) }
                if(showBottomSheet) {
                    HazeBottomSheet(
                        onDismissRequest = {
                            showBottomSheet = false
                        },
                        showBottomSheet = showBottomSheet,
                    ) {
                        Column {
                            HazeBottomSheetTopBar("结果", isPaddingStatusBar = false)
                            TransferCancelStatusUI(vm,batchId, applyList[idx].id)
                            Spacer(Modifier.height(APP_HORIZONTAL_DP).navigationBarsPadding())
                        }
                    }
                }

                if(applyList.isNotEmpty()) {
                    LazyColumn {
                        item { InnerPaddingHeight(innerPadding,true) }
                        items(applyList.size, key = { it }) { index ->
                            val data = applyList[index]
                            val info = data.changeMajorSubmit
                            val dest = TransferMajorAppliedDetailDestination(batchId,index)
                            CardListItem(
                                shape = RoundedCornerShape(0.dp),
                                cardModifier = Modifier
                                    .sharedContainer(dest.key, MaterialTheme.shapes.medium,cardNormalColor()),
                                headlineContent = { Text(info.major.nameZh) },
                                leadingContent = { DepartmentIcons(info.department.nameZh) },
                                trailingContent = {
                                    FilledTonalIconButton(
                                        onClick = {
                                            idx = index
                                            displayWarningDialog = true
                                        }
                                    ) {
                                        Icon(painterResource(R.drawable.close),null)
                                    }
                                },
                                modifier = Modifier.clickable {
                                    navController.push(dest)
                                }
                            )
                        }
                        item { InnerPaddingHeight(innerPadding,false) }
                    }
                } else {
                    CenterScreen {
                        EmptyIcon()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApply(vm: NetWorkViewModel, batchId : String, indexs : Int) {

    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val navController = LocalNavController.current


    val uiState1 by vm.myApplyData.state.collectAsState()
    val refreshNetwork1 = suspend {
        val cookie = getJxglstuCookie()
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

    val successLoad = uiState1 is UiState.Success
    val refreshNetwork2 : suspend () -> Unit = {
        onListenStateHolder(vm.myApplyData) { data ->
            val cookie = getJxglstuCookie()

            val list = data.models
            val id = if(list.isNotEmpty()) {
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

    var data by remember { mutableStateOf<TransferData?>(null) }
    var list by remember { mutableStateOf<List<MyApplyModels>?>(null) }

    LaunchedEffect(uiState1) {
        if(uiState1 is UiState.Success) {
            val response = (uiState1 as UiState.Success).data
            list = response.models
            data = getMyTransfer(response.models,indexs)
        }
    }

    val isSuccessTransfer = remember { isSuccessTransfer() }

    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column(
                modifier = Modifier.topBarBlur(hazeState),
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text("申请详情") },
                    navigationIcon = {
                        TopBarNavigationIcon()
                    }
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .hazeSource(hazeState)
                .fillMaxSize()
        ) {
            InnerPaddingHeight(innerPadding,true)

            DividerTextExpandedWith(text = "状态",false) {
                Box {
                    LoadingLargeCard(
                        prepare = false,
                        title = if(isSuccessTransfer)"恭喜 已转入"
                        else if(getApplyStatus(list,indexs) == true) "学籍尚未变更"
                        else if(getApplyStatus(list,indexs) == false) "未申请或申请不通过"
                        else "状态未知",
                        loading = !successLoad
                    ) {
                        if(isSuccessTransfer) {
                            TransplantListItem(
                                headlineContent = { getPersonInfo().major?.let { ScrollText(text = it) } },
                                overlineContent = { getPersonInfo().department?.let { ScrollText(text = it) } },
                                leadingContent = { getPersonInfo().department?.let { DepartmentIcons(it) } }
                            )
                        } else {
                            if(data != null) {
                                Row {
                                    TransplantListItem(
                                        headlineContent = { getPersonInfo().major?.let { ScrollText(text = it) } },
                                        overlineContent = { getPersonInfo().department?.let { ScrollText(text = it) } },
                                        modifier = Modifier.weight(.4f)
                                    )
                                    TransplantListItem(
                                        headlineContent = { ScrollText(text = data!!.major.nameZh) },
                                        overlineContent = { ScrollText(text = data!!.department.nameZh) },
                                        leadingContent = { Icon(painterResource(R.drawable.arrow_forward), contentDescription = "") },
                                        modifier = Modifier.weight(.6f)
                                    )
                                }
                                TransplantListItem(
                                    leadingContent = { Icon(painter = painterResource(id = R.drawable.group), contentDescription = "") },
                                    overlineContent = { ScrollText(text = "已申请/计划录取") },
                                    headlineContent = { Text(text = "${data!!.applyStdCount} / ${data!!.preparedStdCount}", fontWeight = FontWeight.Bold ) },
                                )
                            }
                        }
                    }
                }
            }

            DividerTextExpandedWith("成绩") {
                CommonNetworkScreen(uiState2, isFullScreen = false, onReload = refreshNetwork2) {
                    val bean = (uiState2 as UiState.Success).data

                    val grade = bean.grade

                    CustomCard(
                        color = cardNormalColor()
                    ) {
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
                            if(data != null) {
                                Row {
                                    TransplantListItem(
//                                leadingContent = { Icon(painter = painterResource(id = R.drawable.award_star), contentDescription = "") },
                                        overlineContent = { ScrollText(text = "绩点") },
                                        headlineContent = { Text(text = "${grade.gpa.score}" ) },
                                        supportingContent = {
                                            Text("${grade.gpa.rank}/${data!!.applyStdCount} 名")
                                        },
                                        modifier = Modifier.weight(.5f)
                                    )
                                    TransplantListItem(
//                                leadingContent = { Icon(painter = painterResource(id = R.drawable.filter_vintage), contentDescription = "") },
                                        overlineContent = { ScrollText(text = "加权均分") },
                                        headlineContent = { Text(text = "${grade.weightAvg.score}" ) },
                                        supportingContent = {
                                            Text("${grade.weightAvg.rank}/${data!!.applyStdCount} 名")
                                        },
                                        modifier = Modifier.weight(.5f)
                                    )
                                }
                                Row {
                                    TransplantListItem(
//                                leadingContent = { Icon(painter = painterResource(id = R.drawable.award_star), contentDescription = "") },
                                        overlineContent = { ScrollText(text = "转专业考核") },
                                        headlineContent = { Text(text = "${grade.transferAvg.score}", fontWeight = FontWeight.Bold ) },
                                        supportingContent = {
                                            val rank = grade.transferAvg.rank
                                            if(rank != null) {
                                                Text("$rank/${data!!.applyStdCount} 名")
                                            } else {
                                                Text("教务无数据")
                                            }
                                        },
                                        modifier = Modifier.weight(.5f)
                                    )
                                    TransplantListItem(
//                                leadingContent = { Icon(painter = painterResource(id = R.drawable.filter_vintage), contentDescription = "") },
                                        overlineContent = { ScrollText(text = "算术均分") },
                                        headlineContent = { Text(text = "${grade.operateAvg.score}") },
                                        supportingContent = {
                                            Text("${grade.operateAvg.rank}/${data!!.applyStdCount} 名")
                                        },
                                        modifier = Modifier.weight(.5f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            InnerPaddingHeight(innerPadding,false)
        }
    }
}


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun TransferCancelStatusUI(vm : NetWorkViewModel, batchId: String, id: Int) {
    val uiState by vm.cancelTransferResponse.state.collectAsState()

    val refreshNetwork: suspend () -> Unit = {
        val cookie = getJxglstuCookie()
        cookie?.let {
            vm.cancelTransferResponse.clear()
            vm.cancelTransfer(it,batchId,id.toString())
        }
    }
    LaunchedEffect(Unit) {
        refreshNetwork()
    }

    CommonNetworkScreen(uiState, onReload = refreshNetwork, isFullScreen = false) {
        val result = (uiState as UiState.Success).data
        var msg  by remember { mutableStateOf("结果") }
        msg = if(result) "成功"  else "未知错误"
        StatusIcon(if(msg == "成功") R.drawable.check else R.drawable.close, text(msg))
    }
}
