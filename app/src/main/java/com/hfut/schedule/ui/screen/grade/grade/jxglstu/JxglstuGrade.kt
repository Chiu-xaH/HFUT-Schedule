package com.hfut.schedule.ui.screen.grade.grade.jxglstu

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.community.GradeResponseJXGLSTU
import com.hfut.schedule.logic.util.network.UiState
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.component.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.AnimationCardListItem
import com.hfut.schedule.ui.component.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.CommonNetworkScreen
import com.hfut.schedule.ui.component.DividerTextExpandedWith
import com.hfut.schedule.ui.component.EmptyUI
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.LargeCard
import com.hfut.schedule.ui.component.LittleDialog
import com.hfut.schedule.ui.component.ScrollText
import com.hfut.schedule.ui.component.TransplantListItem
 
  
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.screen.card.count.RadarChart
import com.hfut.schedule.ui.screen.card.count.RadarData
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.survey.SurveyUI
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.CourseTotalForApi
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun GradeItemUIJXGLSTU(innerPadding: PaddingValues, vm: NetWorkViewModel, showSearch : Boolean, hazeState: HazeState) {
    val uiState by vm.jxglstuGradeData.state.collectAsState()
    val refreshNetwork: suspend () -> Unit = {
        val cookie = if(!vm.webVpn) prefs.getString("redirect", "")  else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket","")
        cookie?.let {
            vm.jxglstuGradeData.clear()
            vm.getGradeFromJxglstu(cookie,null)
        }
    }
    LaunchedEffect(Unit) {
        refreshNetwork()
    }

    var showBottomSheet by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("成绩详情") }
    var num by remember { mutableStateOf(GradeResponseJXGLSTU("","","","","")) }
    var showBottomSheet_Survey by remember { mutableStateOf(false) }

    if (showBottomSheet_Survey) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_Survey = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_Survey
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("评教") {
                        CourseTotalForApi(vm=vm, hazeState = hazeState)
                    }
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    SurveyUI(vm,hazeState)
                }
            }
        }
    }

    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet= false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar(title)
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ){
                    GradeInfo(num,vm)
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }

    var input by remember { mutableStateOf("") }

    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        val gradeList = (uiState as UiState.Success).data
        var searchList = mutableListOf<GradeResponseJXGLSTU>()
        Column {
            if(showSearch) {
                Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))

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
                        label = { Text("搜索课程名") },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(
                                onClick = {}) {
                                Icon(
                                    painter = painterResource(R.drawable.search),
                                    contentDescription = "description"
                                )
                            }
                        },
                        shape = MaterialTheme.shapes.medium,
                        colors = textFiledTransplant(),
                    )
                }
                gradeList.forEach { item ->
                    if (item.title.contains(input) || item.title.contains(input)) {
                        searchList.add(item)
                    }
                }

                Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
            } else {
                searchList = gradeList.toMutableList()
            }

            if(gradeList.isEmpty()) EmptyUI()
            else {
                LazyColumn{
                    if(!showSearch) {
                        item { Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding())) }
                        item { Spacer(modifier = Modifier.height(5.dp)) }
                    }

                    items(searchList.size) { item ->
                        val grade = searchList[item]
                        AnimationCardListItem(
                            headlineContent = {  Text(grade.title) },
                            overlineContent = { Text( "成绩  "+ grade.totalGrade + "  |  绩点  " + grade.GPA +  "  |  学分  " + grade.score) },
                            leadingContent = { Icon(painterResource(R.drawable.article), contentDescription = "Localized description",) },
                            supportingContent = { Text(grade.grade) },
                            modifier = Modifier.clickable {
                                if(grade.grade.contains("评教")) {
                                    showToast("请为任课老师评教(存在多位老师需全评教，老师列表可在课程汇总查看)")
                                    showBottomSheet_Survey = true
                                } else {
                                    title = grade.title
                                    num = grade
                                    showBottomSheet = true
                                }
                            },
                            index = item
                        )
                    }
                    item { Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding())) }
                }
            }
        }
    }
}


@Composable
fun GradeInfo(num : GradeResponseJXGLSTU,vm: NetWorkViewModel) {
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val list = num.grade.split(" ")
    val radarList = mutableListOf<RadarData>()
    list.forEach { item ->
        val label = item.substringBefore(":")
        val score = try {
            if (item.substringAfter(":").contains("中") || item.substringAfter(":")
                    .contains("及格")
            ) .6f
            else if (item.substringAfter(":").contains("高") || item.substringAfter(":")
                    .contains("优秀")
            ) .9f
            else if (item.substringAfter(":").contains("低")) .3f
            else item.substringAfter(":").toFloat() / 100
        } catch (_: Exception) {
            if (item.substringAfter(":").contains("中") || item.substringAfter(":")
                    .contains("及格")
            ) .6f
            else if (item.substringAfter(":").contains("高") || item.substringAfter(":")
                    .contains("优秀")
            ) .9f
            else if (item.substringAfter(":").contains("低")) .3f
            else 0f
        }
        radarList.add(RadarData(label, score))
    }
    val scrollState = rememberScrollState()
    val GPA = num.GPA
    val title = "成绩 ${num.totalGrade} 绩点 $GPA"

    var avgPingshi = 0f
    var examScore = 0f
    var count = 0
    radarList.forEach { item ->
        if (!item.label.contains("期末")) {
            avgPingshi += item.value
            count++
        } else {
            examScore = item.value
        }
    }
    if (count != 0)
        avgPingshi /= count
    else avgPingshi = 0f
    if (examScore != 0f)
        avgPingshi /= examScore
    else avgPingshi = 0f

    var showDialog by remember {
        mutableStateOf(false)
    }
    if (showDialog) {
        LittleDialog(
            onDismissRequest = { showDialog = false },
            onConfirmation = { showDialog = false },
            dialogText = "平时因数=除去期末成绩各项平均分/期末分数,可大致反映最终成绩平时分占比\n越接近1则平衡,越>1则表明最终成绩可能更靠平时分,越<1表明最终成绩可能因平时分拖后腿",
            conformText = "好",
            dismissText = "关闭",
            hazeState = hazeState
        )
    }

    LaunchedEffect(key1 = title) {
        delay(500L)
        scrollState.animateScrollTo(scrollState.maxValue)
        delay(4000L)
        scrollState.animateScrollTo(0)
    }
    Column(modifier = Modifier.hazeSource(hazeState)) {
        DividerTextExpandedWith(text = "雷达图") {
//        Divider()
            val topPadding = if(radarList.size == 0) {
                0
            } else {
                35
            }
            Spacer(modifier = Modifier.height(topPadding.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                RadarChart(data = radarList, modifier = Modifier.size(200.dp))
            }
            val bottomPadding = if(radarList.size == 0) {
                0
            } else if(radarList.size == 1) {
                0
            } else if(radarList.size == 3) {
                0
            } else {
                25
            }
            Spacer(modifier = Modifier.height(bottomPadding.dp))
//        Divider()
        }
        DividerTextExpandedWith(text = "成绩详情",false) {
            LargeCard(title) {
                for (i in list.indices step 2)
                    Row {
                        val l1 = list[i]
                        val t1 = l1.substringBefore(":")
                        val score1 = l1.substringAfter(":")

                        TransplantListItem(
                            headlineContent = {
                                Text(
                                    text = score1,
                                    fontWeight = if(t1.contains("期末考试") || t1.contains("期中考试")) FontWeight.Bold else FontWeight.Normal,
                                    textDecoration = if(t1.contains("期末考试") || t1.contains("期中考试")) TextDecoration.Underline else TextDecoration.None
                                ) },
                            overlineContent = { Text(t1) },
                            modifier = Modifier.weight(.5f),
                            leadingContent = { GradeIcons(t1) }
                        )
                        if (i + 1 < list.size) {
                            val l2 = list[i+1]
                            val t2 = l2.substringBefore(":")
                            val score2 = l2.substringAfter(":")
                            TransplantListItem(
                                headlineContent = {
                                    Text(
                                        text = score2,
                                        fontWeight = if(t2.contains("期末考试") || t2.contains("期中考试")) FontWeight.Bold else FontWeight.Normal,
                                        textDecoration = if(t2.contains("期末考试") || t2.contains("期中考试")) TextDecoration.Underline else TextDecoration.None
                                    ) },
                                overlineContent = { Text(t2) },
                                modifier = Modifier.weight(.5f),
                                leadingContent = { GradeIcons(t2) }
                            )
                        }
                    }
                Row {
//                ListItem(
//                    leadingContent = {
//                        Icon(
//                            painter = painterResource(id = if (GPA.toFloat() >= 3.0) R.drawable.sentiment_very_satisfied else R.drawable.sentiment_dissatisfied),
//                            contentDescription = ""
//                        )
//                    },
//                    headlineContent = {
//                        ScrollText(
//                            text = if (GPA == "4.3") "满绩" else if (GPA == "4") "优秀" else if (GPA.toFloat() >= 3) "不错" else if (GPA == "0") "挂科" else "薄弱"
//                        )
//                    },
//                    modifier = Modifier.weight(.4f)
//                )
                    TransplantListItem(
                        leadingContent = {
                            Icon(
                                painter = painterResource(id = R.drawable.filter_vintage),
                                contentDescription = ""
                            )
                        },
                        headlineContent = {
                            ScrollText(
                                text =  num.score
                            )
                        },
                        overlineContent = {
                            Text("学分")
                        },
                        modifier = Modifier.weight(.5f)
                    )
                    TransplantListItem(
                        leadingContent = {
                            Icon(painter = painterResource(R.drawable.percent), contentDescription = "")
                        },
                        headlineContent = {
                            ScrollText(
                                text = if (avgPingshi != 0f) formatDecimal(
                                    avgPingshi.toDouble(),
                                    2
                                ) else "未知"
                            )
                        },
                        overlineContent = {
                            Text("平时因数")
                        },
                        modifier = Modifier
                            .weight(.5f)
                            .clickable {
                                showDialog = true
                            }
                    )
                }
            }
        }
    }
}


@Composable
fun GradeIcons(text : String) {
    if(text.contains("考试")) {
        Icon(painterResource(R.drawable.draw),null)
    } else if(text.contains("报告")) {
        Icon(painterResource(R.drawable.description),null)
    } else if(text.contains("实验")) {
        Icon(painterResource(R.drawable.body_fat),null)
    } else if(text.contains("作业")) {
        Icon(painterResource(R.drawable.article),null)
    } else if(text.contains("实习")) {
        Icon(painterResource(R.drawable.work),null)
    } else if(text.contains("测试")) {
        Icon(painterResource(R.drawable.tactic),null)
    } else if(text == "平时成绩") {
        Icon(painterResource(R.drawable.hotel_class),null)
    } else if(text.contains("演讲")) {
        Icon(painterResource(R.drawable.groups),null)
    }  else if(text.contains("提问及讨论")) {
        Icon(painterResource(R.drawable.lightbulb),null)
    } else if(text.contains("口试")) {
        Icon(painterResource(R.drawable.voice_selection),null)
    } else if(text.contains("上机")) {
        Icon(painterResource(R.drawable.computer),null)
    } else if(text.contains("论文")) {
        Icon(painterResource(R.drawable.newsstand),null)
    } else {
        Icon(painterResource(R.drawable.category),null)
    }
}