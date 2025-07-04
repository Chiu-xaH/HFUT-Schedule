package com.hfut.schedule.ui.screen.home

import android.annotation.SuppressLint
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Observer
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.BottomBarItems
import com.hfut.schedule.logic.enumeration.BottomBarItems.COURSES
import com.hfut.schedule.logic.enumeration.BottomBarItems.FOCUS
import com.hfut.schedule.logic.enumeration.BottomBarItems.SEARCH
import com.hfut.schedule.logic.enumeration.BottomBarItems.SETTINGS
import com.hfut.schedule.logic.enumeration.SortType
import com.hfut.schedule.logic.model.NavigationBarItemData
import com.hfut.schedule.logic.util.network.ParseJsons.isNextOpen
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveInt
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.Date_MM_dd
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.weeksBetween
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.ui.component.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.custom.CustomTabRow
import com.hfut.schedule.ui.component.DividerTextExpandedWith
import com.hfut.schedule.ui.component.custom.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.custom.ScrollText
import com.hfut.schedule.ui.component.StyleCardListItem
 
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.onListenStateHolder
import com.hfut.schedule.ui.screen.home.calendar.communtiy.CommunityCourseTableUI
import com.hfut.schedule.ui.screen.home.calendar.communtiy.ScheduleTopDate
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.JxglstuCourseTableUI
import com.hfut.schedule.ui.screen.home.calendar.multi.CourseType
import com.hfut.schedule.ui.screen.home.calendar.multi.CustomSchedules
import com.hfut.schedule.ui.screen.home.calendar.multi.MultiScheduleSettings
import com.hfut.schedule.ui.screen.home.cube.SettingsScreen
import com.hfut.schedule.ui.screen.home.cube.sub.MyAPIItem
import com.hfut.schedule.ui.screen.home.cube.sub.update.getUpdates
import com.hfut.schedule.ui.screen.home.focus.TodayScreen
import com.hfut.schedule.ui.screen.home.focus.funiction.AddEventFloatButton
import com.hfut.schedule.ui.screen.home.search.SearchFuncs
import com.hfut.schedule.ui.screen.home.search.SearchScreen
import com.hfut.schedule.ui.screen.home.search.function.community.termInfo.ApiForTermInfo
import com.hfut.schedule.ui.screen.home.search.function.my.notification.NotificationItems
import com.hfut.schedule.ui.screen.home.search.function.my.notification.getNotifications
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.CourseTotalForApi
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.LabUI
import com.hfut.schedule.ui.screen.login.MainNav
import com.hfut.schedule.ui.screen.supabase.login.ApiToSupabase
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.bottomBarBlur
import com.hfut.schedule.ui.style.topBarBlur
import com.hfut.schedule.ui.style.topBarTransplantColor
import com.hfut.schedule.ui.style.transitionBackground
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.ui.util.AppAnimationManager.currentPage
import com.hfut.schedule.ui.util.navigateAndSave
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("SuspiciousIndentation", "CoroutineCreationDuringComposition",
    "UnusedMaterial3ScaffoldPaddingParameter"
)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun MainScreen(
    vm : NetWorkViewModel,
    vm2 : LoginViewModel,
    vmUI : UIViewModel,
    celebrationText : String?,
    webVpn : Boolean,
    isLogin : Boolean,
    navHostTopController : NavHostController,
) {
    val navController = rememberNavController()
    var isEnabled by rememberSaveable(MainNav.HOME.name) { mutableStateOf(!isLogin) }
    val switch = prefs.getBoolean("SWITCH",true)
    var showlable by remember { mutableStateOf(switch) }
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)

    val showBadge by remember { mutableStateOf(getUpdates().version != AppVersion.getVersionName()) }

//判定是否以聚焦作为第一页
    val first  by remember { mutableStateOf(
        if(isLogin) COURSES
        else when (prefs.getBoolean("SWITCHFOCUS",true)) {
                true -> FOCUS
                false -> COURSES
            }
    ) }

    // 按下底栏按钮后，要准备去的导航
    var targetPage by rememberSaveable(MainNav.HOME.name) { mutableStateOf(
        if(isLogin) COURSES
        else when (prefs.getBoolean("SWITCHFOCUS",true)) {
            true -> FOCUS
            false -> COURSES
        }
    ) }
    // 记录上一个

    var showAll by remember { mutableStateOf(DateTimeManager.isOnWeekend()) }
    var findCourse by remember { mutableStateOf(false) }

    var showBottomSheet by remember { mutableStateOf(false) }

    var ifSaved by remember { mutableStateOf(!isLogin) }
    val defaultCalendar = prefs.getInt("SWITCH_DEFAULT_CALENDAR", CourseType.JXGLSTU.code)
    var swapUI by remember { mutableIntStateOf(if(ifSaved) defaultCalendar else CourseType.JXGLSTU.code) }
    var isFriend by remember { mutableStateOf(false) }

    var showBottomSheet_multi by remember { mutableStateOf(false) }

//    val examObserver = Observer<Int> { result ->
//        ifSaved = when(result) {
//            200 -> {
//                false
//                //登录Token未过期
//            }
//            else -> {
//                true
//            }
//        }
//    }

    if (showBottomSheet) {
        saveString("Notifications", getNotifications().size.toString())
        HazeBottomSheet(onDismissRequest = { showBottomSheet = false }, showBottomSheet = showBottomSheet, isFullExpand = true, hazeState = hazeState) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("收纳")
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ){
                    MyAPIItem()
                    DividerTextExpandedWith("通知") {
                        NotificationItems()
                    }
                    DividerTextExpandedWith("实验室") {
                        LabUI()
                    }
                }
            }
        }
    }
    val currentAnimationIndex by DataStoreManager.animationTypeFlow.collectAsState(initial = 0)

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
                    onFriendChange = { newed ->
                        isFriend = newed
                    },
                    vmUI,
                    hazeState
                )
                Spacer(modifier = Modifier.height(APP_HORIZONTAL_DP))
            }
        }
    }

    //监听是否周六周日有课，有则显示红点
    val courseObserver = Observer<Boolean> { result ->
        findCourse = result
    }

    var today by remember { mutableStateOf(DateTimeManager.getToday()) }

    val pagerState = rememberPagerState(pageCount = {2})

    val titles = listOf("重要安排","其他事项")

    var searchText by remember { mutableStateOf("") }

    var showSearch by remember { mutableStateOf(false) }


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
            if(!webVpn) ifSaved = false
            val card = prefs.getString("card", "")
            val json = prefs.getString("json","")
            if (json != null) {
                if (card == "请登录刷新" || !json.contains("课")) {
                    showToast("正在后台登录其他接口,请等待提示Community登录成功和一卡通登陆成功后,再切换界面")
//                    delay(8000)
//                        isEnabled = true
//                } else {
                    delay(3000)
//                    isEnabled = true
//                }
            }
                }
        }
    }
    // 保存上一页页码 用于决定左右动画
    if(currentAnimationIndex == 2) {
        LaunchedEffect(targetPage) {
            currentPage = targetPage.page
        }
    }
    val isAddUIExpanded by remember { derivedStateOf { vmUI.isAddUIExpanded } }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var isNavigationIconVisible by remember { mutableStateOf(true) }
    // 监听滚动状态
    if(targetPage == FOCUS) {
        LaunchedEffect(scrollBehavior.state) {
            snapshotFlow { scrollBehavior.state.collapsedFraction }
                .collect { collapsedFraction -> isNavigationIconVisible = collapsedFraction < 0.5f }
        }
    }
    var sortReversed by remember { mutableStateOf(false) }
    var sortType by remember { mutableStateOf(SortType.TIME_LINE) }
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
                StyleCardListItem(
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

            IconButton(onClick = { showBottomSheet = true }) {
                BadgedBox(badge = {
                    if (getNotifications().size.toString() != prefs.getString("Notifications",""))
                        Badge()
                }) { Icon(painterResource(id = R.drawable.notifications), contentDescription = "", tint = MaterialTheme.colorScheme.primary) }
            }
        }
    }
//    val statusIcon = @Composable {
//        if(ifSaved) {
//            IconButton (onClick = { refreshLogin() }) {
//                Icon(painter = painterResource(id =  R.drawable.login), contentDescription = "",tint = MaterialTheme.colorScheme.primary)
//            }
//        } else {
//            Spacer(modifier = Modifier.width(7.5.dp))
//            Text(text = if(webVpn)"WEBVPN" else "已登录", color = MaterialTheme.colorScheme.primary)
//            Spacer(modifier = Modifier.width(APP_HORIZONTAL_DP))
//        }
//    }
    LaunchedEffect(ifSaved) {
        if(ifSaved == false) {
            isEnabled = true
        }
        // 等待加载完毕可切换标签
//        if(isLogin) {
//            if(!webVpn) ifSaved = false
//            val card = prefs.getString("card", "")
//            val json = prefs.getString("json","")
//            if (json != null) {
//                if (card == "请登录刷新" || !json.contains("课")) {
//                    showToast("正在后台登录其他接口，请稍作等待再切换界面")
//                    delay(8000)
//                    if(ifSaved)
//                        isEnabled = true
//                } else {
//                    delay(3000)
//                    isEnabled = true
//                }
//            }
//        }
    }


    Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
        var innerPaddingValues by remember { mutableStateOf<PaddingValues?>(null) }

        Box(modifier = Modifier
            .align(Alignment.BottomEnd)
            .zIndex(3f)) {
            innerPaddingValues?.let { AddEventFloatButton(isSupabase = false,isVisible = isNavigationIconVisible && (targetPage == FOCUS),vmUI,it,vm) }
        }

        Scaffold(
            modifier = transitionBackground(isAddUIExpanded).fillMaxSize().let {
                if (targetPage == FOCUS) {
                    it.nestedScroll(scrollBehavior.nestedScrollConnection)
                } else {
                    it
                }
            },
            topBar = {
                Column(modifier = Modifier.topBarBlur(hazeState)) {
                    if(targetPage == FOCUS) {
                        MediumTopAppBar(
                            colors = topBarTransplantColor(),
                            navigationIcon = {
                                if(isNavigationIconVisible && celebrationText != null) {
                                    Box(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP-3.dp)) {
                                        Text(
                                            text = celebrationText,
                                            fontSize = 30.sp,
                                            color = MaterialTheme.colorScheme.secondaryContainer
                                        )
                                    }
                                }
                            },
                            title = { ScrollText(texts(targetPage)) },
                            actions = { focusActions() },
                            scrollBehavior = scrollBehavior
                        )
                    } else {
                        TopAppBar(
                            colors = topBarTransplantColor(),
                            title = {
                                if(targetPage != SEARCH) {
                                    ScrollText(texts(targetPage))
                                } else {
                                    if(!showSearch) {
                                        ScrollText(texts(SEARCH))
                                    } else {
                                        SearchFuncs(ifSaved,searchText) {
                                            searchText = it
                                        }
                                    }
                                }
                            },
                            actions = {
                                when(targetPage){
                                    COURSES -> {
                                        if(isFriend) {
                                            ApiForTermInfo(swapUI.toString(),hazeState)
                                        } else {
                                            CourseTotalForApi(vm=vm, isIconOrText = true, hazeState = hazeState)
                                        }

                                        IconButton(onClick = {
                                            showBottomSheet_multi = true
                                        }) {
                                            Icon(painter = painterResource(id =  R.drawable.tab_inactive), contentDescription = "",tint = MaterialTheme.colorScheme.primary)
                                        }
                                        TextButton(onClick = { showAll = !showAll }) {
                                            BadgedBox(badge = {
                                                if (findCourse) Badge()
                                            }) { Icon(painter = painterResource(id = if (showAll) R.drawable.collapse_content else R.drawable.expand_content), contentDescription = "") }
                                        }
                                    }
                                    SEARCH -> {
                                        if(!showSearch) {
                                            IconButton(onClick = { showSearch = !showSearch }) {
                                                Icon(painter = painterResource(id =  R.drawable.search), contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                                            }
//                                            statusIcon()
                                            if(ifSaved) {
                                                IconButton (onClick = { refreshLogin() }) {
                                                    Icon(painter = painterResource(id =  R.drawable.login), contentDescription = "",tint = MaterialTheme.colorScheme.primary)
                                                }
                                            } else {
                                                Spacer(modifier = Modifier.width(7.5.dp))
                                                Text(text = if(webVpn)"WEBVPN" else "已登录", color = MaterialTheme.colorScheme.primary)
                                                Spacer(modifier = Modifier.width(APP_HORIZONTAL_DP))
                                            }
                                        } else {
                                            Spacer(modifier = Modifier.width(APP_HORIZONTAL_DP))
                                        }
                                    }
                                    FOCUS -> focusActions()
                                    else -> {}
                                }
                            },
                        )
                    }
                    when(targetPage) {
                        COURSES -> ScheduleTopDate(showAll,today)
                        FOCUS -> CustomTabRow(pagerState, titles)
                        else -> {}
                    }
                }
            },
            bottomBar = {
                Column {
                    NavigationBar(containerColor = Color.Transparent ,
                        modifier = Modifier.bottomBarBlur(hazeState)
                    ) {
                        val items = listOf(
                            NavigationBarItemData(COURSES.name, "课程表", painterResource(R.drawable.calendar ), painterResource(R.drawable.calendar_month_filled)),
                            NavigationBarItemData(FOCUS.name,"聚焦", painterResource(R.drawable.lightbulb), painterResource(R.drawable.lightbulb_filled)),
                            NavigationBarItemData(SEARCH.name,"查询中心", painterResource(R.drawable.search),painterResource(R.drawable.search_filledx)),
                            NavigationBarItemData(SETTINGS.name,"选项", painterResource(if (getUpdates().version == AppVersion.getVersionName())R.drawable.deployed_code else R.drawable.deployed_code_update), painterResource(if (getUpdates().version == AppVersion.getVersionName()) R.drawable.deployed_code_filled else R.drawable.deployed_code_update_filled ))
                        )
                        items.forEach { item ->
                            val interactionSource = remember { MutableInteractionSource() }
                            val isPressed by interactionSource.collectIsPressedAsState()
                            val scale = animateFloatAsState(
                                targetValue = if (isPressed) 0.8f else 1f, // 按下时为0.9，松开时为1
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                                label = "" // 使用弹簧动画
                            )
                            val route = item.route
                            val selected = navController.currentBackStackEntryAsState().value?.destination?.route == route
                            NavigationBarItem(
                                selected = selected,
                                alwaysShowLabel = showlable,
                                enabled = isEnabled,
                                modifier = Modifier.scale(scale.value),
                                interactionSource = interactionSource,
                                onClick = {
                                    if(isLogin) saveString("tip","0000")
                                    when(item) {
                                        items[0] -> targetPage = COURSES
                                        items[1] -> targetPage = FOCUS
                                        items[2] -> targetPage = SEARCH
                                        items[3] -> targetPage = SETTINGS
                                    }
                                    if (!selected) {
                                        navController.navigateAndSave(route)
                                    }
                                },
                                label = { Text(text = item.label) },
                                icon = {
                                    BadgedBox(badge = {
                                        if (item == items[3]){
                                            if (showBadge)
                                                Badge{ Text(text = "1")}
                                        }
                                    }) { Icon(if(selected)item.filledIcon else item.icon, contentDescription = item.label) }
                                },
                                colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = .9f))

                            )
                        }
                    }
                }
//                AnimatedVisibility(
//                    visible = !isAddUIExpanded,
//                    enter = expandVertically(animationSpec = tween(durationMillis = MyAnimationManager.ANIMATION_SPEED),expandFrom = Alignment.Top,),
//                    exit = shrinkVertically(animationSpec = tween(durationMillis = MyAnimationManager.ANIMATION_SPEED),shrinkTowards = Alignment.Top,),
//                ) {
//
//                }
            },
        ) { innerPadding ->
            innerPaddingValues = innerPadding
            val animation = AppAnimationManager.getAnimationType(currentAnimationIndex,targetPage.page)
            NavHost(
                navController = navController,
                startDestination = first.name,
                enterTransition = { animation.enter },
                exitTransition = { animation.exit },
                modifier = Modifier.hazeSource(state = hazeState)
            ) {
                composable(COURSES.name) {
                    Scaffold(
                        modifier = Modifier.pointerInput(Unit) {
                            detectTransformGestures { _, _, zoom, _ ->
                                if (zoom >= 1f) {
                                    showAll = false
                                } else if (zoom < 1f) {
                                    showAll = true
                                }
                            }
                        }) {
                        if(!isFriend) {
                            // 非好友课表
                            when (swapUI) {
                                // 社区 1
                                CourseType.COMMUNITY.code -> CommunityCourseTableUI(showAll, innerPadding,vmUI, onDateChange = { new -> today = new}, today = today, vm = vm, hazeState = hazeState)
                                // 教务 0
                                CourseType.JXGLSTU.code -> JxglstuCourseTableUI(showAll,vm,innerPadding,vmUI,if(isLogin) webVpn else false,isLogin,{ newDate -> today = newDate},today,hazeState)
                                // 自定义导入课表 数据库id+3=swapUI
                                else -> CustomSchedules(showAll,innerPadding,vmUI,swapUI-3,{newDate-> today = newDate}, today)
                            }
                        }
                        else // 好友课表 swapUI为学号
                            CommunityCourseTableUI(showAll,innerPadding,vmUI,friendUserName = swapUI.toString(),onDateChange = { new -> today = new}, today = today,vm,hazeState)
                    }

                }
                composable(FOCUS.name) {
                    Scaffold {
                        TodayScreen(vm,vm2,innerPadding,vmUI,ifSaved,pagerState, hazeState = hazeState,sortType,sortReversed)
                    }
                }
                composable(SEARCH.name) {
                    Scaffold {
                        SearchScreen(vm,ifSaved,innerPadding,vmUI,searchText, hazeState = hazeState, navController = navHostTopController)
                    }
                }
                composable(SETTINGS.name) {
                    Scaffold {
                        SettingsScreen(
                            vm,
                            showlable,
                            showlablechanged = { showlablech -> showlable = showlablech},
                            ifSaved,
                            innerPadding,
                            vm2,
                            hazeState,
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

