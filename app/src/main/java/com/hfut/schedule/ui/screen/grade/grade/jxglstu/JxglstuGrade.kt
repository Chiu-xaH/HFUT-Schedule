package com.hfut.schedule.ui.screen.grade.grade.jxglstu


import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.ScoreGrade
import com.hfut.schedule.logic.model.ScoreWithGPA
import com.hfut.schedule.logic.model.community.GradeResponseJXGLSTU
import com.hfut.schedule.logic.model.scoreWithGPA
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.CommonNetworkScreen
import com.hfut.schedule.ui.component.DividerTextExpandedWith
import com.hfut.schedule.ui.component.EmptyUI
import com.hfut.schedule.ui.component.LargeCard
import com.hfut.schedule.ui.component.MyCustomCard
import com.hfut.schedule.ui.component.StyleCardListItem
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.cardNormalColor
import com.hfut.schedule.ui.component.chart.RadarChart
import com.hfut.schedule.ui.component.chart.RadarData
import com.hfut.schedule.ui.component.custom.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.custom.LittleDialog
import com.hfut.schedule.ui.component.custom.RefreshIndicator
import com.hfut.schedule.ui.component.custom.ScrollText
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.survey.SurveyUI
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun GradeItemUIJXGLSTU(innerPadding: PaddingValues, vm: NetWorkViewModel, showSearch : Boolean, hazeState: HazeState) {
    val uiState by vm.jxglstuGradeData.state.collectAsState()

    val refreshNetwork: suspend () -> Unit = {
        val cookie = getJxglstuCookie(vm)
        cookie?.let {
            vm.jxglstuGradeData.clear()
            vm.getGradeFromJxglstu(cookie,null)
        }
    }
    val refreshing = uiState is UiState.Loading

    LaunchedEffect(Unit) {
        if(refreshing) {
            refreshNetwork()
        }
    }

    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        scope.launch { refreshNetwork() }
    })

    var showBottomSheet by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("成绩详情") }
    var num by remember { mutableStateOf(GradeResponseJXGLSTU("","","","","","")) }
    var showBottomSheet_Survey by remember { mutableStateOf(false) }
    var surveyCode by remember { mutableStateOf("") }

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
                    HazeBottomSheetTopBar("评教 $surveyCode")
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    SurveyUI(vm,hazeState,surveyCode)
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
                    GradeInfo(num)
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }

    var input by remember { mutableStateOf("") }

    Box(modifier = Modifier
        .fillMaxHeight()
        .pullRefresh(pullRefreshState)){
        RefreshIndicator(refreshing, pullRefreshState, Modifier
            .padding(innerPadding)
            .align(Alignment.TopCenter))
        CommonNetworkScreen(uiState, onReload = refreshNetwork) {
            val gradeList = (uiState as UiState.Success).data
            var searchList = mutableListOf<GradeResponseJXGLSTU>()

            val Item = @Composable { grade : GradeResponseJXGLSTU ->
                val needSurvey = grade.grade.contains("评教")
                StyleCardListItem(
                    headlineContent = {  Text(grade.title) },
                    overlineContent = { Text(
                        if(!needSurvey)
                            "分数 "+ grade.totalGrade + " | 绩点 " + grade.GPA +  " | 学分 " + grade.score
                        else grade.code
                    ) },
                    leadingContent = { Icon(painterResource(R.drawable.article), contentDescription = "Localized description",) },
                    supportingContent = { Text(if(needSurvey) "点击跳转评教" else grade.grade) },
                    color = if(needSurvey) MaterialTheme.colorScheme.secondaryContainer else null,
                    modifier = Modifier.clickable {
                        if(needSurvey) {
                            surveyCode = grade.code
                            showToast("请为本课程的所有老师评教，下拉刷新以查看成绩")
                            showBottomSheet_Survey = true
                        } else {
                            title = grade.title
                            num = grade
                            showBottomSheet = true
                        }
                    },
                )
            }
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
                            label = { Text("搜索 课程名、代码") },
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
                    gradeList.flatMap { it.list }.forEach { item ->
                        if (item.title.contains(input) || item.title.contains(input)) {
                            searchList.add(item)
                        }
                    }

                    Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                }
                if(gradeList.isEmpty()) EmptyUI()
                else {
                    if(showSearch) {
                        LazyColumn{
//                            if(!showSearch) {
//                                item { Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding())) }
//                                item { Spacer(modifier = Modifier.height(5.dp)) }
//                            }

                            items(searchList.size) { item ->
                                Item(searchList[item])
//                                val grade = searchList[item]
//                                val needSurvey = grade.grade.contains("评教")
//                                AnimationCardListItem(
//                                    headlineContent = {  Text(grade.title) },
//                                    overlineContent = { Text(
//                                        if(!needSurvey)
//                                            "分数 "+ grade.totalGrade + " | 绩点 " + grade.GPA +  " | 学分 " + grade.score
//                                        else grade.code
//                                    ) },
//                                    leadingContent = { Icon(painterResource(R.drawable.article), contentDescription = "Localized description",) },
//                                    supportingContent = { Text(if(needSurvey) "点击跳转评教" else grade.grade) },
//                                    color = if(needSurvey) MaterialTheme.colorScheme.secondaryContainer else null,
//                                    modifier = Modifier.clickable {
//                                        if(needSurvey) {
//                                            surveyCode = grade.code
//                                            showToast("请为本课程的所有老师评教，下拉刷新以查看成绩")
//                                            showBottomSheet_Survey = true
//                                        } else {
//                                            title = grade.title
//                                            num = grade
//                                            showBottomSheet = true
//                                        }
//                                    },
//                                    index = item
//                                )
                            }
                            item { Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding())) }
                        }
                    } else {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
                            Spacer(modifier = Modifier.height(5.dp))
                            for(i in gradeList) {
                                DividerTextExpandedWith(i.term) {
                                    for(j in i.list) {
                                        Item(j)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun GradeInfo(num : GradeResponseJXGLSTU) {
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

    Column(modifier = Modifier.hazeSource(hazeState)) {
        DividerTextExpandedWith(text = "雷达图") {
//        Divider()
            val topPadding = if(radarList.isEmpty()) {
                0
            } else {
                35
            }
            Spacer(modifier = Modifier.height(topPadding.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                RadarChart(data = radarList, modifier = Modifier.size(200.dp))
            }
            val bottomPadding = if(radarList.isEmpty()) {
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
        DividerTextExpandedWith(text = "详情",false) {
            LargeCard("分数 ${num.totalGrade} 绩点 ${num.GPA}") {
                for (i in list.indices step 2) {
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
                }
            }
        }
        DividerTextExpandedWith("其他信息") {
            MyCustomCard(containerColor = cardNormalColor()) {
                Row {
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
                TransplantListItem(
                    leadingContent = {
                        Icon(
                            painter = painterResource(id = R.drawable.tag),
                            contentDescription = ""
                        )
                    },
                    headlineContent = {
                        ScrollText(
                            text =  num.code
                        )
                    },
                    overlineContent = {
                        Text("代码")
                    }
                )
                with(num) {
                    TransplantListItem(
                        headlineContent = {
                            GPAStarGroup(totalGrade,GPA)
                        },
                        leadingContent = {
                            Icon(painterResource(R.drawable.award_star),null)
                        },
                        overlineContent = {
                            getGradeNextLevel(totalGrade,GPA)?.let { target ->
                                totalGrade.toDoubleOrNull()?.let { current -> Text(" 距离下一绩点${target.gpa}需要${target.score.min - current}分") }
                            }
                        }
                    )
                }
            }

        }
        DividerTextExpandedWith("绩点与分数对应关系") {
            GPAWithScore()
        }
    }
}
@Composable
fun GPAWithScore(index : Pair<Int, Int>? = null) {
    MyCustomCard(containerColor = cardNormalColor()) {
//        var boldIndex =
//            if(index?.second == scoreWithGPA.size - 1) {
//                index.first
//            }  else {
//                null
//            }
        for(i in 0..scoreWithGPA.size-1 step 3) {
            val item1 = scoreWithGPA[i]
            val item2 = scoreWithGPA[i+1]
            val item3 = scoreWithGPA[i+2]

            Row {
                with(item1) {
                    TransplantListItem(
                        supportingContent = {
                            Text("${score.min}-${score.max}")
                        },
                        headlineContent = { Text(gpa.toString()) },
                        modifier = Modifier.weight(1/3f)
                    )
                }
                with(item2) {
                    TransplantListItem(
                        supportingContent = {
                            Text("${score.min}-${score.max}")
                        },
                        headlineContent = { Text(gpa.toString()) },
                        modifier = Modifier.weight(1/3f)
                    )
                }
                with(item3) {
                    TransplantListItem(
                        supportingContent = {
                            Text("${score.min}-${score.max}")
                        },
                        headlineContent = { Text(gpa.toString()) },
                        modifier = Modifier.weight(1/3f)
                    )
                }
            }
        }
    }
    MyCustomCard(containerColor = cardNormalColor()) {
//        var boldIndex =
//             if(index?.second == ScoreGrade.entries.size - 1) {
//                index.first
//            } else {
//                null
//            }
        val item1 = ScoreGrade.A
        val item2 = ScoreGrade.B
        val item3 = ScoreGrade.C
        val item4 = ScoreGrade.D
        val item5 = ScoreGrade.F
        Row {
            with(item1) {
                TransplantListItem(
                    supportingContent = {
                        Text("$label($name)")
                    },
                    headlineContent = { Text(gpa.toString()) },
                    modifier = Modifier.weight(1/3f)
                )
            }
            with(item2) {
                TransplantListItem(
                    supportingContent = {
                        Text("$label($name)")
                    },
                    headlineContent = { Text(gpa.toString()) },
                    modifier = Modifier.weight(1/3f)
                )
            }
            with(item3) {
                TransplantListItem(
                    supportingContent = {
                        Text("$label($name)")
                    },
                    headlineContent = { Text(gpa.toString()) },
                    modifier = Modifier.weight(1/3f)
                )
            }
        }
        Row {
            with(item4) {
                TransplantListItem(
                    supportingContent = {
                        Text("$label($name)")
                    },
                    headlineContent = { Text(gpa.toString()) },
                    modifier = Modifier.weight(1/3f)
                )
            }
            with(item5) {
                TransplantListItem(
                    supportingContent = {
                        Text("$label($name)")
                    },
                    headlineContent = { Text(gpa.toString()) },
                    modifier = Modifier.weight(1/3f)
                )
            }
            TransplantListItem(
                supportingContent = {
                    Text("五星制")
                },
                headlineContent = { Text("GPA") },
                modifier = Modifier.weight(1/3f)
            )
        }
    }
}

// 传入分数，判定分级
fun getGradeLevel(score : String,gpa : String) : Pair<Int, Int>? {
    val gpaLevels = scoreWithGPA.reversed()
    try {
        return when(score) {
            ScoreGrade.A.label -> Pair(4,4)
            ScoreGrade.B.label -> Pair(3,4)
            ScoreGrade.C.label -> Pair(2,4)
            ScoreGrade.D.label -> Pair(1,4)
            ScoreGrade.F.label -> Pair(0,4)
            else -> {
                // gpa等于gpaLevels列表某个值，对应后返回Pair(在列表的索引值,列表总长度-1)
                val index = gpaLevels.indexOfFirst { it.gpa == gpa.toFloat() }
                if(index == -1) {
                    // 未找到
                    null
                }
                Pair(index, gpaLevels.size - 1)
            }
        }
    } catch (e : Exception) {
        e.printStackTrace()
        return null
    }
}

// 获取距离下一个级别需要的分数
fun getGradeNextLevel(score : String,gpa : String) : ScoreWithGPA? {
    val gpaLevels = scoreWithGPA.reversed()
    try {
        when(score) {
            ScoreGrade.A.label -> return null
            ScoreGrade.B.label -> return null
            ScoreGrade.C.label -> return null
            ScoreGrade.D.label -> return null
            ScoreGrade.F.label -> return null
            else -> {
                val gpaF = gpa.toFloat()
                if(gpaF == gpaLevels.last().gpa) {
                    return null
                }
                // gpa等于gpaLevels列表某个值，对应后返回Pair(在列表的索引值,列表总长度-1)
                val index = gpaLevels.indexOfFirst { it.gpa == gpaF }
                return gpaLevels[index+1]
            }
        }
    } catch (e : Exception) {
        e.printStackTrace()
        return null
    }
}

// 等级星星
@Composable
fun StarGroup(level: Pair<Int, Int>) {
    val totalNum = level.second
    val onNum = level.first
    // 挂科
    val color = if(onNum == 0) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.secondary
    }
    // 满绩
    if(totalNum == onNum) {
        Text("满绩")
    } else {
        Row {
            for(i in 1..onNum) {
                Icon(painterResource(R.drawable.star_filled),null, tint = color)
            }
            for(i in onNum+1..totalNum) {
                Icon(painterResource(R.drawable.star),null,tint = color)
            }
        }
    }
}

@Composable
fun GPAStarGroup(score : String,gpa : String) = getGradeLevel(score,gpa)?.let { StarGroup(it) }

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