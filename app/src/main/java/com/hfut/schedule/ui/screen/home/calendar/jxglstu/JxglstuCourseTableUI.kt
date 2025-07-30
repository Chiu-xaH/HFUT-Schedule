package com.hfut.schedule.ui.screen.home.calendar.jxglstu

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.model.community.LoginCommunityResponse
import com.hfut.schedule.logic.model.community.courseDetailDTOList
import com.hfut.schedule.logic.model.jxglstu.CourseUnitBean
import com.hfut.schedule.logic.model.jxglstu.LessonTimesResponse
import com.hfut.schedule.logic.model.jxglstu.datumResponse
import com.hfut.schedule.logic.util.network.ParseJsons.isNextOpen
import com.hfut.schedule.logic.util.network.state.CasInHFUT
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.SemseterParser
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveInt
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.LargeCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.status.LoadingUI
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.calendar.communtiy.CourseDetailApi
import com.hfut.schedule.ui.screen.home.calendar.communtiy.DetailInfos
import com.hfut.schedule.ui.screen.home.calendar.examToCalendar
import com.hfut.schedule.ui.screen.home.calendar.getScheduleDate
import com.hfut.schedule.ui.screen.home.calendar.next.parseCourseName
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getJxglstuStartDate
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getTotalCourse
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.InnerPaddingHeight
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.xah.transition.component.containerShare
import com.xah.transition.util.navigateAndSaveForTransition
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

suspend fun parseTimeTable(json : String, isNext : Boolean = false) : List<CourseUnitBean> {
    try {
        if(json.isEmpty()) {
            // 使用预置的作息表
            val campus = getPersonInfo().school ?: return emptyList()
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
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun JxglstuCourseTableUI(
    showAll: Boolean,
    vm: NetWorkViewModel,
    innerPadding: PaddingValues,
    vmUI: UIViewModel,
    webVpn: Boolean,
    load: Boolean,
    onDateChange: (LocalDate) ->Unit,
    today: LocalDate,
    hazeState: HazeState,
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
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

    var loading by rememberSaveable { mutableStateOf(load) }

    val table = rememberSaveable { List(30) { mutableStateListOf<String>() } }
    val tableAll = rememberSaveable { List(42) { mutableStateListOf<String>() } }

    val dateList  =  getScheduleDate(showAll, today)
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

    val times by DataStoreManager.courseTableTime.collectAsState(initial = "")
    var timeTable by rememberSaveable { mutableStateOf(emptyList<CourseUnitBean>()) }
    LaunchedEffect(times) {
        timeTable = parseTimeTable(times)
    }

    fun refreshUI(showAll : Boolean,timeList : List<CourseUnitBean>) {
        // 清空
        if(showAll) {
            clearUnit(tableAll)
        } else {
            clearUnit(table)
        }

        try {
            // 组装
            val json = prefs.getString("json", "")
            val datumResponse = Gson().fromJson(json, datumResponse::class.java)
            val scheduleList = datumResponse.result.scheduleList
            val lessonList = datumResponse.result.lessonList

            for (i in scheduleList.indices) {
                val item = scheduleList[i]
                var startTime = item.startTime.toString()
                startTime =
                    startTime.substring(0, startTime.length - 2) + ":" + startTime.substring(
                        startTime.length - 2
                    )
                var room = item.room.nameZh
                var courseId = item.lessonId.toString()
                room = room.replace("学堂","")


                for (j in lessonList.indices) {
                    if (courseId == lessonList[j].id) {
                        courseId = lessonList[j].courseName
                    }
                }

                val text = startTime + "\n" + courseId + "\n" + room

                if (item.weekIndex == currentWeek.toInt()) {
                    when(item.weekday) {
                        6 -> { Handler(Looper.getMainLooper()).post { vmUI.findNewCourse.value = text.isNotEmpty() } }
                        7 -> { Handler(Looper.getMainLooper()).post { vmUI.findNewCourse.value = text.isNotEmpty() } }
                    }
                    if(showAll) {
                        if (item.weekday == 1) {
                            if (item.startTime == timeList[0].startTime || item.startTime == timeList[1].startTime) {
                                tableAll[0].add(text)
                            }
                            if (item.startTime == timeList[2].startTime || item.startTime == timeList[3].startTime) {
                                tableAll[7].add(text)
                            }
                            if (item.startTime == timeList[4].startTime || item.startTime == timeList[5].startTime) {
                                tableAll[14].add(text)
                            }
                            if (item.startTime == timeList[6].startTime || item.startTime == timeList[7].startTime) {
                                tableAll[21].add(text)
                            }
                            if (item.startTime == timeList[8].startTime || item.startTime == timeList[9].startTime || item.startTime == timeList[10].startTime) {
                                tableAll[28].add(text)
                            }
                        }
                        if (item.weekday == 2) {
                            if (item.startTime == timeList[0].startTime || item.startTime == timeList[1].startTime) {
                                tableAll[1].add(text)
                            }
                            if (item.startTime == timeList[2].startTime || item.startTime == timeList[3].startTime) {
                                tableAll[8].add(text)
                            }
                            if (item.startTime == timeList[4].startTime || item.startTime == timeList[5].startTime) {
                                tableAll[15].add(text)
                            }
                            if (item.startTime == timeList[6].startTime || item.startTime == timeList[7].startTime) {
                                tableAll[22].add(text)
                            }
                            if (item.startTime == timeList[8].startTime || item.startTime == timeList[9].startTime || item.startTime == timeList[10].startTime) {
                                tableAll[29].add(text)
                            }
                        }
                        if (item.weekday == 3) {
                            if (item.startTime == timeList[0].startTime || item.startTime == timeList[1].startTime) {
                                tableAll[2].add(text)
                            }
                            if (item.startTime == timeList[2].startTime || item.startTime == timeList[3].startTime) {
                                tableAll[9].add(text)
                            }
                            if (item.startTime == timeList[4].startTime || item.startTime == timeList[5].startTime) {
                                tableAll[16].add(text)
                            }
                            if (item.startTime == timeList[6].startTime || item.startTime == timeList[7].startTime) {
                                tableAll[23].add(text)
                            }
                            if (item.startTime == timeList[8].startTime || item.startTime == timeList[9].startTime || item.startTime == timeList[10].startTime) {
                                tableAll[30].add(text)
                            }
                        }
                        if (item.weekday == 4) {
                            if (item.startTime == timeList[0].startTime || item.startTime == timeList[1].startTime) {
                                tableAll[3].add(text)
                            }
                            if (item.startTime == timeList[2].startTime || item.startTime == timeList[3].startTime) {
                                tableAll[10].add(text)
                            }
                            if (item.startTime == timeList[4].startTime || item.startTime == timeList[5].startTime) {
                                tableAll[17].add(text)
                            }
                            if (item.startTime == timeList[6].startTime || item.startTime == timeList[7].startTime) {
                                tableAll[24].add(text)
                            }
                            if (item.startTime == timeList[8].startTime || item.startTime == timeList[9].startTime || item.startTime == timeList[10].startTime) {
                                tableAll[31].add(text)
                            }
                        }
                        if (item.weekday == 5) {
                            if (item.startTime == timeList[0].startTime || item.startTime == timeList[1].startTime) {
                                tableAll[4].add(text)
                            }
                            if (item.startTime == timeList[2].startTime || item.startTime == timeList[3].startTime) {
                                tableAll[11].add(text)
                            }
                            if (item.startTime == timeList[4].startTime || item.startTime == timeList[5].startTime) {
                                tableAll[18].add(text)
                            }
                            if (item.startTime == timeList[6].startTime || item.startTime == timeList[7].startTime) {
                                tableAll[25].add(text)
                            }
                            if (item.startTime == timeList[8].startTime || item.startTime == timeList[9].startTime || item.startTime == timeList[10].startTime) {
                                tableAll[32].add(text)
                            }
                        }
                        if (item.weekday == 6) {
                            if (item.startTime == timeList[0].startTime || item.startTime == timeList[1].startTime) {
                                tableAll[5].add(text)
                            }
                            if (item.startTime == timeList[2].startTime || item.startTime == timeList[3].startTime) {
                                tableAll[12].add(text)
                            }
                            if (item.startTime == timeList[4].startTime || item.startTime == timeList[5].startTime) {
                                tableAll[19].add(text)
                            }
                            if (item.startTime == timeList[6].startTime || item.startTime == timeList[7].startTime) {
                                tableAll[26].add(text)
                            }
                            if (item.startTime == timeList[8].startTime || item.startTime == timeList[9].startTime || item.startTime == timeList[10].startTime) {
                                tableAll[33].add(text)
                            }
                        }
                        if (item.weekday == 7) {
                            if (item.startTime == timeList[0].startTime || item.startTime == timeList[1].startTime) {
                                tableAll[6].add(text)
                            }
                            if (item.startTime == timeList[2].startTime || item.startTime == timeList[3].startTime) {
                                tableAll[13].add(text)
                            }
                            if (item.startTime == timeList[4].startTime || item.startTime == timeList[5].startTime) {
                                tableAll[20].add(text)
                            }
                            if (item.startTime == timeList[6].startTime || item.startTime == timeList[7].startTime) {
                                tableAll[27].add(text)
                            }
                            if (item.startTime == timeList[8].startTime || item.startTime == timeList[9].startTime || item.startTime == timeList[10].startTime) {
                                tableAll[34].add(text)
                            }
                        }
                    } else {
                        if (item.weekday == 1) {
                            if (item.startTime == timeList[0].startTime || item.startTime == timeList[1].startTime) {
                                table[0].add(text)
                            }
                            if (item.startTime == timeList[2].startTime || item.startTime == timeList[3].startTime) {
                                table[5].add(text)
                            }
                            if (item.startTime == timeList[4].startTime || item.startTime == timeList[5].startTime) {
                                table[10].add(text)
                            }
                            if (item.startTime == timeList[6].startTime || item.startTime == timeList[7].startTime) {
                                table[15].add(text)
                            }
                            if (item.startTime == timeList[8].startTime || item.startTime == timeList[9].startTime || item.startTime == timeList[10].startTime) {
                                table[20].add(text)
                            }
                        }
                        if (item.weekday == 2) {
                            if (item.startTime == timeList[0].startTime || item.startTime == timeList[1].startTime) {
                                table[1].add(text)
                            }
                            if (item.startTime == timeList[2].startTime || item.startTime == timeList[3].startTime) {
                                table[6].add(text)
                            }
                            if (item.startTime == timeList[4].startTime || item.startTime == timeList[5].startTime) {
                                table[11].add(text)
                            }
                            if (item.startTime == timeList[6].startTime || item.startTime == timeList[7].startTime) {
                                table[16].add(text)
                            }
                            if (item.startTime == timeList[8].startTime || item.startTime == timeList[9].startTime || item.startTime == timeList[10].startTime) {
                                table[21].add(text)
                            }
                        }
                        if (item.weekday == 3) {
                            if (item.startTime == timeList[0].startTime || item.startTime == timeList[1].startTime) {
                                table[2].add(text)
                            }
                            if (item.startTime == timeList[2].startTime || item.startTime == timeList[3].startTime) {
                                table[7].add(text)
                            }
                            if (item.startTime == timeList[4].startTime || item.startTime == timeList[5].startTime) {
                                table[12].add(text)
                            }
                            if (item.startTime == timeList[6].startTime || item.startTime == timeList[7].startTime) {
                                table[17].add(text)
                            }
                            if (item.startTime == timeList[8].startTime || item.startTime == timeList[9].startTime || item.startTime == timeList[10].startTime) {
                                table[22].add(text)
                            }
                        }
                        if (item.weekday == 4) {
                            if (item.startTime == timeList[0].startTime || item.startTime == timeList[1].startTime) {
                                table[3].add(text)
                            }
                            if (item.startTime == timeList[2].startTime || item.startTime == timeList[3].startTime) {
                                table[8].add(text)
                            }
                            if (item.startTime == timeList[4].startTime || item.startTime == timeList[5].startTime) {
                                table[13].add(text)
                            }
                            if (item.startTime == timeList[6].startTime || item.startTime == timeList[7].startTime) {
                                table[18].add(text)
                            }
                            if (item.startTime == timeList[8].startTime || item.startTime == timeList[9].startTime || item.startTime == timeList[10].startTime) {
                                table[23].add(text)
                            }
                        }
                        if (item.weekday == 5) {
                            if (item.startTime == timeList[0].startTime || item.startTime == timeList[1].startTime) {
                                table[4].add(text)
                            }
                            if (item.startTime == timeList[2].startTime || item.startTime == timeList[3].startTime) {
                                table[9].add(text)
                            }
                            if (item.startTime == timeList[4].startTime || item.startTime == timeList[5].startTime) {
                                table[14].add(text)
                            }
                            if (item.startTime == timeList[6].startTime || item.startTime == timeList[7].startTime) {
                                table[19].add(text)
                            }
                            if (item.startTime == timeList[8].startTime || item.startTime == timeList[9].startTime || item.startTime == timeList[10].startTime) {
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
            e.printStackTrace()
        }
    }

    LaunchedEffect(showAll,loading,currentWeek,times) {
        refreshUI(showAll,timeTable)
    }


//////////////////////////////////////////////////////////////////////////////////
   if(load) {
        var num2 = 1
        val ONE = CasInHFUT.casCookies
        val TGC = prefs.getString("TGC", "")
        val cookies = "$ONE;$TGC"
        val ticket = prefs.getString("TICKET", "")
        val CommuityTOKEN = prefs.getString("TOKEN", "")
        var a by rememberSaveable { mutableIntStateOf(0) }
        val job = Job()
        val job2 = Job()
        val nextBoolean = isNextOpen()


       //检测若登陆成功（200）则解析出CommunityTOKEN
       val loginCommunityObserver = Observer<String?> { result ->
           if (result != null) {
               if (result.contains("200") && result.contains("token")) {
                   try {
                       val tokens = Gson().fromJson(result, LoginCommunityResponse::class.java).result.token
                       SharedPrefs.saveString("TOKEN", tokens)
                       if (num2 == 1) {
                           showToast("Community登陆成功")
                           num2++
                       }
                   }catch (_:Exception) {}
               }
           }
       }

       //检测CommunityTOKEN的可用性
       val examObserver = Observer<Int> { result ->
           if (result == 500) {
               CoroutineScope(Job()).launch {
                   async { vm.gotoCommunity(cookies) }.await()
                   async {
                       delay(1000)
                       ticket?.let { vm.loginCommunity(it) }
                   }.await()
                   launch {
                       Handler(Looper.getMainLooper()).post {
                           vm.loginCommunityData.observeForever(loginCommunityObserver)
                       }
                   }
               }
           }
       }

        if(!webVpn) {
//            val token = prefs.getString("bearer", "")
            //检测慧新易校可用性
            if (prefs.getString("auth", "") == "") vm.goToHuiXin(cookies)
           CoroutineScope(job2).launch {
               async { vm.goToHuiXin(cookies) }
               async { CommuityTOKEN?.let { vm.getExamFromCommunity(it) } }

               Handler(Looper.getMainLooper()).post { vm.examCodeFromCommunityResponse.observeForever(examObserver) }

               //登录信息门户的接口,还没做重构（懒）
               async {
                   async { vm.goToOne(cookies) }.await()
                   async {
                       delay(500)
                       vm.getToken()
                   }.await()
               }
//               if (token != null) {
//                   if (token.contains("AT") && cardvalue != "未获取") {
//                   } else {
//
//                   }
//               }
           }
       }

        if (nextBoolean) saveInt("FIRST", 1)

       LaunchedEffect(Unit) {
           val cookie = getJxglstuCookie(vm)
           // 等待读取本地Cookie
           if(loading == false) return@LaunchedEffect
           cookie?: return@LaunchedEffect
           vm.getStudentId(cookie)
           val studentId = (vm.studentId.state.value as? UiState.Success)?.data ?: return@LaunchedEffect
           launch { vm.getInfo(cookie) }
           launch {
               if (prefs.getString("photo", "") == null || prefs.getString("photo", "") == "")
                   vm.getPhoto(cookie)
           }
           vm.getBizTypeId(cookie,studentId)
           val bizTypeId = (vm.bizTypeIdResponse.state.value as? UiState.Success)?.data ?: return@LaunchedEffect
           launch {
               vm.getLessonIds(cookie, studentId = studentId, bizTypeId = bizTypeId)
               val lessonResponse = (vm.lessonIds.state.value as? UiState.Success)?.data ?: return@launch
               vm.getLessonTimes(cookie,lessonResponse.timeTableLayoutId)
               vm.getDatum(cookie,lessonResponse.lessonIds)
               val datum = (vm.datumData.state.value as? UiState.Success)?.data
               if(datum == null) {
                   showToast("数据为空,尝试刷新")
                   return@launch
               }
               async {
                   delay(200)
                   a++
                   loading = false
               }
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
//        scope.launch {
//加载其他教务信息////////////////////////////////////////////////////////////////////////////////////////////////////
//            async {
//                val studentIdObserver = Observer<Int> { result ->
//                    if (result != 0) {
//                        SharedPrefs.saveString("studentId", result.toString())
//                        CoroutineScope(Job()).launch {
//                            async { vm.getBizTypeId(cookie!!) }.await()
//                            async { vm.getInfo(cookie!!) }
//                            if(prefs.getString("photo","") == null || prefs.getString("photo","") == "")
//                            async { cookie?.let { vm.getPhoto(it) } }
//                        }
//                    }
//                }
//                val getBizTypeIdObserver = Observer<String?> { result ->
//                    if(result != null) {
//                        // 开始解析
//                        val bizTypeId = CasInHFUT.bizTypeId ?: CasInHFUT.getBizTypeId(result)
//                        if(bizTypeId != null) {
//                            vm.getLessonIds(cookie!!,bizTypeId,vm.studentId.value.toString())
//                            if(nextBoolean) {
//                                vm.getLessonIdsNext(cookie,bizTypeId,vm.studentId.value.toString())
//                            }
//                        }
//                    }
//                }
//                val lessonIdObserver = Observer<List<Int>> { result ->
//                    if (result.toString() != "") {
//                        val lessonIdsArray = JsonArray()
//                        result.forEach { lessonIdsArray.add(JsonPrimitive(it)) }
//                        val jsonObject = JsonObject().apply {
//                            add("lessonIds", lessonIdsArray)//课程ID
//                            addProperty("studentId", vm.studentId.value)//学生ID
//                            addProperty("weekIndex", "")
//                        }
//                        vm.getDatum(cookie!!, jsonObject)
//                        vm.bizTypeIdResponse.removeObserver(getBizTypeIdObserver)
//                        vm.studentId.removeObserver(studentIdObserver)
//                    }
//                }
//                val lessonIdObserverNext = Observer<List<Int>> { result ->
//                    if (result.toString() != "") {
//                        val lessonIdsArray = JsonArray()
//                        result.forEach { lessonIdsArray.add(JsonPrimitive(it)) }
//                        val jsonObject = JsonObject().apply {
//                            add("lessonIds", lessonIdsArray)//课程ID
//                            addProperty("studentId", vm.studentId.value)//学生ID
//                            addProperty("weekIndex", "")
//                        }
//                        vm.getDatumNext(cookie!!, jsonObject)
//                        // vm.lessonIdsNext.removeObserver(lessonIdObserver)
//                    }
//                }

//                val datumObserver = Observer<String?> { result ->
//                    if (result != null) {
//                        if (result.contains("result")) {
//                            CoroutineScope(Job()).launch {
//                                async { if (showAll) updateAll() else update() }.await()
//                                async {
//                                    Handler(Looper.getMainLooper()).post {
//                                        vm.lessonIds.removeObserver(
//                                            lessonIdObserver
//                                        )
//                                    }
//                                }
//                                async {
//                                    delay(200)
//                                    a++
//                                    loading = false
//                                }
//                            }
//                        } else showToast("数据为空,尝试刷新")
//                    }
//                }

//                async { vm.getStudentId(cookie!!) }.await()

//                Handler(Looper.getMainLooper()).post {
//                    vm.studentId.observeForever(studentIdObserver)
//                    vm.bizTypeIdResponse.observeForever(getBizTypeIdObserver)
//                    vm.lessonIds.observeForever(lessonIdObserver)
//                    vm.datumData.observeForever(datumObserver)
//                    if (nextBoolean)
//                        vm.lessonIdsNext.observeForever(lessonIdObserverNext)
//                }

//            }
//        }

        if (a > 0) job.cancel()
        if (prefs.getString("tip", "0") != "0") loading = false
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Column(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(
                visible = loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column() { LoadingUI(if(webVpn) "若加载时间过长，请重新刷新登陆状态" else null) }
                }
            }

            AnimatedVisibility(
                visible = !loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                //在这里插入课程表布局
                Column {
                    Box(modifier = Modifier.fillMaxHeight()) {
                        val scrollState = rememberLazyGridState()
                        val shouldShowAddButton by remember { derivedStateOf { scrollState.firstVisibleItemScrollOffset == 0 } }

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(if(showAll)7 else 5),
                            modifier = Modifier.padding(10.dp),
                            state = scrollState
                        ) {
                            items(if(showAll)7 else 5) { InnerPaddingHeight(innerPadding,true) }
                            items(if(showAll)42 else 30) { cell ->
                                val texts = if(showAll)tableAll[cell].toMutableList() else table[cell].toMutableList()
                                with(sharedTransitionScope) {
                                    val route = AppNavRoute.CourseDetail.withArgs(AppNavRoute.CourseDetail.Args.NAME.default as String,cell)
                                    Card(
                                        shape = MaterialTheme.shapes.extraSmall,
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                                        modifier = containerShare(
                                            Modifier.height(125.dp)
                                                .padding(if (showAll) 1.dp else 2.dp)
                                                .clickable {
                                                    // 只有一节课
                                                    if (texts.size == 1) {
                                                        // 如果是考试
                                                        if (texts[0].contains("考试")) {
                                                            return@clickable
                                                        }
                                                        val name =
                                                            parseCourseName(if (showAll) tableAll[cell][0] else table[cell][0])
                                                        if (name != null) {
                                                            navController.navigateAndSaveForTransition(AppNavRoute.CourseDetail.withArgs(name,cell))
//                                                    courseName = name
//                                                    showBottomSheetTotalCourse = true
                                                        }
                                                    } else if (texts.size > 1) {
                                                        multiWeekday =
                                                            if (showAll) (cell + 1) % 7 else (cell + 1) % 5
                                                        multiWeek = currentWeek.toInt()
                                                        courses = texts
                                                        showBottomSheetMultiCourse = true
                                                    }
                                                    // 空数据
                                                },
                                            animatedContentScope,
                                            route = if (texts.size == 1) {
                                                // 如果是考试
                                                if (texts[0].contains("考试")) {
                                                    route
                                                } else {
                                                    val name =
                                                        parseCourseName(if (showAll) tableAll[cell][0] else table[cell][0])
                                                    if (name != null) {
                                                        AppNavRoute.CourseDetail.withArgs(name,cell)
                                                    } else {
                                                        route
                                                    }
                                                }
                                            } else {
                                                route
                                            }
                                        )

                                    ) {
                                        //存在待考时
                                        if(examList.isNotEmpty()){
                                            val numa = if(showAll) 7 else 5
                                            val i = cell % numa
                                            val j = cell / numa
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
                                                fontSize = if(showAll)12.sp else 14.sp,
                                                textAlign = TextAlign.Center,
                                                fontWeight = if(texts.toString().contains("考试")) FontWeight.SemiBold else FontWeight.Normal
                                            )
                                        }
                                    }
                                }
                            }
                            item {  InnerPaddingHeight(innerPadding,false) }
                        }
                        // 上一周
                        androidx.compose.animation.AnimatedVisibility(
                            visible = shouldShowAddButton,
                            enter = scaleIn(),
                            exit = scaleOut(),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(innerPadding)
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
//                                            refreshUI(showAll)
                                            onDateChange(today.minusDays(7))
                                        }
                                    },
                                ) { Icon(Icons.Filled.ArrowBack, "Add Button") }
                            }
                        }
                        // 中间
                        androidx.compose.animation.AnimatedVisibility(
                            visible = shouldShowAddButton,
                            enter = scaleIn(),
                            exit = scaleOut(),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(innerPadding)
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
                        }
                        // 学期显示
//                        androidx.compose.animation.AnimatedVisibility(
//                            visible = !shouldShowAddButton,
//                            enter = scaleIn(),
//                            exit = scaleOut(),
//                            modifier = Modifier
//                                .align(Alignment.BottomCenter)
//                                .padding(innerPadding)
//                                .padding(
//                                    horizontal = APP_HORIZONTAL_DP,
//                                    vertical = APP_HORIZONTAL_DP
//                                )
//                        ) {
//                            TextButton(onClick = {  }) {
//                                Text(
//                                    text = parseSemseter(getSemseter()) + " 第${currentWeek}周",
//                                    style = TextStyle(shadow = Shadow(
//                                        color = Color.Gray,
//                                        offset = Offset(5.0f,5.0f),
//                                        blurRadius = 10.0f
//                                    )
//                                    )
//                                )
//                            }
//                        }
                        // 下一周
                        androidx.compose.animation.AnimatedVisibility(
                            visible = shouldShowAddButton,
                            enter = scaleIn(),
                            exit = scaleOut(),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(innerPadding)
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
//                                            refreshUI(showAll)
                                            onDateChange(today.plusDays(7))
                                        }
                                    },
                                ) { Icon(Icons.Filled.ArrowForward, "Add Button") }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(100.dp))
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

private fun numToChinese(num : Int) : String {
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