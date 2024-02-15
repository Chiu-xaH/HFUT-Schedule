package com.hfut.schedule.ui.ComposeUI.Saved

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
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
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.ViewModel.LoginViewModel
import com.hfut.schedule.logic.datamodel.NavigationBarItemData
import com.hfut.schedule.ui.ComposeUI.BottomBar.SearchScreen
import com.hfut.schedule.ui.ComposeUI.BottomBar.SettingsScreen
import com.hfut.schedule.ui.ComposeUI.BottomBar.TodayScreen
import com.hfut.schedule.ui.ComposeUI.SavedCourse.SaveCourse
import com.hfut.schedule.ui.ComposeUI.Settings.getMyVersion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation", "CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationGraphicsApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NoNetWork(vm : LoginSuccessViewModel,vm2 : LoginViewModel) {
    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
    val switch = prefs.getBoolean("SWITCH",true)
    val navController = rememberNavController()
    var isEnabled by remember { mutableStateOf(true) }
    var showlable by remember { mutableStateOf(switch) }
    var first = "1"

    CoroutineScope(Job()).launch { NetWorkUpdate(vm, vm2) }

    var showBadge by remember { mutableStateOf(false) }
    if (MyApplication.version != getMyVersion()) showBadge = true
    var showBadge2 by remember { mutableStateOf(false) }
   // val savenum = prefs.getInt("GradeNum",0) + prefs.getInt("ExamNum",0) + prefs.getInt("Notifications",0)
    //val getnum = getGrade().size + getExam().size + getNotifications().size
    //if (savenum != getnum) showBadge2 = true


//判定是否以聚焦作为第一页
    first = when (prefs.getBoolean("SWITCHFOCUS",true)) {
        true -> "2"
        false -> "1"
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),

        bottomBar = {
            NavigationBar() {
            //    val image = AnimatedImageVector.animatedVectorResource(R.drawable.ic_hourglass_animated)
              //  var atEnd by remember { mutableStateOf(false) }

                val items = listOf(
                    NavigationBarItemData("1", "课程表", painterResource(R.drawable.calendar ), painterResource(R.drawable.calendar_month_filled)),
                    NavigationBarItemData("2","聚焦", painterResource(R.drawable.lightbulb), painterResource(R.drawable.lightbulb_filled)),
                    NavigationBarItemData("search","查询中心", painterResource(R.drawable.search),painterResource(R.drawable.search)),
                    NavigationBarItemData("3","选项", painterResource(R.drawable.cube), painterResource(R.drawable.deployed_code_filled))
                )
                items.forEach { item ->
                    val route = item.route
                    val selected = navController.currentBackStackEntryAsState().value?.destination?.route == route
                    NavigationBarItem(
                        selected = selected,
                        alwaysShowLabel = showlable,
                        enabled = isEnabled,
                        onClick = {
                         //   if(item == items[2])
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
        NavHost(navController = navController, startDestination = first) {
            composable("1") { SaveCourse() }
            composable("2") { TodayScreen(vm,vm2) }
            composable("search") { SearchScreen(vm,true) }
            composable("3") { SettingsScreen(vm,showlable, showlablechanged = {showlablech -> showlable = showlablech},true)
            }

        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {}
    }
}