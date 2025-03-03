package com.hfut.schedule.ui.activity.home.search.functions.transferMajor

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.utils.data.SharePrefs
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.logic.utils.data.reEmptyLiveDta
import com.hfut.schedule.ui.activity.home.calendar.multi.getApplyingList
import com.hfut.schedule.ui.activity.home.search.functions.life.countFunc
import com.hfut.schedule.ui.activity.home.search.functions.person.getPersonInfo
import com.hfut.schedule.ui.utils.components.APIIcons
import com.hfut.schedule.ui.utils.components.AppHorizontalDp
import com.hfut.schedule.ui.utils.components.BottomTip
import com.hfut.schedule.ui.utils.components.CustomTopBar
import com.hfut.schedule.ui.utils.style.CardForListColor
import com.hfut.schedule.ui.utils.components.DividerText
import com.hfut.schedule.ui.utils.components.DividerTextExpandedWith
import com.hfut.schedule.ui.utils.components.LoadingUI
import com.hfut.schedule.ui.utils.components.MyCustomCard
import com.hfut.schedule.ui.utils.components.ScrollText
import com.hfut.schedule.ui.utils.components.DepartmentIcons
import com.hfut.schedule.ui.utils.components.EmptyUI
import com.hfut.schedule.ui.utils.components.LoadingLargeCard
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.components.StyleCardListItem
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.components.statusUI2
import com.hfut.schedule.ui.utils.style.Round
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApplyListUI(vm: NetWorkViewModel,batchId : String) {
    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }
    var indexs by remember { mutableStateOf(0) }
    val cookie = if (!vm.webVpn) prefs.getString(
        "redirect",
        ""
    ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")

    val sheetState_apply = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_apply by remember { mutableStateOf(false) }

    if (showBottomSheet_apply) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_apply = false },
            sheetState = sheetState_apply,
            shape = Round(sheetState_apply)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    CustomTopBar("申请详情")
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
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    if(showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState,
            shape = Round(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    CustomTopBar("结果")
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    countFunc = 0
                    getMyTransferPre(vm)?.get(indexs)?.let { TransferCancelStatusUI(vm,batchId, it.id) }
                }
            }
        }
    }

    if(refresh) {
        loading = true
        CoroutineScope(Job()).launch{
            async { reEmptyLiveDta(vm.myApplyData) }.await()
            async{ cookie?.let { vm.getMyApply(it,batchId) } }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.myApplyData.observeForever { result ->
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
        val applyList = getMyTransferPre(vm)
        if (applyList != null) {
            if(applyList.isNotEmpty()) {
                LazyColumn {
                    items(applyList.size) { index ->
                        val data = applyList.get(index)
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
//                        }
                    }
                }
            } else {
                EmptyUI()
            }
        }
    }
}

@Composable
fun MyApply(vm: NetWorkViewModel,batchId : String,indexs : Int) {

    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }

    var loading2 by remember { mutableStateOf(true) }
    var refresh2 by remember { mutableStateOf(false) }
    val cookie = if (!vm.webVpn) prefs.getString(
        "redirect",
        ""
    ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")


//    val campus = getCampus()

    if(refresh) {
        loading = true
        CoroutineScope(Job()).launch{
            async { reEmptyLiveDta(vm.myApplyData) }.await()
            async{ cookie?.let {
                vm.getMyApply(it,batchId)
            } }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.myApplyData.observeForever { result ->
                        if (result != null) {
                            if(result.contains("转专业")) {
                                loading = false
                                refresh = false
                                refresh2 = true
                            }
                        }
                    }
                }
            }
        }
    }

    if(refresh2) {
        loading2 = true
        val list = getMyTransferPre(vm)
        val id = if(list?.isNotEmpty() == true) {
            list[indexs].id
        } else {
            null
        }
        CoroutineScope(Job()).launch{
            async { reEmptyLiveDta(vm.myApplyInfoData) }
            async{ cookie?.let { id?.let { it1 -> vm.getMyApplyInfo(it, it1) } } }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.myApplyInfoData.observeForever { result ->
                        if (result != null) {
                            if(result.contains("面试安排")) {
                                loading2 = false
                                refresh2 = false
                            }
                        }
                    }
                }
            }
        }
    }


    val scale = animateFloatAsState(
        targetValue = if (loading) 0.9f else 1f, // 按下时为0.9，松开时为1
        //animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        animationSpec = tween(MyApplication.Animation / 2, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )
    val scale2 = animateFloatAsState(
        targetValue = if (loading) 0.97f else 1f, // 按下时为0.9，松开时为1
        //animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        animationSpec = tween(MyApplication.Animation / 2, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )

    val blurSize by animateDpAsState(
        targetValue = if (loading) 10.dp else 0.dp, label = ""
        ,animationSpec = tween(MyApplication.Animation / 2, easing = LinearOutSlowInEasing),
    )
//    val status = getPersonInfo().status
    val data = getMyTransfer(vm,indexs)
    val isSuccessTransfer = isSuccessTransfer()
    DividerTextExpandedWith(text = "状态",false) {
        Box {
            LoadingLargeCard(
                title = if(isSuccessTransfer)"恭喜 已转入"
                else if(getApplyStatus(vm,indexs) == true) "学籍尚未变更"
                else if(getApplyStatus(vm,indexs) == false) "未申请或申请不通过"
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
        if(loading2) {
            LoadingUI()
        } else {
            val bean = getMyTransferInfo(vm)
            val grade = bean?.grade

            if(!isSuccessTransfer) {
                val examSchedule = bean?.examSchedule
                val meetSchedule = bean?.meetSchedule

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

            if(grade != null) {
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


//    BottomTip("具体详情请一定关注QQ群、查询中心-通知公告、教务系统")
}


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun TransferCancelStatusUI(vm : NetWorkViewModel,batchId: String,id: Int) {

    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }

//    var phoneNumber by remember { mutableStateOf("") }
    var msg  by remember { mutableStateOf("结果") }

    var cookie = if (!vm.webVpn) prefs.getString(
        "redirect",
        ""
    ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")

    if(refresh) {
        loading = true
        CoroutineScope(Job()).launch {
            async { reEmptyLiveDta(vm.cancelTransferResponse) }
            async {
                cookie?.let { vm.cancelTransfer(it,batchId,id.toString()) }
            }.await()
            async {
                Handler(Looper.getMainLooper()).post {
                    vm.cancelTransferResponse.observeForever { result ->
                        if (result != null) {
                            msg = if(result) {
                                "成功"
                            } else {
                                "未知错误"
                            }
                            refresh = false
                            loading = false
                        }
                    }
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
            statusUI2(painter =
            if(msg == "成功" ) Icons.Filled.Check
            else Icons.Filled.Close
                , text = msg)
        }
    }

}
