package com.hfut.schedule.ui.activity.grade.grade.jxglstu

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.R
import com.hfut.schedule.logic.beans.Community.GradeResponseJXGLSTU
import com.hfut.schedule.logic.utils.ReservDecimal
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.utils.reEmptyLiveDta
import com.hfut.schedule.ui.activity.card.counts.RadarChart
import com.hfut.schedule.ui.activity.card.counts.RadarData
import com.hfut.schedule.ui.activity.grade.getGradeJXGLSTU
import com.hfut.schedule.ui.activity.home.search.functions.survey.SurveyUI
import com.hfut.schedule.ui.activity.home.search.functions.totalCourse.CourseTotalForApi
import com.hfut.schedule.ui.utils.components.DividerText
import com.hfut.schedule.ui.utils.components.DividerTextExpandedWith
import com.hfut.schedule.ui.utils.components.EmptyUI
import com.hfut.schedule.ui.utils.components.LittleDialog
import com.hfut.schedule.ui.utils.components.LoadingUI
import com.hfut.schedule.ui.utils.components.MyCard
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.style.Round
import com.hfut.schedule.ui.utils.components.ScrollText
import com.hfut.schedule.viewmodel.NetWorkViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun GradeItemUIJXGLSTU(innerPadding: PaddingValues, vm: NetWorkViewModel,showSearch : Boolean) {


    val cookie = if(!vm.webVpn) prefs.getString("redirect", "")  else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket","")

//        vm.getGrade(cookie!!,null)

    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }


    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var title by remember { mutableStateOf("成绩详情") }
//    var num by remember { mutableStateOf(0) }

    var num by remember { mutableStateOf(GradeResponseJXGLSTU("","","","","")) }
    val sheetState_Survey = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_Survey by remember { mutableStateOf(false) }

    if (showBottomSheet_Survey) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_Survey = false },
            sheetState = sheetState_Survey,
            shape = Round(sheetState_Survey)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("评教") },
                        actions = {
                            CourseTotalForApi(modifier = Modifier.padding(horizontal = 15.dp),vm)
                        }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    SurveyUI(vm)
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet= false },
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
                        title = { Text(title) }
                    )
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


    if(refresh) {
        loading = true
        CoroutineScope(Job()).launch{
            async { reEmptyLiveDta(vm.jxglstuGradeData) }
            async{ cookie?.let { vm.getGrade(cookie,null)} }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.jxglstuGradeData.observeForever { result ->
                        if (result != null) {
                            if(result.contains("成绩")) {
                                loading = false
                                refresh = false
                            }
                        }
                    }
                }
            }
        }
    }

    Box() {
        AnimatedVisibility(
            visible = loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column  {
                Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
                LoadingUI()
            }
        }

        AnimatedVisibility(
            visible = !loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            var input by remember { mutableStateOf("") }
            val gradeList = getGradeJXGLSTU(vm)
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
                                .padding(horizontal = 15.dp),
                            value = input,
                            onValueChange = {
                                input = it
                            },
                            label = { Text("搜索课程") },
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
                            colors = TextFieldDefaults.textFieldColors(
                                focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
                                unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
                            ),
                        )
                    }
                    gradeList.forEach { item ->
                        if (item.title.contains(input) || item.title.contains(input)) {
                            searchList.add(item)
                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))
                } else {
                    searchList = gradeList
                }

                if(gradeList.size == 0) EmptyUI()
                else {
                    LazyColumn{
                        if(!showSearch) {
                            item { Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding())) }
                            item { Spacer(modifier = Modifier.height(5.dp)) }
                        }

                        items(searchList.size) { item ->
                            val grade = searchList[item]
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                Column() {
                                    MyCard{
                                        ListItem(
                                            headlineContent = {  Text(grade.title) },
                                            overlineContent = { Text( "成绩  "+ grade.totalGrade + "  |  绩点  " + grade.GPA +  "  |  学分  " + grade.score) },
                                            leadingContent = { Icon(painterResource(R.drawable.article), contentDescription = "Localized description",) },
                                            supportingContent = { Text(grade.grade) },
                                            modifier = Modifier.clickable {
                                                if(grade.grade.contains("评教")) {
                                                    MyToast("请为任课老师评教(存在多位老师需全评教，老师列表可在课程汇总查看)")
                                                    showBottomSheet_Survey = true
                                                } else {
                                                    title = grade.title
                                                    num = grade
                                                    showBottomSheet = true
                                                }
                                            },
                                        )
                                    }
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding())) }
                    }
                }
            }

//            val searchList by remember { mutableStateOf(mutableListOf<GradeResponseJXGLSTU>()) }

        }
    }
}


@Composable
fun GradeInfo(num : GradeResponseJXGLSTU,vm: NetWorkViewModel) {
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
            dialogTitle = "提示",
            dialogText = "平时因数=除去期末成绩各项平均分/期末分数,可大致反映最终成绩平时分占比\n越接近1则平衡,越>1则表明最终成绩可能更靠平时分,越<1表明最终成绩可能因平时分拖后腿",
            conformtext = "好",
            dismisstext = "关闭"
        )
    }

    LaunchedEffect(key1 = title) {
        delay(500L)
        scrollState.animateScrollTo(scrollState.maxValue)
        delay(4000L)
        scrollState.animateScrollTo(0)
    }
    DividerTextExpandedWith(text = "雷达图") {
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            RadarChart(data = radarList, modifier = Modifier.size(200.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))
    }

    DividerTextExpandedWith(text = "成绩详情") {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 15.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp),
            shape = MaterialTheme.shapes.medium,
        ) {
            ListItem(
                headlineContent = {
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.horizontalScroll(scrollState),
                        fontSize = 28.sp
                    )
                },
            )
            for (i in list.indices step 2)
                Row {
                    val l1 = list[i]
                    val t1 = l1.substringBefore(":")
                    val score1 = l1.substringAfter(":")

                    ListItem(
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
                        ListItem(
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
                ListItem(
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
                ListItem(
                    leadingContent = {
                        Icon(painter = painterResource(R.drawable.percent), contentDescription = "")
                    },
                    headlineContent = {
                        ScrollText(
                            text = if (avgPingshi != 0f) ReservDecimal.reservDecimal(
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