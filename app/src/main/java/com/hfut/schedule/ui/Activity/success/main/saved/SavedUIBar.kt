package com.hfut.schedule.ui.Activity.success.main.saved

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.ViewModel.LoginViewModel
import com.hfut.schedule.ViewModel.UIViewModel
import com.hfut.schedule.logic.Enums.BottomBarItems
import com.hfut.schedule.logic.Enums.BottomBarItems.*
import com.hfut.schedule.logic.datamodel.MyAPIResponse
import com.hfut.schedule.logic.datamodel.NavigationBarItemData
import com.hfut.schedule.logic.utils.APPVersion
import com.hfut.schedule.logic.utils.AndroidVersion
import com.hfut.schedule.logic.utils.GetDate
import com.hfut.schedule.logic.utils.GetDate.Benweeks
import com.hfut.schedule.logic.utils.GetDate.Date_MM_dd
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.Activity.success.calendar.login.CalendarScreen
import com.hfut.schedule.ui.Activity.success.calendar.nonet.SaveCourse
import com.hfut.schedule.ui.Activity.success.cube.Settings.getUpdates
import com.hfut.schedule.ui.Activity.success.cube.main.SettingsScreen
import com.hfut.schedule.ui.Activity.success.focus.main.TodayScreen
import com.hfut.schedule.ui.Activity.success.search.Search.More.Login
import com.hfut.schedule.ui.Activity.success.calendar.next.NextCourse
import com.hfut.schedule.ui.Activity.success.search.Search.NotificationsCenter.NotificationItems
import com.hfut.schedule.ui.Activity.success.search.Search.NotificationsCenter.getNotifications
import com.hfut.schedule.ui.Activity.success.search.main.SearchScreen
import com.hfut.schedule.ui.Activity.success.search.main.getName
import com.hfut.schedule.ui.UIUtils.ScrollText
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation", "CoroutineCreationDuringComposition",
    "UnusedMaterial3ScaffoldPaddingParameter"
)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationGraphicsApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NoNetWork(vm : LoginSuccessViewModel,vm2 : LoginViewModel,vmUI : UIViewModel) {
   // val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
    val navController = rememberNavController()
    var isEnabled by remember { mutableStateOf(true) }
    val switch = prefs.getBoolean("SWITCH",true)
    var showlable by remember { mutableStateOf(switch) }
    val hazeState = remember { HazeState() }
    CoroutineScope(Job()).launch { NetWorkUpdate(vm, vm2,vmUI,true) }
    var bottomBarItems by remember { mutableStateOf(FOCUS) }
    var showBadge by remember { mutableStateOf(false) }
    if (getUpdates().version != APPVersion.getVersionName()) showBadge = true
    val switchblur = prefs.getBoolean("SWITCHBLUR", AndroidVersion.sdkInt >= 32)
    var blur by remember { mutableStateOf(switchblur) }
   // val savenum = prefs.getInt("GradeNum",0) + prefs.getInt("ExamNum",0) + prefs.getInt("Notifications",0)
    //val getnum = getGrade().size + getExam().size + getNotifications().size
    //if (savenum != getnum) showBadge2 = true
    var animation by remember { mutableStateOf(prefs.getInt("ANIMATION",MyApplication.Animation)) }

    //Log.d("动画",animation.toString())
//判定是否以聚焦作为第一页
    var first : String = when (prefs.getBoolean("SWITCHFOCUS",true)) {
        true -> FOCUS.name
        false -> COURSES.name
    }

    var showAll by remember { mutableStateOf(false) }
    var findCourse by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    var ifSaved by remember { mutableStateOf(true) }
    var swapUI by remember { mutableStateOf(false) }

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


    Handler(Looper.getMainLooper()).post { vm.examCode.observeForever(ExamObserver) }


    if (showBottomSheet) {
        SharePrefs.Save("Notifications", getNotifications().size.toString())
        ModalBottomSheet(onDismissRequest = { showBottomSheet = false }, sheetState = sheetState, modifier = Modifier) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("消息中心") }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    NotificationItems()
                }
            }
        }
    }



    //监听是否周六周日有课，有则显示红点
    val Observer = Observer<Boolean> { result ->
        findCourse = result
    }
    vmUI.findNewCourse.observeForever(Observer)
    if(findCourse) vmUI.findNewCourse.removeObserver(Observer)
    Scaffold(
        modifier = Modifier.fillMaxSize(),
            //.blur(blurRadius, BlurredEdgeTreatment.Unbounded),
        topBar = {
            Column {
                TopAppBar(
                    modifier = Modifier.hazeChild(state = hazeState, blurRadius = MyApplication.Blur, tint = Color.Transparent, noiseFactor = 0f),
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = if(blur).50f else 1f),
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = { ScrollText(texts(vm,bottomBarItems)) },
                    actions = {
                        when(bottomBarItems){
                            COURSES -> {
                                FilledTonalButton(onClick = { swapUI = !swapUI }) {
                                    Text(text = if(swapUI)"教务" else "社区" )
                                }
                                if(Gson().fromJson(prefs.getString("my",MyApplication.NullMy),
                                        MyAPIResponse::class.java).Next)
                                    NextCourse(vmUI, true)
                                TextButton(onClick = { showAll = !showAll }) {
                                    BadgedBox(badge = {
                                        if (findCourse) Badge()
                                    }) { Icon(painter = painterResource(id = if (showAll) R.drawable.collapse_content else R.drawable.expand_content), contentDescription = "") }
                                }
                            }
                            FOCUS -> {
                                Row {
                                    TextButton(onClick = { showBottomSheet = true }) {
                                        BadgedBox(badge = {
                                            if (getNotifications().size.toString() != prefs.getString("Notifications",""))
                                                Badge()
                                        }) { Icon(painterResource(id = R.drawable.notifications), contentDescription = "") }
                                    }
                                }
                            }
                            SEARCH -> {
                                if(ifSaved) {
                                    TextButton(onClick = { Login() }) {
                                        Icon(painter = painterResource(id =  R.drawable.login), contentDescription = "")
                                    }
                                } else {
                                    Text(text = "已登录",Modifier.padding(horizontal = 15.dp), color = MaterialTheme.colorScheme.primary)
                                }
                            }
                            SETTINGS -> null
                        }
                    },
                )
                if(bottomBarItems != FOCUS)
                Divider()
            }
        },
        bottomBar = {
            Column {
                Divider()
                NavigationBar(containerColor = if(blur) MaterialTheme.colorScheme.primaryContainer.copy(.25f) else ListItemDefaults.containerColor ,
                    modifier = Modifier.hazeChild(state = hazeState, blurRadius = MyApplication.Blur, tint = Color.Transparent, noiseFactor = 0f)) {
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
                                if(item == items[0]) bottomBarItems = COURSES
                                if(item == items[1]) bottomBarItems = FOCUS
                                if(item == items[2]) bottomBarItems = SEARCH
                                if(item == items[3]) bottomBarItems = SETTINGS
                                //     atEnd = !atEnd
                                if (!selected) {
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
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
        NavHost(
            navController = navController,
            startDestination = first,
            enterTransition = {
                     //   fadeIn(animationSpec = tween(durationMillis = animation)) +
                        scaleIn(animationSpec = tween(durationMillis = animation)) +
                        expandVertically(expandFrom = Alignment.Top,animationSpec = tween(durationMillis = animation))
            },
            exitTransition = {
                      //  fadeOut(animationSpec = tween(durationMillis = animation)) +
                        scaleOut(animationSpec = tween(durationMillis = animation)) +
                        shrinkVertically(shrinkTowards = Alignment.Top,animationSpec = tween(durationMillis = animation))
            },
            modifier = Modifier
            .haze(
                state = hazeState,
                backgroundColor = MaterialTheme.colorScheme.surface,
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
                if(!swapUI)
                SaveCourse(showAll, innerPadding,vmUI)
                else
                prefs.getString("Username","")?.let { it1 -> CalendarScreen(showAll,vm, it1.substring(0,2),innerPadding,vmUI,false,vm2,false) }
            }

            }
            composable(FOCUS.name) {
                Scaffold {
                    TodayScreen(vm,vm2,innerPadding, blur,vmUI,ifSaved,false)
                }

                //Test()
            }
            composable(SEARCH.name) {
                Scaffold {
                    SearchScreen(vm,ifSaved,innerPadding,vmUI,false)
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
fun texts(vm : LoginSuccessViewModel,num : BottomBarItems) : String {
    when(num){
        COURSES -> {
            val dayweek = GetDate.dayweek
            var chinesenumber  = GetDate.chinesenumber

            when (dayweek) {
                1 -> chinesenumber = "一"
                2 -> chinesenumber = "二"
                3 -> chinesenumber = "三"
                4 -> chinesenumber = "四"
                5 -> chinesenumber = "五"
                6 -> chinesenumber = "六"
                0 -> chinesenumber = "日"
            }
            return "$Date_MM_dd  第${Benweeks}周  周$chinesenumber"
        }
        FOCUS -> {
            val dayweek = GetDate.dayweek
            var chinesenumber  = GetDate.chinesenumber

            when (dayweek) {
                1 -> chinesenumber = "一"
                2 -> chinesenumber = "二"
                3 -> chinesenumber = "三"
                4 -> chinesenumber = "四"
                5 -> chinesenumber = "五"
                6 -> chinesenumber = "六"
                0 -> chinesenumber = "日"
            }
            return "今天  $Date_MM_dd  第${Benweeks}周  周$chinesenumber"
        }
        SEARCH -> {
            var text  = "你好"
            if(GetDate.formattedTime_Hour.toInt() == 12) text = "午饭时间到~"
            if(GetDate.formattedTime_Hour.toInt() in 13..17) text = "下午要忙什么呢"
            if(GetDate.formattedTime_Hour.toInt() in 7..11) text = "上午好呀"
            if(GetDate.formattedTime_Hour.toInt() in 5..6) text = "起的好早呀"
            if(GetDate.formattedTime_Hour.toInt() in 18..23) text = "晚上好"
            if(GetDate.formattedTime_Hour.toInt() in 0..4) text = "熬夜也要早睡哦"

            return "$text ${getName(vm)} 同学"
        }
        SETTINGS -> {
            return "选项"
        }
        else -> return "HFUT Focus"
    }
}