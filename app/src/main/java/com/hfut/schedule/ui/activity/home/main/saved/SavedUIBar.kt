package com.hfut.schedule.ui.activity.home.main.saved

import android.annotation.SuppressLint
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

import androidx.lifecycle.Observer
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.viewmodel.LoginViewModel
import com.hfut.schedule.viewmodel.UIViewModel
import com.hfut.schedule.logic.enums.BottomBarItems
import com.hfut.schedule.logic.enums.BottomBarItems.*
import com.hfut.schedule.logic.beans.NavigationBarItemData
import com.hfut.schedule.logic.utils.APPVersion
import com.hfut.schedule.logic.utils.AndroidVersion.canBlur
import com.hfut.schedule.logic.utils.DataStoreManager
import com.hfut.schedule.logic.utils.DateTimeManager
import com.hfut.schedule.logic.utils.DateTimeManager.Benweeks
import com.hfut.schedule.logic.utils.DateTimeManager.Date_MM_dd
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.utils.SharePrefs.saveInt
import com.hfut.schedule.logic.utils.Starter.refreshLogin
import com.hfut.schedule.ui.activity.home.calendar.jxglstu.CalendarScreen
import com.hfut.schedule.ui.activity.home.calendar.communtiy.SaveCourse
import com.hfut.schedule.ui.activity.home.cube.items.subitems.update.getUpdates
import com.hfut.schedule.ui.activity.home.cube.main.SettingsScreen
import com.hfut.schedule.ui.activity.home.focus.main.TodayScreen

import com.hfut.schedule.ui.activity.home.calendar.communtiy.ScheduleTopDate
import com.hfut.schedule.ui.activity.home.cube.items.subitems.MyAPIItem
import com.hfut.schedule.ui.activity.home.calendar.multi.CustomSchedules
import com.hfut.schedule.ui.activity.home.search.functions.life.ApiFromLife
import com.hfut.schedule.ui.activity.home.search.functions.notifications.NotificationItems
import com.hfut.schedule.ui.activity.home.search.functions.notifications.getNotifications
import com.hfut.schedule.ui.activity.home.search.functions.totalCourse.CourseTotalForApi
import com.hfut.schedule.ui.activity.home.search.functions.webLab.LabUI
import com.hfut.schedule.ui.activity.home.search.main.SearchFuncs
import com.hfut.schedule.ui.activity.home.search.main.SearchScreen
import com.hfut.schedule.ui.activity.login.First
import com.hfut.schedule.ui.utils.NavigateAndAnimationManager
import com.hfut.schedule.ui.utils.NavigateAndAnimationManager.ANIMATION_SPEED
import com.hfut.schedule.ui.utils.NavigateAndAnimationManager.currentPage
import com.hfut.schedule.ui.utils.NavigateAndAnimationManager.turnToAndClear
import com.hfut.schedule.ui.utils.components.CustomTabRow
import com.hfut.schedule.ui.utils.components.DividerText
import com.hfut.schedule.ui.utils.components.DividerTextExpandedWith
import com.hfut.schedule.ui.utils.style.Round
import com.hfut.schedule.ui.utils.components.ScrollText
import com.hfut.schedule.ui.utils.style.bottomBarBlur
import com.hfut.schedule.ui.utils.style.topBarBlur
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate

@SuppressLint("SuspiciousIndentation", "CoroutineCreationDuringComposition",
    "UnusedMaterial3ScaffoldPaddingParameter"
)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationGraphicsApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NoNetWork(vm : NetWorkViewModel, vm2 : LoginViewModel, vmUI : UIViewModel) {
    if(!isNextOpen()) {
        //重置
        saveInt("FIRST",0)
    }
   // val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
    val navController = rememberNavController()
    val isEnabled by remember { mutableStateOf(true) }
    val switch = prefs.getBoolean("SWITCH",true)
    var showlable by remember { mutableStateOf(switch) }
    val hazeState = remember { HazeState() }

//    var bottomBarItems by remember { mutableStateOf(FOCUS) }
    var showBadge by remember { mutableStateOf(false) }
    if (getUpdates().version != APPVersion.getVersionName()) showBadge = true
    val switchblur = prefs.getBoolean("SWITCHBLUR", canBlur)
    var blur by remember { mutableStateOf(switchblur) }
   // val savenum = prefs.getInt("GradeNum",0) + prefs.getInt("ExamNum",0) + prefs.getInt("Notifications",0)
    //val getnum = getGrade().size + getExam().size + getNotifications().size
    //if (savenum != getnum) showBadge2 = true
    val animation by remember { mutableStateOf(prefs.getInt("ANIMATION", MyApplication.Animation)) }



    //Log.d("动画",animation.toString())
//判定是否以聚焦作为第一页
    val first  = when (prefs.getBoolean("SWITCHFOCUS",true)) {
        true -> FOCUS
        false -> COURSES
    }

    // 按下底栏按钮后，要准备去的导航
    var targetPage by remember { mutableStateOf(first) }
    // 记录上一个

    var showAll by remember { mutableStateOf(false) }
    var findCourse by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    var ifSaved by remember { mutableStateOf(true) }
    var swapUI by remember { mutableStateOf(if(ifSaved) COMMUNITY else JXGLSTU) }
    var isFriend by remember { mutableStateOf(false) }

    val sheetState_multi = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_multi by remember { mutableStateOf(false) }

    val sheetState_totalCourse = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_totalCourse by remember { mutableStateOf(false) }

    val ExamObserver = Observer<Int> { result ->
      //  Log.d("re",result.toString())
        when(result) {
            200 -> {
                ifSaved = false
                //登录Token未过期
            }
            else -> {
                ifSaved = true
            }
        }
    }

    CoroutineScope(Job()).launch { NetWorkUpdate(vm, vm2,vmUI, ifSaved) }

    Handler(Looper.getMainLooper()).post { vm.examCode.observeForever(ExamObserver) }



    if (showBottomSheet) {
        SharePrefs.saveString("Notifications", getNotifications().size.toString())
        ModalBottomSheet(onDismissRequest = { showBottomSheet = false }, sheetState = sheetState, modifier = Modifier,
            shape = Round(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("收纳") }
                    )
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
        ModalBottomSheet(onDismissRequest = { showBottomSheet_multi = false }, sheetState = sheetState_multi, modifier = Modifier,
          //  shape = Round(sheetState_multi)
        ) {
            Column(

            ){
                MultiScheduleSettings(ifSaved,swapUI,
                    onSelectedChange = { newSelected ->
                        swapUI = newSelected
                    },
                    vm,
                    onFriendChange = { newed ->
                        isFriend = newed
                    },
                    vmUI
                )
            }
        }
    }

    //监听是否周六周日有课，有则显示红点
    val Observer = Observer<Boolean> { result ->
        findCourse = result
    }
    var today by remember { mutableStateOf(LocalDate.now()) }


    val pagerState = rememberPagerState(pageCount = { 2 })

    val titles = listOf("重要安排","其他事项")


    var searchText by remember { mutableStateOf("") }

    var showSearch by remember { mutableStateOf(false) }
    vmUI.findNewCourse.observeForever(Observer)
    if(findCourse) vmUI.findNewCourse.removeObserver(Observer)

    // 保存上一页页码 用于决定左右动画
    if(currentAnimationIndex == 2) {
        LaunchedEffect(targetPage) {
            currentPage = targetPage.page
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
            //.blur(blurRadius, BlurredEdgeTreatment.Unbounded),
        topBar = {
            Column(modifier = Modifier.topBarBlur(hazeState, blur)) {
                TopAppBar(
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color.Transparent
                        ///MaterialTheme.colorScheme.primaryContainer.copy(alpha = if(blur) 0f else 1f)
                        ,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {

                        if(targetPage != SEARCH) {
                            ScrollText(texts(targetPage))
                        } else {
                            if(!showSearch) {
                                ScrollText(texts(SEARCH))
                            } else {
                                SearchFuncs(ifSaved,blur,searchText) {
                                    searchText = it
                                }
                            }
                        }
                            },
                    actions = {
                        when(targetPage){
                            COURSES -> {
                                CourseTotalForApi(vm=vm, isIconOrText = true)
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
                            FOCUS -> {
                                Row {
                                    ApiFromLife(vm)
                                    TextButton(onClick = { showBottomSheet = true }) {
                                        BadgedBox(badge = {
                                            if (getNotifications().size.toString() != prefs.getString("Notifications",""))
                                                Badge()
                                        }) { Icon(painterResource(id = R.drawable.notifications), contentDescription = "") }
                                    }
                                }
                            }
                            SEARCH -> {
                                if(!showSearch) {
                                    IconButton(onClick = { showSearch = !showSearch }) {
                                        Icon(painter = painterResource(id =  R.drawable.search), contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                                    }

                                    if(ifSaved) {
                                        TextButton(onClick = { refreshLogin() }) {
                                            Icon(painter = painterResource(id =  R.drawable.login), contentDescription = "")
                                        }
                                    } else {
                                        Text(text = "已登录",Modifier.padding(horizontal = 15.dp), color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                Spacer(modifier = Modifier.width(15.dp))
                                //null
                            }
                            SETTINGS -> null
                        }
                    },
                )
//                if(!blur) {
//                    if(bottomBarItems != FOCUS)
//                        Divider()
//                }
                when(targetPage){
                    COURSES -> ScheduleTopDate(showAll,today,blur)
                    FOCUS -> CustomTabRow(pagerState, titles, blur)
                   // SEARCH -> SearchFuncs()
                    else -> null
                }
            }
        },
        bottomBar = {
            Column {
//                if(!blur)
//                    Divider()
                NavigationBar(containerColor = Color.Transparent ,
                    modifier = Modifier.bottomBarBlur(hazeState, blur)
                ) {
                    //悬浮底栏效果
                    //modifier = Modifier.padding(15.dp).shadow(10.dp).clip(RoundedCornerShape(14.dp))
                    val items = listOf(
                        NavigationBarItemData(COURSES.name, "课程表", painterResource(R.drawable.calendar ), painterResource(R.drawable.calendar_month_filled)),
                        NavigationBarItemData(FOCUS.name,"聚焦", painterResource(R.drawable.lightbulb), painterResource(R.drawable.lightbulb_filled)),
                        NavigationBarItemData(SEARCH.name,"查询中心", painterResource(R.drawable.search),painterResource(R.drawable.search_filledx)),
                        NavigationBarItemData(SETTINGS.name,"选项", painterResource(if (getUpdates().version == APPVersion.getVersionName())R.drawable.deployed_code else R.drawable.deployed_code_update), painterResource(if (getUpdates().version == APPVersion.getVersionName()) R.drawable.deployed_code_filled else R.drawable.deployed_code_update_filled ))
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
                                when(item) {
                                    items[0] -> targetPage = COURSES
                                    items[1] -> targetPage = FOCUS
                                    items[2] -> targetPage = SEARCH
                                    items[3] -> targetPage = SETTINGS
                                }
                                if (!selected) {
                                    turnToAndClear(navController,route)
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
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        val animation = NavigateAndAnimationManager.getAnimationType(currentAnimationIndex,targetPage.page)
        NavHost(
            navController = navController,
            startDestination = first.name,
            enterTransition = { animation.enter },
            exitTransition = { animation.exit },
            modifier = Modifier
            .haze(
                state = hazeState,
            )) {
            composable(COURSES.name) {
            Scaffold(modifier = Modifier.pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    if (zoom >= 1f) {
                        showAll = false
                    } else if (zoom < 1f) {
                        showAll = true
                    }
                }
            }) {
                if(!isFriend)
                    when (swapUI) {
                        COMMUNITY -> SaveCourse(showAll, innerPadding,vmUI, onDateChange = { new -> today = new}, today = today, vm = vm)
                        JXGLSTU -> prefs.getString("Username","")?.let { it1 -> CalendarScreen(showAll,vm, it1.substring(0,2),innerPadding,vmUI,false,vm2,false,{newDate -> today = newDate},today) }
                            ///CustomSchedules(showAll,innerPadding,vmUI,-1)
//                        NEXT -> {
//                            Column(modifier = Modifier.padding(innerPadding)) {
//                                prefs.getString("gradeNext","23")?.let { DatumUI(showAll, it, innerPadding, vmUI,)}
//                            }
//                        }
                        else -> {
                            CustomSchedules(showAll,innerPadding,vmUI,swapUI-2,{newDate-> today = newDate}, today)
                        }
                    }
                else {
                    SaveCourse(showAll,innerPadding,vmUI,swapUI.toString(),onDateChange = { new -> today = new}, today = today,vm)
                }

            }

            }
            composable(FOCUS.name) {
                Scaffold {
                    TodayScreen(vm,vm2,innerPadding, blur,vmUI,ifSaved,false,pagerState)
                }

                //Test()
            }
            composable(SEARCH.name) {
                Scaffold {
                    SearchScreen(vm,ifSaved,innerPadding,vmUI,searchText)
                }

            }
            composable(SETTINGS.name) {
                Scaffold {
                    SettingsScreen(vm,showlable, showlablechanged = { showlablech -> showlable = showlablech},ifSaved,innerPadding, blur,blurchanged = { blurch -> blur = blurch},vm2)
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun texts(num : BottomBarItems) : String {
    when(num){
        COURSES -> {
            val dayweek = DateTimeManager.dayweek
            var chinesenumber  = DateTimeManager.chinesenumber

            when (dayweek) {
                1 -> chinesenumber = "一"
                2 -> chinesenumber = "二"
                3 -> chinesenumber = "三"
                4 -> chinesenumber = "四"
                5 -> chinesenumber = "五"
                6 -> chinesenumber = "六"
                0 -> chinesenumber = "日"
            }
            return "$Date_MM_dd 第${Benweeks}周 周$chinesenumber"
        }
        FOCUS -> {
            val dayweek = DateTimeManager.dayweek
            var chinesenumber  = DateTimeManager.chinesenumber

            when (dayweek) {
                1 -> chinesenumber = "一"
                2 -> chinesenumber = "二"
                3 -> chinesenumber = "三"
                4 -> chinesenumber = "四"
                5 -> chinesenumber = "五"
                6 -> chinesenumber = "六"
                0 -> chinesenumber = "日"
            }
            return "$Date_MM_dd 第${Benweeks}周 周$chinesenumber"
        }
        SEARCH -> {
//            var text  = "你好"
//            if(GetDate.formattedTime_Hour.toInt() == 12) text = "午饭时间到~"
//            if(GetDate.formattedTime_Hour.toInt() in 13..17) text = "下午要忙什么呢"
//            if(GetDate.formattedTime_Hour.toInt() in 7..11) text = "上午好呀"
//            if(GetDate.formattedTime_Hour.toInt() in 5..6) text = "起的好早呀"
//            if(GetDate.formattedTime_Hour.toInt() in 18..23) text = "晚上好"
//            if(GetDate.formattedTime_Hour.toInt() in 0..4) text = "熬夜也要早睡哦"
//
//            return "$text ${getName(vm)} 同学"
            return "查询中心"
        }
        SETTINGS -> {
            return "选项"
        }
        else -> return "HFUT Focus"
    }
}

