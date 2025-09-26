package com.hfut.schedule.ui.screen

import android.annotation.SuppressLint
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.datetime.getCelebration
import com.hfut.schedule.logic.util.sys.datetime.getUserAge
import com.hfut.schedule.logic.util.sys.datetime.isUserBirthday
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.screen.Party
import com.hfut.schedule.ui.component.webview.WebViewScreenForNavigation
import com.hfut.schedule.ui.component.webview.getPureUrl
import com.hfut.schedule.ui.screen.grade.GradeScreen
import com.hfut.schedule.ui.screen.home.MainScreen
import com.hfut.schedule.ui.screen.home.SearchEditScreen
import com.hfut.schedule.ui.screen.home.calendar.communtiy.CourseDetailApiScreen
import com.hfut.schedule.ui.screen.home.focus.funiction.AddEventScreen
import com.hfut.schedule.ui.screen.home.search.function.community.bus.BusScreen
import com.hfut.schedule.ui.screen.home.search.function.community.failRate.FailRateScreen
import com.hfut.schedule.ui.screen.home.search.function.community.library.LibraryScreen
import com.hfut.schedule.ui.screen.home.search.function.community.library.screen.LibraryBorrowedScreen
import com.hfut.schedule.ui.screen.home.search.function.community.workRest.TimeTableScreen
import com.hfut.schedule.ui.screen.home.search.function.huiXin.washing.HaiLeWashingScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.courseSearch.CourseSearchCalendarScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.courseSearch.CourseSearchScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam.ExamScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.nextCourse.NextCourseScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.ClassmatesScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.PersonScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.program.ProgramCompetitionDetailScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.program.ProgramCompetitionScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.program.ProgramScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.program.ProgramSearchScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.selectCourse.DropCourseScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.selectCourse.SelectCourseDetailScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.selectCourse.SelectCourseScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.survey.SurveyScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.TotalCourseScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.TransferDetailScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.TransferScreen
import com.hfut.schedule.ui.screen.home.search.function.my.holiday.HolidayScreen
import com.hfut.schedule.ui.screen.home.search.function.my.notification.NotificationsScreen
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.NotificationBoxScreen
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.WebNavigationScreen
import com.hfut.schedule.ui.screen.home.search.function.one.emptyRoom.ClassroomDetailScreen
import com.hfut.schedule.ui.screen.home.search.function.one.emptyRoom.ClassroomScreen
import com.hfut.schedule.ui.screen.home.search.function.one.pay.FeeScreen
import com.hfut.schedule.ui.screen.home.search.function.other.life.LifeScreen
import com.hfut.schedule.ui.screen.home.search.function.other.wechat.WeChatScreen
import com.hfut.schedule.ui.screen.home.search.function.school.admission.AdmissionRegionScreen
import com.hfut.schedule.ui.screen.home.search.function.school.admission.AdmissionScreen
import com.hfut.schedule.ui.screen.home.search.function.school.dormitoryScore.DormitoryScoreScreen
import com.hfut.schedule.ui.screen.home.search.function.school.hall.OfficeHallScreen
import com.hfut.schedule.ui.screen.home.search.function.school.scan.ScanScreen
import com.hfut.schedule.ui.screen.home.search.function.school.student.StuTodayCampusScreen
import com.hfut.schedule.ui.screen.home.search.function.school.teacherSearch.TeacherSearchScreen
import com.hfut.schedule.ui.screen.home.search.function.school.webvpn.WebVpnScreen
import com.hfut.schedule.ui.screen.home.search.function.school.work.WorkScreen
import com.hfut.schedule.ui.screen.login.LoginScreen
import com.hfut.schedule.ui.screen.news.home.NewsScreen
import com.hfut.schedule.ui.screen.util.ControlCenterScreen
import com.hfut.schedule.ui.screen.util.EmptyScreen
import com.hfut.schedule.ui.screen.util.NavigationExceptionScreen
import com.hfut.schedule.ui.screen.util.OpenOuterApplicationScreen
import com.hfut.schedule.ui.screen.util.limitDrawerSwipeArea
import com.hfut.schedule.ui.screen.welcome.UpdateSuccessScreen
import com.hfut.schedule.ui.screen.welcome.UseAgreementScreen
import com.hfut.schedule.ui.screen.welcome.VersionInfoScreen
import com.hfut.schedule.ui.util.AppAnimationManager.CONTROL_CENTER_ANIMATION_SPEED
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.xah.transition.component.TransitionNavHost
import com.xah.transition.component.transitionComposable
import com.xah.transition.state.TransitionConfig
import com.xah.transition.style.TransitionCurveStyle
import com.xah.transition.util.isCurrentRouteWithoutArgs
import com.xah.uicommon.style.align.CenterScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.text.toInt

private const val OFFSET_KEY = "OFFSET_DRAWERS"
suspend fun getDrawOpenOffset(drawerState : DrawerState) : Float = withContext(Dispatchers.IO) {
    drawerState.close()
    val currentValue = prefs.getFloat(OFFSET_KEY,0f)
    val newValue = drawerState.currentOffset
    if(currentValue == 0f || newValue != currentValue) {
        showToast("正在校准，请勿动稍后")
        SharedPrefs.saveFloat(OFFSET_KEY,newValue,0f)
        showToast("校准完成")
        return@withContext newValue
    } else {
        return@withContext currentValue
    }
}

suspend fun DrawerState.animationClose() = this.animateTo(DrawerValue.Closed, tween(CONTROL_CENTER_ANIMATION_SPEED,easing = FastOutSlowInEasing))
suspend fun DrawerState.animationOpen() = this.animateTo(DrawerValue.Open, spring(dampingRatio = 0.8f, stiffness = 125f))


@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("NewApi")
@Composable
fun MainHost(
    networkVm : NetWorkViewModel,
    loginVm : LoginViewModel,
    uiVm : UIViewModel,
    login : Boolean,
    isSuccessActivity: Boolean,
//    isSuccessActivityWebVpn : Boolean,
    startRoute : String? = null
) {
    val switchUpload by remember { mutableStateOf(prefs.getBoolean("SWITCHUPLOAD",true )) }
    val startActivity by produceState<Boolean>(initialValue = prefs.getBoolean("SWITCHFASTSTART",prefs.getString("TOKEN","")?.isNotEmpty() ?: false)) {
        value = DataStoreManager.enableQuickStart.first()
    }
    val celebration = remember { getCelebration() }
    val navController = rememberNavController()
    val first by remember { mutableStateOf(
        if(prefs.getBoolean("canUse",false)) {
            startRoute
                ?: if(prefs.getString("versionName", "上版本") == AppVersion.getVersionName()) {
                    AppNavRoute.Home.route
                } else {
                    AppNavRoute.UpdateSuccess.route
                }
        } else {
            AppNavRoute.UseAgreement.route
        }
    ) }
    var value by remember { mutableIntStateOf(0) }
    // 初始化网络请求
    if(!isSuccessActivity)
        LaunchedEffect(Unit) {
            launch {
                if(isUserBirthday()) {
                    showToast("祝您${getUserAge()}周岁🎈生日快乐🎂")
                }
            }
            launch {
                // 修正之前的Bug
                val auth = prefs.getString("auth","") ?: return@launch
                if(auth.contains("&")) {
                    SharedPrefs.saveString("auth",auth.substringBefore("&"))
                    showToast("已自动修复一卡通登录状态无效的Bug")
                }
            }
            // 如果进入的是登陆界面 未登录做准备
            if(!(startActivity && login)) {
                //从服务器获取信息
                launch { networkVm.getMyApi() }
                launch { loginVm.getCookie() }
                launch {  loginVm.getKey() }
                launch {
                    loginVm.getTicket()
                    val cookie = (loginVm.webVpnTicket.state.value as? UiState.Success)?.data ?: return@launch
                    loginVm.putKey(cookie)
                    val status = (loginVm.status.state.value as? UiState.Success)?.data ?: return@launch
                    if(status) {
                        loginVm.getKeyWebVpn()
                    }
                }
            } else { // 否则进入的是主界面
                //上传用户统计数据
                if(switchUpload && value == 0 && !AppVersion.isPreview() && !AppVersion.isInDebugRunning()) {
                    launch {
                        networkVm.postUser()
                        value++
                    }
                }
            }
        }
    val configuration = LocalConfiguration.current
    var screenWidth by remember { mutableIntStateOf(0) }
    val drawerState =  rememberDrawerState(DrawerValue.Closed)
    var maxOffset by rememberSaveable { mutableFloatStateOf(
        prefs.getFloat(OFFSET_KEY,0f)
    ) }
    val enableControlCenter by DataStoreManager.enableControlCenter.collectAsState(initial = false)
    val scope = rememberCoroutineScope()
    val isWebView = navController.isCurrentRouteWithoutArgs(AppNavRoute.WebView.route) || navController.isCurrentRouteWithoutArgs(AppNavRoute.UseAgreement.route)
    val isScan = navController.isCurrentRouteWithoutArgs(AppNavRoute.Scan.route)

    val enableGesture = enableControlCenter && !isWebView
    var containerColor by remember { mutableStateOf<Color?>(null) }
    LaunchedEffect(configuration,enableControlCenter) {
        if(enableControlCenter) {
            snapshotFlow { configuration.screenWidthDp }
                .collect {
                    screenWidth = it
                    // 你可以在这里更新 maxOffset
                    maxOffset = getDrawOpenOffset(drawerState)
                }
        }
    }
    val motionBlur by DataStoreManager.enableMotionBlur.collectAsState(initial = AppVersion.CAN_MOTION_BLUR)
    val blurDp by remember {
        derivedStateOf {
            if (maxOffset == 0f) {
                0.dp // 未校准前不模糊
            } else {
                val fraction = 1 - (drawerState.currentOffset / maxOffset).coerceIn(0f, 1f)
                (fraction * 42.5).dp//37.5
            }
        }
    }
    val scale by remember {
        derivedStateOf {
            if (maxOffset == 0f) {
                1f
            } else {
                val fraction =  (drawerState.currentOffset / maxOffset).coerceIn(0f, 1f)
                (0.85f) * (1 - fraction) + fraction// 0.85f else 0.8875
            }
        }
    }

    // 返回拦截
    if (enableControlCenter) {
        val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
        val callback = remember {
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    scope.launch { drawerState.animationClose() }
                }
            }
        }
        DisposableEffect(drawerState.currentOffset) {
            if(drawerState.currentOffset != maxOffset) {
                dispatcher?.addCallback(callback)
            }
            onDispose {
                callback.remove()
            }
        }
    }
    val alpha = if(motionBlur && !isScan) {
        0.4f
    } else 1f
    //0.35f//45f  0.425f

    ModalNavigationDrawer  (
        scrimColor = MaterialTheme.colorScheme.surface.copy(alpha),
        drawerState = drawerState,
        gesturesEnabled = enableGesture,
        drawerContent = {
            ControlCenterScreen(
                if(isWebView) {
                    containerColor?.copy(if(motionBlur) 0.425f else 0.1f)?.compositeOver(MaterialTheme.colorScheme.surface)
                } else {
                    null
                }
                ,navController) {
                scope.launch {
                    drawerState.animationClose()
                }
            }
        },
    ) {
        Box(modifier = Modifier.fillMaxSize()
            .let {
                if(enableGesture) it.limitDrawerSwipeArea(allowedArea = with(LocalDensity.current) { Rect(0f,0f, screenWidth.dp.toPx(),150.dp.toPx()) })
                else it
            }
        ) {
            Party(show = celebration.use, timeSecond = celebration.time*500)
            // 磁钉体系
            TransitionNavHost(
                navController = navController,
                startDestination = first,
                modifier = Modifier
                    .let {
                        if(isWebView) {
                            containerColor?.let { color ->
                                it.background(color)
                            } ?: it
                        }
                        else
                            it.background(MaterialTheme.colorScheme.surface)
                    }
                    .let{ if(motionBlur && enableControlCenter && !isScan) it.blur(blurDp) else it }
                    .let { if(enableControlCenter) it.scale(scale) else it }

            )  {
                // 主UI
                transitionComposable(AppNavRoute.Home.route) {
                    val mainUI = @Composable { celebrationText : String? ->
                        if(isSuccessActivity) {
                            MainScreen(
                                vm = networkVm,
                                vmUI = uiVm,
                                celebrationText = celebrationText,
                                isLogin = true,
                                navHostTopController = navController,
                            )
                        } else if(startActivity && login) {
                            MainScreen(
                                networkVm,
                                uiVm,
                                celebrationText,
                                false,
                                navHostTopController = navController,
                            )
                        } else LoginScreen(
                            loginVm,
                            networkVm,
                        )
                    }
                    mainUI(celebration.str)
                }
                // 用户协议
                transitionComposable(AppNavRoute.UseAgreement.route) {
                    Box {
                        UseAgreementScreen(navController)
                        Party()
                    }
                }
                // 更新完成引导
                transitionComposable(AppNavRoute.UpdateSuccess.route) {
                    UpdateSuccessScreen(navController, )
                }
                // 本版本新特性
                transitionComposable(AppNavRoute.VersionInfo.route) {
                    VersionInfoScreen(networkVm,navController)
                }
                // 打开外部应用
                transitionComposable(
                    route = AppNavRoute.OpenOuterApplication.receiveRoute(),
                    arguments = getArgs(AppNavRoute.OpenOuterApplication.Args.entries)
                ) { backStackEntry ->
                    val target = backStackEntry.arguments?.getString(AppNavRoute.OpenOuterApplication.Args.PACKAGE_NAME.argName) ?: return@transitionComposable
                    val app = Starter.AppPackages.entries.find { it.packageName == target } ?: return@transitionComposable
                    OpenOuterApplicationScreen(
                        app,
                        navController,
                    )
                }
                // 图书借阅
                transitionComposable(AppNavRoute.LibraryBorrowed.route) {
                    LibraryBorrowedScreen(networkVm,navController)
                }
                // 添加聚焦日程
                transitionComposable(AppNavRoute.AddEvent.route) {
                    AddEventScreen(networkVm,navController, )
                }
                // 导航中转空白页
                transitionComposable(
                    route = AppNavRoute.Empty.receiveRoute(),
                    arguments = getArgs(AppNavRoute.Empty.Args.entries)
                ) { backStackEntry ->
                    val targetRoute = backStackEntry.arguments?.getString(AppNavRoute.Empty.Args.TARGET_ROUTE.argName) ?: return@transitionComposable
                    EmptyScreen(
                        targetRoute,
                        navController,
                    )
                }
                // 成绩
                transitionComposable(
                    route = AppNavRoute.Grade.receiveRoute(),
                    arguments = getArgs(AppNavRoute.Grade.Args.entries)
                ) { backStackEntry ->
                    val ifSaved = backStackEntry.arguments?.getBoolean(AppNavRoute.Grade.Args.IF_SAVED.argName) ?: (AppNavRoute.Grade.Args.IF_SAVED.default as Boolean)
                    GradeScreen(
                        ifSaved,
                        networkVm,
                        navController,
                    )
                }
                // 招生
                transitionComposable(AppNavRoute.Admission.route) {
                    AdmissionScreen(networkVm , navController)
                }
                // 招生 二级界面
                transitionComposable(
                    route = AppNavRoute.AdmissionRegionDetail.receiveRoute(),
                    arguments = getArgs(AppNavRoute.AdmissionRegionDetail.Args.entries)
                ) { backStackEntry ->
                    val index = backStackEntry.arguments?.getInt(AppNavRoute.AdmissionRegionDetail.Args.INDEX.argName) ?: (AppNavRoute.AdmissionRegionDetail.Args.INDEX.default as Int)
                    val type = backStackEntry.arguments?.getString(AppNavRoute.AdmissionRegionDetail.Args.TYPE.argName) ?: (AppNavRoute.AdmissionRegionDetail.Args.TYPE.default as String)
                    if(index < 0) return@transitionComposable
                    AdmissionRegionScreen(
                        networkVm,
                        navController,
                        type,
                        index,
                    )
                }
                // 课程详情
                transitionComposable(
                    route = AppNavRoute.CourseDetail.receiveRoute(),
                    arguments = getArgs(AppNavRoute.CourseDetail.Args.entries)
                ) { backStackEntry ->
                    val courseName = backStackEntry.arguments?.getString(AppNavRoute.CourseDetail.Args.NAME.argName) ?: (AppNavRoute.CourseDetail.Args.NAME.default as String)
                    val id = backStackEntry.arguments?.getString(AppNavRoute.CourseDetail.Args.INDEX.argName) ?: (AppNavRoute.CourseDetail.Args.INDEX.default as String)

                    CourseDetailApiScreen(
                        courseName,
                        id,
                        networkVm,
                        navController
                    )
                }
                // 教室 二级界面
                transitionComposable(
                    route = AppNavRoute.ClassroomDetail.receiveRoute(),
                    arguments = getArgs(AppNavRoute.ClassroomDetail.Args.entries)
                ) { backStackEntry ->
                    val code = backStackEntry.arguments?.getString(AppNavRoute.ClassroomDetail.Args.CODE.argName) ?: (AppNavRoute.ClassroomDetail.Args.CODE.default as String)
                    val name = backStackEntry.arguments?.getString(AppNavRoute.ClassroomDetail.Args.NAME.argName) ?: (AppNavRoute.ClassroomDetail.Args.NAME.default as String)

                    ClassroomDetailScreen(
                        networkVm,
                        navController,
                        code,
                        name,
                    )
                }
                // 教室状态
                transitionComposable(route = AppNavRoute.Classroom.route) {
                    ClassroomScreen(networkVm,navController ,)
                }
                // 法定假日
                transitionComposable(route = AppNavRoute.Holiday.route,) {
                    HolidayScreen(navController ,)
                }
                // 微信专区
                transitionComposable(route = AppNavRoute.Wechat.route) {
                    WeChatScreen(navController ,)
                }
                // 作息
                transitionComposable(route = AppNavRoute.TimeTable.route) {
                    TimeTableScreen(navController ,)
                }
                // 海乐生活
                transitionComposable(route = AppNavRoute.HaiLeWashing.route) {
                    HaiLeWashingScreen(networkVm,navController )
                }
                // 学费
                transitionComposable(route = AppNavRoute.Fee.route) {
                    FeeScreen(networkVm,navController )
                }
                // 学工系统/今日校园
                transitionComposable(route = AppNavRoute.StuTodayCampus.route) {
                    StuTodayCampusScreen(networkVm,navController )
                }
                // 教师检索
                transitionComposable(route = AppNavRoute.TeacherSearch.route) {
                    TeacherSearchScreen(networkVm,navController )
                }
                // 就业
                transitionComposable(route = AppNavRoute.Work.route) {
                    WorkScreen(networkVm,navController )
                }
                // 开课查询
                transitionComposable(route = AppNavRoute.CourseSearch.route) {
                    CourseSearchScreen(networkVm,navController )
                }
                // 个人信息
                transitionComposable(route = AppNavRoute.Person.route) {
                    PersonScreen(networkVm,navController )
                }
                // 消息中心
                transitionComposable(route = AppNavRoute.Notifications.route) {
                    NotificationsScreen(navController )
                }
                // 寝室评分
                transitionComposable(route = AppNavRoute.DormitoryScore.route) {
                    DormitoryScoreScreen(networkVm,navController )
                }
                // 考试
                transitionComposable(route = AppNavRoute.Exam.route) {
                    ExamScreen(navController)
                }
                // 转专业
                transitionComposable(route = AppNavRoute.Transfer.route) {
                    TransferScreen(networkVm,navController )
                }
                // 评教
                transitionComposable(route = AppNavRoute.Survey.route) {
                    SurveyScreen(networkVm,navController )
                }
                // 图书馆
                transitionComposable(route = AppNavRoute.Library.route) {
                    LibraryScreen(networkVm,navController )
                }
                // 挂科率
                transitionComposable(AppNavRoute.FailRate.route) {
                    FailRateScreen(networkVm, navController )
                }
                // 选课
                transitionComposable(AppNavRoute.SelectCourse.route) {
                    SelectCourseScreen(networkVm, uiVm,navController)
                }
                // 网址导航
                transitionComposable(AppNavRoute.WebNavigation.route) {
                    WebNavigationScreen(navController)
                }
                // 收纳
                transitionComposable(AppNavRoute.NotificationBox.route) {
                    NotificationBoxScreen(navController)
                }
                // 生活服务
                transitionComposable(
                    route = AppNavRoute.Life.receiveRoute(),
                    arguments = getArgs(AppNavRoute.Life.Args.entries)
                ) { backStackEntry ->
                    val inFocus = backStackEntry.arguments?.getBoolean(AppNavRoute.Life.Args.IN_FOCUS.argName) ?: (AppNavRoute.Life.Args.IN_FOCUS.default as Boolean)
                    LifeScreen(inFocus,networkVm,navController)
                }
                // 选课二级界面
                transitionComposable(
                    route = AppNavRoute.SelectCourseDetail.receiveRoute(),
                    arguments = getArgs(AppNavRoute.SelectCourseDetail.Args.entries)
                ) { backStackEntry ->
                    val index = backStackEntry.arguments?.getInt(AppNavRoute.SelectCourseDetail.Args.COURSE_ID.argName) ?: (AppNavRoute.SelectCourseDetail.Args.COURSE_ID.default as Int)
                    val title = backStackEntry.arguments?.getString(AppNavRoute.SelectCourseDetail.Args.NAME.argName) ?: (AppNavRoute.SelectCourseDetail.Args.NAME.default as String)

                    if(index <= 0) {
                        return@transitionComposable
                    }
                    SelectCourseDetailScreen(
                        networkVm,
                        index,
                        title,
                        navController,
                    )
                }
                // 退课
                transitionComposable(
                    route = AppNavRoute.DropCourse.receiveRoute(),
                    arguments = getArgs(AppNavRoute.DropCourse.Args.entries)
                ) { backStackEntry ->
                    val index = backStackEntry.arguments?.getInt(AppNavRoute.DropCourse.Args.COURSE_ID.argName) ?: (AppNavRoute.DropCourse.Args.COURSE_ID.default as Int)
                    val title = backStackEntry.arguments?.getString(AppNavRoute.DropCourse.Args.NAME.argName) ?: (AppNavRoute.DropCourse.Args.NAME.default as String)

                    if(index <= 0) {
                        return@transitionComposable
                    }
                    DropCourseScreen(
                        networkVm,
                        index,
                        title,
                        navController,
                    )
                }
                // 转专业二级界面
                transitionComposable(
                    route = AppNavRoute.TransferDetail.receiveRoute(),
                    arguments = getArgs(AppNavRoute.TransferDetail.Args.entries)
                ) { backStackEntry ->
                    val title = backStackEntry.arguments?.getString(AppNavRoute.TransferDetail.Args.NAME.argName) ?: (AppNavRoute.TransferDetail.Args.NAME.default as String)
                    val batchId = backStackEntry.arguments?.getString(AppNavRoute.TransferDetail.Args.BATCH_ID.argName) ?: (AppNavRoute.TransferDetail.Args.BATCH_ID.default as String)
                    val isHidden = backStackEntry.arguments?.getBoolean(AppNavRoute.TransferDetail.Args.IS_HIDDEN.argName) ?: (AppNavRoute.TransferDetail.Args.IS_HIDDEN.default as Boolean)

                    TransferDetailScreen(
                        isHidden,
                        batchId,
                        title,
                        networkVm,
                        navController,
                    )
                }
                // WebVpn
                transitionComposable(AppNavRoute.WebVpn.route) {
                    WebVpnScreen(networkVm,navController)
                }
                // 通知公告
                transitionComposable(AppNavRoute.News.route) {
                    NewsScreen(networkVm,navController)
                }
                // 同班同学
                transitionComposable(AppNavRoute.Classmates.route) {
                    ClassmatesScreen(networkVm,navController)
                }
                // 扫码登录
                transitionComposable(AppNavRoute.Scan.route) {
                    ScanScreen(networkVm,navController)
                }
                // 校车
                transitionComposable(AppNavRoute.Bus.route) {
                    BusScreen(networkVm,navController)
                }
                // 办事
                transitionComposable(AppNavRoute.OfficeHall.route) {
                    OfficeHallScreen(networkVm,navController)
                }
                // 查询中心编辑
                transitionComposable(AppNavRoute.SearchEdit.route) {
                    SearchEditScreen(navController)
                }
                // 全校培养方案
                transitionComposable(
                    route = AppNavRoute.ProgramSearch.receiveRoute(),
                    arguments = getArgs(AppNavRoute.ProgramSearch.Args.entries)
                ) { backStackEntry ->
                    val ifSaved = backStackEntry.arguments?.getBoolean(AppNavRoute.ProgramSearch.Args.IF_SAVED.argName) ?: (AppNavRoute.ProgramSearch.Args.IF_SAVED.default as Boolean)

                    ProgramSearchScreen(
                        networkVm,
                        ifSaved,
                        navController,
                    )
                }
                // 培养方案
                transitionComposable(
                    route = AppNavRoute.Program.receiveRoute(),
                    arguments = getArgs(AppNavRoute.Program.Args.entries)
                ) { backStackEntry ->
                    val ifSaved = backStackEntry.arguments?.getBoolean(AppNavRoute.Program.Args.IF_SAVED.argName) ?: (AppNavRoute.Program.Args.IF_SAVED.default as Boolean)

                    ProgramScreen(
                        networkVm,
                        ifSaved,
                        navController,
                    )
                }
                // 课程汇总
                transitionComposable(
                    route = AppNavRoute.TotalCourse.receiveRoute(),
                    arguments = getArgs(AppNavRoute.TotalCourse.Args.entries)
                ) { backStackEntry ->
                    val ifSaved = backStackEntry.arguments?.getBoolean(AppNavRoute.TotalCourse.Args.IF_SAVED.argName) ?: (AppNavRoute.TotalCourse.Args.IF_SAVED.default as Boolean)

                    TotalCourseScreen(
                        networkVm,
                        ifSaved,
                        navController,
                    )
                }
                // 下学期课表
                transitionComposable(
                    route = AppNavRoute.NextCourse.receiveRoute(),
                    arguments = getArgs(AppNavRoute.NextCourse.Args.entries)
                ) { backStackEntry ->
                    val ifSaved = backStackEntry.arguments?.getBoolean(AppNavRoute.NextCourse.Args.IF_SAVED.argName) ?: (AppNavRoute.NextCourse.Args.IF_SAVED.default as Boolean)

                    NextCourseScreen(
                        networkVm,
                        uiVm,
                        ifSaved,
                        navController,
                    )
                }
                // 培养方案完成情况
                transitionComposable(
                    route = AppNavRoute.ProgramCompetition.receiveRoute(),
                    arguments = getArgs(AppNavRoute.ProgramCompetition.Args.entries)
                ) { backStackEntry ->
                    val ifSaved = backStackEntry.arguments?.getBoolean(AppNavRoute.ProgramCompetition.Args.IF_SAVED.argName) ?: (AppNavRoute.ProgramCompetition.Args.IF_SAVED.default as Boolean)

                    ProgramCompetitionScreen(
                        networkVm,
                        ifSaved,
                        navController,
                    )
                }
                // 培养方案完成情况 二级界面
                transitionComposable(
                    route = AppNavRoute.ProgramCompetitionDetail.receiveRoute(),
                    arguments = getArgs(AppNavRoute.ProgramCompetitionDetail.Args.entries)
                ) { backStackEntry ->
                    val index = backStackEntry.arguments?.getInt(AppNavRoute.ProgramCompetitionDetail.Args.INDEX.argName) ?: (AppNavRoute.ProgramCompetitionDetail.Args.INDEX.default as Int)
                    val title = backStackEntry.arguments?.getString(AppNavRoute.ProgramCompetitionDetail.Args.TITLE.argName) ?: (AppNavRoute.ProgramCompetitionDetail.Args.TITLE.default as String)

                    if(index < 0) {
                        return@transitionComposable
                    }
                    ProgramCompetitionDetailScreen(
                        networkVm,
                        title,
                        index,
                        navController,
                    )
                }
                // WebView
                transitionComposable(
                    route = AppNavRoute.WebView.receiveRoute(),
                    arguments = getArgs(AppNavRoute.WebView.Args.entries)
                ) { backStackEntry ->
                    val url = backStackEntry.arguments?.getString(AppNavRoute.WebView.Args.URL.argName) ?: return@transitionComposable
                    val cookies = backStackEntry.arguments?.getString(AppNavRoute.WebView.Args.COOKIES.argName)
                    val title = backStackEntry.arguments?.getString(AppNavRoute.WebView.Args.TITLE.argName) ?: getPureUrl(url)
                    val icon = backStackEntry.arguments?.getInt(AppNavRoute.WebView.Args.ICON.argName)

                    WebViewScreenForNavigation(
                        url,
                        title,
                        icon,
                        cookies,
                        navController,
                        drawerState
                    ) { containerColor = it }
                }
                // 开课课程表
                transitionComposable(
                    route = AppNavRoute.CourseSearchCalendar.receiveRoute(),
                    arguments = getArgs(AppNavRoute.CourseSearchCalendar.Args.entries)
                ) { backStackEntry ->
                    val term = backStackEntry.arguments?.getInt(AppNavRoute.CourseSearchCalendar.Args.TERM.argName)
                    if(term == null || term == -1) {
                        return@transitionComposable
                    }
                    val name = backStackEntry.arguments?.getString(AppNavRoute.CourseSearchCalendar.Args.COURSE_NAME.argName)
                    val code = backStackEntry.arguments?.getString(AppNavRoute.CourseSearchCalendar.Args.COURSE_CODE.argName)
                    val classes = backStackEntry.arguments?.getString(AppNavRoute.CourseSearchCalendar.Args.CLASSES.argName)

                    CourseSearchCalendarScreen(
                        term,
                        name,
                        code,
                        classes,
                        networkVm,
                        uiVm,
                        navController,
                    )
                }
                // Exception
                transitionComposable(
                    route = AppNavRoute.Exception.receiveRoute(),
                    arguments = getArgs(AppNavRoute.Exception.Args.entries)
                ) { backStackEntry ->
                    val exception = backStackEntry.arguments?.getString(AppNavRoute.Exception.Args.EXCEPTION.argName) ?: (AppNavRoute.Exception.Args.EXCEPTION.default as String)

                    NavigationExceptionScreen(
                        exception,
                        navController,
                    )
                }
            }
        }
    }
}


@Composable
fun TestAnimation() {
    val anim = remember { Animatable(0f) }
    var result: Long by remember { mutableLongStateOf(0L) }
    val scope = rememberCoroutineScope()
    CenterScreen {
        Button(
            onClick = {
                scope.launch {
                    // 复位
                    anim.animateTo(0f)
                    // 测量
                    val start = System.currentTimeMillis()
                    anim.animateTo(
                        targetValue = 1f,
                        animationSpec = spring(
                            dampingRatio = TransitionConfig.curveStyle.dampingRatio,
                            stiffness = TransitionConfig.curveStyle.stiffness.toFloat()
                        )
                    )
                    val end = System.currentTimeMillis()
                    result = end-start
                }
            }
        ) {
            Text("测量 $result ms")
        }
    }
}