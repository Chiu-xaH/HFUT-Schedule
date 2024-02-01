package com.hfut.schedule.ui.ComposeUI.SavedCourse

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.with
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState

import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.datamodel.Community.courseDetailDTOList
import com.hfut.schedule.logic.utils.GetDate
import com.hfut.schedule.logic.utils.GetDate.Benweeks
import com.hfut.schedule.logic.utils.GetDate.Date_MM_dd
import com.hfut.schedule.ui.UIUtils.MyToast
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class,
    ExperimentalMaterialApi::class
)
@SuppressLint("SuspiciousIndentation", "CoroutineCreationDuringComposition")
@Composable
fun NoNet() {


    var Mon by rememberSaveable { mutableStateOf("") }
    var Tue by rememberSaveable { mutableStateOf("") }
    var Wed by rememberSaveable { mutableStateOf("") }
    var Thur by rememberSaveable { mutableStateOf("") }
    var Fri by rememberSaveable { mutableStateOf("") }

    var table_1_1 by rememberSaveable { mutableStateOf("") }
    var table_1_2 by rememberSaveable { mutableStateOf("") }
    var table_1_3 by rememberSaveable { mutableStateOf("") }
    var table_1_4 by rememberSaveable { mutableStateOf("") }
    var table_1_5 by rememberSaveable { mutableStateOf("") }
    var table_2_1 by rememberSaveable { mutableStateOf("") }
    var table_2_2 by rememberSaveable { mutableStateOf("") }
    var table_2_3 by rememberSaveable { mutableStateOf("") }
    var table_2_4 by rememberSaveable { mutableStateOf("") }
    var table_2_5 by rememberSaveable { mutableStateOf("") }
    var table_3_1 by rememberSaveable { mutableStateOf("") }
    var table_3_2 by rememberSaveable { mutableStateOf("") }
    var table_3_3 by rememberSaveable { mutableStateOf("") }
    var table_3_4 by rememberSaveable { mutableStateOf("") }
    var table_3_5 by rememberSaveable { mutableStateOf("") }
    var table_4_1 by rememberSaveable { mutableStateOf("") }
    var table_4_2 by rememberSaveable { mutableStateOf("") }
    var table_4_3 by rememberSaveable { mutableStateOf("") }
    var table_4_4 by rememberSaveable { mutableStateOf("") }
    var table_4_5 by rememberSaveable { mutableStateOf("") }
    var table_5_1 by rememberSaveable { mutableStateOf("") }
    var table_5_2 by rememberSaveable { mutableStateOf("") }
    var table_5_3 by rememberSaveable { mutableStateOf("") }
    var table_5_4 by rememberSaveable { mutableStateOf("") }
    var table_5_5 by rememberSaveable { mutableStateOf("") }
    var table_6_1 by rememberSaveable { mutableStateOf("") }
    var table_6_2 by rememberSaveable { mutableStateOf("") }
    var table_6_3 by rememberSaveable { mutableStateOf("") }
    var table_6_4 by rememberSaveable { mutableStateOf("") }
    var table_6_5 by rememberSaveable { mutableStateOf("") }


    var sheet_1_1 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    var sheet_1_2  = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    var sheet_1_3 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    var sheet_1_4 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    var sheet_1_5 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    var sheet_2_1 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    var sheet_2_2 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    var sheet_2_3 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    var sheet_2_4 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    var sheet_2_5 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    var sheet_3_1 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    var sheet_3_2  = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    var sheet_3_3 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    var sheet_3_4  = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    var sheet_3_5  = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    var sheet_4_1  = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    var sheet_4_2  = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    var sheet_4_3 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    var sheet_4_4 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    var sheet_4_5 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    var sheet_5_1 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    var sheet_5_2  = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    var sheet_5_3 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    var sheet_5_4 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")
    var sheet_5_5 = courseDetailDTOList(0,0,"","","", listOf(0),0,"")





    //切换周数
    var Bianhuaweeks by rememberSaveable { mutableStateOf(GetDate.weeksBetween) }
    var date by rememberSaveable { mutableStateOf(LocalDate.now()) }

    val table = arrayOf(
        table_1_1, table_1_2, table_1_3, table_1_4, table_1_5,
        table_2_1, table_2_2, table_2_3, table_2_4, table_2_5,
        table_3_1, table_3_2, table_3_3, table_3_4, table_3_5,
        table_4_1, table_4_2, table_4_3, table_4_4, table_4_5,
        table_5_1, table_5_2, table_5_3, table_5_4, table_5_5,
        table_6_1, table_6_2, table_6_3, table_6_4, table_6_5,
    )

    val sheet = arrayOf(
        sheet_1_1, sheet_1_2, sheet_1_3, sheet_1_4, sheet_1_5,
        sheet_2_1, sheet_2_2, sheet_2_3, sheet_2_4, sheet_2_5,
        sheet_3_1, sheet_3_2, sheet_3_3, sheet_3_4, sheet_3_5,
        sheet_4_1, sheet_4_2, sheet_4_3, sheet_4_4, sheet_4_5,
        sheet_5_1, sheet_5_2, sheet_5_3, sheet_5_4, sheet_5_5,
    )



    val dayweek = GetDate.dayweek


    var chinesenumber  = GetDate.chinesenumber

    when (dayweek) {
        1 -> chinesenumber = "一"
        2 -> chinesenumber = "二"
        3 -> chinesenumber = "三"
        4 -> chinesenumber = "四"
        5 -> chinesenumber = "五"
        6 -> chinesenumber = "六"
        0 -> chinesenumber = "日"
    }
    //填充UI与更新
    fun Updates() {
        table_1_1 = ""
        table_1_2 = ""
        table_1_3 = ""
        table_1_4 = ""
        table_1_5 = ""
        table_2_1 = ""
        table_2_2 = ""
        table_2_3 = ""
        table_2_4 = ""
        table_2_5 = ""
        table_3_1 = ""
        table_3_2 = ""
        table_3_3 = ""
        table_3_4 = ""
        table_3_5 = ""
        table_4_1 = ""
        table_4_2 = ""
        table_4_3 = ""
        table_4_4 = ""
        table_4_5 = ""
        table_5_1 = ""
        table_5_2 = ""
        table_5_3 = ""
        table_5_4 = ""
        table_5_5 = ""
        for (j in 0 until 5 ) {
            var info = ""
            val lists = getCourseINFO(j +1 ,Bianhuaweeks.toInt())

            for(i in 0 until lists.size) {
                val text = lists[i][0]
                val name = text.name
                var time = text.classTime
                time = time.substringBefore("-")
                var room = text.place
                room = room.replace("学堂","")
                info = time + "\n" + name + "\n" + room


                when (j) {
                    0 -> {
                        when(text.section) {
                            1 -> {
                                table[0] = info
                                sheet[0] = text
                            }
                            3 -> {
                                table[5] = info
                                sheet[5] = text
                            }
                            5 -> {
                                table[10] = info
                                sheet[10] = text
                            }
                            7 -> {
                                table[15] = info
                                sheet[15] = text
                            }
                            9 -> {
                                table[20] = info
                                sheet[20] = text
                            }
                        }
                    }
                    1 -> {
                        when(text.section) {
                            1 -> {
                                table[1] = info
                                sheet[1] = text
                            }
                            3 -> {
                                table[6] = info
                                sheet[6] = text
                            }
                            5 -> {
                                table[11] = info
                                sheet[11] = text
                            }
                            7 -> {
                                table[16] = info
                                sheet[16] = text
                            }
                            9 -> {
                                table[21] = info
                                sheet[21] = text
                            }
                        }
                    }
                    2 -> {
                        when(text.section) {
                            1 -> {
                                table[2] = info
                                sheet[2] = text
                            }
                            3 -> {
                                table[7] = info
                                sheet[7] = text
                            }
                            5 -> {
                                table[12] = info
                                sheet[12] = text
                            }
                            7 -> {
                                table[17] = info
                                sheet[17] = text
                            }
                            9 -> {
                                table[22] = info
                                sheet[22] = text
                            }
                        }
                    }
                    3 -> {
                        when(text.section) {
                            1 -> {
                                table[3] = info
                                sheet[3] = text
                            }
                            3 -> {
                                table[8] = info
                                sheet[8] = text
                            }
                            5 -> {
                                table[13] = info
                                sheet[13] = text
                            }
                            7 -> {
                                table[18] = info
                                sheet[18] = text
                            }
                            9 -> {
                                table[23] = info
                                sheet[23] = text
                            }
                        }
                    }
                    4 -> {
                        when(text.section) {
                            1 -> {
                                table[4] = info
                                sheet[4] = text
                            }
                            3 -> {
                                table[9] = info
                                sheet[9] = text
                            }
                            5 -> {
                                table[14] = info
                                sheet[14] = text
                            }
                            7 -> {
                                table[19] = info
                                sheet[19] = text
                            }
                            9 -> {
                                table[24] = info
                                sheet[24] = text
                            }
                        }
                    }
                }
            }
        }
    }

    //装载数组和信息
    Updates()

    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
    val json = prefs.getString("json", "")
    if (json?.contains("result") == true) {
        Updates()//填充UI与更新
    } else Toast.makeText(MyApplication.context,"本地数据为空,请登录以更新数据",Toast.LENGTH_SHORT).show()


    //刷新
    var refreshing by remember { mutableStateOf(false) }
    // 用协程模拟一个耗时加载
    val scope = rememberCoroutineScope()
    var states = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        scope.launch {
            async {
                refreshing = true
                Updates()
            }.await()
            async {
                refreshing = false
                MyToast("刷新成功")
            }
        }
    })

    //课程详情
    var num by remember { mutableStateOf(0) }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
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
                        title = { Text(sheet[num].name) }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    DetailInfos(sheet[num])
                }
            }
        }
    }

    var today by rememberSaveable { mutableStateOf(LocalDate.now()) }
    val mondayOfCurrentWeek = today.minusDays(today.dayOfWeek.value - 1L)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("今天  第${Benweeks}周  周$chinesenumber  $Date_MM_dd") }
            )
        },) {innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {


            Spacer(modifier = Modifier.height(5.dp))

            LazyVerticalGrid(columns = GridCells.Fixed(5),modifier = Modifier.padding(horizontal = 10.dp)){
                items(5) { item ->
                    if (Benweeks > 0)
                        Text(
                            text = mondayOfCurrentWeek.plusDays(item.toLong()).toString()
                                .substringAfter("-") ,
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    else Text(
                        text = "未开学",
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }

                    Box( modifier = Modifier
                        .fillMaxHeight()
                        .pullRefresh(states)
                    ) {
                        val scrollstate = rememberLazyGridState()
                        val shouldShowAddButton = scrollstate.firstVisibleItemScrollOffset == 0

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(5),
                            modifier = Modifier.padding(10.dp),
                            state = scrollstate
                        ) {
                            items(30) { cell ->
                                Card(
                                    shape = MaterialTheme.shapes.extraSmall,
                                    modifier = Modifier
                                        .height(125.dp)
                                        .padding(2.dp)
                                        .clickable {
                                            num = cell
                                            if (sheet[cell].name != "")
                                                showBottomSheet = true
                                            else MyToast("空数据")
                                        }
                                ) {
                                    Text(text = table[cell],fontSize = 14.sp, textAlign = TextAlign.Center)
                                }
                            }
                        }
                        androidx.compose.animation.AnimatedVisibility(
                            visible = shouldShowAddButton,
                            enter = scaleIn(),
                            exit = scaleOut(),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(horizontal = 15.dp, vertical = 100.dp)
                        ) {
                            if (shouldShowAddButton) {
                                FloatingActionButton(
                                    onClick = {
                                        if (Bianhuaweeks > 1) {
                                            Bianhuaweeks-- - 1
                                            today = today.minusDays(7)
                                        }
                                    },
                                ) { Icon(Icons.Filled.ArrowBack, "Add Button") }
                            }
                        }


                        androidx.compose.animation.AnimatedVisibility(
                            visible = shouldShowAddButton,
                            enter = scaleIn(),
                            exit = scaleOut(),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = 15.dp, vertical = 100.dp)
                        ) {
                            if (shouldShowAddButton) {
                                ExtendedFloatingActionButton(
                                    onClick = {
                                        Bianhuaweeks = GetDate.Benweeks
                                        today = LocalDate.now()
                                    },
                                ) {
                                    AnimatedContent(
                                        targetState = Bianhuaweeks,
                                        transitionSpec = {  scaleIn(animationSpec = tween(500)
                                        ) with scaleOut(animationSpec = tween(500))
                                        }, label = ""
                                    ){annumber ->
                                        Text(text = "第 $annumber 周",)
                                    }
                                }
                            }
                        }

                        androidx.compose.animation.AnimatedVisibility(
                            visible = shouldShowAddButton,
                            enter = scaleIn(),
                            exit = scaleOut(),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(horizontal = 15.dp, vertical = 100.dp)
                        ) {
                            if (shouldShowAddButton) {
                                    FloatingActionButton(
                                        onClick = {
                                             if (Bianhuaweeks < 20) {
                                                 Bianhuaweeks++ + 1
                                                 today = today.plusDays(7)
                                             }
                                        },
                                    ) { Icon(Icons.Filled.ArrowForward, "Add Button") }
                            }
                        }
                        PullRefreshIndicator(refreshing, states, Modifier.align(Alignment.TopCenter))
                    }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}