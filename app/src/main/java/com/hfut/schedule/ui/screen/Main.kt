package com.hfut.schedule.ui.screen

import android.annotation.SuppressLint
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.datetime.getCelebration
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.screen.Party
import com.hfut.schedule.ui.component.screen.PartyPlace
import com.hfut.schedule.ui.component.webview.WebViewScreenForNavigation
import com.hfut.schedule.ui.component.webview.getPureUrl
import com.hfut.schedule.ui.screen.control.ControlCenterScreen
import com.hfut.schedule.ui.screen.control.limitDrawerSwipeArea
import com.hfut.schedule.ui.screen.grade.GradeScreen
import com.hfut.schedule.ui.screen.home.MainScreen
import com.hfut.schedule.ui.screen.home.calendar.communtiy.CourseDetailApiScreen
import com.hfut.schedule.ui.screen.home.search.function.community.bus.BusScreen
import com.hfut.schedule.ui.screen.home.search.function.community.failRate.FailRateScreen
import com.hfut.schedule.ui.screen.home.search.function.community.library.LibraryScreen
import com.hfut.schedule.ui.screen.home.search.function.community.workRest.TimeTableScreen
import com.hfut.schedule.ui.screen.home.search.function.huiXin.washing.HaiLeWashingScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.courseSearch.CourseSearchScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam.ExamScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.nextCourse.NextCourseScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.ClassmatesScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.PersonScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.program.ProgramCompetitionDetailScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.program.ProgramCompetitionScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.program.ProgramScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.program.ProgramSearchScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.selectCourse.SelectCourseScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.survey.SurveyScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.TotalCourseScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.TransferScreen
import com.hfut.schedule.ui.screen.home.search.function.my.holiday.HolidayScreen
import com.hfut.schedule.ui.screen.home.search.function.my.notification.NotificationsScreen
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.NotificationBoxScreen
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.WebNavigationScreen
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
import com.hfut.schedule.ui.screen.login.UseAgreementScreen
import com.hfut.schedule.ui.screen.news.home.NewsScreen
import com.hfut.schedule.ui.screen.other.NavigationExceptionScreen
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.ui.util.AppAnimationManager.CONTROL_CENTER_ANIMATION_SPEED
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.xah.transition.util.isCurrentRouteWithoutArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    isSuccessActivityWebVpn : Boolean
) {
    val switchUpload by remember { mutableStateOf(prefs.getBoolean("SWITCHUPLOAD",true )) }
    val startActivity by produceState<Boolean>(initialValue = prefs.getBoolean("SWITCHFASTSTART",prefs.getString("TOKEN","")?.isNotEmpty() ?: false)) {
        value = DataStoreManager.enableQuickStart.first()
    }
    val navController = rememberNavController()
    val first by remember { mutableStateOf(if(prefs.getBoolean("canUse",false)) AppNavRoute.Home.route else AppNavRoute.UseAgreement.route) }
    var value by remember { mutableIntStateOf(0) }
    // 初始化网络请求
    if(!isSuccessActivity)
        LaunchedEffect(Unit) {
            launch {
                // 修正之前的Bug
                val auth = prefs.getString("auth","") ?: return@launch
                if(auth.contains("&")) {
                    SharedPrefs.saveString("auth",auth.substringBefore("&"))
                    showToast("已自动修复一卡通登录状态无效的Bug")
                }
            }
            launch { AppAnimationManager.updateAnimationSpeed() }
            // 如果进入的是登陆界面 未登录做准备
            if(!(startActivity && login)) {
                //从服务器获取信息
                launch { loginVm.getMyApi() }
                launch { loginVm.getCookie() }
                launch { SharedPrefs.saveString("tip","0") }
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
                (fraction * 37.5).dp
            }
        }
    }
    val scale by remember {
        derivedStateOf {
            if (maxOffset == 0f) {
                1f
            } else {
                val fraction =  (drawerState.currentOffset / maxOffset).coerceIn(0f, 1f)
                (if(motionBlur) 0.85f else 0.8875f) * (1 - fraction) + fraction
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

    ModalNavigationDrawer  (
        scrimColor = MaterialTheme.colorScheme.surface.copy(if(motionBlur && !isScan) 0.35f else 0.925f),
        drawerState = drawerState,
        gesturesEnabled = enableGesture,
        drawerContent = {
            ControlCenterScreen(navController) {
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
            // 磁钉体系
            SharedTransitionLayout(
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

            ) {
                NavHost(
                    navController = navController,
                    startDestination = first,
                    enterTransition = { AppAnimationManager.fadeAnimation.enter },
                    exitTransition = { AppAnimationManager.fadeAnimation.exit },
                ) {
                    // 主UI
                    composable(AppNavRoute.Home.route) {
                        val mainUI = @Composable { celebrationText : String? ->
                            if(isSuccessActivity) {
                                MainScreen(
                                    vm = networkVm,
                                    vm2 = loginVm,
                                    vmUI = uiVm,
                                    celebrationText = celebrationText,
                                    webVpn = isSuccessActivityWebVpn,
                                    isLogin = true,
                                    navHostTopController = navController,
                                    this@SharedTransitionLayout,
                                    this@composable
                                )
                            } else if(startActivity && login) {
                                MainScreen(
                                    networkVm,
                                    loginVm,
                                    uiVm,
                                    celebrationText,
                                    false,
                                    false,
                                    navHostTopController = navController,
                                    this@SharedTransitionLayout,
                                    this@composable,
                                )
                            } else LoginScreen(
                                loginVm,
                                navController,
                                this@SharedTransitionLayout,
                                this@composable,
                            )
                        }
                        // 如果庆祝为true则庆祝
                        getCelebration().let {
                            Box() {
                                mainUI(it.str)
                                Party(show = it.use, timeSecond = it.time)
                            }

                        }
                    }
                    // 用户协议
                    composable(AppNavRoute.UseAgreement.route) {
                        Box() {
                            Party(timeSecond = 2L)
                            UseAgreementScreen(navController,this@SharedTransitionLayout, this@composable,)
                        }
                    }
                    // 成绩
                    composable(
                        route = AppNavRoute.Grade.receiveRoute(),
                        arguments = getArgs(AppNavRoute.Grade.Args.entries)
                    ) { backStackEntry ->
                        val ifSaved = backStackEntry.arguments?.getBoolean(AppNavRoute.Grade.Args.IF_SAVED.argName) ?: (AppNavRoute.Grade.Args.IF_SAVED.default as Boolean)
                        GradeScreen(
                            ifSaved,
                            networkVm,
                            navController,
                            this@SharedTransitionLayout,
                            this@composable,
                        )
                    }
                    // 招生
                    composable(AppNavRoute.Admission.route) {
                        AdmissionScreen(networkVm, this@SharedTransitionLayout, this@composable, navController)
                    }
                    // 招生 二级界面
                    composable(
                        route = AppNavRoute.AdmissionRegionDetail.receiveRoute(),
                        arguments = getArgs(AppNavRoute.AdmissionRegionDetail.Args.entries)
                    ) { backStackEntry ->
                        val index = backStackEntry.arguments?.getInt(AppNavRoute.AdmissionRegionDetail.Args.INDEX.argName) ?: (AppNavRoute.AdmissionRegionDetail.Args.INDEX.default as Int)
                        val type = backStackEntry.arguments?.getString(AppNavRoute.AdmissionRegionDetail.Args.TYPE.argName) ?: (AppNavRoute.AdmissionRegionDetail.Args.TYPE.default as String)

                        AdmissionRegionScreen(
                            networkVm,
                            this@SharedTransitionLayout,
                            this@composable,
                            navController,
                            type,
                            index,
                        )
                    }
                    // 课程详情
                    composable(
                        route = AppNavRoute.CourseDetail.receiveRoute(),
                        arguments = getArgs(AppNavRoute.CourseDetail.Args.entries)
                    ) { backStackEntry ->
                        val courseName = backStackEntry.arguments?.getString(AppNavRoute.CourseDetail.Args.NAME.argName) ?: (AppNavRoute.CourseDetail.Args.NAME.default as String)
                        val index = backStackEntry.arguments?.getInt(AppNavRoute.CourseDetail.Args.INDEX.argName) ?: (AppNavRoute.CourseDetail.Args.INDEX.default as Int)

                        CourseDetailApiScreen(
                            courseName,
                            index,
                            networkVm,
                            this@SharedTransitionLayout,
                            this@composable,
                            navController
                        )
                    }
                    // 法定假日
                    composable(route = AppNavRoute.Holiday.route,) {
                        HolidayScreen(navController, this@SharedTransitionLayout, this@composable,)
                    }
                    // 微信专区
                    composable(route = AppNavRoute.Wechat.route) {
                        WeChatScreen(navController, this@SharedTransitionLayout, this@composable,)
                    }
                    // 作息
                    composable(route = AppNavRoute.TimeTable.route) {
                        TimeTableScreen(navController, this@SharedTransitionLayout, this@composable,)
                    }
                    // 海乐生活
                    composable(route = AppNavRoute.HaiLeWashing.route) {
                        HaiLeWashingScreen(networkVm,navController, this@SharedTransitionLayout, this@composable)
                    }
                    // 学费
                    composable(route = AppNavRoute.Fee.route) {
                        FeeScreen(networkVm,navController, this@SharedTransitionLayout, this@composable)
                    }
                    // 学工系统
                    composable(route = AppNavRoute.StuTodayCampus.route) {
                        StuTodayCampusScreen(networkVm,navController, this@SharedTransitionLayout, this@composable)
                    }
                    // 教师检索
                    composable(route = AppNavRoute.TeacherSearch.route) {
                        TeacherSearchScreen(networkVm,navController, this@SharedTransitionLayout, this@composable)
                    }
                    // 就业
                    composable(route = AppNavRoute.Work.route) {
                        WorkScreen(networkVm,navController, this@SharedTransitionLayout, this@composable)
                    }
                    // 开课查询
                    composable(route = AppNavRoute.CourseSearch.route) {
                        CourseSearchScreen(networkVm,navController, this@SharedTransitionLayout, this@composable)
                    }
                    // 个人信息
                    composable(route = AppNavRoute.Person.route) {
                        PersonScreen(networkVm,navController, this@SharedTransitionLayout, this@composable)
                    }
                    // 消息中心
                    composable(route = AppNavRoute.Notifications.route) {
                        NotificationsScreen(navController, this@SharedTransitionLayout, this@composable)
                    }
                    // 寝室评分
                    composable(route = AppNavRoute.DormitoryScore.route) {
                        DormitoryScoreScreen(networkVm,navController, this@SharedTransitionLayout, this@composable)
                    }
                    // 考试
                    composable(route = AppNavRoute.Exam.route) {
                        ExamScreen(navController, this@SharedTransitionLayout, this@composable)
                    }
                    // 转专业
                    composable(route = AppNavRoute.Transfer.route) {
                        TransferScreen(networkVm,navController, this@SharedTransitionLayout, this@composable)
                    }
                    // 评教
                    composable(route = AppNavRoute.Survey.route) {
                        SurveyScreen(networkVm,navController, this@SharedTransitionLayout, this@composable)
                    }
                    // 图书馆
                    composable(route = AppNavRoute.Library.route) {
                        LibraryScreen(networkVm,navController, this@SharedTransitionLayout, this@composable)
                    }
                    // 挂科率
                    composable(AppNavRoute.FailRate.route) {
                        FailRateScreen(networkVm, navController,this@SharedTransitionLayout, this@composable)
                    }
                    // 选课
                    composable(AppNavRoute.SelectCourse.route) {
                        SelectCourseScreen(networkVm, uiVm,navController,this@SharedTransitionLayout, this@composable)
                    }
                    // 网址导航
                    composable(AppNavRoute.WebNavigation.route) {
                        WebNavigationScreen(navController,this@SharedTransitionLayout, this@composable)
                    }
                    // 收纳
                    composable(AppNavRoute.NotificationBox.route) {
                        NotificationBoxScreen(navController,this@SharedTransitionLayout, this@composable)
                    }
                    // 生活服务
                    composable(AppNavRoute.Life.route) {
                        LifeScreen(networkVm,navController,this@SharedTransitionLayout, this@composable)
                    }
                    // WebVpn
                    composable(AppNavRoute.WebVpn.route) {
                        WebVpnScreen(networkVm,navController,this@SharedTransitionLayout, this@composable)
                    }
                    // 通知公告
                    composable(AppNavRoute.News.route) {
                        NewsScreen(networkVm,navController,this@SharedTransitionLayout, this@composable)
                    }
                    // 同班同学
                    composable(AppNavRoute.Classmates.route) {
                        ClassmatesScreen(networkVm,navController,this@SharedTransitionLayout, this@composable)
                    }
                    // 扫码登录
                    composable(AppNavRoute.Scan.route) {
                        ScanScreen(networkVm,navController,this@SharedTransitionLayout, this@composable)
                    }
                    // 校车
                    composable(AppNavRoute.Bus.route) {
                        BusScreen(networkVm,navController,this@SharedTransitionLayout, this@composable)
                    }
                    // 办事
                    composable(AppNavRoute.OfficeHall.route) {
                        OfficeHallScreen(networkVm,navController,this@SharedTransitionLayout, this@composable)
                    }
                    // 全校培养方案
                    composable(
                        route = AppNavRoute.ProgramSearch.receiveRoute(),
                        arguments = getArgs(AppNavRoute.ProgramSearch.Args.entries)
                    ) { backStackEntry ->
                        val ifSaved = backStackEntry.arguments?.getBoolean(AppNavRoute.ProgramSearch.Args.IF_SAVED.argName) ?: (AppNavRoute.ProgramSearch.Args.IF_SAVED.default as Boolean)

                        ProgramSearchScreen(
                            networkVm,
                            ifSaved,
                            navController,
                            this@SharedTransitionLayout,
                            this@composable,
                        )
                    }
                    // 培养方案
                    composable(
                        route = AppNavRoute.Program.receiveRoute(),
                        arguments = getArgs(AppNavRoute.Program.Args.entries)
                    ) { backStackEntry ->
                        val ifSaved = backStackEntry.arguments?.getBoolean(AppNavRoute.Program.Args.IF_SAVED.argName) ?: (AppNavRoute.Program.Args.IF_SAVED.default as Boolean)

                        ProgramScreen(
                            networkVm,
                            ifSaved,
                            navController,
                            this@SharedTransitionLayout,
                            this@composable,
                        )
                    }
                    // 课程汇总
                    composable(
                        route = AppNavRoute.TotalCourse.receiveRoute(),
                        arguments = getArgs(AppNavRoute.TotalCourse.Args.entries)
                    ) { backStackEntry ->
                        val ifSaved = backStackEntry.arguments?.getBoolean(AppNavRoute.TotalCourse.Args.IF_SAVED.argName) ?: (AppNavRoute.TotalCourse.Args.IF_SAVED.default as Boolean)

                        TotalCourseScreen(
                            networkVm,
                            ifSaved,
                            navController,
                            this@SharedTransitionLayout,
                            this@composable,
                        )
                    }
                    // 下学期课表
                    composable(
                        route = AppNavRoute.NextCourse.receiveRoute(),
                        arguments = getArgs(AppNavRoute.NextCourse.Args.entries)
                    ) { backStackEntry ->
                        val ifSaved = backStackEntry.arguments?.getBoolean(AppNavRoute.NextCourse.Args.IF_SAVED.argName) ?: (AppNavRoute.NextCourse.Args.IF_SAVED.default as Boolean)

                        NextCourseScreen(
                            networkVm,
                            uiVm,
                            ifSaved,
                            navController,
                            this@SharedTransitionLayout,
                            this@composable,
                        )
                    }
                    // 培养方案完成情况
                    composable(
                        route = AppNavRoute.ProgramCompetition.receiveRoute(),
                        arguments = getArgs(AppNavRoute.ProgramCompetition.Args.entries)
                    ) { backStackEntry ->
                        val ifSaved = backStackEntry.arguments?.getBoolean(AppNavRoute.ProgramCompetition.Args.IF_SAVED.argName) ?: (AppNavRoute.ProgramCompetition.Args.IF_SAVED.default as Boolean)

                        ProgramCompetitionScreen(
                            networkVm,
                            ifSaved,
                            navController,
                            this@SharedTransitionLayout,
                            this@composable,
                        )
                    }
                    // 培养方案完成情况 二级界面
                    composable(
                        route = AppNavRoute.ProgramCompetitionDetail.receiveRoute(),
                        arguments = getArgs(AppNavRoute.ProgramCompetitionDetail.Args.entries)
                    ) { backStackEntry ->
                        val index = backStackEntry.arguments?.getInt(AppNavRoute.ProgramCompetitionDetail.Args.INDEX.argName) ?: (AppNavRoute.ProgramCompetitionDetail.Args.INDEX.default as Int)
                        val title = backStackEntry.arguments?.getString(AppNavRoute.ProgramCompetitionDetail.Args.TITLE.argName) ?: (AppNavRoute.ProgramCompetitionDetail.Args.TITLE.default as String)

                        if(index < 0) {
                            return@composable
                        }
                        ProgramCompetitionDetailScreen(
                            networkVm,
                            title,
                            index,
                            navController,
                            this@SharedTransitionLayout,
                            this@composable,
                        )
                    }
                    // WebView
                    composable(
                        route = AppNavRoute.WebView.receiveRoute(),
                        arguments = getArgs(AppNavRoute.WebView.Args.entries)
                    ) { backStackEntry ->
                        val url = backStackEntry.arguments?.getString(AppNavRoute.WebView.Args.URL.argName) ?: return@composable
                        val cookies = backStackEntry.arguments?.getString(AppNavRoute.WebView.Args.COOKIES.argName)
                        val title = backStackEntry.arguments?.getString(AppNavRoute.WebView.Args.TITLE.argName) ?: getPureUrl(url)
                        val icon = backStackEntry.arguments?.getInt(AppNavRoute.WebView.Args.ICON.argName)

                        WebViewScreenForNavigation(
                            url,
                            title,
                            icon,
                            cookies,
                            navController,
                            this@SharedTransitionLayout,
                            this@composable,
                            drawerState
                        ) { containerColor = it }
                    }
                    // Exception
                    composable(
                        route = AppNavRoute.Exception.receiveRoute(),
                        arguments = getArgs(AppNavRoute.Exception.Args.entries)
                    ) { backStackEntry ->
                        val exception = backStackEntry.arguments?.getString(AppNavRoute.Exception.Args.EXCEPTION.argName) ?: (AppNavRoute.Exception.Args.EXCEPTION.default as String)

                        NavigationExceptionScreen(
                            exception,
                            navController,
                            this@SharedTransitionLayout,
                            this@composable,
                        )
                    }
                }
            }
        }
    }
}