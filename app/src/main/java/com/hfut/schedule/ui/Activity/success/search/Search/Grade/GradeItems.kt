package com.hfut.schedule.ui.Activity.success.search.Search.Grade

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.activity.LoginActivity
import com.hfut.schedule.logic.utils.GetDate
import com.hfut.schedule.logic.utils.ReservDecimal
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.Activity.card.counts.RadarChart
import com.hfut.schedule.ui.Activity.card.counts.RadarData
import com.hfut.schedule.ui.UIUtils.CardForListColor
import com.hfut.schedule.ui.UIUtils.DividerText
import com.hfut.schedule.ui.UIUtils.EmptyUI
import com.hfut.schedule.ui.UIUtils.LittleDialog
import com.hfut.schedule.ui.UIUtils.MyToast
import com.hfut.schedule.ui.UIUtils.ScrollText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

@Composable
fun TotaGrade() {

    val json = SharePrefs.prefs.getString("Grade", MyApplication.NullGrades)
    val result = Gson().fromJson(json,com.hfut.schedule.logic.datamodel.Community.GradeResponse::class.java).result
    val Class = result?.classRanking
    val Major = result?.majorRanking
    val TotalGPA = result?.gpa



    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Column() {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium,
            ){
                ListItem(
                    headlineContent = {  Text("绩点(GPA)  $TotalGPA") },
                    supportingContent = { Text("班级排名: $Class   专业排名: $Major") },
                    leadingContent = { Icon(painterResource(R.drawable.flag), contentDescription = "Localized description",) },
                    modifier = Modifier.clickable {},
                    colors = ListItemDefaults.colors(MaterialTheme.colorScheme.primaryContainer)
                )
            }
        }
    }
}





@Composable
fun GradeItemUI(vm :LoginSuccessViewModel,innerPadding : PaddingValues) {

    var loading by remember { mutableStateOf(true) }
    var clicked by remember { mutableStateOf(false) }

    var term  by remember { mutableStateOf( "1") }
    val month = GetDate.Date_MM.toInt()
    if( month >= 9 || month <= 2) term = "1"
    else term = "2"
    var Years by remember { mutableStateOf(
        if (month <= 8) GetDate.Date_yyyy.toInt() - 1
        else GetDate.Date_yyyy.toInt()
    ) }


    var termBoolean by remember { mutableStateOf(prefs.getBoolean("term",true)) }

    if (termBoolean) term = "1"
    else term = "2"

    var showitem_year by remember { mutableStateOf(false) }
    DropdownMenu(expanded = showitem_year, onDismissRequest = { showitem_year = false }, offset = DpOffset(15.dp,0.dp)) {
        DropdownMenuItem(text = { Text(text = "${(GetDate.Date_yyyy.toInt() - 3).toString()} - ${(GetDate.Date_yyyy.toInt() - 2).toString()}" )}, onClick = {
            Years = (GetDate.Date_yyyy.toInt() - 3)
            showitem_year = false})
        DropdownMenuItem(text = { Text(text = "${(GetDate.Date_yyyy.toInt() - 2).toString()} - ${(GetDate.Date_yyyy.toInt() - 1).toString()}" )}, onClick = {
            Years =  (GetDate.Date_yyyy.toInt() - 2)
            showitem_year = false})
        DropdownMenuItem(text = { Text(text = "${(GetDate.Date_yyyy.toInt() - 1).toString()} - ${(GetDate.Date_yyyy.toInt() ).toString()}" )},onClick = {
            Years =  (GetDate.Date_yyyy.toInt() - 1)
            showitem_year = false})
        DropdownMenuItem(text = { Text(text = "${(GetDate.Date_yyyy.toInt() ).toString()} - ${(GetDate.Date_yyyy.toInt() + 1).toString()}" )}, onClick = {
            Years =  GetDate.Date_yyyy.toInt()
            showitem_year = false})
        DropdownMenuItem(text = { Text(text = "${(GetDate.Date_yyyy.toInt() + 1).toString()} - ${(GetDate.Date_yyyy.toInt() + 2).toString()}" )}, onClick = {
            Years =  (GetDate.Date_yyyy.toInt() + 1)
            showitem_year = false})
        DropdownMenuItem(text = { Text(text = "${(GetDate.Date_yyyy.toInt() + 2).toString()} - ${(GetDate.Date_yyyy.toInt() + 3).toString()}" )}, onClick = {
            Years =  (GetDate.Date_yyyy.toInt() + 2)
            showitem_year = false})
        DropdownMenuItem(text = { Text(text = "${(GetDate.Date_yyyy.toInt() + 3).toString()} - ${(GetDate.Date_yyyy.toInt() + 4).toString()}" )}, onClick = {
            Years =  (GetDate.Date_yyyy.toInt() + 3)
            showitem_year = false})
    }





    @SuppressLint("SuspiciousIndentation")
    @Composable
    fun UIS(){
        Column {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 0.dp), horizontalArrangement = Arrangement.Start){

                AssistChip(
                    onClick = { showitem_year = true },
                    label = { Text(text = "${Years} - ${Years + 1}") }
                )

                Spacer(modifier = Modifier.width(10.dp))

                AssistChip(
                    onClick = {termBoolean = !termBoolean},
                    label = { Text(text = "第 $term 学期") },
                )

                Spacer(modifier = Modifier.width(10.dp))
                val CommuityTOKEN = prefs.getString("TOKEN","")
                AssistChip(
                    onClick = {
                        CoroutineScope(Job()).launch {
                            async {
                                loading = true
                                clicked = true
                                SharePrefs.SaveBoolean("term",true,termBoolean)
                            }.await()
                            async { CommuityTOKEN?.let { vm.getGrade(it,Years.toString() + "-"+(Years+1),term) } }
                            async {
                                Handler(Looper.getMainLooper()).post{
                                    vm.GradeData.observeForever { result ->
                                        if (result != null) {
                                            if(result.contains("success")) {
                                                CoroutineScope(Job()).launch {
                                                    async {
                                                        delay(500)
                                                        async { loading = false }
                                                        async { getGrade() }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    label = { Text(text = "搜索") },
                    leadingIcon = { Icon(painter = painterResource(R.drawable.search), contentDescription = "description") }
                )
            }

            if(getGrade().size == 0) EmptyUI()
            else
                LazyColumn {
                    item { TotaGrade() }
                    items(getGrade().size) { item ->
                        val pass = getGrade()[item].pass
                        var passs = ""
                        if (pass == true) passs = "通过"
                        else passs = "挂科"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Column() {
                                Card(
                                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 15.dp, vertical = 5.dp),
                                    shape = MaterialTheme.shapes.medium,
                                ) {
                                    ListItem(
                                        headlineContent = { Text(getGrade()[item].courseName) },
                                        supportingContent = { Text("学分: " + getGrade()[item].credit + "   绩点: " + getGrade()[item].gpa + "   分数: ${getGrade()[item].score}") },
                                        leadingContent = {
                                            Icon(
                                                painterResource(R.drawable.article),
                                                contentDescription = "Localized description",
                                            )
                                        },
                                        trailingContent = { Text(passs) },
                                        modifier = Modifier.clickable {},
                                    )
                                }
                            }
                        }
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Column() {
                                Card(
                                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 15.dp, vertical = 5.dp),
                                    shape = MaterialTheme.shapes.medium,
                                ) {
                                    ListItem(
                                        headlineContent = { Text("查看分数详细请点击此处进入教务数据") },
                                        supportingContent = { Text(text = "您现在使用的是智慧社区接口,使用教务系统数据可查看详细成绩") },
                                        trailingContent = { Icon(Icons.Filled.ArrowForward, contentDescription = "") },
                                        modifier = Modifier.clickable {
                                            val it = Intent(MyApplication.context, LoginActivity::class.java).apply {
                                                putExtra("nologin",false)
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            }
                                            MyApplication.context.startActivity(it)
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
        }
    }


    if(clicked){
        Box{
            AnimatedVisibility(
                visible = loading,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .padding(innerPadding)
                    .align(Alignment.Center)
            ) {
                Column {
                    Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
                    Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center)  {
                        CircularProgressIndicator()
                    }
                }
            }

            AnimatedVisibility(
                visible = !loading,
                enter = fadeIn(),
                exit = fadeOut()
            ){ UIS() }
        }
    }
    else { UIS() }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun GradeItemUIJXGLSTU(innerPadding: PaddingValues) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var title by remember { mutableStateOf("成绩详情") }
    var num by remember { mutableStateOf(0) }
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet= false },
            sheetState = sheetState
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
       // item { TotaGrade() }
        item { Spacer(modifier = Modifier.height(5.dp)) }
        items(getGradeJXGLSTU().size) { item ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Column() {
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp, vertical = 5.dp),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        ListItem(
                            headlineContent = {  Text(getGradeJXGLSTU()[item].title) },
                            overlineContent = { Text( "成绩  "+ getGradeJXGLSTU()[item].totalGrade + "  |  绩点  " + getGradeJXGLSTU()[item].GPA +  "  |  学分  " + getGradeJXGLSTU()[item].score) },
                            leadingContent = { Icon(painterResource(R.drawable.article), contentDescription = "Localized description",) },
                            supportingContent = { Text(getGradeJXGLSTU()[item].grade) },
                            modifier = Modifier.clickable {
                                title = getGradeJXGLSTU()[item].title
                                num = item
                                showBottomSheet = true
                            },
                        )
                    }
                }
            }
        }
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
        } catch (_:Exception) {
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
                headlineContent = { ScrollText(text = if(GPA == "4")"满绩" else if(GPA == "3.7") "接近满绩" else if(GPA.toFloat() >= 3) "不错" else "有点薄弱"
                ) },
                modifier = Modifier.weight(.4f)
            )
            ListItem(
                leadingContent = {
                    Icon(painter = painterResource(R.drawable.percent), contentDescription = "")
                },
                headlineContent = {
                                  ScrollText(text = "平时因数 ${if(avgPingshi != 0f)ReservDecimal.reservDecimal(avgPingshi.toDouble(),2) else "未知"}")
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