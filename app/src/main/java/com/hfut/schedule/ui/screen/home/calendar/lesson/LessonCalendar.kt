package com.hfut.schedule.ui.screen.home.calendar.lesson

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.logic.model.jxglstu.lessons
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.LargeCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.network.onListenStateHolder
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.clearUnit
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.distinctUnit
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.getNewWeek
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.numToChinese
import com.hfut.schedule.ui.screen.home.calendar.next.parseSingleChineseDigit
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.DetailItems
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.TotalCourseDataSource
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getJxglstuStartDate
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.style.special.containerBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.xah.uicommon.style.padding.navigationBarHeightPadding
import dev.chrisbanes.haze.HazeState
import java.time.LocalDate
import kotlin.collections.toMutableList
import kotlin.text.split


@Composable
private fun MultiCourseSheetUIForSearch(
    week : Int,
    weekday : Int,
    courses : List<CardBean>,
    vm: NetWorkViewModel,
    hazeState: HazeState,
    list : List<lessons>
) {
    var numItem by remember { mutableIntStateOf(0) }

    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar(list[numItem].course.nameZh)
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    DetailItems(list[numItem],vm,hazeState,mapOf(),)
                }
            }
        }
    }

    Column {
        HazeBottomSheetTopBar("第${week}周 周${numToChinese(weekday)}", isPaddingStatusBar = false)
        LargeCard(
            title = "${courses.size}节课冲突"
        ) {
            for(index in courses.indices) {
                val course = courses[index]
                val list = course.text.split("\n")
                val startTime = list[0]
                val name = list[1]
                val place =
                    if(list.size > 2) {
                        list[2]
                    } else null
                TransplantListItem(
                    headlineContent = {
                        Text(name)
                    },
                    supportingContent = {
                        Text("$place $startTime")
                    },
                    leadingContent = {
                        Text((index+1).toString())
                    },
                    modifier = Modifier.clickable {
                        numItem = course.lessonNum
                        showBottomSheet = true
                    }
                )
            }
        }
        Spacer(Modifier.height(40.dp))
    }

}


// 传入lessons列表 显示课表 适用于课程汇总、开课查询
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun JxglstuCourseTableTwo(
    showAll: Boolean,
    vm: NetWorkViewModel,
    vmUI: UIViewModel,
    hazeState: HazeState,
    innerPadding : PaddingValues,
    dataSource : TotalCourseDataSource,
    onDateChange: (LocalDate) ->Unit,
    today: LocalDate,
    backGroundHaze : HazeState?
) {
    val list by produceState(initialValue = emptyList<lessons>(),key1 = dataSource) {
        when(dataSource) {
            TotalCourseDataSource.MINE -> {
                prefs.getString("courses","")?.let { value = vm.parseDatumCourse(it) }
            }
            TotalCourseDataSource.SEARCH -> {
                onListenStateHolder(vm.courseSearchResponse) { data ->
                    value = data
                }
            }
            TotalCourseDataSource.MINE_NEXT -> {
                prefs.getString("coursesNext","")?.let { value = vm.parseDatumCourse(it) }
            }
        }
    }

    JxglstuCourseTableSearch(showAll,vm,vmUI,hazeState,innerPadding,list,onDateChange,today,backGroundHaze)
}


// 传入lessons列表 显示课表 适用于课程汇总、开课查询
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun JxglstuCourseTableSearch(
    showAll: Boolean,
    vm: NetWorkViewModel,
    vmUI: UIViewModel? = null,
    hazeState: HazeState,
    innerPadding : PaddingValues,
    list : List<lessons>,
    onDateChange: ((LocalDate) ->Unit)? = null,
    today: LocalDate? = null,
    backGroundHaze : HazeState? = null
) {
    var numItem by remember { mutableIntStateOf(0) }

    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar(list[numItem].course.nameZh)
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    DetailItems(list[numItem],vm,hazeState,mapOf())
                }
            }
        }
    }

    var showBottomSheetMultiCourse by remember { mutableStateOf(false) }

    var courses by remember { mutableStateOf(listOf<CardBean>()) }
    var multiWeekday by remember { mutableIntStateOf(0) }
    var multiWeek by remember { mutableIntStateOf(0) }


    if (showBottomSheetMultiCourse) {
        HazeBottomSheet (
            showBottomSheet = showBottomSheetMultiCourse,
            onDismissRequest = {
                showBottomSheetMultiCourse = false
            },
            autoShape = false,
            hazeState = hazeState
        ) {
            MultiCourseSheetUIForSearch(courses = courses ,weekday = multiWeekday,week = multiWeek,vm = vm, hazeState = hazeState, list = list)
        }
    }

    val table = rememberSaveable { List(30) { mutableStateListOf<CardBean>() } }
    val tableAll = rememberSaveable { List(42) { mutableStateListOf<CardBean>() } }

    var currentWeek by rememberSaveable { mutableLongStateOf(
        if(onDateChange == null) {
            1L
        } else {
            if(DateTimeManager.weeksBetweenJxglstu > 20) {
                getNewWeek()
            } else if(DateTimeManager.weeksBetweenJxglstu < 1) {
                onDateChange(getJxglstuStartDate())
                1L
            } else {
                DateTimeManager.weeksBetweenJxglstu
            }
        }
    ) }

    fun refreshUI() {
        // 清空
        if(showAll) {
            clearUnit(tableAll)
        } else {
            clearUnit(table)
        }

        try {
            // 组装
            for(i in list.indices) {
                val lessonItem = list[i]
                val scheduleText = lessonItem.scheduleText.dateTimePlacePersonText.textZh
                    ?: continue
                val schedule = parseScheduleText(scheduleText)
                for(j in schedule.indices) {
                    val item = schedule[j]

                    val text =
                        "${item.periodRange.first}-${item.periodRange.second}节" +"\n" +
                                (if(onDateChange == null) "(${lessonItem.code.substringAfter("--")}班)" else "")+ lessonItem.course.nameZh +
                                (item.place?.let { "\n" + it } ?: "")
                    val bean = CardBean(i,text)
                    val weekday = item.weekday
                    val weekRange = item.weekRange
                    if (currentWeek.toInt() in weekRange) {

                        vmUI?.let {
                            when(weekday) {
                                6 -> { Handler(Looper.getMainLooper()).post { it.findNewCourse.value = text.isNotEmpty() } }
                                7 -> { Handler(Looper.getMainLooper()).post { it.findNewCourse.value = text.isNotEmpty() } }
                            }
                        }

                        if(showAll) {
                            if (weekday == 1) {
                                if (item.periodRange.first  in 1..2) {
                                    tableAll[0].add(bean)
                                }
                                if (item.periodRange.first  in 3..4) {
                                    tableAll[7].add(bean)
                                }
                                if (item.periodRange.first  in 5..6) {
                                    tableAll[14].add(bean)
                                }
                                if (item.periodRange.first  in 7..8) {
                                    tableAll[21].add(bean)
                                }
                                if (item.periodRange.first  >= 9) {
                                    tableAll[28].add(bean)
                                }
                            }
                            if (weekday == 2) {
                                if (item.periodRange.first  in 1..2) {
                                    tableAll[1].add(bean)
                                }
                                if (item.periodRange.first  in 3..4) {
                                    tableAll[8].add(bean)
                                }
                                if (item.periodRange.first  in 5..6) {
                                    tableAll[15].add(bean)
                                }
                                if (item.periodRange.first  in 7..8) {
                                    tableAll[22].add(bean)
                                }
                                if (item.periodRange.first  >= 9) {
                                    tableAll[29].add(bean)
                                }
                            }
                            if (weekday == 3) {
                                if (item.periodRange.first  in 1..2) {
                                    tableAll[2].add(bean)
                                }
                                if (item.periodRange.first  in 3..4) {
                                    tableAll[9].add(bean)
                                }
                                if (item.periodRange.first  in 5..6) {
                                    tableAll[16].add(bean)
                                }
                                if (item.periodRange.first  in 7..8) {
                                    tableAll[23].add(bean)
                                }
                                if (item.periodRange.first  >= 9) {
                                    tableAll[30].add(bean)
                                }
                            }
                            if (weekday == 4) {
                                if (item.periodRange.first  in 1..2) {
                                    tableAll[3].add(bean)
                                }
                                if (item.periodRange.first  in 3..4) {
                                    tableAll[10].add(bean)
                                }
                                if (item.periodRange.first  in 5..6) {
                                    tableAll[17].add(bean)
                                }
                                if (item.periodRange.first  in 7..8) {
                                    tableAll[24].add(bean)
                                }
                                if (item.periodRange.first  >= 9) {
                                    tableAll[31].add(bean)
                                }
                            }
                            if (weekday == 5) {
                                if (item.periodRange.first  in 1..2) {
                                    tableAll[4].add(bean)
                                }
                                if (item.periodRange.first  in 3..4) {
                                    tableAll[11].add(bean)
                                }
                                if (item.periodRange.first  in 5..6) {
                                    tableAll[18].add(bean)
                                }
                                if (item.periodRange.first  in 7..8) {
                                    tableAll[25].add(bean)
                                }
                                if (item.periodRange.first  >= 9) {
                                    tableAll[32].add(bean)
                                }
                            }
                            if (weekday == 6) {
                                if (item.periodRange.first  in 1..2) {
                                    tableAll[5].add(bean)
                                }
                                if (item.periodRange.first  in 3..4) {
                                    tableAll[12].add(bean)
                                }
                                if (item.periodRange.first  in 5..6) {
                                    tableAll[19].add(bean)
                                }
                                if (item.periodRange.first  in 7..8) {
                                    tableAll[26].add(bean)
                                }
                                if (item.periodRange.first  >= 9) {
                                    tableAll[33].add(bean)
                                }
                            }
                            if (weekday == 7) {
                                if (item.periodRange.first  in 1..2) {
                                    tableAll[6].add(bean)
                                }
                                if (item.periodRange.first  in 3..4) {
                                    tableAll[13].add(bean)
                                }
                                if (item.periodRange.first  in 5..6) {
                                    tableAll[20].add(bean)
                                }
                                if (item.periodRange.first  in 7..8) {
                                    tableAll[27].add(bean)
                                }
                                if (item.periodRange.first  >= 9) {
                                    tableAll[34].add(bean)
                                }
                            }
                        } else {
                            if (weekday == 1) {
                                if (item.periodRange.first  in 1..2) {
                                    table[0].add(bean)
                                }
                                if (item.periodRange.first  in 3..4) {
                                    table[5].add(bean)
                                }
                                if (item.periodRange.first  in 5..6) {
                                    table[10].add(bean)
                                }
                                if (item.periodRange.first  in 7..8) {
                                    table[15].add(bean)
                                }
                                if (item.periodRange.first  >= 9) {
                                    table[20].add(bean)
                                }
                            }
                            if (weekday == 2) {
                                if (item.periodRange.first  in 1..2) {
                                    table[1].add(bean)
                                }
                                if (item.periodRange.first  in 3..4) {
                                    table[6].add(bean)
                                }
                                if (item.periodRange.first  in 5..6) {
                                    table[11].add(bean)
                                }
                                if (item.periodRange.first  in 7..8) {
                                    table[16].add(bean)
                                }
                                if (item.periodRange.first  >= 9) {
                                    table[21].add(bean)
                                }
                            }
                            if (weekday == 3) {
                                if (item.periodRange.first  in 1..2) {
                                    table[2].add(bean)
                                }
                                if (item.periodRange.first  in 3..4) {
                                    table[7].add(bean)
                                }
                                if (item.periodRange.first  in 5..6) {
                                    table[12].add(bean)
                                }
                                if (item.periodRange.first  in 7..8) {
                                    table[17].add(bean)
                                }
                                if (item.periodRange.first  >= 9) {
                                    table[22].add(bean)
                                }
                            }
                            if (weekday == 4) {
                                if (item.periodRange.first  in 1..2) {
                                    table[3].add(bean)
                                }
                                if (item.periodRange.first  in 3..4) {
                                    table[8].add(bean)
                                }
                                if (item.periodRange.first  in 5..6) {
                                    table[13].add(bean)
                                }
                                if (item.periodRange.first  in 7..8) {
                                    table[18].add(bean)
                                }
                                if (item.periodRange.first  >= 9) {
                                    table[23].add(bean)
                                }
                            }
                            if (weekday == 5) {
                                if (item.periodRange.first  in 1..2) {
                                    table[4].add(bean)
                                }
                                if (item.periodRange.first  in 3..4) {
                                    table[9].add(bean)
                                }
                                if (item.periodRange.first  in 5..6) {
                                    table[14].add(bean)
                                }
                                if (item.periodRange.first  in 7..8) {
                                    table[19].add(bean)
                                }
                                if (item.periodRange.first  >= 9) {
                                    table[24].add(bean)
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

    LaunchedEffect(showAll,currentWeek) {
        refreshUI()
    }
    Box(modifier = Modifier.fillMaxHeight()) {
        val scrollState = rememberLazyGridState()
        val padding = if (showAll) 1.dp else 2.dp
        val shouldShowAddButton by remember { derivedStateOf { scrollState.firstVisibleItemScrollOffset == 0 } }
        val textSize = if(showAll)12.sp else 14.sp

        LazyVerticalGrid(
            columns = GridCells.Fixed(if(showAll)7 else 5),
            modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP-CARD_NORMAL_DP-padding*2, vertical = padding),
            state = scrollState
        ) {
            items(if(showAll)7 else 5) { InnerPaddingHeight(innerPadding,true) }
            items(if(showAll)42 else 30) { cell ->
                val texts = if(showAll)tableAll[cell].toMutableList() else table[cell].toMutableList()
                Card(
                    shape = MaterialTheme.shapes.extraSmall,
                    colors = CardDefaults.cardColors(containerColor = if(backGroundHaze != null) Color.Transparent else MaterialTheme.colorScheme.surfaceContainerHigh),
                    modifier = Modifier
                        .fillMaxWidth() // 填满列宽
                        // 高度由内容撑开
                        .height(125.dp)
                        .padding(padding)
                        .let {
                            backGroundHaze?.let { haze ->
                                it
                                    .clip(MaterialTheme.shapes.extraSmall)
                                    .containerBlur(haze,MaterialTheme.colorScheme.surfaceContainerHigh)
                            } ?: it
                        }
                        .clickable {
                            // 只有一节课
                            if (texts.size == 1) {
                                numItem = texts[0].lessonNum
                                showBottomSheet = true
                            } else if (texts.size > 1) {
                                multiWeekday =
                                    if (showAll) (cell + 1) % 7 else (cell + 1) % 5
                                multiWeek = currentWeek.toInt()
                                courses = texts
                                showBottomSheetMultiCourse = true
                            }
                        },
                ) {
                    if(texts.size == 1) {
                        val l = texts[0].text.split("\n")
                        val time = l[0]
                        val name = l[1]
                        val place = if(l.size>=3) l[2] else null
                        Column(
                            modifier = Modifier.fillMaxSize().padding(horizontal = CARD_NORMAL_DP) ,
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = time,
                                fontSize = textSize,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f) // 占据中间剩余的全部空间
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Text(
                                    text = name,
                                    fontSize = textSize,
                                    textAlign = TextAlign.Center,
                                    overflow = TextOverflow.Ellipsis, // 超出显示省略号
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            place?.let {
                                Text(
                                    text = it,
                                    fontSize = textSize,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    } else {
                        Column (
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
//                        contentAlignment = Alignment.Center // 文字垂直水平居中
                        ) {
                            Text(
                                text =
                                    if (texts.size == 1) texts[0].text
                                    else if (texts.size > 1) "${texts[0].text.substringBefore("\n")}\n" + "${texts.size}节课冲突\n点击查看"
                                    else "",
                                fontSize = textSize ,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }

                }
            }
            item { InnerPaddingHeight(innerPadding,false) }
        }
        // 上一周
        AnimatedVisibility(
            visible = shouldShowAddButton,
            enter = scaleIn(),
            exit = scaleOut(),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = innerPadding.calculateBottomPadding() - if(onDateChange != null) navigationBarHeightPadding else 0.dp).let { if(onDateChange == null) it.navigationBarsPadding() else it }

                .padding(
                    horizontal = APP_HORIZONTAL_DP,
                    vertical = APP_HORIZONTAL_DP
                )
        ) {
            if (shouldShowAddButton) {
                FloatingActionButton(
                    onClick = {
                        if (currentWeek > 1) {
                            currentWeek-- - 1
                            onDateChange?.let { today?.minusDays(7)?.let { p1 -> it(p1) } }
                        }
                    },
                ) { Icon(Icons.Filled.ArrowBack, "Add Button") }
            }
        }
        // 中间
        AnimatedVisibility(
            visible = shouldShowAddButton,
            enter = scaleIn(),
            exit = scaleOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = innerPadding.calculateBottomPadding() - if(onDateChange != null) navigationBarHeightPadding else 0.dp).let { if(onDateChange == null) it.navigationBarsPadding() else it }
                .padding(
                    horizontal = APP_HORIZONTAL_DP,
                    vertical = APP_HORIZONTAL_DP
                )
        ) {
            if (shouldShowAddButton) {
                ExtendedFloatingActionButton(
                    onClick = {
                        if(DateTimeManager.weeksBetweenJxglstu < 1) {
                            currentWeek = 1
                            onDateChange?.let { it(getJxglstuStartDate()) }
                        } else {
                            currentWeek = DateTimeManager.weeksBetweenJxglstu
                            onDateChange?.let { it(LocalDate.now()) }
                        }
                    },
                ) {
                    AnimatedContent(
                        targetState = currentWeek,
                        transitionSpec = {
                            scaleIn(animationSpec = tween(500)
                            ) togetherWith(scaleOut(animationSpec = tween(500)))
                        }, label = ""
                    ){ n ->
                        Text(text = "第 $n 周")
                    }
                }
            }
        }
        // 下一周
        AnimatedVisibility(
            visible = shouldShowAddButton,
            enter = scaleIn(),
            exit = scaleOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = innerPadding.calculateBottomPadding() - if(onDateChange != null) navigationBarHeightPadding else 0.dp).let { if(onDateChange == null) it.navigationBarsPadding() else it }
                .padding(
                    horizontal = APP_HORIZONTAL_DP,
                    vertical = APP_HORIZONTAL_DP
                )
        ) {
            if (shouldShowAddButton) {
                FloatingActionButton(
                    onClick = {
                        if (currentWeek < 20) {
                            currentWeek++ + 1
                            onDateChange?.let { today?.plusDays(7)?.let { p1 -> it(p1) } }
                        }
                    },
                ) { Icon(Icons.Filled.ArrowForward, "Add Button") }
            }
        }
    }
}

private data class Schedule(
    val weekRange : List<Int>,
    val weekday : Int,
    val periodRange : Pair<Int,Int>,
    val place : String?,
//    val teacher : String
)

private data class CardBean(
    val lessonNum : Int,
    val text : String
)

private fun parseScheduleText(text : String) : List<Schedule> {
    val result = mutableListOf<Schedule>()
    try {
        val textList = text.split("\n")
        textList.forEach { item ->
            val tinyList = item.split(" ")
            val weekText = parseWeek(tinyList[0])
            val weekday = parseWeekday(tinyList[1])
            val periodText = parsePeriod(tinyList[2])
            var place : String? = null
            tinyList[4].let {
                if(it.contains("(")) {
                    place = it.substringBefore("(")
                    place = if(place.contains("学堂")) place.replace("学堂","") else it
                }
            }
            result.add(Schedule(weekText,weekday,periodText,place))
        }
        return result
    } catch (e : Exception) {
        return result
    }
}

private fun parseWeekday(text: String): Int = when (text) {
    "周一" -> 1
    "周二" -> 2
    "周三" -> 3
    "周四" -> 4
    "周五" -> 5
    "周六" -> 6
    "周日" -> 7
    else -> throw IllegalArgumentException("未知的星期: $text")
}

// 解析 1~3,5~9周 为list(1,2,3,5,6,7,8,9)
private fun parseWeek(text: String) : List<Int> {
    val textList = text.let {
        if (it.endsWith("周")) it.dropLast(1) else it
    }.split(",")
    return textList.flatMap { part ->
        if ("~" in part) {
            val (start, end) = part.split("~").map { it.toInt() }
            (start..end).toList()
        } else {
            listOf(part.toInt())
        }
    }
}

private fun parsePeriod(text: String) : Pair<Int,Int> {
    val textList = text.split("~")
    val start = parseSingleChineseDigit(textList[0][1])
    val end = parseSingleChineseDigit(textList[1][1])
    return Pair(start,end)
}
