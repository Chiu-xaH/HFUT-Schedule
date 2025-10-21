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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.CustomEventType
import com.hfut.schedule.logic.database.util.CustomEventMapper.entityToDto
import com.hfut.schedule.logic.model.community.courseDetailDTOList
import com.hfut.schedule.logic.network.util.toStr
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.weeksBetween
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.padding.navigationBarHeightPadding
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.home.calendar.ExamToCalenderBean

import com.hfut.schedule.ui.screen.home.calendar.examToCalendar
import com.hfut.schedule.ui.screen.home.calendar.getScheduleDate
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.MultiCourseSheetUI
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.calendarSquareGlass
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.clearUnit
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.dateToWeek
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.distinctUnit
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.getNewWeek
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getCourseInfoFromCommunity
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getStartWeekFromCommunity
import com.hfut.schedule.ui.style.CalendarStyle
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.ui.style.special.containerBlur
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.mirror.util.ShaderState
import com.xah.uicommon.style.ClickScale
import com.xah.uicommon.style.clickableWithScale
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch
import java.time.LocalDate
fun distinctUnitForCommunity(list : List<SnapshotStateList<courseDetailDTOList>>) {
    for(t in list) {
        val uniqueItems = t.distinctBy {
            it.name + it.place + it.week + it.section
        }
        t.clear()
        t.addAll(uniqueItems)
    }
}
@Composable
fun CommunityCourseTableUI(
    showAll: Boolean,
    innerPaddings: PaddingValues,
    vmUI : UIViewModel,
    friendUserName : String? = null,
    onDateChange : (LocalDate) ->Unit,
    today: LocalDate,
    vm : NetWorkViewModel,
    hazeState: HazeState,
    backGroundHaze : ShaderState?
) {
    val context = LocalContext.current
    var examList: List<ExamToCalenderBean> by remember { mutableStateOf(emptyList()) }
    LaunchedEffect(Unit) {
        examList = if(weeksBetween < 1 || friendUserName != null) {
            emptyList()
        } else {
            examToCalendar(context)
        }
    }
    val enableHideEmptyCalendarSquare by DataStoreManager.enableHideEmptyCalendarSquare.collectAsState(initial = false)

    //切换周数
    var currentWeek by rememberSaveable {
        mutableLongStateOf(
            if(weeksBetween > 20) {
                getNewWeek()
            } else if(weeksBetween < 1) {
                onDateChange(getStartWeekFromCommunity())
                1
            } else weeksBetween
        )
    }

    val focusList by produceState(initialValue = emptyList()) {
        value = if(friendUserName == null){
            DataBaseManager.customEventDao.getAll(CustomEventType.SCHEDULE.name).map {
                entityToDto(it)
            }
        } else {
            emptyList()
        }
    }

    val table = remember { List(30) { mutableStateListOf<courseDetailDTOList>() } }
    val tableAll = remember { List(42) { mutableStateListOf<courseDetailDTOList>() } }
    var sheet by remember { mutableStateOf(courseDetailDTOList(0,0,"","","", listOf(0),0,"","")) }
    val scope = rememberCoroutineScope()

    //填充UI与更新
    fun refreshUI() {
        // 清空
        if(showAll) {
            clearUnit(tableAll)
        } else {
            clearUnit(table)
        }
        Handler(Looper.getMainLooper()).post { vmUI.findNewCourse.value = false }

        try {
            scope.launch {
                launch course@ {
                    // 组装课表
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
                        distinctUnitForCommunity(tableAll)
                    } else {
                        distinctUnitForCommunity(table)
                    }
                }
                launch focus@ {
                    // 组装日程
                    for(item in focusList) {
                        val start = item.dateTime.start.toStr().split(" ")
                        if(start.size != 2) {
                            continue
                        }
                        val startDate = start[0]
                        val startTime = start[1]
                        val weekInfo = dateToWeek(startDate) ?: continue
                        // 是同一周
                        if(weekInfo.first != currentWeek.toInt()) {
                            continue
                        }
                        val name = item.title
                        val place = item.description
                        val hour = startTime.substringBefore(":").toIntOrNull() ?: continue
                        val index = weekInfo.second - 1
                        val offset = if(showAll) 7 else 5
                        if(hour <= 9) {
                            val finalIndex = index+offset*0
                            if(showAll) {
                                tableAll[finalIndex]
                            } else {
                                table[finalIndex]
                            }
                                .add(courseDetailDTOList(-1,-1,place?.replace("学堂",""),"空",startTime,emptyList(),-1,name  + "(日程)","空"))
                        } else if(hour in 10..12) {
                            val finalIndex = index+offset*1
                            if(showAll) {
                                tableAll[finalIndex]
                            } else {
                                table[finalIndex]
                            }
                                .add(courseDetailDTOList(-1,-1,place?.replace("学堂",""),"空",startTime,emptyList(),-1,name  + "(日程)","空"))
                        } else if(hour in 13..15) {
                            val finalIndex = index+offset*2
                            if(showAll) {
                                tableAll[finalIndex]
                            } else {
                                table[finalIndex]
                            }
                                .add(courseDetailDTOList(-1,-1,place?.replace("学堂",""),"空",startTime,emptyList(),-1,name  + "(日程)","空"))
                        } else if(hour in 16..17) {
                            val finalIndex = index+offset*3
                            if(showAll) {
                                tableAll[finalIndex]
                            } else {
                                table[finalIndex]
                            }
                                .add(courseDetailDTOList(-1,-1,place?.replace("学堂",""),"空",startTime,emptyList(),-1,name  + "(日程)","空"))
                        } else if(hour >= 18) {
                            val finalIndex = index+offset*4
                            if(showAll) {
                                tableAll[finalIndex]
                            } else {
                                table[finalIndex]
                            }
                                .add(courseDetailDTOList(-1,-1,place?.replace("学堂",""),"空",startTime,emptyList(),-1,name  + "(日程)","空"))
                        }
                    }
                }
                launch exam@ {
                    // 组装考试
                    for(item in examList) {
                        val startTime = item.startTime ?: continue
                        val startDate = item.day ?: continue
                        val weekInfo = dateToWeek(startDate) ?: continue
                        // 是同一周
                        if(weekInfo.first != currentWeek.toInt()) {
                            continue
                        }
                        val name = item.course
                        val place = item.place
                        val hour = startTime.substringBefore(":").toIntOrNull() ?: continue
                        val index = weekInfo.second - 1
                        val offset = if(showAll) 7 else 5
                        if(hour <= 9) {
                            val finalIndex = index+offset*0
                            if(showAll) {
                                tableAll[finalIndex]
                            } else {
                                table[finalIndex]
                            }
                                .add(courseDetailDTOList(-1,-1,place?.replace("学堂",""),"空",startTime,emptyList(),-1,name  + "(考试)","空"))
                        } else if(hour in 10..12) {
                            val finalIndex = index+offset*1
                            if(showAll) {
                                tableAll[finalIndex]
                            } else {
                                table[finalIndex]
                            }
                                .add(courseDetailDTOList(-1,-1,place?.replace("学堂",""),"空",startTime,emptyList(),-1,name  + "(考试)","空"))
                        } else if(hour in 13..15) {
                            val finalIndex = index+offset*2
                            if(showAll) {
                                tableAll[finalIndex]
                            } else {
                                table[finalIndex]
                            }
                                .add(courseDetailDTOList(-1,-1,place?.replace("学堂",""),"空",startTime,emptyList(),-1,name  + "(考试)","空"))
                        } else if(hour in 16..17) {
                            val finalIndex = index+offset*3
                            if(showAll) {
                                tableAll[finalIndex]
                            } else {
                                table[finalIndex]
                            }
                                .add(courseDetailDTOList(-1,-1,place?.replace("学堂",""),"空",startTime,emptyList(),-1,name  + "(考试)","空"))
                        } else if(hour >= 18) {
                            val finalIndex = index+offset*4
                            if(showAll) {
                                tableAll[finalIndex]
                            } else {
                                table[finalIndex]
                            }
                                .add(courseDetailDTOList(-1,-1,place?.replace("学堂",""),"空",startTime,emptyList(),-1,name  + "(考试)","空"))
                        }
                    }
                }
            }

        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    //装载数组和信息
    LaunchedEffect(showAll,currentWeek) {
        refreshUI()
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

    val customBackgroundAlpha by DataStoreManager.customCalendarSquareAlpha.collectAsState(initial = 1f)
    val enableTransition = !(backGroundHaze != null && AppVersion.CAN_SHADER)
    val enableLiquidGlass by DataStoreManager.enableLiquidGlass.collectAsState(initial = AppVersion.CAN_SHADER)

    Column(modifier = Modifier.fillMaxSize()) {
            Box {
                val scrollState = rememberLazyGridState()
                val shouldShowAddButton by remember { derivedStateOf { scrollState.firstVisibleItemScrollOffset == 0 } }
                val style = CalendarStyle(showAll)
                val color =  if(enableTransition) style.containerColor.copy(customBackgroundAlpha) else Color.Transparent

                LazyVerticalGrid(
                    columns = GridCells.Fixed(style.rowCount),
                    modifier = style.calendarPadding(),
                    state = scrollState
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) { InnerPaddingHeight(innerPaddings,true) }
                    items(style.rowCount*style.columnCount) { cell ->
                        val itemList = if(showAll)tableAll[cell].toMutableList() else table[cell].toMutableList()
                        val texts = transferSummaryCourseInfos(itemList).toMutableList()
                        if(texts.isEmpty() && enableHideEmptyCalendarSquare) {
                            // 隐藏
                            Box(modifier = Modifier.height(style.height).padding(style.everyPadding))
                        } else {
                            Card(
                                shape = style.containerCorner,
                                colors = CardDefaults.cardColors(containerColor = color),
                                modifier = Modifier
                                    .height(style.height)
                                    .padding(style.everyPadding)
                                    .let {
                                        if(backGroundHaze != null) {
                                            it
                                                .clip(style.containerCorner)
                                                .let {
                                                    if(AppVersion.CAN_SHADER) {
                                                        it.calendarSquareGlass(
                                                            backGroundHaze,
                                                            style.containerColor.copy(customBackgroundAlpha),
                                                            enableLiquidGlass
                                                        )
                                                    } else {
                                                        it
                                                    }
                                                }
                                        } else {
                                            it
                                        }
                                    }
                                    .clickableWithScale(ClickScale.SMALL.scale) {
                                        if (texts.size == 1) {
                                            // 如果是考试
                                            if (texts[0].contains("考试")) {
                                                showToast(texts[0].replace("\n"," "))
                                                return@clickableWithScale
                                            }
                                            if (texts[0].contains("日程")) {
                                                showToast(texts[0].replace("\n"," "))
                                                return@clickableWithScale
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
                                if(texts.size == 1) {
                                    val l = texts[0].split("\n")
                                    if(l.size < 2) {
                                        return@Card
                                    }
                                    val time = l[0]
                                    val name = l[1]
                                    val place = if(l.size == 3) {
                                        val p = l[2]
                                        if(p == "null" || p.isBlank() || p.isEmpty()) {
                                            null
                                        } else {
                                            p
                                        }
                                    } else null

                                    Column(
                                        modifier = Modifier.fillMaxSize().padding(horizontal = CARD_NORMAL_DP) ,
                                        verticalArrangement = Arrangement.SpaceBetween,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = time,
                                            fontSize = style.textSize,
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
                                                fontSize = style.textSize,
                                                textAlign = TextAlign.Center,
                                                overflow = TextOverflow.Ellipsis, // 超出显示省略号
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                        place?.let {
                                            Text(
                                                text = it,
                                                fontSize = style.textSize,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                } else if(texts.size > 1) {
                                    val name = texts.map {
                                        it.split("\n")[1][0]
                                    }.joinToString(",")
                                    val isExam = if(texts.toString().contains("考试")) FontWeight.SemiBold else FontWeight.Normal
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = CARD_NORMAL_DP) ,
                                        verticalArrangement = Arrangement.SpaceBetween,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = texts[0].substringBefore("\n"),
                                            fontSize = style.textSize,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth(),
                                            fontWeight = isExam
                                        )
                                        Box(
                                            modifier = Modifier
                                                .weight(1f) // 占据中间剩余的全部空间
                                                .fillMaxWidth(),
                                            contentAlignment = Alignment.TopCenter
                                        ) {
                                            Text(
                                                text = "${texts.size}节课冲突",
                                                fontSize = style.textSize,
                                                textAlign = TextAlign.Center,
                                                overflow = TextOverflow.Ellipsis, // 超出显示省略号
                                                modifier = Modifier.fillMaxWidth(),
                                                fontWeight = isExam
                                            )
                                        }
                                        Text(
                                            text = name,
                                            fontSize = style.textSize,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) { InnerPaddingHeight(innerPaddings,false) }
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = shouldShowAddButton,
                    enter = scaleIn(),
                    exit = scaleOut(),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = innerPaddings.calculateBottomPadding()-navigationBarHeightPadding)
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
                        .padding(bottom = innerPaddings.calculateBottomPadding()-navigationBarHeightPadding)
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

                androidx.compose.animation.AnimatedVisibility(
                    visible = shouldShowAddButton,
                    enter = scaleIn(),
                    exit = scaleOut(),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = innerPaddings.calculateBottomPadding()-navigationBarHeightPadding)
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
fun ScheduleTopDate(showAll: Boolean,today : LocalDate) {
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
        Spacer(modifier = Modifier.height(5.dp))
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
    return time + "\n" + name + "\n" + (room ?: "")
}

private fun transferSummaryCourseInfos(text: List<courseDetailDTOList>): List<String> = text.map { item -> transferSummaryCourseInfo(item) }
