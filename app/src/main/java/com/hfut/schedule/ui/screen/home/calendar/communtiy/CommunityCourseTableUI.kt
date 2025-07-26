package com.hfut.schedule.ui.screen.home.calendar.communtiy

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.logic.model.community.courseDetailDTOList
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.weeksBetween
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.weeksBetweenJxglstu
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
 
import com.hfut.schedule.ui.screen.home.calendar.examToCalendar
import com.hfut.schedule.ui.screen.home.calendar.getScheduleDate
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.MultiCourseSheetUI
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.clearUnit
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.distinctUnit
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.getNewWeek
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getCourseInfoFromCommunity
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getStartWeekFromCommunity
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.InnerPaddingHeight
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import java.time.LocalDate

@Composable
fun CommunityCourseTableUI(
    showAll: Boolean,
    innerPaddings: PaddingValues,
    vmUI : UIViewModel,
    friendUserName : String? = null,
    onDateChange : (LocalDate) ->Unit,
    today: LocalDate,
    vm : NetWorkViewModel,
    hazeState: HazeState
) {
    var examList by remember { mutableStateOf(examToCalendar()) }

    //切换周数
    var currentWeek by rememberSaveable {
        mutableLongStateOf(
            if(weeksBetween > 20) {
                getNewWeek()
            } else if(weeksBetween < 1) {
                onDateChange(getStartWeekFromCommunity())
                examList = emptyList()
                1
            } else weeksBetween
        )
    }

    val table = remember { List(30) { mutableStateListOf<courseDetailDTOList>() } }
    val tableAll = remember { List(42) { mutableStateListOf<courseDetailDTOList>() } }
    var sheet by remember { mutableStateOf(courseDetailDTOList(0,0,"","","", listOf(0),0,"","")) }
    
    //填充UI与更新
    fun refreshUI(showAll: Boolean) {
        // 清空
        if(showAll) {
            clearUnit(tableAll)
        } else {
            clearUnit(table)
        }
        try {
            for (j in 0 until 7 ) {
                val lists = getCourseInfoFromCommunity(j +1 ,currentWeek.toInt(),friendUserName)

                for(i in 0 until lists.size) {
                    val data = lists[i]
                    for(text in data) {
                        if(showAll) {
                            when (j) {
                                0 -> {
                                    when(text.section) {
                                        1,2 -> {
                                            tableAll[0].add(text)
                                        }
                                        3,4 -> {
                                            tableAll[7].add(text)
                                        }
                                        5,6 -> {
                                            tableAll[14].add(text)
                                        }
                                        7,8 -> {
                                            tableAll[21].add(text)
                                        }
                                        9,10,11 -> {
                                            tableAll[28].add(text)
                                        }
                                    }
                                }
                                1 -> {
                                    when(text.section) {
                                        1,2 -> {
                                            tableAll[1].add(text)
                                        }
                                        3,4 -> {
                                            tableAll[8].add(text)
                                        }
                                        5,6 -> {
                                            tableAll[15].add(text)
                                        }
                                        7,8 -> {
                                            tableAll[22].add(text)
                                        }
                                        9,10,11 -> {
                                            tableAll[29].add(text)
                                        }
                                    }
                                }
                                2 -> {
                                    when(text.section) {
                                        1,2 -> {
                                            tableAll[2].add(text)
                                        }
                                        3,4 -> {
                                            tableAll[9].add(text)
                                        }
                                        5,6 -> {
                                            tableAll[16].add(text)
                                        }
                                        7,8 -> {
                                            tableAll[23].add(text)
                                        }
                                        9,10,11 -> {
                                            tableAll[30].add(text)
                                        }
                                    }
                                }
                                3 -> {
                                    when(text.section) {
                                        1,2 -> {
                                            tableAll[3].add(text)
                                        }
                                        3,4 -> {
                                            tableAll[10].add(text)
                                        }
                                        5,6 -> {
                                            tableAll[17].add(text)
                                        }
                                        7,8 -> {
                                            tableAll[24].add(text)
                                        }
                                        9,10,11 -> {
                                            tableAll[31].add(text)
                                        }
                                    }
                                }
                                4 -> {
                                    when(text.section) {
                                        1,2 -> {
                                            tableAll[4].add(text)
                                        }
                                        3,4 -> {
                                            tableAll[11].add(text)
                                        }
                                        5,6 -> {
                                            tableAll[18].add(text)
                                        }
                                        7,8 -> {
                                            tableAll[25].add(text)
                                        }
                                        9,10,11 -> {
                                            tableAll[32].add(text)
                                        }
                                    }
                                }
                                5 -> {
                                    Handler(Looper.getMainLooper()).post { vmUI.findNewCourse.value = text.name.isNotEmpty() }
                                    when(text.section) {
                                        1,2 -> {
                                            tableAll[5].add(text)
                                        }
                                        3,4 -> {
                                            tableAll[12].add(text)
                                        }
                                        5,6 -> {
                                            tableAll[19].add(text)
                                        }
                                        7,8 -> {
                                            tableAll[26].add(text)
                                        }
                                        9,10,11 -> {
                                            tableAll[33].add(text)
                                        }
                                    }
                                }
                                6 -> {
                                    Handler(Looper.getMainLooper()).post { vmUI.findNewCourse.value = text.name.isNotEmpty() }
                                    when(text.section) {
                                        1,2 -> {
                                            tableAll[6].add(text)
                                        }
                                        3,4 -> {
                                            tableAll[13].add(text)
                                        }
                                        5,6 -> {
                                            tableAll[20].add(text)
                                        }
                                        7,8 -> {
                                            tableAll[27].add(text)
                                        }
                                        9,10,11 -> {
                                            tableAll[34].add(text)
                                        }
                                    }
                                }
                            }
                        } else {
                            when (j) {
                                0 -> {
                                    when(text.section) {
                                        1,2 -> {
                                            table[0].add(text)
                                        }
                                        3,4 -> {
                                            table[5].add(text)
                                        }
                                        5,6 -> {
                                            table[10].add(text)
                                        }
                                        7,8 -> {
                                            table[15].add(text)
                                        }
                                        9,10,11 -> {
                                            table[20].add(text)
                                        }
                                    }
                                }
                                1 -> {
                                    when(text.section) {
                                        1,2 -> {
                                            table[1].add(text)
                                        }
                                        3,4 -> {
                                            table[6].add(text)
                                        }
                                        5,6 -> {
                                            table[11].add(text)
                                        }
                                        7,8 -> {
                                            table[16].add(text)
                                        }
                                        9,10,11 -> {
                                            table[21].add(text)
                                        }
                                    }
                                }
                                2 -> {
                                    when(text.section) {
                                        1,2 -> {
                                            table[2].add(text)
                                        }
                                        3,4 -> {
                                            table[7].add(text)
                                        }
                                        5,6 -> {
                                            table[12].add(text)
                                        }
                                        7,8 -> {
                                            table[17].add(text)
                                        }
                                        9,10,11 -> {
                                            table[22].add(text)
                                        }
                                    }
                                }
                                3 -> {
                                    when(text.section) {
                                        1,2 -> {
                                            table[3].add(text)
                                        }
                                        3,4 -> {
                                            table[8].add(text)
                                        }
                                        5,6 -> {
                                            table[13].add(text)
                                        }
                                        7,8 -> {
                                            table[18].add(text)
                                        }
                                        9,10,11 -> {
                                            table[23].add(text)
                                        }
                                    }
                                }
                                4 -> {
                                    when(text.section) {
                                        1,2 -> {
                                            table[4].add(text)
                                        }
                                        3,4 -> {
                                            table[9].add(text)
                                        }
                                        5,6 -> {
                                            table[14].add(text)
                                        }
                                        7,8 -> {
                                            table[19].add(text)
                                        }
                                        9,10,11 -> {
                                            table[24].add(text)
                                        }
                                    }
                                }
                                in 5..6 -> {
                                    Handler(Looper.getMainLooper()).post { vmUI.findNewCourse.value = text.name.isNotEmpty() }
                                }
                            }
                        }
                    }
                }
            }
            // 去重
            if(showAll) {
                distinctUnit(tableAll)
            } else {
                distinctUnit(table)
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    //装载数组和信息
    LaunchedEffect(showAll,currentWeek) {
        refreshUI(showAll)
    }

    var showBottomSheetMultiCourse by remember { mutableStateOf(false) }
    var multiWeekday by remember { mutableIntStateOf(0) }
    var multiWeek by remember { mutableIntStateOf(0) }
    var courses by remember { mutableStateOf(listOf<courseDetailDTOList>()) }
    if (showBottomSheetMultiCourse) {
        HazeBottomSheet (
            showBottomSheet = showBottomSheetMultiCourse,
            onDismissRequest = {
                showBottomSheetMultiCourse = false
            },
            autoShape = false,
            hazeState = hazeState
        ) {
            MultiCourseSheetUI(week = multiWeek, weekday = multiWeekday,courses = courses ,vm = vm, hazeState = hazeState, friendUserName)
        }
    }

    //课程详情
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {

        HazeBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState,
            autoShape = false
        ) {
            HazeBottomSheetTopBar(sheet.name, isPaddingStatusBar = false)
            DetailInfos(sheet,friendUserName != null, vm = vm, hazeState )
        }
    }
    val dateList  = getScheduleDate(showAll,today)

    Column(modifier = Modifier.fillMaxSize()) {
            Box {
                val scrollState = rememberLazyGridState()
                val shouldShowAddButton by remember { derivedStateOf { scrollState.firstVisibleItemScrollOffset == 0 } }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(if(showAll)7 else 5),
                    modifier = Modifier.padding(7.dp),
                    state = scrollState
                ) {
                    items(if(showAll)7 else 5) { InnerPaddingHeight(innerPaddings,true) }
                    items(if(showAll)42 else 30) { cell ->
                        val itemList = if(showAll)tableAll[cell].toMutableList() else table[cell].toMutableList()
                        val texts = transferSummaryCourseInfos(itemList).toMutableList()
                        Card(
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            shape = MaterialTheme.shapes.extraSmall,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                            modifier = Modifier
                                .height(125.dp)
                                .padding(if (showAll) 1.dp else 2.dp)
                                .clickable {
                                    if (texts.size == 1) {
                                        // 如果是考试
                                        if (friendUserName == null && texts[0].contains("考试")) {
                                            return@clickable
                                        }
                                        sheet = itemList[0]
                                        showBottomSheet = true
                                    } else if (itemList.size > 1) {
                                        multiWeekday =
                                            if (showAll) (cell + 1) % 7 else (cell + 1) % 5
                                        multiWeek = currentWeek.toInt()
                                        courses = itemList
                                        showBottomSheetMultiCourse = true
                                    }
                                }
                        ) {
                            //存在待考时
                            if(examList.isNotEmpty() && friendUserName == null){
                                val numa = if(showAll) 7 else 5
                                val i = cell % numa
                                val j = cell / numa
                                val date = dateList[i]
                                examList.forEach {
                                    if(date == it.day) {
                                        val hour = it.startTime?.substringBefore(":")?.toIntOrNull() ?: 99
                                        val data  = it.startTime + "\n" + it.course  + "(考试)"+ "\n" + it.place?.replace("学堂","")
                                        if(hour in 7..9 && j == 0) {
                                            texts.add(data)
                                        } else if(hour in 10..12 && j == 1) {
                                            texts.add(data)
                                        } else if(hour in 14..15  && j == 2) {
                                            texts.add(data)
                                        } else if(hour in 16..17  && j == 3) {
                                            texts.add(data)
                                        } else if(hour >= 18  && j == 4) {
                                            texts.add(data)
                                        }
                                    }
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text =
                                        if(texts.size == 1) texts[0]
                                        else if(texts.size > 1) "${texts[0].substringBefore("\n")}\n" + "${texts.size}节课冲突\n点击查看"
                                        else "",
                                    fontSize = if (showAll) 12.sp else 14.sp,
                                    textAlign = TextAlign.Center,
                                    fontWeight = if(friendUserName == null && texts.toString().contains("考试")) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }

                        }
                    }
                    item { InnerPaddingHeight(innerPaddings,false) }
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = shouldShowAddButton,
                    enter = scaleIn(),
                    exit = scaleOut(),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(innerPaddings)
                        .padding(horizontal = APP_HORIZONTAL_DP, vertical = APP_HORIZONTAL_DP)
                ) {
                    if (shouldShowAddButton) {
                        FloatingActionButton(
                            onClick = {
                                if (currentWeek > 1) {
                                    currentWeek-- - 1
                                    onDateChange(today.minusDays(7))
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
                        .padding(innerPaddings)
                        .padding(horizontal = APP_HORIZONTAL_DP, vertical = APP_HORIZONTAL_DP)
                ) {
                    if (shouldShowAddButton) {
                        ExtendedFloatingActionButton(
                            onClick = {
                                if(weeksBetween < 1) {
                                    currentWeek = 1
                                    onDateChange(getStartWeekFromCommunity())
                                } else {
                                    currentWeek = weeksBetween
                                    onDateChange(LocalDate.now())
                                }
                            },
                        ) {
                            AnimatedContent(
                                targetState = currentWeek,
                                transitionSpec = {
                                    scaleIn(animationSpec = tween(500)
                                    ) togetherWith scaleOut(animationSpec = tween(500))
                                }, label = ""
                            ){ n ->
                                Text(text = "第 $n 周",)
                            }
                        }
                    }
                }

//                androidx.compose.animation.AnimatedVisibility(
//                    visible = !shouldShowAddButton,
//                    enter = scaleIn(),
//                    exit = scaleOut(),
//                    modifier = Modifier
//                        .align(Alignment.BottomCenter)
//                        .padding(innerPaddings)
//                        .padding(horizontal = APP_HORIZONTAL_DP, vertical = APP_HORIZONTAL_DP)
//                ) {
//                    TextButton(onClick = {  }) {
//                        Text(
//                            text = parseSemseter(getSemseter()) + " 第${currentWeek}周",
//                            style = TextStyle(shadow = Shadow(
//                                color = Color.Gray,
//                                offset = Offset(5.0f,5.0f),
//                                blurRadius = 10.0f
//                            ))
//                        )
//                    }
//                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = shouldShowAddButton,
                    enter = scaleIn(),
                    exit = scaleOut(),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(innerPaddings)
                        .padding(horizontal = APP_HORIZONTAL_DP, vertical = APP_HORIZONTAL_DP)
                ) {
                    if (shouldShowAddButton) {
                        FloatingActionButton(
                            onClick = {
                                if (currentWeek < 20) {
                                    currentWeek++ + 1
                                    onDateChange(today.plusDays(7))
                                }
                            },
                        ) { Icon(Icons.Filled.ArrowForward, "Add Button") }
                    }
                }
            }
        }
}

@Composable
fun ScheduleTopDate(showAll: Boolean,today : LocalDate,isJxglstu : Boolean) {
    val mondayOfCurrentWeek = today.minusDays(today.dayOfWeek.value - 1L)
    val todayDate = DateTimeManager.Date_yyyy_MM_dd

    Column(modifier = Modifier.background(Color.Transparent)) {
        Spacer(modifier = Modifier.height(5.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(if(showAll)7 else 5),modifier = Modifier.padding(horizontal = 10.dp)){
            items(if(showAll)7 else 5) { item ->

                val date = mondayOfCurrentWeek.plusDays(item.toLong()).toString() //YYYY-MM-DD 与考试对比
                val isToday = date == todayDate

                var animated by remember { mutableStateOf(false) }
                val fontSize = if (showAll) 12f else 14f
                val fontSizeAnimated by animateFloatAsState(
                    targetValue = if (isToday && animated) fontSize*1.25f else fontSize,
                    animationSpec = tween(durationMillis = AppAnimationManager.ANIMATION_SPEED), label = "fontSizeAnimation",
                    finishedListener = { if (isToday) animated = false }
                )
                LaunchedEffect(isToday) {
                    if (isToday) {
                        animated = true
                    }
                }

                if (
//                    if(isJxglstu) {
//                        weeksBetweenJxglstu
//                    } else {
//                        weeksBetween
//                    }
//                > 0
                    true
                    )
                    Text(
                        text = date.substringAfter("-"),
                        textAlign = TextAlign.Center,
                        fontSize = fontSizeAnimated.sp,
                        color = if(isToday) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary,
                        fontWeight = if(isToday) FontWeight.Bold else FontWeight.Normal
                    )
                else Text(
                    text = "未开学",
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    fontSize = if(showAll)12.sp else 14.sp
                )
            }
        }
    }
}

private fun transferSummaryCourseInfo(text : courseDetailDTOList) : String {
    val name = text.name
    var time = text.classTime
    time = time.substringBefore("-")
    var room = text.place
    if (room != null) {
        room = room.replace("学堂","")
    }
    return time + "\n" + name + "\n" + room
}

private fun transferSummaryCourseInfos(text: List<courseDetailDTOList>): List<String> = text.map { item -> transferSummaryCourseInfo(item) }
