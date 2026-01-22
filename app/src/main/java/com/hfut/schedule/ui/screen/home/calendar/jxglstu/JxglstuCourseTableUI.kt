package com.hfut.schedule.ui.screen.home.calendar.jxglstu

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.network.interceptor.CasGoToInterceptorState
import com.hfut.schedule.logic.network.util.CasInHFUT
import com.hfut.schedule.logic.network.util.MyApiParse.isNextOpen
import com.hfut.schedule.logic.network.util.isNotBadRequest
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.LIBRARY_TOKEN
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.saveInt
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.weeksBetweenJxglstu
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.ShareTwoContainer2D
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.calendar.common.DraggableWeekButton
import com.hfut.schedule.ui.screen.home.calendar.common.TimeTableWeekSwap
import com.hfut.schedule.ui.screen.home.calendar.communtiy.CourseDetailApi
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.next.CourseDetailOrigin
import com.hfut.schedule.ui.screen.home.calendar.timetable.ui.TimeTablePreview
import com.hfut.schedule.ui.screen.home.calendar.timetable.ui.TimeTable
import com.hfut.schedule.ui.screen.home.calendar.timetable.ui.TimeTableDetail
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.TimeTableItem
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.TimeTableType
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.allToTimeTableData
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.getCardPsk
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getDefaultStartTerm
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getTotalCourse
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.safelySetDate
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.hfut.schedule.ui.util.state.GlobalUIStateHolder
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.mirror.util.ShaderState
import com.xah.uicommon.component.status.LoadingUI
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.align.CenterScreen
import com.xah.uicommon.style.padding.navigationBarHeightPadding
import com.xah.uicommon.util.LogUtil
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.time.LocalDate
import java.time.temporal.ChronoUnit

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
    scaleFactor : Float,
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
    onSwapShowAll : (Boolean) -> Unit,
    onRestoreHeight : () -> Unit
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

    val termStartDate by DataStoreManager.termStartDate.collectAsState(initial = null)
    var currentWeek by rememberSaveable { mutableLongStateOf(1) }
    // 记录上一次的学期开始时间
    var lastTermStartDate by rememberSaveable { mutableStateOf<String?>(null) }

    val weekSwap = remember(currentWeek) { object : TimeTableWeekSwap {
        override fun backToCurrentWeek() {
            if(DateTimeManager.weeksBetweenJxglstu < 1) {
                if(termStartDate == null) {
                    return
                }
                currentWeek = 1
                onDateChange(
                    safelySetDate(termStartDate!!)
                )
            } else {
                currentWeek = DateTimeManager.weeksBetweenJxglstu
                onDateChange(LocalDate.now())
            }
        }

        override fun goToWeek(i: Long) {
            if(currentWeek == i) {
                return
            }
            if (i in 1..MyApplication.MAX_WEEK) {
                val day = 7L*(i - currentWeek)
                onDateChange(today.plusDays(day))
                currentWeek = i
            }
            showToast("第${currentWeek}周")
        }

        override fun nextWeek() {
            if (currentWeek < MyApplication.MAX_WEEK) {
                onDateChange(today.plusDays(7))
                currentWeek++
            }
        }

        override fun previousWeek() {
            if (currentWeek > 1) {
                onDateChange(today.minusDays(7))
                currentWeek--
            }
        }
    } }

    /**
     * 用户修改学期开始时间  termStartDate变化且不为空  ----->   重新初始化currentWeek
     * 第一次启动     ----->   初始化currentWeek    后续开关界面不要初始化（rememberSaveable）
     */
    LaunchedEffect(termStartDate) {
        val start = termStartDate ?: return@LaunchedEffect

        // 冷启动 or 用户修改学期开始时间
        if (lastTermStartDate != start) {
            LogUtil.debug("重新初始化currentWeek")
            weekSwap.backToCurrentWeek()
            lastTermStartDate = start
        }
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
                   if (LargeStringDataManager.read( LargeStringDataManager.PHOTO) == null) {
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
    val shouldShowAddButton by remember { derivedStateOf { scrollState.value == 0 } }
    var isExpand by remember { mutableStateOf(false) }


    if(loadingJxglstu) {
        CenterScreen {
            LoadingUI(if(webVpn) "请等待 WebVpn延迟有时比较高" else null)
        }
    } else {
        val items by produceState(initialValue = List(MyApplication.MAX_WEEK) { emptyList() }) {
            value = allToTimeTableData()
        }

        LaunchedEffect(currentWeek,items) {
            if(currentWeek > items.size) {
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
                        if (totalDragX > MyApplication.SWIPE) { // 阈值
                            weekSwap.previousWeek()
                        } else if (totalDragX < -MyApplication.SWIPE) {
                            weekSwap.nextWeek()
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
            TimeTable(
                items,
                currentWeek.toInt(),
                showAll,
                scaleFactor = scaleFactor,
                modifier = Modifier
                    .padding(horizontal = APP_HORIZONTAL_DP-(if (showAll) 1.75.dp else 2.5.dp)-1.dp)
                    .verticalScroll(scrollState)
                ,
                innerPadding = innerPadding,
                shaderState = backGroundHaze,
                onTapBlankRegion = {
                    if(isExpand) {
                        isExpand = false
                    } else {
                        onRestoreHeight()
//                        showToast("空白区域双击添加日程,长按切换周")
                    }
                },
                onLongTapBlankRegion = {
                    isExpand = !isExpand
                },
                onDoubleTapBlankRegion = {
                    navController.navigateForTransition(
                        AppNavRoute.AddEvent,
                        AppNavRoute.AddEvent.withArgs()
                    )
                }
            ) { list ->
                // 只有一节课
                if (list.size == 1) {
                    val item = list[0]
                    // 如果是考试
                    when(item.type) {
                        TimeTableType.COURSE -> {
                            navController.navigateForTransition(AppNavRoute.CourseDetail, AppNavRoute.CourseDetail.withArgs(item.name, CourseDetailOrigin.CALENDAR_JXGLSTU.t + "@${item.hashCode()}" ))
                        }
                        TimeTableType.FOCUS -> {
                            item.id?.let {
                                navController.navigateForTransition(AppNavRoute.AddEvent, AppNavRoute.AddEvent.withArgs(it, CourseDetailOrigin.CALENDAR_JXGLSTU.t + "@${item.hashCode()}" ))
                            }
                        }
                        TimeTableType.EXAM -> {
                            navController.navigateForTransition(AppNavRoute.Exam, AppNavRoute.Exam.withArgs(CourseDetailOrigin.CALENDAR_JXGLSTU.t + "@${item.hashCode()}"))
                        }
                    }
                } else if (list.size > 1) {
                    bean = list
                    showBottomSheetDetail = true
                }
            }

            ShareTwoContainer2D(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = innerPadding.calculateBottomPadding() - navigationBarHeightPadding)
                    .padding(APP_HORIZONTAL_DP),
                show = !isExpand,
                defaultContent = {
                    TimeTablePreview(
                        items = items, // 一周课程,
                        currentWeek = currentWeek.toInt(),
                        innerPadding = innerPadding,
                    ) {
                        weekSwap.goToWeek(it.toLong())
                        isExpand = !isExpand
                    }
                },
                secondContent = {
                    DraggableWeekButton(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(.5f).compositeOver(MaterialTheme.colorScheme.surface),
                        expanded = shouldShowAddButton,
                        onClick = {
                            weekSwap.backToCurrentWeek()
                        },
                        shaderState = backGroundHaze,
                        currentWeek = currentWeek,
                        key = today,
                        onNext = { weekSwap.nextWeek() },
                        onPrevious = { weekSwap.previousWeek() },
                        onLongClick = {
                            isExpand = !isExpand
                        }
                    )
                }
            )
            // 中间
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

