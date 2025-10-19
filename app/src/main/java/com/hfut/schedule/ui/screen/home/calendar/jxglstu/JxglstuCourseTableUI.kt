package com.hfut.schedule.ui.screen.home.calendar.jxglstu

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.CustomEventType
import com.hfut.schedule.logic.database.util.CustomEventMapper.entityToDto
import com.hfut.schedule.logic.model.community.courseDetailDTOList
import com.hfut.schedule.logic.model.jxglstu.CourseUnitBean
import com.hfut.schedule.logic.model.jxglstu.DatumResponse
import com.hfut.schedule.logic.model.jxglstu.LessonTimesResponse
import com.hfut.schedule.logic.network.interceptor.CasGoToInterceptorState
import com.hfut.schedule.logic.network.util.CasInHFUT
import com.hfut.schedule.logic.network.util.MyApiParse.getSchedule
import com.hfut.schedule.logic.network.util.MyApiParse.isNextOpen
import com.hfut.schedule.logic.network.util.isNotBadRequest
import com.hfut.schedule.logic.network.util.toStr
import com.hfut.schedule.logic.util.development.getKeyStackTrace
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.parse.SemseterParser
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.LIBRARY_TOKEN
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.saveInt
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.LargeCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.calendar.ExamToCalenderBean
import com.hfut.schedule.ui.screen.home.calendar.communtiy.CourseDetailApi
import com.hfut.schedule.ui.screen.home.calendar.communtiy.DetailInfos
import com.hfut.schedule.ui.screen.home.calendar.examToCalendar
import com.hfut.schedule.ui.screen.home.calendar.getScheduleDate
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.next.CourseDetailOrigin
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.next.parseCourseName
import com.hfut.schedule.ui.screen.home.focus.funiction.parseTimeItem
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.getCardPsk
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getJxglstuStartDate
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getTotalCourse
import com.hfut.schedule.ui.style.CalendarStyle
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.style.special.containerBlur
import com.hfut.schedule.ui.util.GlobalUIStateHolder
import com.hfut.schedule.ui.util.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.xah.mirror.shader.GlassStyle
import com.xah.mirror.shader.glassLayer
import com.xah.mirror.util.ShaderState
import com.xah.transition.component.containerShare
import com.xah.uicommon.component.status.LoadingUI
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.ClickScale
import com.xah.uicommon.style.align.CenterScreen
import com.xah.uicommon.style.clickableWithScale
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.xah.uicommon.style.padding.navigationBarHeightPadding
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.time.LocalDate
import java.time.temporal.ChronoUnit

suspend fun parseTimeTable(json : String, isNext : Boolean = false) : List<CourseUnitBean> {
    try {
        if(json.isEmpty()) {
            // 使用预置的作息表
            val campus = getPersonInfo().campus ?: return emptyList()
            if(campus.contains("宣城")) {
                return MyApplication.XC_TXL1
            } else if(campus.contains("翡翠湖")) {
                val upOrDown = SemseterParser.parseSemseterUpOrDown(SemseterParser.getSemseter() + if(isNext) 20 else 0)
                return when(upOrDown) {
                    1 -> MyApplication.FCH1
                    2 -> MyApplication.FCH2
                    else -> emptyList()
                }
            } else if(campus.contains("屯溪路")) {
                val upOrDown = SemseterParser.parseSemseterUpOrDown(SemseterParser.getSemseter() + if(isNext) 20 else 0)
                return when(upOrDown) {
                    1 -> MyApplication.XC_TXL1
                    2 -> MyApplication.TXL2
                    else -> emptyList()
                }
            } else {
                return emptyList()
            }
        } else {
            return Gson().fromJson(json, LessonTimesResponse::class.java).result.courseUnitList
        }
    } catch (e : Exception) {
        return emptyList()
    }
}

// 去重
fun <T>distinctUnit(list : List<SnapshotStateList<T>>) {
    for(t in list) {
        val uniqueItems = t.distinct()
        t.clear()
        t.addAll(uniqueItems)
    }
}
// 清空
fun <T>clearUnit(list : List<SnapshotStateList<T>>) {
    for(t in list) {
        t.clear()
    }
}

suspend fun loginHuiXin(vm: NetWorkViewModel) {
    val username = getPersonInfo().studentId ?: return
    val password = getCardPsk() ?: return
    vm.huiXinLoginResp.clear()
    vm.huiXinSingleLogin(username,password)
}


private suspend fun loginCommunity(cookies: String, vm: NetWorkViewModel) {
    val result = vm.gotoCommunity(cookies)
    if (isNotBadRequest(result)) {
        CasGoToInterceptorState.toCommunityTicket
            .filterNotNull()
            .collect { value ->
                vm.loginCommunity(value)
            }
    }
}

private suspend fun loginOne(cookies: String, vm: NetWorkViewModel) {
    vm.goToOne(cookies)
    vm.goToOne(cookies)
    // byd为啥发两次才给302
    CasGoToInterceptorState.toOneCode
        .filterNotNull()
        .collect { value ->
            vm.loginOne(value)
        }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun JxglstuCourseTableUI(
    showAll: Boolean,
    vm: NetWorkViewModel,
    innerPadding: PaddingValues,
    vmUI: UIViewModel,
    webVpn: Boolean,
    refreshLogin: Boolean,
    onDateChange: (LocalDate) ->Unit,
    today: LocalDate,
    hazeState: HazeState,
    navController: NavHostController,
    backGroundHaze : ShaderState?,
    isEnabled : Boolean,
    onEnabled : (Boolean) -> Unit
) {
    val context = LocalContext.current
    val enableHideEmptyCalendarSquare by DataStoreManager.enableHideEmptyCalendarSquare.collectAsState(initial = false)

    var showBottomSheetTotalCourse by remember { mutableStateOf(false) }
    var showBottomSheetMultiCourse by remember { mutableStateOf(false) }
    var courseName by remember { mutableStateOf("") }

    var courses by remember { mutableStateOf(listOf<String>()) }
    var multiWeekday by remember { mutableIntStateOf(0) }
    var multiWeek by remember { mutableIntStateOf(0) }

    if (showBottomSheetTotalCourse) {
        HazeBottomSheet (
            onDismissRequest = {
                showBottomSheetTotalCourse = false
            },
            showBottomSheet = showBottomSheetTotalCourse,
            hazeState = hazeState
        ) {
            CourseDetailApi(courseName = courseName, vm = vm, hazeState = hazeState)
        }
    }

    if (showBottomSheetMultiCourse) {
        HazeBottomSheet (
            showBottomSheet = showBottomSheetMultiCourse,
            onDismissRequest = {
                showBottomSheetMultiCourse = false
            },
            autoShape = false,
            hazeState = hazeState
        ) {
            MultiCourseSheetUI(courses = courses ,weekday = multiWeekday,week = multiWeek,vm = vm, hazeState = hazeState)
        }
    }

    var loadingJxglstu by rememberSaveable { mutableStateOf(refreshLogin) }

    val table = rememberSaveable { List(30) { mutableStateListOf<String>() } }
    val tableAll = rememberSaveable { List(42) { mutableStateListOf<String>() } }

    var examList: List<ExamToCalenderBean> by remember { mutableStateOf(emptyList()) }
    LaunchedEffect(Unit) {
        examList = if(DateTimeManager.weeksBetweenJxglstu < 1) {
            emptyList()
        } else {
            examToCalendar(context)
        }
    }

    val focusList by produceState(initialValue = emptyList()) {
        value = DataBaseManager.customEventDao.getAll(CustomEventType.SCHEDULE.name).map {
            entityToDto(it)
        }
    }


    var currentWeek by rememberSaveable {
        mutableLongStateOf(
            if(DateTimeManager.weeksBetweenJxglstu > 20) {
                getNewWeek()
            } else if(DateTimeManager.weeksBetweenJxglstu < 1) {
                onDateChange(getJxglstuStartDate())
                1L
            } else {
                DateTimeManager.weeksBetweenJxglstu
            }
        )
    }



    val times by DataStoreManager.courseTableTimeValue.collectAsState(initial = "")
    val timeTable by produceState(initialValue = emptyList<CourseUnitBean>(), key1 = times) {
        value = parseTimeTable(times)
    }

    val json by produceState<String?>(initialValue = null) {
        value = LargeStringDataManager.read(context, LargeStringDataManager.DATUM)
    }


    var exception by remember { mutableStateOf<Exception?>(null) }
    val scope = rememberCoroutineScope()

    fun refreshUI() {
        // 初始化
        exception = null
        if(showAll) {
            clearUnit(tableAll)
        } else {
            clearUnit(table)
        }
        Handler(Looper.getMainLooper()).post { vmUI.findNewCourse.value = false }
        scope.launch {
            launch course@ {
                // 组装课表
                if(json == null) {
                    return@course
                }
                try {
                    val datumResponse = Gson().fromJson(json, DatumResponse::class.java)
                    val scheduleList = datumResponse.result.scheduleList
                    val lessonList = datumResponse.result.lessonList

                    for (i in scheduleList.indices) {
                        val item = scheduleList[i]
                        val startTime = item.startTime
                        val startHour = startTime / 100
                        val startMinute = startTime % 100
                        val startTimeStr = "$startHour:${parseTimeItem(startMinute)}"

                        var room = item.room?.nameZh
                        var courseId = item.lessonId.toString()
                        room = room?.replace("学堂","") ?: ""


                        for (j in lessonList.indices) {
                            if (courseId == lessonList[j].id) {
                                courseId = lessonList[j].courseName
                            }
                        }

                        val text = startTimeStr + "\n" + courseId + "\n" + room

                        if (item.weekIndex == currentWeek.toInt()) {
                            when(item.weekday) {
                                6 -> { Handler(Looper.getMainLooper()).post { vmUI.findNewCourse.value = text.isNotEmpty() } }
                                7 -> { Handler(Looper.getMainLooper()).post { vmUI.findNewCourse.value = text.isNotEmpty() } }
                            }
                            if(showAll) {
                                if (item.weekday == 1) {
                                    if (startHour in 8..9 || startTime == timeTable[0].startTime || startTime == timeTable[1].startTime) {
                                        tableAll[0].add(text)
                                    }
                                    if (startHour in 10..11 || startTime == timeTable[2].startTime || startTime == timeTable[3].startTime) {
                                        tableAll[7].add(text)
                                    }
                                    if (startHour in 14..15 || startTime == timeTable[4].startTime || startTime == timeTable[5].startTime) {
                                        tableAll[14].add(text)
                                    }
                                    if (startHour in 16..17 || startTime == timeTable[6].startTime || startTime == timeTable[7].startTime) {
                                        tableAll[21].add(text)
                                    }
                                    if (startHour in 19..21 || startTime == timeTable[8].startTime || startTime == timeTable[9].startTime || startTime == timeTable[10].startTime) {
                                        tableAll[28].add(text)
                                    }
                                }
                                if (item.weekday == 2) {
                                    if (startHour in 8..9 || startTime == timeTable[0].startTime || startTime == timeTable[1].startTime) {
                                        tableAll[1].add(text)
                                    }
                                    if (startHour in 10..11 || startTime == timeTable[2].startTime || startTime == timeTable[3].startTime) {
                                        tableAll[8].add(text)
                                    }
                                    if (startHour in 14..15 || startTime == timeTable[4].startTime || startTime == timeTable[5].startTime) {
                                        tableAll[15].add(text)
                                    }
                                    if (startHour in 16..17 || startTime == timeTable[6].startTime || startTime == timeTable[7].startTime) {
                                        tableAll[22].add(text)
                                    }
                                    if (startHour in 19..21 || startTime == timeTable[8].startTime || startTime == timeTable[9].startTime || startTime == timeTable[10].startTime) {
                                        tableAll[29].add(text)
                                    }
                                }
                                if (item.weekday == 3) {
                                    if (startHour in 8..9 || startTime == timeTable[0].startTime || startTime == timeTable[1].startTime) {
                                        tableAll[2].add(text)
                                    }
                                    if (startHour in 10..11 || startTime == timeTable[2].startTime || startTime == timeTable[3].startTime) {
                                        tableAll[9].add(text)
                                    }
                                    if (startHour in 14..15 || startTime == timeTable[4].startTime || startTime == timeTable[5].startTime) {
                                        tableAll[16].add(text)
                                    }
                                    if (startHour in 16..17 || startTime == timeTable[6].startTime || startTime == timeTable[7].startTime) {
                                        tableAll[23].add(text)
                                    }
                                    if (startHour in 19..21 || startTime == timeTable[8].startTime || startTime == timeTable[9].startTime || startTime == timeTable[10].startTime) {
                                        tableAll[30].add(text)
                                    }
                                }
                                if (item.weekday == 4) {
                                    if (startHour in 8..9 || startTime == timeTable[0].startTime || startTime == timeTable[1].startTime) {
                                        tableAll[3].add(text)
                                    }
                                    if (startHour in 10..11 || startTime == timeTable[2].startTime || startTime == timeTable[3].startTime) {
                                        tableAll[10].add(text)
                                    }
                                    if (startHour in 14..15 || startTime == timeTable[4].startTime || startTime == timeTable[5].startTime) {
                                        tableAll[17].add(text)
                                    }
                                    if (startHour in 16..17 || startTime == timeTable[6].startTime || startTime == timeTable[7].startTime) {
                                        tableAll[24].add(text)
                                    }
                                    if (startHour in 19..21 || startTime == timeTable[8].startTime || startTime == timeTable[9].startTime || startTime == timeTable[10].startTime) {
                                        tableAll[31].add(text)
                                    }
                                }
                                if (item.weekday == 5) {
                                    if (startHour in 8..9 || startTime == timeTable[0].startTime || startTime == timeTable[1].startTime) {
                                        tableAll[4].add(text)
                                    }
                                    if (startHour in 10..11 || startTime == timeTable[2].startTime || startTime == timeTable[3].startTime) {
                                        tableAll[11].add(text)
                                    }
                                    if (startHour in 14..15 || startTime == timeTable[4].startTime || startTime == timeTable[5].startTime) {
                                        tableAll[18].add(text)
                                    }
                                    if (startHour in 16..17 || startTime == timeTable[6].startTime || startTime == timeTable[7].startTime) {
                                        tableAll[25].add(text)
                                    }
                                    if (startHour in 19..21 || startTime == timeTable[8].startTime || startTime == timeTable[9].startTime || startTime == timeTable[10].startTime) {
                                        tableAll[32].add(text)
                                    }
                                }
                                if (item.weekday == 6) {
                                    if (startHour in 8..9 || startTime == timeTable[0].startTime || startTime == timeTable[1].startTime) {
                                        tableAll[5].add(text)
                                    }
                                    if (startHour in 10..11 || startTime == timeTable[2].startTime || startTime == timeTable[3].startTime) {
                                        tableAll[12].add(text)
                                    }
                                    if (startHour in 14..15 || startTime == timeTable[4].startTime || startTime == timeTable[5].startTime) {
                                        tableAll[19].add(text)
                                    }
                                    if (startHour in 16..17 || startTime == timeTable[6].startTime || startTime == timeTable[7].startTime) {
                                        tableAll[26].add(text)
                                    }
                                    if (startHour in 19..21 || startTime == timeTable[8].startTime || startTime == timeTable[9].startTime || startTime == timeTable[10].startTime) {
                                        tableAll[33].add(text)
                                    }
                                }
                                if (item.weekday == 7) {
                                    if (startHour in 8..9 || startTime == timeTable[0].startTime || startTime == timeTable[1].startTime) {
                                        tableAll[6].add(text)
                                    }
                                    if (startHour in 10..11 || startTime == timeTable[2].startTime || startTime == timeTable[3].startTime) {
                                        tableAll[13].add(text)
                                    }
                                    if (startHour in 14..15 || startTime == timeTable[4].startTime || startTime == timeTable[5].startTime) {
                                        tableAll[20].add(text)
                                    }
                                    if (startHour in 16..17 || startTime == timeTable[6].startTime || startTime == timeTable[7].startTime) {
                                        tableAll[27].add(text)
                                    }
                                    if (startHour in 19..21 || startTime == timeTable[8].startTime || startTime == timeTable[9].startTime || startTime == timeTable[10].startTime) {
                                        tableAll[34].add(text)
                                    }
                                }
                            } else {
                                if (item.weekday == 1) {
                                    if (startHour in 8..9 || startTime == timeTable[0].startTime || startTime == timeTable[1].startTime) {
                                        table[0].add(text)
                                    }
                                    if (startHour in 10..11 || startTime == timeTable[2].startTime || startTime == timeTable[3].startTime) {
                                        table[5].add(text)
                                    }
                                    if (startHour in 14..15 || startTime == timeTable[4].startTime || startTime == timeTable[5].startTime) {
                                        table[10].add(text)
                                    }
                                    if (startHour in 16..17 || startTime == timeTable[6].startTime || startTime == timeTable[7].startTime) {
                                        table[15].add(text)
                                    }
                                    if (startHour in 19..21 || startTime == timeTable[8].startTime || startTime == timeTable[9].startTime || startTime == timeTable[10].startTime) {
                                        table[20].add(text)
                                    }
                                }
                                if (item.weekday == 2) {
                                    if (startHour in 8..9 || startTime == timeTable[0].startTime || startTime == timeTable[1].startTime) {
                                        table[1].add(text)
                                    }
                                    if (startHour in 10..11 || startTime == timeTable[2].startTime || startTime == timeTable[3].startTime) {
                                        table[6].add(text)
                                    }
                                    if (startHour in 14..15 || startTime == timeTable[4].startTime || startTime == timeTable[5].startTime) {
                                        table[11].add(text)
                                    }
                                    if (startHour in 16..17 || startTime == timeTable[6].startTime || startTime == timeTable[7].startTime) {
                                        table[16].add(text)
                                    }
                                    if (startHour in 19..21 || startTime == timeTable[8].startTime || startTime == timeTable[9].startTime || startTime == timeTable[10].startTime) {
                                        table[21].add(text)
                                    }
                                }
                                if (item.weekday == 3) {
                                    if (startHour in 8..9 || startTime == timeTable[0].startTime || startTime == timeTable[1].startTime) {
                                        table[2].add(text)
                                    }
                                    if (startHour in 10..11 || startTime == timeTable[2].startTime || startTime == timeTable[3].startTime) {
                                        table[7].add(text)
                                    }
                                    if (startHour in 14..15 || startTime == timeTable[4].startTime || startTime == timeTable[5].startTime) {
                                        table[12].add(text)
                                    }
                                    if (startHour in 16..17 || startTime == timeTable[6].startTime || startTime == timeTable[7].startTime) {
                                        table[17].add(text)
                                    }
                                    if (startHour in 19..21 || startTime == timeTable[8].startTime || startTime == timeTable[9].startTime || startTime == timeTable[10].startTime) {
                                        table[22].add(text)
                                    }
                                }
                                if (item.weekday == 4) {
                                    if (startHour in 8..9 || startTime == timeTable[0].startTime || startTime == timeTable[1].startTime) {
                                        table[3].add(text)
                                    }
                                    if (startHour in 10..11 || startTime == timeTable[2].startTime || startTime == timeTable[3].startTime) {
                                        table[8].add(text)
                                    }
                                    if (startHour in 14..15 || startTime == timeTable[4].startTime || startTime == timeTable[5].startTime) {
                                        table[13].add(text)
                                    }
                                    if (startHour in 16..17 || startTime == timeTable[6].startTime || startTime == timeTable[7].startTime) {
                                        table[18].add(text)
                                    }
                                    if (startHour in 19..21 || startTime == timeTable[8].startTime || startTime == timeTable[9].startTime || startTime == timeTable[10].startTime) {
                                        table[23].add(text)
                                    }
                                }
                                if (item.weekday == 5) {
                                    if (startHour in 8..9 || startTime == timeTable[0].startTime || startTime == timeTable[1].startTime) {
                                        table[4].add(text)
                                    }
                                    if (startHour in 10..11 || startTime == timeTable[2].startTime || startTime == timeTable[3].startTime) {
                                        table[9].add(text)
                                    }
                                    if (startHour in 14..15 || startTime == timeTable[4].startTime || startTime == timeTable[5].startTime) {
                                        table[14].add(text)
                                    }
                                    if (startHour in 16..17 || startTime == timeTable[6].startTime || startTime == timeTable[7].startTime) {
                                        table[19].add(text)
                                    }
                                    if (startHour in 19..21 || startTime == timeTable[8].startTime || startTime == timeTable[9].startTime || startTime == timeTable[10].startTime) {
                                        table[24].add(text)
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
                    exception = e
                }
            }
            launch focus@ {
                // 组装日程
                try {
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
                                .add(startTime + "\n" + name  + "(日程)"+ "\n" + place?.replace("学堂",""))
                        } else if(hour in 10..12) {
                            val finalIndex = index+offset*1
                            if(showAll) {
                                tableAll[finalIndex]
                            } else {
                                table[finalIndex]
                            }
                                .add(startTime + "\n" + name  + "(日程)"+ "\n" + place?.replace("学堂",""))
                        } else if(hour in 13..15) {
                            val finalIndex = index+offset*2
                            if(showAll) {
                                tableAll[finalIndex]
                            } else {
                                table[finalIndex]
                            }
                                .add(startTime + "\n" + name  + "(日程)"+ "\n" + place?.replace("学堂",""))
                        } else if(hour in 16..17) {
                            val finalIndex = index+offset*3
                            if(showAll) {
                                tableAll[finalIndex]
                            } else {
                                table[finalIndex]
                            }
                                .add(startTime + "\n" + name  + "(日程)"+ "\n" + place?.replace("学堂",""))
                        } else if(hour >= 18) {
                            val finalIndex = index+offset*4
                            if(showAll) {
                                tableAll[finalIndex]
                            } else {
                                table[finalIndex]
                            }
                                .add(startTime + "\n" + name  + "(日程)"+ "\n" + place?.replace("学堂",""))
                        }
                    }
                } catch (e : Exception) {
                    e.printStackTrace()
                }
            }
            launch exam@ {
                // 组装考试
                try {
                    for (item in examList) {
                        val startTime = item.startTime ?: continue
                        val startDate = item.day ?: continue
                        val weekInfo = dateToWeek(startDate) ?: continue
                        // 是同一周
                        if (weekInfo.first != currentWeek.toInt()) {
                            continue
                        }
                        val name = item.course
                        val place = item.place
                        val hour = startTime.substringBefore(":").toIntOrNull() ?: continue
                        val index = weekInfo.second - 1
                        val offset = if (showAll) 7 else 5
                        if (hour <= 9) {
                            val finalIndex = index + offset * 0
                            if (showAll) {
                                tableAll[finalIndex]
                            } else {
                                table[finalIndex]
                            }
                                .add(
                                    startTime + "\n" + name + "(考试)" + "\n" + place?.replace(
                                        "学堂",
                                        ""
                                    )
                                )
                        } else if (hour in 10..12) {
                            val finalIndex = index + offset * 1
                            if (showAll) {
                                tableAll[finalIndex]
                            } else {
                                table[finalIndex]
                            }
                                .add(
                                    startTime + "\n" + name + "(考试)" + "\n" + place?.replace(
                                        "学堂",
                                        ""
                                    )
                                )
                        } else if (hour in 13..15) {
                            val finalIndex = index + offset * 2
                            if (showAll) {
                                tableAll[finalIndex]
                            } else {
                                table[finalIndex]
                            }
                                .add(
                                    startTime + "\n" + name + "(考试)" + "\n" + place?.replace(
                                        "学堂",
                                        ""
                                    )
                                )
                        } else if (hour in 16..17) {
                            val finalIndex = index + offset * 3
                            if (showAll) {
                                tableAll[finalIndex]
                            } else {
                                table[finalIndex]
                            }
                                .add(
                                    startTime + "\n" + name + "(考试)" + "\n" + place?.replace(
                                        "学堂",
                                        ""
                                    )
                                )
                        } else if (hour >= 18) {
                            val finalIndex = index + offset * 4
                            if (showAll) {
                                tableAll[finalIndex]
                            } else {
                                table[finalIndex]
                            }
                                .add(
                                    startTime + "\n" + name + "(考试)" + "\n" + place?.replace(
                                        "学堂",
                                        ""
                                    )
                                )
                        }
                    }
                } catch (e : Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    LaunchedEffect(showAll,loadingJxglstu,currentWeek,times,timeTable) {
        refreshUI()
    }

    if(refreshLogin) {
        val casCookies = CasInHFUT.casCookies
        val tgcCookie = prefs.getString("TGC", "")
        val nextBoolean = remember { isNextOpen() }

       LaunchedEffect(Unit) {
           // 如果已经加载过 跳过
           if(isEnabled) {
               loadingJxglstu = false
               return@LaunchedEffect
           }
           launch {
               if (nextBoolean) saveInt("FIRST", 1)
           }
           // 等待读取本地Cookie
           if(loadingJxglstu == false) return@LaunchedEffect
           val cookie = getJxglstuCookie()

           launch {
               onEnabled(false)
               val job = async(Dispatchers.IO) {
                   if(casCookies == null) {
                       showToast("异常 中止登录其他平台")
                       return@async
                   }
                   val cookies =  "$casCookies;$tgcCookie"
                   val useWebVpn = webVpn && !GlobalUIStateHolder.excludeJxglstu
                   // 智慧社区
                   launch community@ {
                       if(useWebVpn) {
                           return@community
                       }
                       val communityAuth = prefs.getString("TOKEN", "")
                       if(communityAuth == null || communityAuth.isEmpty()) {
                           loginCommunity(cookies,vm)
                       } else {
                           // 检测智慧社区可用性
                           vm.checkCommunityLogin(communityAuth)
                           val result = (vm.checkCommunityResponse.state.value as? UiState.Success)?.data
                           if(result == true) {
//                                   showToast("无需刷新智慧社区")
                               return@community
                           } else {
                               // 登录community
                               loginCommunity(cookies,vm)
                           }
                       }
                   }
                   // 慧新易校
                   launch huiXin@ {
                       //检测慧新易校可用性
                       val auth = prefs.getString("auth", "")
                       if(auth == null || auth.isEmpty()) {
                           vm.goToHuiXin(cookies)
                       } else {
                           vm.checkHuiXinLogin(auth)
                           val result = (vm.huiXinCheckLoginResp.state.value as? UiState.Success)?.data
                           if(result == true) {
//                                   showToast("无需刷新慧新易校")
                               return@huiXin
                           } else {
                               if(useWebVpn || GlobalUIStateHolder.excludeJxglstu) {
                                   loginHuiXin(vm)
                               } else {
                                   vm.goToHuiXin(cookies)
                               }
                           }
                       }
                   }
                   // 信息门户
                   launch one@ {
                       if(useWebVpn) {
                           return@one
                       }
                       val token = prefs.getString("bearer","")
                       if(token == null|| token.isEmpty()) {
                           loginOne(cookies,vm)
                       } else {
                           vm.checkOneLogin(token)
                           val result = (vm.checkOneLoginResp.state.value as? UiState.Success)?.data
                           if(result == true) {
//                                   showToast("无需刷新信息门户")
                               return@one
                           } else {
                               loginOne(cookies,vm)
                           }
                       }
                   }
                   // 学工系统
                   launch stu@ {
                       if(useWebVpn) {
                           return@stu
                       }
                       val auth = prefs.getString("stu", "")
                       if(auth == null || auth.isEmpty()) {
                           vm.goToStu(cookies)
                       } else {
                           // 检测学工系统可用性
                           vm.checkStuLogin(auth)
                           val result =  (vm.checkStuLoginResp.state.value as? UiState.Success)?.data
                           if(result == true) {
//                               showToast("无需刷新学工平台")
                               return@stu
                           } else {
                               // 登录
                               vm.goToStu(cookies)
                           }
                       }
                   }
                   // 图书馆
                   launch library@ {
                       if(useWebVpn) {
                           return@library
                       }
                       val auth = prefs.getString(LIBRARY_TOKEN, "")
                       if(auth == null || auth.isEmpty()) {
                           vm.gotoLibrary(cookies)
                       } else {
                           // 检测可用性
                           vm.checkLibraryLogin(auth)
                           val result =  (vm.checkLibraryLoginResp.state.value as? UiState.Success)?.data
                           if(result == true) {
//                               showToast("无需刷新图书馆")
                               return@library
                           } else {
                               // 登录
                               vm.gotoLibrary(cookies)
                           }
                       }
                   }
                   // 指间工大
                   launch zhiJian@ {
                       if(useWebVpn) {
                           return@zhiJian
                       }
                       val auth = prefs.getString("ZhiJian", "")
                       if(auth == null || auth.isEmpty()) {
                           vm.gotoZhiJian(cookies)
                       } else {
                           // 检测可用性
                           vm.zhiJianCheckLogin(auth)
                           val result = (vm.zhiJianCheckLoginResp.state.value as? UiState.Success)?.data
                           if(result == true) {
                               return@zhiJian
                           } else {
//                                登录
                               vm.gotoZhiJian(cookies)
                           }
                       }
                   }
                   // 体测平台
                   launch pe@ {
                       if(useWebVpn) {
                           return@pe
                       }
                       val auth = prefs.getString("PE", "")
                       if(auth == null || auth.isEmpty()) {
                           vm.goToPe(cookies)
                       } else {
                           // 检测可用性
                           vm.checkPeLogin(auth)
                           val result = (vm.checkPeLoginResp.state.value as? UiState.Success)?.data
                           if(result == true) {
                               return@pe
                           } else {
//                                登录
                               vm.goToPe(cookies)
                           }
                       }
                   }
               }
               // 超时10s
               withTimeoutOrNull(10000) {
                   job.await()
               }
               if(GlobalUIStateHolder.excludeJxglstu) {
                   loadingJxglstu = false
               }
               onEnabled(true)
           }
           // 教务系统
           launch(Dispatchers.IO) jxglstu@ {
               if(GlobalUIStateHolder.excludeJxglstu) {
                   return@jxglstu
               }
               cookie?: return@jxglstu
               vm.getStudentId(cookie)
               val studentId = (vm.studentId.state.value as? UiState.Success)?.data
               if(studentId == null) {
                   showToast("获取studentId失败 教务登录中止")
                   loadingJxglstu = false
                   return@jxglstu
               }
               launch { vm.getInfo(cookie) }
               launch {
                   if (LargeStringDataManager.read(context, LargeStringDataManager.PHOTO) == null) {
                       vm.getPhoto(cookie)
                   }
               }
               vm.getBizTypeId(cookie,studentId)
               val bizTypeId = (vm.bizTypeIdResponse.state.value as? UiState.Success)?.data
               if(bizTypeId == null) {
                   showToast("获取bizTypeId失败 教务登录中止")
                   loadingJxglstu = false
                   return@jxglstu
               }
               launch {
                   vm.getLessonIds(cookie, studentId = studentId, bizTypeId = bizTypeId)
                   val lessonResponse = (vm.lessonIds.state.value as? UiState.Success)?.data ?: return@launch
                   vm.getLessonTimes(cookie,lessonResponse.timeTableLayoutId)
                   vm.getDatum(cookie,lessonResponse.lessonIds)
                   val datum = (vm.datumData.state.value as? UiState.Success)?.data
                   if(datum == null) {
                       showToast("数据为空,尝试刷新")
                   }
                   loadingJxglstu = false
               }

               launch {
                   if(nextBoolean) {
                       vm.getLessonIdsNext(cookie, studentId = studentId, bizTypeId = bizTypeId)
                       val lessonResponse = (vm.lessonIdsNext.state.value as? UiState.Success)?.data ?: return@launch
                       vm.getLessonTimesNext(cookie,lessonResponse.timeTableLayoutId)
                       vm.getDatumNext(cookie,lessonResponse.lessonIds)
                   }
               }
           }
       }
    }
    val enableLiquidGlass by DataStoreManager.enableLiquidGlass.collectAsState(initial = AppVersion.CAN_SHADER)
    val customBackgroundAlpha by DataStoreManager.customCalendarSquareAlpha.collectAsState(initial = 1f)
    val enableTransition = !(backGroundHaze != null && AppVersion.CAN_SHADER)
    if(loadingJxglstu) {
        CenterScreen {
            LoadingUI(if(webVpn) "请等待 WebVpn延迟有时比较高" else null)
        }
    } else {
        // 课程表布局
        Box(modifier = Modifier.fillMaxSize()) {
            val scrollState = rememberLazyGridState()
            val shouldShowAddButton by remember { derivedStateOf { scrollState.firstVisibleItemScrollOffset == 0 } }
            val style = CalendarStyle(showAll)
            val color =  if(enableTransition) style.containerColor.copy(customBackgroundAlpha) else Color.Transparent
            LazyVerticalGrid(
                columns = GridCells.Fixed(style.rowCount),
                modifier = style.calendarPadding(),
                state = scrollState
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) { InnerPaddingHeight(innerPadding,true) }
                exception?.let {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        TransplantListItem(
                            headlineContent = { Text("解析错误") },
                            leadingContent = { Icon(painterResource(R.drawable.warning),null)},
                            supportingContent = { Text(getKeyStackTrace(it)) }
                        )
                    }
                }
                items(style.rowCount*style.columnCount, key = { it }) { index ->
                    val texts = if(showAll)tableAll[index].toMutableList() else table[index].toMutableList()
                    if(texts.isEmpty() && enableHideEmptyCalendarSquare) {
                        Box(modifier = Modifier
                            .height(style.height)
                            .padding(style.everyPadding))
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
                                    // 只有一节课
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
                                        val name =
                                            parseCourseName(if (showAll) tableAll[index][0] else table[index][0])
                                        if (name != null) {
                                            navController.navigateForTransition(
                                                AppNavRoute.CourseDetail,
                                                AppNavRoute.CourseDetail.withArgs(name, CourseDetailOrigin.CALENDAR_JXGLSTU.t + "$index")
                                            )
                                        }
                                    } else if (texts.size > 1) {
                                        multiWeekday =
                                            if (showAll) (index + 1) % 7 else (index + 1) % 5
                                        multiWeek = currentWeek.toInt()
                                        courses = texts
                                        showBottomSheetMultiCourse = true
                                    }
                                    // 空数据
                                }
                                .let {
                                    if(enableTransition) {
                                        val route = if(texts.size == 1 && !texts[0].contains("考试")) {
                                            val name = parseCourseName(if (showAll) tableAll[index][0] else table[index][0])
                                            if (name != null) {
                                                AppNavRoute.CourseDetail.withArgs(name, CourseDetailOrigin.CALENDAR_JXGLSTU.t + "$index")
                                            } else {
                                                null
                                            }
                                        } else {
                                            null
                                        }

                                        route?.let { it1 ->
                                            it.containerShare(
                                                route = it1,
                                                roundShape = MaterialTheme.shapes.extraSmall,
                                            )
                                        } ?: it
                                    } else {
                                        it
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
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = CARD_NORMAL_DP) ,
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
                            } else if(texts.size > 1){
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
                item(span = { GridItemSpan(maxLineSpan) }) {  InnerPaddingHeight(innerPadding,false) }
            }
            // 上一周
            AnimatedVisibility(
                visible = shouldShowAddButton,
                enter = scaleIn(),
                exit = scaleOut(),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = innerPadding.calculateBottomPadding() - navigationBarHeightPadding)
                    .padding(APP_HORIZONTAL_DP,)
            ) {
                FloatingActionButton(
                    onClick = {
                        if (currentWeek > 1) {
                            currentWeek-- - 1
                            onDateChange(today.minusDays(7))
                        }
                    },
                ) { Icon(Icons.Filled.ArrowBack, "Add Button") }
            }
            // 中间
            AnimatedVisibility(
                visible = shouldShowAddButton,
                enter = scaleIn(),
                exit = scaleOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = innerPadding.calculateBottomPadding() - navigationBarHeightPadding)
                    .padding(APP_HORIZONTAL_DP,)
            ) {
                ExtendedFloatingActionButton(
                    onClick = {
                        if(DateTimeManager.weeksBetweenJxglstu < 1) {
                            currentWeek = 1
                            onDateChange(getJxglstuStartDate())
                        } else {
                            currentWeek = DateTimeManager.weeksBetweenJxglstu
                            onDateChange(LocalDate.now())
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
            // 下一周
            AnimatedVisibility(
                visible = shouldShowAddButton,
                enter = scaleIn(),
                exit = scaleOut(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = innerPadding.calculateBottomPadding() - navigationBarHeightPadding)
                    .padding(APP_HORIZONTAL_DP,)
            ) {
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

fun getNewWeek() : Long {
    return try {
        val jxglstuJson = prefs.getString("courses","")
        val resultJxglstu = getTotalCourse(jxglstuJson)[0].semester.startDate
        val firstWeekStartJxglstu: LocalDate = LocalDate.parse(resultJxglstu)
        val weeksBetweenJxglstu = ChronoUnit.WEEKS.between(firstWeekStartJxglstu, DateTimeManager.getToday()) + 1
        weeksBetweenJxglstu  //固定本周
    } catch (_ : Exception) {
        DateTimeManager.weeksBetweenJxglstu
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiCourseSheetUI(week : Int, weekday : Int, courses : List<courseDetailDTOList>, vm: NetWorkViewModel, hazeState: HazeState,friendUserName : String?) {
    var sheet by remember { mutableStateOf(courseDetailDTOList(0,0,"","","", listOf(0),0,"","")) }

    var showBottomSheetTotalCourse by remember { mutableStateOf(false) }
    if (showBottomSheetTotalCourse) {
        HazeBottomSheet(
            onDismissRequest = { showBottomSheetTotalCourse = false },
            showBottomSheet = showBottomSheetTotalCourse,
            hazeState = hazeState,
            autoShape = false
        ) {
            HazeBottomSheetTopBar(sheet.name, isPaddingStatusBar = false)
            DetailInfos(sheet,friendUserName != null, vm = vm, hazeState )
        }
    }
    Column {
        HazeBottomSheetTopBar("第${week}周 周${numToChinese(weekday)}", isPaddingStatusBar = false)
        LargeCard(
            title = "${courses.size}节课冲突"
        ) {
            for(index in courses.indices) {
                val course = courses[index]
                val startTime = course.classTime.substringBefore("-")
                val name = course.name
                val place = course.place
                TransplantListItem(
                    headlineContent = {
                        Text(name)
                    },
                    supportingContent = {
                        Text("${place?.replace("学堂","")} $startTime")
                    },
                    leadingContent = {
                        Text((index+1).toString())
                    },
                    colors =  if(name.contains("考试")) MaterialTheme.colorScheme.errorContainer else null,
                    modifier = Modifier.clickable {
                        // 如果是考试
                        if(name.contains("考试")) {
                            return@clickable
                        }
                        sheet = course
                        showBottomSheetTotalCourse = true
                    }
                )
            }
        }
        Spacer(Modifier.height(40.dp))
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiCourseSheetUI(week : Int, weekday : Int, courses : List<String>, vm: NetWorkViewModel, hazeState: HazeState) {
    var courseName by remember { mutableStateOf("") }
    var showBottomSheetTotalCourse by remember { mutableStateOf(false) }
    if (showBottomSheetTotalCourse) {
        HazeBottomSheet (
            onDismissRequest = {
                showBottomSheetTotalCourse = false
            },
            hazeState = hazeState,
            showBottomSheet = showBottomSheetTotalCourse
        ) {
            CourseDetailApi(courseName = courseName, vm = vm, hazeState = hazeState)
        }
    }
    Column {
        HazeBottomSheetTopBar("第${week}周 周${numToChinese(weekday)}", isPaddingStatusBar = false)
        LargeCard(
            title = "${courses.size}节课冲突"
        ) {
            for(index in courses.indices) {
                val course = courses[index]
                val list = course.split("\n")
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
                        Text("${place?.replace("学堂","")} $startTime")
                    },
                    leadingContent = {
                        Text((index+1).toString())
                    },
                    colors =  if(name.contains("考试")) MaterialTheme.colorScheme.errorContainer else null,
                    modifier = Modifier.clickable {
                        // 如果是考试
                        if(name.contains("考试")) {
                            return@clickable
                        }
                        if(name.contains("日程")) {
                            return@clickable
                        }
                        courseName = name
                        showBottomSheetTotalCourse = true
                    }
                )
            }
        }
        Spacer(Modifier.height(40.dp))
    }

}

fun numToChinese(num : Int) : String {
    return when(num) {
        1 -> "一"
        2 -> "二"
        3 -> "三"
        4 -> "四"
        5 -> "五"
        6 -> "六"
        7 -> "日"
        else -> ""
    }
}


// 传入YYYY-MM-DD 返回当前第几周周几
fun dateToWeek(date : String) : Pair<Int,Int>? {
    try {
        // 第一周的开始日期 为周一  LocalDate
        val start = getJxglstuStartDate()
        val target = LocalDate.parse(date)

        val days = ChronoUnit.DAYS.between(start, target)
        return if (days < 0) {
            // 目标日期早于学期开始
            null
        } else {
            val week = (days / 7 + 1).toInt()   // 第几周（从1开始）
            val dayOfWeek = ((days % 7) + 1).toInt() // 周几（1=周一，7=周日）
            Pair(week, dayOfWeek)
        }
    } catch (e : Exception) {
        e.printStackTrace()
        return null
    }
}
// 反函数
fun weekToDate(week : Int,weekday : Int) : String? {
    try {
        if (week < 1 || weekday !in 1..7) return null

        val start = getJxglstuStartDate()
        val daysToAdd = (week - 1) * 7L + (weekday - 1)
        val target = start.plusDays(daysToAdd)
        return target.toString() // 返回 "YYYY-MM-DD"
    } catch (e : Exception) {
        e.printStackTrace()
        return null
    }
}

fun Modifier.calendarSquareGlass(
    state : ShaderState,
    color : Color,
    enabled : Boolean,
) : Modifier =
    if(enabled && !GlobalUIStateHolder.isTransiting) {
        this.glassLayer(
            state,
            style = GlassStyle(
                blur = 3.5.dp,
                border = 30f,
                dispersion = 0f,
                distortFactor = 0.1f,
                overlayColor = color
            ),
        )
    } else{
        this.background(color)
    }