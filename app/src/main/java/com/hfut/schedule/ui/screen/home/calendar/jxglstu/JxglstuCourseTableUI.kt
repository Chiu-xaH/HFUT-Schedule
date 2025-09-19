package com.hfut.schedule.ui.screen.home.calendar.jxglstu

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.community.courseDetailDTOList
import com.hfut.schedule.logic.model.jxglstu.CourseUnitBean
import com.hfut.schedule.logic.model.jxglstu.DatumResponse
import com.hfut.schedule.logic.model.jxglstu.LessonTimesResponse
import com.hfut.schedule.logic.network.interceptor.CasGoToInterceptorState
import com.hfut.schedule.logic.network.util.isNotBadRequest
import com.hfut.schedule.logic.util.development.getKeyStackTrace
import com.hfut.schedule.logic.network.util.MyApiParse.isNextOpen
import com.hfut.schedule.logic.network.util.CasInHFUT
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.SemseterParser
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveInt
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.LargeCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.calendar.communtiy.CourseDetailApi
import com.hfut.schedule.ui.screen.home.calendar.communtiy.DetailInfos
import com.hfut.schedule.ui.screen.home.calendar.examToCalendar
import com.hfut.schedule.ui.screen.home.calendar.getScheduleDate
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.next.CourseDetailOrigin
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.next.parseCourseName
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
import com.xah.transition.component.containerShare
import com.xah.uicommon.component.status.LoadingUI
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.ClickScale
import com.xah.uicommon.style.align.CenterScreen
import com.xah.uicommon.style.clickableWithScale
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.xah.uicommon.style.padding.navigationBarHeightPadding
import dev.chrisbanes.haze.HazeState
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
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    backGroundHaze : HazeState?,
    isEnabled : Boolean,
    onEnabled : (Boolean) -> Unit
) {
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

    val dateList  = getScheduleDate(showAll, today)
    var examList by rememberSaveable { mutableStateOf(examToCalendar()) }

    var currentWeek by rememberSaveable {
        mutableLongStateOf(
            if(DateTimeManager.weeksBetweenJxglstu > 20) {
                getNewWeek()
            } else if(DateTimeManager.weeksBetweenJxglstu < 1) {
                onDateChange(getJxglstuStartDate())
                examList = emptyList()
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

    var exception by remember { mutableStateOf<Exception?>(null) }

    fun refreshUI() {
        exception = null
        // 清空
        if(showAll) {
            clearUnit(tableAll)
        } else {
            clearUnit(table)
        }
        Handler(Looper.getMainLooper()).post { vmUI.findNewCourse.value = false }

        try {
            // 组装
            val json = prefs.getString("json", "")
            if(json == null || json.isBlank() == true || json.isEmpty() == true) {
                return
            }
            val datumResponse = Gson().fromJson(json, DatumResponse::class.java)
            val scheduleList = datumResponse.result.scheduleList
            val lessonList = datumResponse.result.lessonList

            for (i in scheduleList.indices) {
                val item = scheduleList[i]
                val startTime = item.startTime
                val startHour = startTime / 100
                val startMinute = startTime % 100
                val startTimeStr = "$startHour:${startMinute.let { if(it < 10) "0${it}" else it }}"

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
            showToast("解析出错")
            exception = e
        }
    }

    LaunchedEffect(showAll,loadingJxglstu,currentWeek,times,timeTable) {
        if(timeTable.isEmpty()) {
            return@LaunchedEffect
        }
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

           // 信息门户 慧新易校 智慧社区
           launch  {
               onEnabled(false)
               val job = async {
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
                       val auth = prefs.getString("LibraryToken", "")
                       if(auth == null || auth.isEmpty()) {
                           vm.gotoLibrary(cookies)
                       } else {
                           // 检测可用性
                           vm.checkLibraryLogin(auth)
                           val result =  (vm.checkLibraryLoginResp.state.value as? UiState.Success)?.data
                           if(result == true) {
//                               showToast("无需刷新学工平台")
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
               }
               withTimeoutOrNull(10000) { // 超时时间 10s
                   job.await()
               }
               if(GlobalUIStateHolder.excludeJxglstu) {
                   loadingJxglstu = false
               }
               onEnabled(true)
           }

           // 教务系统
           launch jxglstu@ {
               if(GlobalUIStateHolder.excludeJxglstu) {
                   return@jxglstu
               }
               cookie?: return@jxglstu
               vm.getStudentId(cookie)
               val studentId = (vm.studentId.state.value as? UiState.Success)?.data ?: return@jxglstu
               launch { vm.getInfo(cookie) }
               launch {
                   if (prefs.getString("photo", "") == null || prefs.getString("photo", "") == "")
                       vm.getPhoto(cookie)
               }
               vm.getBizTypeId(cookie,studentId)
               val bizTypeId = (vm.bizTypeIdResponse.state.value as? UiState.Success)?.data ?: return@jxglstu
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

    if(loadingJxglstu) {
        CenterScreen {
            LoadingUI(if(webVpn) "请等待,若加载超过10s则为超时,可重新登录" else null)
        }
    } else {
        // 课程表布局
        Box(modifier = Modifier.fillMaxSize()) {
            val scrollState = rememberLazyGridState()
            val shouldShowAddButton by remember { derivedStateOf { scrollState.firstVisibleItemScrollOffset == 0 } }
            val style = CalendarStyle(showAll)

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
                            colors = CardDefaults.cardColors(containerColor = if(backGroundHaze != null) Color.Transparent else style.containerColor),
                            modifier = Modifier
                                .height(style.height)
                                .padding(style.everyPadding)
                                .let {
                                    backGroundHaze?.let { haze ->
                                        it
                                            .clip(style.containerCorner)
                                            .containerBlur(haze, style.containerColor)
                                    } ?: it
                                }
                                .clickableWithScale(ClickScale.SMALL.scale) {
                                    // 只有一节课
                                    if (texts.size == 1) {
                                        // 如果是考试
                                        if (texts[0].contains("考试")) {
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
                                            sharedTransitionScope,
                                            animatedContentScope,
                                            route = it1,
                                            roundShape = MaterialTheme.shapes.extraSmall,
                                        )
                                    } ?: it
                                }
                        ) {
                            //存在待考时
                            if(examList.isNotEmpty()){
                                val numa = if(showAll) 7 else 5
                                val i = index % numa
                                val j = index / numa
                                val date = dateList[i]
                                examList.forEach {
                                    if(date == it.day) {
                                        val hour = it.startTime?.substringBefore(":")?.toIntOrNull() ?: 99

                                        if(hour in 7..9 && j == 0) {
                                            texts.add(it.startTime + "\n" + it.course  + "(考试)"+ "\n" + it.place?.replace("学堂",""))
                                        } else if(hour in 10..12 && j == 1) {
                                            texts.add(it.startTime + "\n" + it.course + "(考试)" + "\n" + it.place?.replace("学堂",""))
                                        } else if(hour in 14..15  && j == 2) {
                                            texts.add(it.startTime + "\n" + it.course  + "(考试)"+ "\n" + it.place?.replace("学堂",""))
                                        } else if(hour in 16..17  && j == 3) {
                                            texts.add(it.startTime + "\n" + it.course  + "(考试)"+ "\n" + it.place?.replace("学堂",""))
                                        } else if(hour >= 18  && j == 4) {
                                            texts.add(it.startTime + "\n" + it.course  + "(考试)"+ "\n" + it.place?.replace("学堂",""))
                                        }
                                    }
                                }
                            }
                            if(texts.size == 1) {
                                val l = texts[0].split("\n")
                                val time = l[0]
                                val name = l[1]
                                val place = l[2]
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
                                    Text(
                                        text = place,
                                        fontSize = style.textSize,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
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
