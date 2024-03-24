package com.hfut.schedule.ui.Activity.success.main.login

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
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
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.ViewModel.LoginViewModel
import com.hfut.schedule.ViewModel.UIViewModel
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.datamodel.NavigationBarItemData
import com.hfut.schedule.logic.utils.AndroidVersion
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.Save
import com.hfut.schedule.ui.Activity.success.search.main.SearchScreen
import com.hfut.schedule.ui.Activity.success.cube.main.SettingsScreen
import com.hfut.schedule.ui.Activity.success.focus.main.TodayScreen
import com.hfut.schedule.logic.Enums.BottomBarItems
import com.hfut.schedule.logic.utils.APPVersion
import com.hfut.schedule.ui.Activity.success.calendar.login.CalendarScreen
import com.hfut.schedule.ui.Activity.success.main.saved.texts
import com.hfut.schedule.ui.Activity.success.cube.Settings.getMyVersion
import com.hfut.schedule.ui.Activity.success.search.Search.NotificationsCenter.NotificationItems
import com.hfut.schedule.ui.Activity.success.search.Search.NotificationsCenter.getNotifications
import com.hfut.schedule.ui.UIUtils.MyToast
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("CoroutineCreationDuringComposition", "SuspiciousIndentation")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SuccessUI(vm : LoginSuccessViewModel, grade : String,vm2 : LoginViewModel,vmUI : UIViewModel) {

    val switch = prefs.getBoolean("SWITCH",true)
    val navController = rememberNavController()
    var isEnabled by remember { mutableStateOf(false) }
    var showlable by remember { mutableStateOf(switch) }
    var bottomBarItems by remember { mutableStateOf(BottomBarItems.COURSES) }
    val hazeState = remember { HazeState() }
    var showBadge by remember { mutableStateOf(false) }
    if (APPVersion.getVersionName() != getMyVersion()) showBadge = true
    val switchblur = prefs.getBoolean("SWITCHBLUR", AndroidVersion.sdkInt >= 32)
    var blur by remember { mutableStateOf(switchblur) }
    //监听是否周六周日有课，有则显示红点
    var findCourse by remember { mutableStateOf(false) }
    val Observer = Observer<Boolean> { result ->
        findCourse = result
    }
    vmUI.findNewCourse.observeForever(Observer)
    if(findCourse) vmUI.findNewCourse.removeObserver(Observer)
    //等待加载完毕可切换标签
    CoroutineScope(Job()).launch {
        val card = prefs.getString("card", "")
        val json = prefs.getString("json","")
        if (json != null) {
            if (card == "请登录刷新" || !json.contains("课")) {
                delay(6000)
                isEnabled = true
            } else {
                delay(1000)
                isEnabled = true
            }
        }
    }

    var showAll by remember { mutableStateOf(false) }


    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {
        Save("Notifications", getNotifications().size.toString())
        ModalBottomSheet(onDismissRequest = { showBottomSheet = false }, sheetState = sheetState) {
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


    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
                            TextButton(onClick = { showAll = !showAll }) {
                                BadgedBox(badge = {
                                    if (findCourse) Badge()
                                }) { Icon(painter = painterResource(id = if (showAll) R.drawable.collapse_content else R.drawable.expand_content), contentDescription = "") }
                            }
                        }
                        if(bottomBarItems == BottomBarItems.FOCUS) {
                            TextButton(onClick = { showBottomSheet = true }) {
                                BadgedBox(badge = {
                                    if (getNotifications().size.toString() != prefs.getString("Notifications",""))
                                        Badge()
                                }) { Icon(painterResource(id = R.drawable.notifications), contentDescription = "") }
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
            NavigationBar(
                containerColor = if(blur) MaterialTheme.colorScheme.primaryContainer.copy(.25f) else ListItemDefaults.containerColor ,
                modifier = Modifier.hazeChild(state = hazeState, blurRadius = MyApplication.Blur, tint = Color.Transparent, noiseFactor = 0f)
            ) {
                val items = listOf(
                    NavigationBarItemData(BottomBarItems.COURSES.name, "课程表", painterResource(R.drawable.calendar),painterResource(R.drawable.calendar_month_filled)),
                    NavigationBarItemData(BottomBarItems.FOCUS.name,"聚焦", painterResource(R.drawable.lightbulb),painterResource(R.drawable.lightbulb_filled)),
                    NavigationBarItemData(BottomBarItems.SEARCH.name,"查询中心", painterResource(R.drawable.search), painterResource(R.drawable.search_filledx)),
                    NavigationBarItemData(BottomBarItems.SETTINGS.name, "选项", painterResource(R.drawable.cube),painterResource(R.drawable.deployed_code_filled))

                )
                items.forEach { item ->
                    val route = item.route
                    val selected = navController.currentBackStackEntryAsState().value?.destination?.route == route
                    NavigationBarItem(
                        selected = selected,
                        alwaysShowLabel = showlable,
                        enabled = isEnabled,
                        onClick = {
                                Save("tip","0000")
                            if(item == items[0]) bottomBarItems = BottomBarItems.COURSES
                            if(item == items[1]) bottomBarItems = BottomBarItems.FOCUS
                            if(item == items[2]) bottomBarItems = BottomBarItems.SEARCH
                            if(item == items[3]) bottomBarItems = BottomBarItems.SETTINGS
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
                            }) { Icon(if(selected)item.filledIcon else item.icon, contentDescription = item.label)}
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = BottomBarItems.COURSES.name,modifier = Modifier
            .haze(
                state = hazeState,
                backgroundColor = MaterialTheme.colorScheme.surface,)) {
            composable(BottomBarItems.COURSES.name) { CalendarScreen(showAll,vm,grade,innerPadding,vmUI) }
            composable(BottomBarItems.FOCUS.name) { TodayScreen(vm,vm2,innerPadding,blur,vmUI) }
            composable(BottomBarItems.SEARCH.name) { SearchScreen(vm,false,innerPadding,vmUI) }
            composable(BottomBarItems.SETTINGS.name) { SettingsScreen(vm, showlable, showlablechanged = {showlablech -> showlable = showlablech}, false,innerPadding,blur,blurchanged = {blurch -> blur = blurch}) }
        }
    }
}