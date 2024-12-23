package com.hfut.schedule.ui.activity.grade.grade.jxglstu

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.ReservDecimal
import com.hfut.schedule.ui.activity.card.counts.RadarChart
import com.hfut.schedule.ui.activity.card.counts.RadarData
import com.hfut.schedule.ui.activity.grade.getGradeJXGLSTU
import com.hfut.schedule.ui.activity.home.search.functions.survey.SurveyUI
import com.hfut.schedule.ui.activity.home.search.functions.totalCourse.CourseTotalForApi
import com.hfut.schedule.ui.utils.DividerText
import com.hfut.schedule.ui.utils.EmptyUI
import com.hfut.schedule.ui.utils.LittleDialog
import com.hfut.schedule.ui.utils.MyCard
import com.hfut.schedule.ui.utils.MyToast
import com.hfut.schedule.ui.utils.Round
import com.hfut.schedule.ui.utils.ScrollText
import com.hfut.schedule.viewmodel.NetWorkViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun GradeItemUIJXGLSTU(innerPadding: PaddingValues, vm: NetWorkViewModel) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var title by remember { mutableStateOf("成绩详情") }
    var num by remember { mutableStateOf(0) }

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
                        .fillMaxSize()
                ){
                    GradeInfo(num)
                }
            }
        }
    }
    if(getGradeJXGLSTU().size == 0) EmptyUI()
    else
        LazyColumn{
            item { Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding())) }
            item { Spacer(modifier = Modifier.height(5.dp)) }
            items(getGradeJXGLSTU().size) { item ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Column() {
                        MyCard{
                            ListItem(
                                headlineContent = {  Text(getGradeJXGLSTU()[item].title) },
                                overlineContent = { Text( "成绩  "+ getGradeJXGLSTU()[item].totalGrade + "  |  绩点  " + getGradeJXGLSTU()[item].GPA +  "  |  学分  " + getGradeJXGLSTU()[item].score) },
                                leadingContent = { Icon(painterResource(R.drawable.article), contentDescription = "Localized description",) },
                                supportingContent = { Text(getGradeJXGLSTU()[item].grade) },
                                modifier = Modifier.clickable {
                                    if(getGradeJXGLSTU()[item].grade.contains("评教")) {
                                        MyToast("请为任课老师评教(存在多位老师需全评教，老师列表可在课程汇总查看)")
                                        showBottomSheet_Survey = true
                                    } else {
                                        title = getGradeJXGLSTU()[item].title
                                        num = item
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


@Composable
fun GradeInfo(num : Int) {
    val data = getGradeJXGLSTU()[num]
    val list = data.grade.split(" ")
    val radarList = mutableListOf<RadarData>()
    list.forEach { item->
        val label = item.substringBefore(":")
        val score = try {
            if(item.substringAfter(":").contains("中") || item.substringAfter(":").contains("及格")) .6f
            else if(item.substringAfter(":").contains("高") || item.substringAfter(":").contains("优秀")) .9f
            else if(item.substringAfter(":").contains("低")) .3f
            else item.substringAfter(":").toFloat() / 100
        } catch (_: Exception) {
            if(item.substringAfter(":").contains("中") || item.substringAfter(":").contains("及格")) .6f
            else if(item.substringAfter(":").contains("高") || item.substringAfter(":").contains("优秀")) .9f
            else if(item.substringAfter(":").contains("低")) .3f
            else 0f
        }
        radarList.add(RadarData(label,score))
    }
    val scrollState = rememberScrollState()
    val GPA = getGradeJXGLSTU()[num].GPA
    val title = "成绩 ${getGradeJXGLSTU()[num].totalGrade} 绩点 ${GPA} 学分 ${getGradeJXGLSTU()[num].score}"

    var avgPingshi = 0f
    var examScore = 0f
    var count = 0
    radarList.forEach{item->
        if(!item.label.contains("期末")) {
            avgPingshi += item.value
            count++
        } else {
            examScore = item.value
        }
    }
    if(count != 0)
        avgPingshi /= count
    else avgPingshi = 0f
    if(examScore != 0f)
        avgPingshi /= examScore
    else avgPingshi = 0f

    var showDialog by remember {
        mutableStateOf(false)
    }
    if(showDialog) {
        LittleDialog(
            onDismissRequest = { showDialog = false },
            onConfirmation = { showDialog = false},
            dialogTitle = "提示",
            dialogText = "平时因数=除去期末成绩各项平均分/期末分数,可大致反映最终成绩平时分占比\n越接近1则平衡,越>1则表明最终成绩可能更靠平时分,越<1表明最终成绩可能因平时分拖后腿",
            conformtext ="好",
            dismisstext ="关闭"
        )
    }

    LaunchedEffect(key1 = title ) {
        delay(500L)
        scrollState.animateScrollTo(scrollState.maxValue)
        delay(4000L)
        scrollState.animateScrollTo(0)
    }
    DividerText(text = "雷达图")
    Spacer(modifier = Modifier.height(10.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
        RadarChart(data = radarList, modifier = Modifier.size(200.dp))
    }
    Spacer(modifier = Modifier.height(10.dp))
    DividerText(text = "成绩详情")
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
                    fontSize = 28.sp)},
        )
        for(i in list.indices step 2)
            Row {
                ListItem(
                    headlineContent = { ScrollText(text = list[i]) },
                    modifier = Modifier.weight(.5f)
                )
                if(i+1 < list.size)
                    ListItem(
                        headlineContent = { ScrollText(text = list[i+1]) },
                        modifier = Modifier.weight(.5f)
                    )
            }
        Row{
            ListItem(
                leadingContent = { Icon(painter = painterResource(id = if(GPA.toFloat() >= 3.0)R.drawable.sentiment_very_satisfied else R.drawable.sentiment_dissatisfied), contentDescription = "") },
                headlineContent = { ScrollText(text = if(GPA == "4.3")"满绩" else if(GPA == "4") "优秀" else if(GPA.toFloat() >= 3) "不错" else if(GPA == "0") "挂科" else "薄弱"
                ) },
                modifier = Modifier.weight(.4f)
            )
            ListItem(
                leadingContent = {
                    Icon(painter = painterResource(R.drawable.percent), contentDescription = "")
                },
                headlineContent = {
                    ScrollText(text = "平时因数 ${if(avgPingshi != 0f) ReservDecimal.reservDecimal(avgPingshi.toDouble(),2) else "未知"}")
                },
                modifier = Modifier
                    .weight(.6f)
                    .clickable {
                        showDialog = true
                    }
            )
        }
    }
}
