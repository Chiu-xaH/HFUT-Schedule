package com.hfut.schedule.ui.Activity.success.main.login

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
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
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.datamodel.NavigationBarItemData
import com.hfut.schedule.logic.utils.AndroidVersion
import com.hfut.schedule.logic.utils.SharePrefs.Save
import com.hfut.schedule.ui.Activity.success.search.main.SearchScreen
import com.hfut.schedule.ui.Activity.success.cube.main.SettingsScreen
import com.hfut.schedule.ui.Activity.success.focus.main.TodayScreen
import com.hfut.schedule.logic.Enums.BottomBarItems.*
import com.hfut.schedule.logic.datamodel.MyAPIResponse
import com.hfut.schedule.logic.utils.APPVersion
import com.hfut.schedule.ui.Activity.success.calendar.login.CalendarScreen
import com.hfut.schedule.ui.Activity.success.main.saved.texts
import com.hfut.schedule.ui.Activity.success.cube.Settings.Update.getUpdates
import com.hfut.schedule.ui.Activity.success.calendar.next.NextCourse
import com.hfut.schedule.ui.Activity.success.calendar.nonet.SaveCourse
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.MyAPIItem
import com.hfut.schedule.ui.Activity.success.search.Search.NotificationsCenter.NotificationItems
import com.hfut.schedule.ui.Activity.success.search.Search.NotificationsCenter.getNotifications
import com.hfut.schedule.ui.Activity.success.search.Search.TotalCourse.CourseTotalUI
import com.hfut.schedule.ui.Activity.success.search.Search.Web.LabUI
import com.hfut.schedule.ui.UIUtils.DividerText
import com.hfut.schedule.ui.UIUtils.Round
import com.hfut.schedule.ui.UIUtils.ScrollText
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("CoroutineCreationDuringComposition", "SuspiciousIndentation",
    "UnusedMaterial3ScaffoldPaddingParameter"
)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SuccessUI(vm : LoginSuccessViewModel, grade : String,vm2 : LoginViewModel,vmUI : UIViewModel,webVpn : Boolean) {

    var animation by remember { mutableStateOf(prefs.getInt("ANIMATION",MyApplication.Animation)) }
    val switch = prefs.getBoolean("SWITCH",true)
    val navController = rememberNavController()
    var isEnabled by remember { mutableStateOf(false) }
    var showlable by remember { mutableStateOf(switch) }
    var bottomBarItems by remember { mutableStateOf(COURSES) }
    val hazeState = remember { HazeState() }
    var showBadge by remember { mutableStateOf(false) }
    if (getUpdates().version != APPVersion.getVersionName()) showBadge = true
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
    var swapUI by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {
        Save("Notifications", getNotifications().size.toString())
        ModalBottomSheet(onDismissRequest = { showBottomSheet = false }, sheetState = sheetState,
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
                    DividerText("通知")
                    NotificationItems()
                    DividerText("实验室")
                    LabUI()
                }
            }
        }
    }
    val sheetState_totalCourse = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_totalCourse by remember { mutableStateOf(false) }

    var sortType by remember { mutableStateOf(true) }

    if (showBottomSheet_totalCourse) {
        ModalBottomSheet(onDismissRequest = { showBottomSheet_totalCourse = false }, sheetState = sheetState_totalCourse, modifier = Modifier,
            shape = Round(sheetState_totalCourse)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("课程汇总") },
                        actions = {
                            FilledTonalButton(
                                onClick = { sortType = !sortType },
                                modifier = Modifier.padding(horizontal = 15.dp
                                )) {
                                Text(text = if(sortType) "开课顺序" else "学分顺序")
                            }
                        }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    val json = prefs.getString("courses","")
                    CourseTotalUI(json,false,sortType)
                    Spacer(modifier = Modifier.height(20.dp))
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
                    title = { ScrollText(texts(vm,bottomBarItems)) },
                    actions = {
                        when(bottomBarItems) {
                            COURSES -> {
                                FilledTonalButton(onClick = {
                                    swapUI = !swapUI
                                }) {
                                    Text(text = if(!swapUI)"教务" else "社区" )
                                }
                                if(Gson().fromJson(prefs.getString("my",MyApplication.NullMy),
                                        MyAPIResponse::class.java).Next) {
                                    NextCourse(vmUI, false)
                                }
                                TextButton(onClick = {
                                    showBottomSheet_totalCourse= true
                                }) {
                                    Icon(painter = painterResource(id =  R.drawable.category), contentDescription = "")
                                }
                                TextButton(onClick = { showAll = !showAll }) {
                                    BadgedBox(badge = {
                                        if (findCourse) Badge()
                                    }) { Icon(painter = painterResource(id = if (showAll) R.drawable.collapse_content else R.drawable.expand_content), contentDescription = "") }
                                }
                            }
                            FOCUS -> {
                                TextButton(onClick = { showBottomSheet = true }) {
                                    BadgedBox(badge = {
                                        if (getNotifications().size.toString() != prefs.getString("Notifications",""))
                                            Badge()
                                    }) { Icon(painterResource(id = R.drawable.notifications), contentDescription = "") }
                                }
                            }
                            SEARCH -> {
                                Text(text = if(webVpn)"外地访问" else "已登录",Modifier.padding(horizontal = 15.dp), color = MaterialTheme.colorScheme.primary)
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
                NavigationBar(
                    containerColor = if(blur) MaterialTheme.colorScheme.primaryContainer.copy(.25f) else ListItemDefaults.containerColor ,
                    modifier = Modifier.hazeChild(state = hazeState, blurRadius = MyApplication.Blur, tint = Color.Transparent, noiseFactor = 0f)
                ) {
                    val items = listOf(
                        NavigationBarItemData(COURSES.name, "课程表", painterResource(R.drawable.calendar),painterResource(R.drawable.calendar_month_filled)),
                        NavigationBarItemData(FOCUS.name,"聚焦", painterResource(R.drawable.lightbulb),painterResource(R.drawable.lightbulb_filled)),
                        NavigationBarItemData(SEARCH.name,"查询中心", painterResource(R.drawable.search), painterResource(R.drawable.search_filledx)),
                        NavigationBarItemData(
                            SETTINGS.name,"选项", painterResource(if (getUpdates().version == APPVersion.getVersionName())R.drawable.
                        deployed_code else R.drawable.deployed_code_update), painterResource(if (getUpdates().version == APPVersion.getVersionName()) R.drawable.deployed_code_filled else R.drawable.deployed_code_update_filled ))

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
                            modifier = Modifier.scale(scale.value),
                            interactionSource = interactionSource,
                            enabled = isEnabled,
                            onClick = {
                                Save("tip","0000")
                                if(item == items[0]) bottomBarItems = COURSES
                                if(item == items[1]) bottomBarItems = FOCUS
                                if(item == items[2]) bottomBarItems = SEARCH
                                if(item == items[3]) bottomBarItems = SETTINGS
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
        }
    ) { innerPadding ->
        NavHost(navController = navController,
            startDestination = COURSES.name,
            enterTransition = {
                scaleIn(animationSpec = tween(durationMillis = animation)) +
                        expandVertically(expandFrom = Alignment.Top,animationSpec = tween(durationMillis = animation))
            },
            exitTransition = {
                scaleOut(animationSpec = tween(durationMillis = animation)) +
                        shrinkVertically(shrinkTowards = Alignment.Top,animationSpec = tween(durationMillis = animation))
            },
            modifier = Modifier
            .haze(
                state = hazeState,
                backgroundColor = MaterialTheme.colorScheme.surface,)) {
            composable(COURSES.name) {
                Scaffold {
                    if(!swapUI) CalendarScreen(showAll,vm,grade,innerPadding,vmUI,webVpn,vm2,true)
                    else SaveCourse(showAll, innerPadding,vmUI)
                }
            }
            composable(FOCUS.name) {
                Scaffold {
                    TodayScreen(vm,vm2,innerPadding,blur,vmUI,false,webVpn)
                }
            }
            composable(SEARCH.name) {
                Scaffold {
                    SearchScreen(vm,false,innerPadding,vmUI,webVpn)
                }
            }
            composable(SETTINGS.name) {
                Scaffold {
                    SettingsScreen(
                        vm, showlable, showlablechanged = {showlablech -> showlable = showlablech},
                        false,innerPadding,blur,blurchanged = {blurch -> blur = blurch},vm2 ) }
            }
        }
    }
}