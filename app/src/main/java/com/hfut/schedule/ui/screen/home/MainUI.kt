package com.hfut.schedule.ui.screen.home

import android.annotation.SuppressLint
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.BottomBarItems
import com.hfut.schedule.logic.enumeration.BottomBarItems.COURSES
import com.hfut.schedule.logic.enumeration.BottomBarItems.FOCUS
import com.hfut.schedule.logic.enumeration.BottomBarItems.SEARCH
import com.hfut.schedule.logic.enumeration.BottomBarItems.SETTINGS
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.logic.enumeration.SortType
import com.hfut.schedule.logic.model.NavigationBarItemData
import com.hfut.schedule.logic.util.network.ParseJsons.isNextOpen
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.DataStoreManager.SEARCH_DEFAULT_STR
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveInt
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.Date_MM_dd
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.weeksBetween
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.HazeBottomBar
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.mixedCardNormalColor
import com.hfut.schedule.ui.component.dialog.LittleDialog
import com.hfut.schedule.ui.component.divider.ScrollHorizontalTopDivider
import com.hfut.schedule.ui.component.network.onListenStateHolder
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.screen.pager.CustomTabRow
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.calendar.communtiy.CommunityCourseTableUI
import com.hfut.schedule.ui.screen.home.calendar.communtiy.ScheduleTopDate
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.JxglstuCourseTableUI
import com.hfut.schedule.ui.screen.home.calendar.lesson.JxglstuCourseTableTwo
import com.hfut.schedule.ui.screen.home.calendar.multi.CourseType
import com.hfut.schedule.ui.screen.home.calendar.multi.MultiScheduleSettings
import com.hfut.schedule.ui.screen.home.calendar.next.JxglstuCourseTableUINext
import com.hfut.schedule.ui.screen.home.cube.SettingsScreen
import com.hfut.schedule.ui.screen.home.cube.sub.update.getUpdates
import com.hfut.schedule.ui.screen.home.focus.TodayScreen
import com.hfut.schedule.ui.screen.home.search.SearchAppBeanLite
import com.hfut.schedule.ui.screen.home.search.SearchFuncs
import com.hfut.schedule.ui.screen.home.search.SearchScreen
import com.hfut.schedule.ui.screen.home.search.function.community.workRest.ApiForTimeTable
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.CourseTotalForApi
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.TotalCourseDataSource
import com.hfut.schedule.ui.screen.home.search.function.my.notification.getNotifications
import com.hfut.schedule.ui.screen.supabase.login.ApiToSupabase
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.ui.util.AppAnimationManager.currentPage
import com.hfut.schedule.ui.util.GlobalUIStateHolder
import com.hfut.schedule.ui.util.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.xah.transition.component.containerShare
import com.xah.transition.component.iconElementShare
import com.xah.transition.util.currentRouteWithoutArgs
import com.xah.uicommon.component.text.ScrollText
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.color.topBarTransplantColor
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState

private val titles = listOf("重要安排","其他事项")

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("SuspiciousIndentation", "CoroutineCreationDuringComposition",
    "UnusedMaterial3ScaffoldPaddingParameter"
)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    ExperimentalGlideComposeApi::class
)
@Composable
fun MainScreen(
    vm : NetWorkViewModel,
    vmUI : UIViewModel,
    celebrationText : String?,
//    webVpn : Boolean,
    isLogin : Boolean,
    navHostTopController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val navController = rememberNavController()
    var isEnabled by rememberSaveable(AppNavRoute.Home.route) { mutableStateOf(!isLogin) }
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val hazeState = rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code)

    val showBadge by produceState(initialValue = false) {
        value = getUpdates().version != AppVersion.getVersionName()
    }

//判定是否以聚焦作为第一页
    val first  by rememberSaveable { mutableStateOf(
        if(isLogin) COURSES
        else when (prefs.getBoolean("SWITCHFOCUS",true)) {
            true -> FOCUS
            false -> COURSES
        }
    ) }
    val targetPage = when(navController.currentRouteWithoutArgs()) {
        COURSES.name -> COURSES
        FOCUS.name -> FOCUS
        SEARCH.name -> SEARCH
        SETTINGS.name -> SETTINGS
        else -> first
    }

    var showAll by rememberSaveable { mutableStateOf(DateTimeManager.isOnWeekend()) }
    var findCourse by remember { mutableStateOf(false) }


    var ifSaved by rememberSaveable { mutableStateOf(!isLogin) }
    val defaultCalendar = prefs.getInt("SWITCH_DEFAULT_CALENDAR", CourseType.JXGLSTU.code)
    var swapUI by rememberSaveable { mutableIntStateOf(if(ifSaved) defaultCalendar else CourseType.JXGLSTU.code) }

    var showBottomSheet_multi by remember { mutableStateOf(false) }

    val currentAnimationIndex by DataStoreManager.animationType.collectAsState(initial = 0)
    val alpha by DataStoreManager.customBackgroundAlpha.collectAsState(initial = 1f)

    if (showBottomSheet_multi) {
        HazeBottomSheet(
            showBottomSheet = showBottomSheet_multi,
            onDismissRequest = { showBottomSheet_multi = false },
            hazeState = hazeState,
            autoShape = false
        ) {
            Column {
                MultiScheduleSettings(ifSaved,swapUI,
                    onSelectedChange = { newSelected ->
                        swapUI = newSelected
                    },
                    vm,
                    vmUI,
                    hazeState,
                )
                Spacer(modifier = Modifier.height(APP_HORIZONTAL_DP))
            }
        }
    }

    //监听是否周六周日有课，有则显示红点
    val courseObserver = Observer<Boolean> { result ->
        findCourse = result
    }

    var today by rememberSaveable(0) { mutableStateOf(DateTimeManager.getToday()) }
    val pagerState = rememberPagerState(pageCount = { titles.size })
    var searchText by rememberSaveable() { mutableStateOf("") }
    var showSearch by rememberSaveable() { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        if(!isNextOpen()) {
            //重置
            saveInt("FIRST",0)
        }
        if(!isLogin) {
            onListenStateHolder(vm.bizTypeIdResponse) { data ->
                // 检测是否教务token还有效
                ifSaved = data == -1
            }
        }
        Handler(Looper.getMainLooper()).post {
            vmUI.findNewCourse.observeForever(courseObserver)
        }
        // 等待加载完毕可切换标签
        if(isLogin) {
            if(!GlobalUIStateHolder.webVpn) ifSaved = false
        }
    }
    // 保存上一页页码 用于决定左右动画
    if(currentAnimationIndex == 2) {
        LaunchedEffect(targetPage) {
            currentPage = targetPage.page
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var isNavigationIconVisible by rememberSaveable { mutableStateOf(true) }
    // 监听滚动状态
    if(targetPage == FOCUS) {
        LaunchedEffect(scrollBehavior.state) {
            snapshotFlow { scrollBehavior.state.collapsedFraction }
                .collect { collapsedFraction -> isNavigationIconVisible = collapsedFraction < 0.5f }
        }
    }

    var sortReversed by rememberSaveable { mutableStateOf(false) }
    var sortType by rememberSaveable { mutableStateOf(SortType.TIME_LINE) }
    var showDialog by remember { mutableStateOf(false) }
    if(showDialog) {
        HazeBottomSheet(
            showBottomSheet = showDialog,
            isFullExpand = false,
            autoShape = false,
            hazeState = hazeState,
            onDismissRequest = { showDialog = false }
        ) {
            Column {
                HazeBottomSheetTopBar("排序", isPaddingStatusBar = false)
                CardListItem(
                    overlineContent = { Text("排序方式") },
                    headlineContent = {
                        Text(
                            when (sortType) {
                                SortType.TIME_LINE -> "时间线"
                                SortType.CREATE_TIME -> "创建时间"
                            }
                        )
                    },
                    leadingContent = { Icon(painterResource(R.drawable.sort),null) },
                    modifier = Modifier.clickable {
                        sortType = when (sortType) {
                            SortType.TIME_LINE -> SortType.CREATE_TIME
                            SortType.CREATE_TIME -> SortType.TIME_LINE
                        }
                    },
                    trailingContent = {
                        FilledTonalIconButton(onClick = {
                            sortReversed = !sortReversed
                        }) {
                            Icon(
                                if (sortReversed) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                                contentDescription = ""
                            )
                        }
                    }
                )
                Spacer(Modifier.height(APP_HORIZONTAL_DP))
            }
        }
    }
    val scope = rememberCoroutineScope()
    val focusActions = @Composable {
        Row {
            val show = isNavigationIconVisible && celebrationText == null
            if(show) {

                IconButton(onClick = {
                    sortReversed = !sortReversed
                }) {
                    Icon(
                        if (sortReversed) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                TextButton(
                    onClick = {
                        sortType = when (sortType) {
                            SortType.TIME_LINE -> SortType.CREATE_TIME
                            SortType.CREATE_TIME -> SortType.TIME_LINE
                        }
                    }
                ) {
                    Text(
                        when (sortType) {
                            SortType.TIME_LINE -> "时间线"
                            SortType.CREATE_TIME -> "创建时间"
                        }
                    )
                }
//                Spacer(Modifier.width(5.dp))
            } else {
                IconButton(
                    onClick = {
                        if(celebrationText != null) {
                            showDialog = true
                        } else {
                            isNavigationIconVisible = true
                        }
                    }
                ) {
                    Icon(
                        painterResource(R.drawable.sort),
                        null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
//            ApiFromLife(vm, hazeState)
            ApiToSupabase(vm)
            val iconRoute = remember { AppNavRoute.NotificationBox.route }
            IconButton(onClick = {
                navHostTopController.navigateForTransition(AppNavRoute.NotificationBox,iconRoute,transplantBackground = true)
            }) {
                BadgedBox(badge = {
                    if (getNotifications().size.toString() != prefs.getString("Notifications",""))
                        Badge()
                }) {
                    Icon(painterResource(id = AppNavRoute.NotificationBox.icon), contentDescription = "", tint = MaterialTheme.colorScheme.primary,modifier = Modifier.iconElementShare(sharedTransitionScope,animatedContentScope = animatedContentScope, route = iconRoute))
                }
            }
        }
    }
    val context = LocalContext.current
    val activity = LocalActivity.current

    BackHandler {
        activity?.finish()
    }
    with(sharedTransitionScope) {
        CustomTransitionScaffold(
            navHostController = navHostTopController,
            animatedContentScope = animatedContentScope,
            route = AppNavRoute.Home.route,
            roundShape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier.let {
                if (targetPage != COURSES) {
                    it.nestedScroll(scrollBehavior.nestedScrollConnection)
                } else {
                    it
                }
            },
            floatingActionButton = {
                val addRoute = remember { AppNavRoute.AddEvent.route }
                AnimatedVisibility(
                    enter = scaleIn(),
                    exit = scaleOut(),
                    visible = isNavigationIconVisible && (targetPage == FOCUS)
                ) {
                    FloatingActionButton(
                        modifier = Modifier
                            .containerShare(
                                sharedTransitionScope,
                                animatedContentScope,
                                addRoute,
                                FloatingActionButtonDefaults.shape
                            ),
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
                        onClick = {
                            navHostTopController.navigateForTransition(
                                AppNavRoute.AddEvent,
                                AppNavRoute.AddEvent.route
                            )
                        },
                    ) {
                        Icon(
                            painterResource(AppNavRoute.AddEvent.icon),
                            "Add Button",
                            modifier = Modifier.iconElementShare(
                                sharedTransitionScope,
                                animatedContentScope,
                                addRoute
                            )
                        )
                    }
                }
            },
            topBar = {
                Column(modifier = Modifier.topBarBlur(hazeState)) {
                    if (targetPage != COURSES) {
                        MediumTopAppBar(
                            colors = topBarTransplantColor(),
                            navigationIcon = {
                                if (targetPage == FOCUS && isNavigationIconVisible && celebrationText != null) {
                                    Box(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP - 7.dp)) {
                                        ScrollText(
                                            text = celebrationText,
                                            style = MaterialTheme.typography.headlineMedium,
                                            color = MaterialTheme.colorScheme.secondaryContainer
                                        )
                                    }
                                }
                            },
                            title = { Text(texts(targetPage)) },
                            actions = {
                                when (targetPage) {
                                    SEARCH -> {
                                        val route = remember { AppNavRoute.SearchEdit.route }
                                        IconButton(onClick = {
                                            navHostTopController.navigateForTransition(
                                                AppNavRoute.SearchEdit,
                                                route,
                                                transplantBackground = true
                                            )
                                        }) {
                                            Icon(
                                                painterResource(id = R.drawable.edit),
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.iconElementShare(
                                                    sharedTransitionScope,
                                                    animatedContentScope = animatedContentScope,
                                                    route = route
                                                )
                                            )
                                        }
                                        IconButton(onClick = { showSearch = !showSearch }) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.search),
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        if (ifSaved) {
                                            IconButton(onClick = { refreshLogin(context) }) {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.login),
                                                    contentDescription = "",
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        } else {
                                            Spacer(modifier = Modifier.width(7.5.dp))
                                            Text(
                                                text = if (GlobalUIStateHolder.webVpn) "WebVpn" else "已登录",
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.width(APP_HORIZONTAL_DP))
                                        }
                                    }

                                    FOCUS -> focusActions()
                                    else -> {}
                                }
                            },
                            scrollBehavior = scrollBehavior
                        )
                        when (targetPage) {
                            FOCUS -> CustomTabRow(pagerState, titles)
                            SEARCH -> {
                                if (showSearch) {
                                    SearchFuncs(searchText, onShow = {
                                        searchText = ""
                                        showSearch = it
                                    }) {
                                        searchText = it
                                    }
                                }
                            }

                            else -> {}
                        }
                    } else {
                        TopAppBar(
                            colors = topBarTransplantColor(),
                            title = {
                                Text(texts(COURSES))
                            },
                            actions = {
                                val isFriend = CourseType.entries.all { swapUI > it.code }
                                if (isFriend) {
                                    ApiForTimeTable(swapUI.toString(), hazeState)
                                } else {
                                    CourseTotalForApi(
                                        vm = vm,
                                        isIconOrText = true,
                                        next = swapUI == CourseType.NEXT.code,
                                        onNextChange = {},
                                        hazeState = hazeState,
                                        ifSaved = ifSaved
                                    )
                                }

                                IconButton(onClick = {
                                    showBottomSheet_multi = true
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.tab_inactive),
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = { showAll = !showAll }) {
                                    BadgedBox(badge = {
                                        if (findCourse) Badge()
                                    }) {
                                        Icon(
                                            painter = painterResource(id = if (showAll) R.drawable.collapse_content else R.drawable.expand_content),
                                            contentDescription = "",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            },
                        )
                        if (swapUI == CourseType.NEXT.code) null else ScheduleTopDate(
                            showAll,
                            today
                        )
                    }
                }
            },
            bottomBar = {
                val items = listOf(
                    NavigationBarItemData(
                        COURSES.name,
                        "课程表",
                        painterResource(R.drawable.calendar),
                        painterResource(R.drawable.calendar_month_filled)
                    ),
                    NavigationBarItemData(
                        FOCUS.name,
                        "聚焦",
                        painterResource(R.drawable.lightbulb),
                        painterResource(R.drawable.lightbulb_filled)
                    ),
                    NavigationBarItemData(
                        SEARCH.name,
                        "查询中心",
                        painterResource(R.drawable.category_search),
                        painterResource(R.drawable.category_search_filled)
                    ),
                    NavigationBarItemData(
                        SETTINGS.name,
                        "选项",
                        painterResource(if (getUpdates().version == AppVersion.getVersionName()) R.drawable.deployed_code else R.drawable.deployed_code_update),
                        painterResource(if (getUpdates().version == AppVersion.getVersionName()) R.drawable.deployed_code_filled else R.drawable.deployed_code_update_filled)
                    )
                )
                HazeBottomBar(hazeState,items,navController,isEnabled,listOf(
                    null,
                    null,
                    null,
                    { if (showBadge) Badge { Text("1") } }
                ))
            },
        ) { innerPadding ->
            val animation = AppAnimationManager.getAnimationType(currentAnimationIndex, targetPage.page)

            NavHost(
                navController = navController,
                startDestination = first.name,
                enterTransition = { animation.enter },
                exitTransition = { animation.exit },
                modifier = Modifier.hazeSource(state = hazeState)
            ) {
                composable(COURSES.name) {
                    val customBackground by DataStoreManager.customBackground.collectAsState(initial = "")
                    val useCustomBackground = customBackground != ""
                    Box(modifier = Modifier.fillMaxSize()) {
                        // 背景图层
                        val backGroundHaze =
                            rememberHazeState(blurEnabled = blur >= HazeBlurLevel.FULL.code)
                        if (useCustomBackground) {
                            GlideImage(
                                model = customBackground,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                alpha = alpha,
                                modifier = Modifier
                                    .hazeSource(backGroundHaze)
                                    .fillMaxSize()
                            )
                        }
                        Scaffold(
                            containerColor = if (!useCustomBackground) {
                                MaterialTheme.colorScheme.background
                            } else {
                                Color.Transparent
                            },
                            modifier = Modifier.pointerInput(Unit) {
                                detectTransformGestures { _, _, zoom, _ ->
                                    if (zoom >= 1f) {
                                        showAll = false
                                    } else if (zoom < 1f) {
                                        showAll = true
                                    }
                                }
                            }) {
                            val isFriend = CourseType.entries.all { swapUI > it.code }
                            if (!isFriend) {
                                // 非好友课表
                                when (swapUI) {
                                    // 下学期
                                    CourseType.NEXT.code -> JxglstuCourseTableUINext(
                                        showAll,
                                        vm,
                                        vmUI,
                                        hazeState,
                                        navHostTopController,
                                        sharedTransitionScope,
                                        animatedContentScope,
                                        innerPadding,
                                        backGroundHaze = if (useCustomBackground) backGroundHaze else null
                                    )
                                    // 社区
                                    CourseType.COMMUNITY.code -> CommunityCourseTableUI(
                                        showAll,
                                        innerPadding,
                                        vmUI,
                                        onDateChange = { new -> today = new },
                                        today = today,
                                        vm = vm,
                                        hazeState = hazeState,
                                        backGroundHaze = if (useCustomBackground) backGroundHaze else null
                                    )
                                    // 教务
                                    CourseType.JXGLSTU.code -> JxglstuCourseTableUI(
                                        showAll,
                                        vm,
                                        innerPadding,
                                        vmUI,
                                        if (isLogin) GlobalUIStateHolder.webVpn else false,
                                        isLogin,
                                        { newDate -> today = newDate },
                                        today,
                                        hazeState,
                                        navHostTopController,
                                        sharedTransitionScope,
                                        animatedContentScope,
                                        if (useCustomBackground) backGroundHaze else null,
                                        isEnabled
                                    ) { isEnabled = it }
                                    // 教务2
                                    CourseType.JXGLSTU2.code -> JxglstuCourseTableTwo(
                                        showAll,
                                        vm,
                                        vmUI,
                                        hazeState,
                                        innerPadding,
                                        TotalCourseDataSource.MINE,
                                        onDateChange = { new -> today = new },
                                        today = today,
                                        backGroundHaze = if (useCustomBackground) backGroundHaze else null
                                    )
                                    // 慧新易校
                                    // 自定义导入课表 数据库id+3=swapUI
//                                else -> CustomSchedules(showAll,innerPadding,vmUI,swapUI-4,{newDate-> today = newDate}, today)
                                }
                            } else // 好友课表 swapUI为学号
                                CommunityCourseTableUI(
                                    showAll,
                                    innerPadding,
                                    vmUI,
                                    friendUserName = swapUI.toString(),
                                    onDateChange = { new -> today = new },
                                    today = today,
                                    vm,
                                    hazeState,
                                    backGroundHaze = if (useCustomBackground) backGroundHaze else null
                                )
                        }
                    }
                }
                composable(FOCUS.name) {
                    Scaffold {
                        TodayScreen(
                            vm,
                            innerPadding,
                            vmUI,
                            ifSaved,
                            pagerState,
                            hazeState = hazeState,
                            sortType,
                            sortReversed,
                            navHostTopController,
                            sharedTransitionScope,
                            animatedContentScope,
                        )
                    }
                }
                composable(SEARCH.name) {
                    Scaffold {
                        SearchScreen(
                            vm,
                            ifSaved,
                            innerPadding,
                            vmUI,
                            searchText,
                            navController = navHostTopController,
                            hazeState = hazeState,
                            sharedTransitionScope,
                            animatedContentScope
                        )
                    }
                }
                composable(SETTINGS.name) {
                    Scaffold {
                        SettingsScreen(
                            vm,
                            ifSaved,
                            innerPadding,
                            hazeState,
                            navHostTopController,
                            sharedTransitionScope,
                            animatedContentScope
                        )
                    }
                }
            }
        }
    }
}


fun texts(num : BottomBarItems) : String = when(num) {
    SEARCH -> "查询中心"
    SETTINGS -> "选项"
    else -> {
        val chineseNumber  = when (DateTimeManager.dayWeek) {
            1 -> "一"
            2 -> "二"
            3 -> "三"
            4 -> "四"
            5 -> "五"
            6 -> "六"
            0 -> "日"
            else -> ""
        }
        "$Date_MM_dd 第${weeksBetween}周 周$chineseNumber"
    }
}

fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    if (index1 in indices && index2 in indices) {
        val tmp = this[index1]
        this[index1] = this[index2]
        this[index2] = tmp
    }
}


// 按 List<Int> 排序，并把未出现的新元素追加到末尾
private fun MutableList<SearchAppBeanLite>.reorderByIds(idOrder: List<Int>): MutableList<SearchAppBeanLite> {
    val map = this.associateBy { it.id }

    // 按顺序取出原有元素
    val sorted = idOrder.mapNotNull { map[it] }.toMutableList()

    // 追加未在 idOrder 中的新元素
    val remaining = this.filter { it.id !in idOrder }
    sorted.addAll(remaining)

    this.clear()
    this.addAll(sorted)
    return this
}

// 按字符串排序
fun MutableList<SearchAppBeanLite>.reorderByIdsStr(idOrder: String): MutableList<SearchAppBeanLite> {
    return try {
        val order = idOrder.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
        reorderByIds(order)
    } catch (e: Exception) {
        e.printStackTrace()
        reorderByIds(GlobalUIStateHolder.funcDefault.map { it.id })
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchEditScreen(
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val searchSort by DataStoreManager.searchSort.collectAsState(initial = SEARCH_DEFAULT_STR)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val route = remember { AppNavRoute.SearchEdit.route }
    val funcMaps by produceState(initialValue = GlobalUIStateHolder.funcMaps, key1 = searchSort, key2 = GlobalUIStateHolder.funcMaps) {
        if(searchSort.isNotEmpty() && searchSort.isNotBlank()) {
            value = GlobalUIStateHolder.funcMaps.reorderByIdsStr(searchSort) as SnapshotStateList<SearchAppBeanLite>
        }
    }
    val scope = rememberCoroutineScope()
    var inEdit by remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current
    val state = rememberLazyGridState()
    val reorderableLazyGridState = rememberReorderableLazyGridState(state,) { from, to ->
        // 交换
        funcMaps.swap(from.index, to.index)
        // 保存
        DataStoreManager.saveSearchSort(funcMaps.map { it.id })
        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
    }
    val transition = rememberInfiniteTransition(label = "shake")

    // 在 -3° 到 3° 之间来回旋转
    val rotation by transition.animateFloat(
        initialValue = -.75f,
        targetValue = .75f,
        animationSpec = infiniteRepeatable(
            animation = tween(100, easing = LinearEasing), // 越短越快
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val hazeState = rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code)
    var showDialog by remember { mutableStateOf(false) }
    if(showDialog) {
        LittleDialog(
            onDismissRequest = { showDialog = false },
            onConfirmation = {
                scope.launch {
                    DataStoreManager.saveSearchSort(GlobalUIStateHolder.funcDefault.map { it.id })
                    showDialog = false
                    showToast("已恢复")
                }
            },
            dialogText = "恢复为初始顺序",
            hazeState = hazeState
        )
    }
    var show by remember { mutableStateOf(false) }
    Column (modifier = Modifier
        .fillMaxSize()
        .hazeSource(hazeState)) {
        MediumTopAppBar(
            modifier = Modifier.let {
                if(show) it
                else {
                    it.onSizeChanged {
                        if(it.height != 0) {
                            show = true
                        }
                    }
                }
            },
            scrollBehavior = scrollBehavior,
            colors = topBarTransplantColor(),
            title = { Text(AppNavRoute.SearchEdit.label) },
            navigationIcon = {
                with(sharedTransitionScope) {
                    TopBarNavigationIcon(navController,animatedContentScope,route, AppNavRoute.SearchEdit.icon)
                }
            },
            actions = {
                Row(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
                    FilledTonalIconButton(
                        onClick = {
                            showDialog = true
                        }
                    ) {
                        Icon(painterResource(R.drawable.rotate_right),null)
                    }
                    Spacer(Modifier.width(APP_HORIZONTAL_DP/5))
                    if(!inEdit) {
                        FilledTonalButton(
                            onClick = {
                                inEdit = true
                            }
                        ) {
                            Text("编辑")
                        }
                    } else {
                        FilledTonalButton(
                            onClick = {
                                inEdit = false
                            }
                        ) {
                            Text("完成")
                        }
                    }
                }
            }
        )
        ScrollHorizontalTopDivider(state,startPadding = false,endPadding = false)
        AnimatedVisibility(
            visible = show,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LazyVerticalGrid (
                columns = GridCells.Fixed(2),
                state = state,
                modifier = Modifier
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .padding(horizontal = APP_HORIZONTAL_DP - 3.dp),
            ) {
                items(funcMaps.size, key = { funcMaps[it].id }) { index->
                    val item = funcMaps[index]
                    ReorderableItem (reorderableLazyGridState, key = item.id, enabled = inEdit) { isDragging ->
                        val elevation by animateDpAsState(
                            targetValue = if (isDragging) APP_HORIZONTAL_DP else 0.dp,
                        )
                        Surface (
                            shadowElevation = elevation,
                            modifier = Modifier
                                .padding(horizontal = 3.dp, vertical = 3.dp)
                                .let {
                                    if(inEdit) {
                                        if(isDragging) it else it.graphicsLayer { rotationZ = rotation }
                                    } else {
                                        it
                                    }
                                },
                            color = mixedCardNormalColor(),
                            shape = MaterialTheme.shapes.small
                        ) {
                            TransplantListItem(
                                headlineContent = { ScrollText(item.name) },
                                leadingContent = {
                                    Icon(painterResource(item.icon),null)
                                },
                                modifier = Modifier
                                    .combinedClickable(
                                        onClick = {
                                            if (!inEdit) {
                                                showToast("长按卡片开始编辑")
                                            } else {
                                                showToast("双击卡片结束编辑")
                                            }
                                        },
                                        onDoubleClick = {
                                            inEdit = false
                                        },
                                        onLongClick = {
                                            inEdit = true
                                        }
                                    )
                                    .longPressDraggableHandle(
                                        enabled = inEdit,
                                        onDragStarted = {
                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                                        },
                                        onDragStopped = {
                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                        },
                                    )
                            )
                        }
                    }
                }
                items(2) { Spacer(Modifier
                    .navigationBarsPadding()
                    .height(APP_HORIZONTAL_DP)) }
            }
        }

    }
}

