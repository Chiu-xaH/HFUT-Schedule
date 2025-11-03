package com.hfut.schedule.ui.screen.home.calendar.jxglstu

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.model.community.courseDetailDTOList
import com.hfut.schedule.logic.model.jxglstu.CourseUnitBean
import com.hfut.schedule.logic.model.jxglstu.LessonTimesResponse
import com.hfut.schedule.logic.network.interceptor.CasGoToInterceptorState
import com.hfut.schedule.logic.network.util.CasInHFUT
import com.hfut.schedule.logic.network.util.MyApiParse.isNextOpen
import com.hfut.schedule.logic.network.util.isNotBadRequest
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.SemseterParser
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.LIBRARY_TOKEN
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.saveInt
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.LargeCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.mixedCardNormalColor
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.calendar.common.DraggableWeekButton
import com.hfut.schedule.ui.screen.home.calendar.common.numToChinese
import com.hfut.schedule.ui.screen.home.calendar.communtiy.CourseDetailApi
import com.hfut.schedule.ui.screen.home.calendar.communtiy.DetailInfos
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.next.CourseDetailOrigin
import com.hfut.schedule.ui.screen.home.calendar.timetable.NewTimeTableUI
import com.hfut.schedule.ui.screen.home.calendar.timetable.TimeTableDetail
import com.hfut.schedule.ui.screen.home.calendar.timetable.TimeTableItem
import com.hfut.schedule.ui.screen.home.calendar.timetable.TimeTableType
import com.hfut.schedule.ui.screen.home.calendar.timetable.allToTimeTableData
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.getCardPsk
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getJxglstuStartDate
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getTotalCourse
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.hfut.schedule.ui.util.state.GlobalUIStateHolder
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.mirror.util.ShaderState
import com.xah.uicommon.component.status.LoadingUI
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.align.CenterScreen
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
    webVpn: Boolean,
    refreshLogin: Boolean,
    onDateChange: (LocalDate) ->Unit,
    today: LocalDate,
    hazeState: HazeState,
    navController: NavHostController,
    backGroundHaze : ShaderState?,
    isEnabled : Boolean,
    onEnabled : (Boolean) -> Unit,
    onSwapShowAll : (Boolean) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showBottomSheetTotalCourse by remember { mutableStateOf(false) }
    var courseName by remember { mutableStateOf("") }
    var showBottomSheetDetail by remember { mutableStateOf(false) }
    var bean by remember { mutableStateOf<List<TimeTableItem>?>(null) }

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

    if (showBottomSheetDetail) {
        HazeBottomSheet (
            onDismissRequest = {
                showBottomSheetDetail = false
            },
            autoShape = false,
            showBottomSheet = showBottomSheetDetail,
            hazeState = hazeState
        ) {
            bean?.let { TimeTableDetail(it) }
        }
    }

    var loadingJxglstu by rememberSaveable { mutableStateOf(refreshLogin) }

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
    var totalDragX by remember { mutableFloatStateOf(0f) }
    val drag = remember { 5f }

    fun nextWeek() {
        if (currentWeek < 20) {
            onDateChange(today.plusDays(7))
            currentWeek++
        }
    }
    fun previousWeek() {
        if (currentWeek > 1) {
            onDateChange(today.minusDays(7))
            currentWeek--
        }
    }
    if(loadingJxglstu) {
        CenterScreen {
            LoadingUI(if(webVpn) "请等待 WebVpn延迟有时比较高" else null)
        }
    } else {
        val items by produceState(initialValue = List(MyApplication.MAX_WEEK) { emptyList() }) {
            value = allToTimeTableData(context)
        }

        LaunchedEffect(currentWeek,items) {
            if(currentWeek >= items.size) {
                Exception("LaunchedEffect received week out of bounds for length ${items.size} of items[${currentWeek-1}]").printStackTrace()
                return@LaunchedEffect
            } else {
                val list = items[currentWeek.toInt()-1]
                val weekend = list.find { it.dayOfWeek == 6 || it.dayOfWeek == 7 } != null
                if(weekend && !showAll) {
                    // 展开
                    onSwapShowAll(true)
                }
            }
        }
        // 课程表布局
        Box(modifier = Modifier
            .fillMaxSize()
            .pointerInput(today) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        // 手指松开后根据累积的水平拖动量决定
                        if (totalDragX > drag) { // 阈值
                            previousWeek()
                        } else if (totalDragX < -drag) {
                            nextWeek()
                        }
                        totalDragX = 0f // 重置
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume() // 防止滚动穿透
                        totalDragX += dragAmount
                    }
                )
            }
        ) {
            val shouldShowAddButton by remember { derivedStateOf { scrollState.value == 0 } }

            NewTimeTableUI(
                items,
                currentWeek.toInt(),
                showAll,
                modifier = Modifier
                    .padding(horizontal = APP_HORIZONTAL_DP-(if (showAll) 1.75.dp else 2.5.dp)-1.dp)
                    .verticalScroll(scrollState)
                ,
                innerPadding = innerPadding,
                shaderState = backGroundHaze,
            ) { list ->
                // 只有一节课
                if (list.size == 1) {
                    val item = list[0]
                    // 如果是考试
                    when(item.type) {
                        TimeTableType.COURSE -> {
                            navController.navigateForTransition(AppNavRoute.CourseDetail, AppNavRoute.CourseDetail.withArgs(item.name, CourseDetailOrigin.CALENDAR_JXGLSTU.t + "@${item}" ))
                        }
                        TimeTableType.FOCUS -> {
                            item.id?.let {
                                navController.navigateForTransition(AppNavRoute.AddEvent, AppNavRoute.AddEvent.withArgs(it, CourseDetailOrigin.CALENDAR_JXGLSTU.t + "@${item}" ))
                            }
                        }
                        TimeTableType.EXAM -> {
                            bean = list
                            showBottomSheetDetail = true
                        }
                    }
                } else if (list.size > 1) {
                    bean = list
                    showBottomSheetDetail = true
                }
            }


            // 中间
            DraggableWeekButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(.5f).compositeOver(MaterialTheme.colorScheme.surface),
                expanded = shouldShowAddButton,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = innerPadding.calculateBottomPadding() - navigationBarHeightPadding)
                    .padding(APP_HORIZONTAL_DP)
                ,
                onClick = {
                    if(DateTimeManager.weeksBetweenJxglstu < 1) {
                        currentWeek = 1
                        onDateChange(getJxglstuStartDate())
                    } else {
                        currentWeek = DateTimeManager.weeksBetweenJxglstu
                        onDateChange(LocalDate.now())
                    }
                },
                shaderState = backGroundHaze,
                currentWeek = currentWeek,
                key = today,
                onNext = { nextWeek() },
                onPrevious = { previousWeek() }
            )
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

