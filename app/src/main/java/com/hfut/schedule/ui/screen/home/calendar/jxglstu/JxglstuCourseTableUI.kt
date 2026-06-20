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
import androidx.compose.ui.unit.dp
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.network.interceptor.GoToInterceptorState
import com.hfut.schedule.logic.network.repo.UniAppRepository
import com.hfut.schedule.logic.util.network.CasInHFUT
import com.hfut.schedule.logic.util.network.isNotBadRequest
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.LIBRARY_TOKEN
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.network.util.Constant
import com.hfut.schedule.network.util.StatusCode
import com.hfut.schedule.ui.nav.destination.AddEventDestination
import com.hfut.schedule.ui.nav.destination.CourseDetailApiDestination
import com.hfut.schedule.ui.nav.destination.ExamDestination
import com.hfut.schedule.ui.nav.window.TimeTablePreviewWindow
import com.hfut.schedule.ui.nav.window.TimeTableSquareWindow
import com.hfut.schedule.ui.screen.home.calendar.common.DraggableWeekButton
import com.hfut.schedule.ui.screen.home.calendar.common.TimeTableWeekSwap
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.TimeTableType
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.allToTimeTableData
import com.hfut.schedule.ui.screen.home.calendar.timetable.ui.TimeTable
import com.hfut.schedule.ui.screen.home.focus.funiction.AddEventOrigin
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.getCardPsk
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.safelySetDate
import com.hfut.schedule.ui.util.state.GlobalStateHolder
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.component.status.LoadingUI
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.align.CenterScreen
import com.xah.common.ui.style.padding.navigationBarHeightPadding
import com.xah.floating.util.LocalFloatingController
import com.xah.mirror.util.ShaderState
import com.xah.navigation.util.LocalNavController
import com.xah.shared.LogUtil
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.time.LocalDate

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
        GoToInterceptorState.toCommunityTicket
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
    GoToInterceptorState.toOneCode
        .filterNotNull()
        .collect { value ->
            vm.loginOne(value)
        }
}

private suspend fun loginHuiXIn(cookies: String, vm: NetWorkViewModel) {
    // 也要发两次才给302
    vm.goToHuiXin(cookies)
    vm.goToHuiXin(cookies)
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
    backGroundHaze : ShaderState?,
    isEnabled : Boolean,
    onEnabled : (Boolean) -> Unit,
    onSwapShowAll : (Boolean) -> Unit,
    onRestoreHeight : () -> Unit
) {
    val navController = LocalNavController.current
    val scrollState = rememberScrollState()
    var loadingJxglstu by rememberSaveable { mutableStateOf(refreshLogin) }

    val termStartDate by DataStoreManager.termStartDate.collectAsState(initial = null)
    var currentWeek by rememberSaveable { mutableLongStateOf(1) }
    // 记录上一次的学期开始时间
    var lastTermStartDate by rememberSaveable { mutableStateOf<String?>(null) }

    val weekSwap = remember(currentWeek) { object : TimeTableWeekSwap {
        override fun backToCurrentWeek() {
            if(DateTimeManager.currentWeek !in 1..20) {
                if(termStartDate == null) {
                    return
                }
                currentWeek = 1
                onDateChange(
                    safelySetDate(termStartDate!!)
                )
            } else {
                currentWeek = DateTimeManager.currentWeek
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

       LaunchedEffect(Unit) {
           // 如果已经加载过 跳过
           if(isEnabled) {
               loadingJxglstu = false
               return@LaunchedEffect
           }
           // 等待读取本地Cookie
           if(!loadingJxglstu) return@LaunchedEffect
           val cookie = getJxglstuCookie()

           launch {
               onEnabled(false)
               val job = async(Dispatchers.IO) {
                   if(casCookies == null) {
                       showToast("异常 中止登录其他平台")
                       return@async
                   }
                   val cookies =  "$casCookies;$tgcCookie"
                   val useWebVpn = webVpn && !GlobalStateHolder.excludeJxglstu
                   // 智慧社区
                   launch community@ {
                       if(useWebVpn) {
                           return@community
                       }
                       val communityAuth = prefs.getString("TOKEN", "")
                       if(communityAuth.isNullOrEmpty()) {
                           loginCommunity(cookies,vm)
                       } else {
                           // 检测智慧社区可用性
                           vm.checkCommunityLogin(communityAuth)
                           val result = (vm.checkCommunityResponse.state.value as? UiState.Success)?.data
                           if(result == true) {
                               LogUtil.debug("无需刷新智慧社区")
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
                       if(auth.isNullOrEmpty()) {
                           loginHuiXIn(cookies, vm)
                       } else {
                           vm.checkHuiXinLogin(auth)
                           val result = (vm.huiXinCheckLoginResp.state.value as? UiState.Success)?.data
                           if(result == true) {
                               LogUtil.debug("无需刷新慧新易校")
                               return@huiXin
                           } else {
                               if(useWebVpn || GlobalStateHolder.excludeJxglstu) {
                                   loginHuiXin(vm)
                               } else {
                                   loginHuiXIn(cookies,vm)
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
                       if(token.isNullOrEmpty()) {
                           loginOne(cookies,vm)
                       } else {
                           vm.checkOneLogin(token)
                           val result = (vm.checkOneLoginResp.state.value as? UiState.Success)?.data
                           if(result == true) {
                               LogUtil.debug("无需刷新信息门户")
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
                       if(auth.isNullOrEmpty()) {
                           vm.goToStu(cookies)
                       } else {
                           // 检测学工系统可用性
                           vm.checkStuLogin(auth)
                           val result =  (vm.checkStuLoginResp.state.value as? UiState.Success)?.data
                           if(result == true) {
                               LogUtil.debug("无需刷新学工平台")
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
                       if(auth.isNullOrEmpty()) {
                           vm.gotoLibrary(cookies)
                       } else {
                           // 检测可用性
                           vm.checkLibraryLogin(auth)
                           val result =  (vm.checkLibraryLoginResp.state.value as? UiState.Success)?.data
                           if(result == true) {
                               LogUtil.debug("无需刷新图书馆")
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
                       if(auth.isNullOrEmpty()) {
                           vm.gotoZhiJian(cookies)
                       } else {
                           // 检测可用性
                           vm.zhiJianCheckLogin(auth)
                           val result = (vm.zhiJianCheckLoginResp.state.value as? UiState.Success)?.data
                           if(result == true) {
                               LogUtil.debug("无需刷新指间工大")
                               return@zhiJian
                           } else {
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
                       if(auth.isNullOrEmpty()) {
                           vm.goToPe(cookies)
                       } else {
                           // 检测可用性
                           vm.checkPeLogin(auth)
                           val result = (vm.checkPeLoginResp.state.value as? UiState.Success)?.data
                           if(result == true) {
                               LogUtil.debug("无需刷新体测平台")
                               return@pe
                           } else {
                               vm.goToPe(cookies)
                           }
                       }
                   }
                   // 第二课堂
                   launch second@ {
                       if(useWebVpn) {
                           return@second
                       }
                       val auth = prefs.getString(SharedPrefs.SECOND_CLASS_TOKEN, "")
                       if(auth.isNullOrEmpty()) {
                           vm.gotoSecondClass(cookies)
                       } else {
                           // 检测可用性
                           vm.checkSecondClassLogin(auth)
                           val result = (vm.checkSecondClassLoginResp.state.value as? UiState.Success)?.data
                           if(result == true) {
                               LogUtil.debug("无需刷新第二课堂")
                               return@second
                           } else {
                               vm.gotoSecondClass(cookies)
                           }
                       }
                   }
                   // 合工大教务
                   launch uniapp@ {
                       val auth = DataStoreManager.uniAppJwt.first()
                       if(auth.isEmpty()) {
                           vm.gotoSecondClass(cookies)
                       } else {
                           // 检测可用性
                           val statusCode = UniAppRepository.checkLogin(auth)
                           val result = statusCode != StatusCode.UNAUTHORIZED.code
                           if(result) {
                               LogUtil.debug("无需刷新合工大教务")
                               return@uniapp
                           } else {
                               UniAppRepository.login()
                           }
                       }
                   }
               }
               // fixme:最高等待时长10s，实测合工大教务接口非常慢大于这个10s
               withTimeoutOrNull(10*1000) {
                   job.await()
               }
               if(GlobalStateHolder.excludeJxglstu) {
                   loadingJxglstu = false
               }
               onEnabled(true)
           }
           // 教务系统
           launch(Dispatchers.IO) jxglstu@ {
               if(GlobalStateHolder.excludeJxglstu) {
                   return@jxglstu
               }
               cookie?: return@jxglstu
               vm.getStudentId(cookie)
               val studentId = (vm.studentId.state.value as? UiState.Success)?.data
               if(studentId == null) {
                   showToast("获取studentId失败 请在聚焦界面下拉刷新")
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
                   showToast("获取bizTypeId失败 请在聚焦界面下拉刷新")
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
           }
       }
    }
    var totalDragX by remember { mutableFloatStateOf(0f) }
    val shouldShowAddButton by remember { derivedStateOf { scrollState.value == 0 } }
    val floatingController = LocalFloatingController.current
    val isExpand = floatingController.isRunning

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
                LogUtil.error("LaunchedEffect received week out of bounds for length ${items.size} of items[${currentWeek-1}]")
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
                    if(!isExpand) {
                        onRestoreHeight()
                    }
                },
                onLongTapBlankRegion = {
                    floatingController.push(TimeTablePreviewWindow(items,currentWeek.toInt() ) {
                        weekSwap.goToWeek(it.toLong())
                        floatingController.pop()
                    })
                },
                onDoubleTapBlankRegion = {
                    navController.push(
                        AddEventDestination(
                            null,
                            AddEventOrigin.FOCUS_EDITED.name
                        )
                    )
                }
            ) { list ->
                // 只有一节课
                if (list.size == 1) {
                    val item = list[0]
                    val origin = CourseDetailOrigin.CALENDAR_JXGLSTU.t +  "${item.hashCode()}"
                    // 如果是考试
                    when(item.type) {
                        TimeTableType.COURSE -> {
                            navController.push(CourseDetailApiDestination(item.name, origin, item.place))
                        }
                        TimeTableType.FOCUS -> {
                            item.detail.eventId?.let {
                                navController.push(AddEventDestination(it, CourseDetailOrigin.CALENDAR_JXGLSTU.t))
                            }
                        }
                        TimeTableType.EXAM -> navController.push(ExamDestination(origin))
                    }
                } else if (list.size > 1) {
                    floatingController.push(TimeTableSquareWindow(list))
                }
            }

            DraggableWeekButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = innerPadding.calculateBottomPadding() - navigationBarHeightPadding)
                    .padding(APP_HORIZONTAL_DP),
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
                    floatingController.push(TimeTablePreviewWindow(items,currentWeek.toInt()) {
                        weekSwap.goToWeek(it.toLong())
                        floatingController.pop()
                    })
                }
            )
        }
    }
}