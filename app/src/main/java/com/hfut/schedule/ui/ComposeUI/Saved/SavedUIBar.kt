package com.hfut.schedule.ui.ComposeUI.Saved

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.datamodel.NavigationBarItemData
import com.hfut.schedule.ui.ComposeUI.BottomBar.SearchScreen
import com.hfut.schedule.ui.ComposeUI.BottomBar.SettingsScreen
import com.hfut.schedule.ui.ComposeUI.BottomBar.TodayScreen
import com.hfut.schedule.ui.ComposeUI.Settings.getMyVersion

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NoNetWork(vm : LoginSuccessViewModel) {
    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
    val switch = prefs.getBoolean("SWITCH",true)
    val navController = rememberNavController()
    var isEnabled by remember { mutableStateOf(true) }
    var showlable by remember { mutableStateOf(switch) }
    var first = "1"



    var showBadge by remember { mutableStateOf(false) }
    if (MyApplication.version != getMyVersion()) showBadge = true


//判定是否以聚焦作为第一页
    when (prefs.getBoolean("SWITCHFOCUS",false)) {
        true -> first = "2"
        false -> first = "1"
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),

        bottomBar = {
            NavigationBar() {
                val items = listOf(
                    NavigationBarItemData("1", "课程表", painterResource(R.drawable.calendar)),
                    NavigationBarItemData("2","聚焦", painterResource(R.drawable.lightbulb)),
                    NavigationBarItemData("search","查询中心", painterResource(R.drawable.search)),
                    NavigationBarItemData("3","选项", painterResource(id = R.drawable.cube))
                )
                items.forEach { item ->
                    val route = item.route
                    val selected = navController.currentBackStackEntryAsState().value?.destination?.route == route
                    NavigationBarItem(
                        selected = selected,
                        alwaysShowLabel = showlable,
                        enabled = isEnabled,
                        onClick = {
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
                            }) { Icon(item.icon, contentDescription = item.label) }
                        }

                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = first) {
            composable("1") { NoNet() }
            composable("2") { TodayScreen(vm) }
            composable("search") { SearchScreen(vm,true) }
            composable("3") { SettingsScreen(vm,showlable, showlablechanged = {showlablech -> showlable = showlablech},)
            }

        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {}
    }
}