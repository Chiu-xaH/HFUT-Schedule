package com.hfut.schedule.ui.ComposeUI.Activity

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.ViewModel.LoginViewModel
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.datamodel.NavigationBarItemData
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.Save
import com.hfut.schedule.ui.ComposeUI.BottomBar.SearchScreen
import com.hfut.schedule.ui.ComposeUI.BottomBar.SettingsScreen
import com.hfut.schedule.ui.ComposeUI.BottomBar.TodayScreen
import com.hfut.schedule.ui.ComposeUI.Saved.texts
import com.hfut.schedule.ui.ComposeUI.Search.NotificationsCenter.NotificationItems
import com.hfut.schedule.ui.ComposeUI.Search.NotificationsCenter.getNotifications
import com.hfut.schedule.ui.ComposeUI.Settings.getMyVersion
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SuccessUI(vm : LoginSuccessViewModel, grade : String,vm2 : LoginViewModel) {
    val switch = prefs.getBoolean("SWITCH",true)
    val navController = rememberNavController()
    var isEnabled by remember { mutableStateOf(false) }
    var showlable by remember { mutableStateOf(switch) }
    var topBarSwap by remember { mutableStateOf(0) }
    val hazeState = remember { HazeState() }
    var showBadge by remember { mutableStateOf(false) }
    if (MyApplication.version != getMyVersion()) showBadge = true
    val switchblur = prefs.getBoolean("SWITCHBLUR",true)
    var blur by remember { mutableStateOf(switchblur) }

    //等待加载完毕可切换标签
    CoroutineScope(Job()).launch {
        val card = prefs.getString("card", "")
        val json = prefs.getString("json","")
        if (json != null) {
            if (card == "请登录刷新" || !json.contains("课")) {
                delay(3000)
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
        SharePrefs.Save("Notifications", getNotifications().size.toString())
        ModalBottomSheet(onDismissRequest = { showBottomSheet = false }, sheetState = sheetState) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = .45f),
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
            TopAppBar(
                modifier = Modifier.hazeChild(state = hazeState, blurRadius = 35.dp, tint = Color.Transparent, noiseFactor = 0f),
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = if(blur).50f else 1f),
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text(texts(vm,topBarSwap)) },
                actions = {
                    if(topBarSwap == 0) {
                        IconButton(onClick = { showAll = !showAll
                        }) { Icon(painter = painterResource(id = if (showAll) R.drawable.collapse_content else R.drawable.expand_content), contentDescription = "") }
                    }
                    if(topBarSwap == 1) {
                        TextButton(onClick = { showBottomSheet = true }) {
                            BadgedBox(badge = {
                                if (getNotifications().size.toString() != prefs.getString("Notifications",""))
                                    Badge()
                            }) { Icon(painterResource(id = R.drawable.notifications), contentDescription = "") }
                        }
                    }
                },
            )
        },

        bottomBar = {
            NavigationBar(
                containerColor = if(blur) MaterialTheme.colorScheme.primaryContainer.copy(.25f) else ListItemDefaults.containerColor ,
                modifier = Modifier.hazeChild(state = hazeState, blurRadius = 35.dp, tint = Color.Transparent, noiseFactor = 0f)
            ) {
                val items = listOf(
                    NavigationBarItemData("calendar", "课程表", painterResource(R.drawable.calendar),painterResource(R.drawable.calendar_month_filled)),
                    NavigationBarItemData("today","聚焦", painterResource(R.drawable.lightbulb),painterResource(R.drawable.lightbulb_filled)),
                    NavigationBarItemData("search","查询中心", painterResource(R.drawable.search), painterResource(R.drawable.search_filledx)),
                    NavigationBarItemData("settings", "选项", painterResource(R.drawable.cube),painterResource(R.drawable.deployed_code_filled))

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
                            if(item == items[0]) topBarSwap = 0
                            if(item == items[1]) topBarSwap = 1
                            if(item == items[2]) topBarSwap = 2
                            if(item == items[3]) topBarSwap = 3
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
        NavHost(navController = navController, startDestination = "calendar",modifier = Modifier
            .haze(
                state = hazeState,
                backgroundColor = MaterialTheme.colorScheme.surface,)) {
            composable("calendar") { CalendarScreen(showAll,vm,grade,innerPadding) }
            composable("search") { SearchScreen(vm,false,innerPadding) }
            composable("settings") { SettingsScreen(
                vm, showlable, showlablechanged = {showlablech -> showlable = showlablech}, false,innerPadding,blur,blurchanged = {blurch -> blur = blurch}
            ) }
            composable("today") { TodayScreen(vm,vm2,innerPadding,blur) }
        }
    }
}