package com.hfut.schedule.ui.Activity.success.main.saved

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
import com.hfut.schedule.logic.datamodel.MyAPIResponse
import com.hfut.schedule.logic.datamodel.NavigationBarItemData
import com.hfut.schedule.logic.utils.APPVersion
import com.hfut.schedule.logic.utils.AndroidVersion
import com.hfut.schedule.logic.utils.GetDate
import com.hfut.schedule.logic.utils.GetDate.Benweeks
import com.hfut.schedule.logic.utils.GetDate.Date_MM_dd
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.Activity.success.calendar.nonet.SaveCourse
import com.hfut.schedule.ui.Activity.success.cube.Settings.getUpdates
import com.hfut.schedule.ui.Activity.success.cube.main.SettingsScreen
import com.hfut.schedule.ui.Activity.success.focus.main.Test
import com.hfut.schedule.ui.Activity.success.focus.main.TodayScreen
import com.hfut.schedule.ui.Activity.success.search.Search.NextCourse
import com.hfut.schedule.ui.Activity.success.search.Search.NotificationsCenter.NotificationItems
import com.hfut.schedule.ui.Activity.success.search.Search.NotificationsCenter.getNotifications
import com.hfut.schedule.ui.Activity.success.search.main.SearchScreen
import com.hfut.schedule.ui.Activity.success.search.main.getName
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation", "CoroutineCreationDuringComposition")
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
    var bottomBarItems by remember { mutableStateOf(BottomBarItems.FOCUS) }
    var showBadge by remember { mutableStateOf(false) }
    if (getUpdates().version != APPVersion.getVersionName()) showBadge = true
    val switchblur = prefs.getBoolean("SWITCHBLUR", AndroidVersion.sdkInt >= 32)
    var blur by remember { mutableStateOf(switchblur) }
   // val savenum = prefs.getInt("GradeNum",0) + prefs.getInt("ExamNum",0) + prefs.getInt("Notifications",0)
    //val getnum = getGrade().size + getExam().size + getNotifications().size
    //if (savenum != getnum) showBadge2 = true


//判定是否以聚焦作为第一页
    var first : String = when (prefs.getBoolean("SWITCHFOCUS",true)) {
        true -> BottomBarItems.FOCUS.name
        false -> BottomBarItems.COURSES.name
    }

    var showAll by remember { mutableStateOf(false) }
    var findCourse by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

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
                    title = { Text(texts(vm,bottomBarItems)) },
                    actions = {
                        if(bottomBarItems == BottomBarItems.COURSES) {
                            if(Gson().fromJson(prefs.getString("my",MyApplication.NullMy),
                                    MyAPIResponse::class.java).Next)
                                NextCourse(vmUI, true)
                            TextButton(onClick = { showAll = !showAll }) {
                                BadgedBox(badge = {
                                    if (findCourse) Badge()
                                }) { Icon(painter = painterResource(id = if (showAll) R.drawable.collapse_content else R.drawable.expand_content), contentDescription = "") }
                            }
                        }
                        if(bottomBarItems == BottomBarItems.FOCUS) {
                            Row {
                                TextButton(onClick = { showBottomSheet = true }) {
                                    BadgedBox(badge = {
                                        if (getNotifications().size.toString() != prefs.getString("Notifications",""))
                                            Badge()
                                    }) { Icon(painterResource(id = R.drawable.notifications), contentDescription = "") }
                                }
                            }
                        }
                    },
                )
                if(bottomBarItems != BottomBarItems.FOCUS)
                Divider()
            }
        },
        bottomBar = {
            Divider()
            NavigationBar(containerColor = if(blur) MaterialTheme.colorScheme.primaryContainer.copy(.25f) else ListItemDefaults.containerColor ,
                modifier = Modifier
                    .hazeChild(state = hazeState, blurRadius = MyApplication.Blur, tint = Color.Transparent, noiseFactor = 0f)) {
            //    val image = AnimatedImageVector.animatedVectorResource(R.drawable.ic_hourglass_animated)
              //  var atEnd by remember { mutableStateOf(false) }

                val items = listOf(
                    NavigationBarItemData(BottomBarItems.COURSES.name, "课程表", painterResource(R.drawable.calendar ), painterResource(R.drawable.calendar_month_filled)),
                    NavigationBarItemData(BottomBarItems.FOCUS.name,"聚焦", painterResource(R.drawable.lightbulb), painterResource(R.drawable.lightbulb_filled)),
                    NavigationBarItemData(BottomBarItems.SEARCH.name,"查询中心", painterResource(R.drawable.search),painterResource(R.drawable.search_filledx)),
                    NavigationBarItemData(BottomBarItems.SETTINGS.name,"选项", painterResource(if (getUpdates().version == APPVersion.getVersionName())R.drawable.deployed_code else R.drawable.deployed_code_update), painterResource(if (getUpdates().version == APPVersion.getVersionName()) R.drawable.deployed_code_filled else R.drawable.deployed_code_update_filled ))
                )
                items.forEach { item ->
                    val route = item.route
                    val selected = navController.currentBackStackEntryAsState().value?.destination?.route == route
                    NavigationBarItem(
                        selected = selected,
                        alwaysShowLabel = showlable,
                        enabled = isEnabled,
                        onClick = {
                            if(item == items[0]) bottomBarItems = BottomBarItems.COURSES
                            if(item == items[1]) bottomBarItems = BottomBarItems.FOCUS
                            if(item == items[2]) bottomBarItems = BottomBarItems.SEARCH
                            if(item == items[3]) bottomBarItems = BottomBarItems.SETTINGS
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
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = first, modifier = Modifier
          //  .padding(innerPadding)
            //.fillMaxSize()
            .haze(
                state = hazeState,
                backgroundColor = MaterialTheme.colorScheme.surface,
            )) {
            composable(BottomBarItems.COURSES.name) { SaveCourse(showAll, innerPadding,vmUI) }
            composable(BottomBarItems.FOCUS.name) {
                TodayScreen(vm,vm2,innerPadding, blur,vmUI,true)
                //Test()
            }
            composable(BottomBarItems.SEARCH.name) { SearchScreen(vm,true,innerPadding,vmUI) }
            composable(BottomBarItems.SETTINGS.name) { SettingsScreen(vm,showlable, showlablechanged = { showlablech -> showlable = showlablech},true,innerPadding, blur,blurchanged = { blurch -> blur = blurch})
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun texts(vm : LoginSuccessViewModel,num : BottomBarItems) : String {
    when(num){
        BottomBarItems.COURSES -> {
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
        BottomBarItems.FOCUS -> {
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
        BottomBarItems.SEARCH -> {
            var text  = "你好"
            if(GetDate.formattedTime_Hour.toInt() == 12) text = "午饭时间到~"
            if(GetDate.formattedTime_Hour.toInt() in 13..17) text = "下午要忙什么呢"
            if(GetDate.formattedTime_Hour.toInt() in 7..11) text = "上午好呀"
            if(GetDate.formattedTime_Hour.toInt() in 5..6) text = "起的好早呀"
            if(GetDate.formattedTime_Hour.toInt() in 18..23) text = "晚上好"
            if(GetDate.formattedTime_Hour.toInt() in 0..4) text = "熬夜也要早睡哦"

            return "$text ${getName(vm)} 同学"
        }
        BottomBarItems.SETTINGS -> {
            return "选项"
        }
        else -> return "HFUT Focus"
    }
}