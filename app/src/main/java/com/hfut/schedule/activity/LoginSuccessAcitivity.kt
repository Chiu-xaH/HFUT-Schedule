package com.hfut.schedule.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.activity.ui.theme.肥工课程表Theme
import com.hfut.schedule.logic.datamodel.NavigationBarItemData
import com.hfut.schedule.ui.ViewModel.JxglstuViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginSuccessAcitivity : ComponentActivity() {

    private val vm by lazy { ViewModelProvider(this).get(JxglstuViewModel::class.java) }
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            肥工课程表Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SuccessUI()
                }
            }
        }

            val prefs = getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
            val cookie = prefs.getString("redirect", "")

            vm.Jxglstulogin(cookie!!)

    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SuccessUI() {
        val navController = rememberNavController()
       
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar() {
                    val items = listOf(
                        NavigationBarItemData("search","信息查询",painterResource(R.drawable.search)),
                        NavigationBarItemData("calendar", "课程表", painterResource(R.drawable.calendar)),
                        NavigationBarItemData("person", "个人主页", painterResource(R.drawable.person)),
                        NavigationBarItemData("settings", "选项", painterResource(R.drawable.cube))
                    )
                    items.forEach { item ->
                        val route = item.route
                        val selected = navController.currentBackStackEntryAsState().value?.destination?.route == route
                        NavigationBarItem(
                            selected = selected,
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
                        icon = { Icon(item.icon, contentDescription = item.label)}

                            )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(navController = navController, startDestination = "search") {
                composable("calendar") { CalendarScreen()}
                composable("search") {SearchScreen()}
                composable("person") {PersonScreen()}
                composable("settings") {SettingsScreen()}
            }

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {

            }

        }
    }
    @SuppressLint("SuspiciousIndentation")
    @Composable
    fun CalendarScreen() {
       // val navController = rememberNavController()
        var loading by remember { mutableStateOf(true) }
        if (loading) {
            LaunchedEffect(Unit) {
                delay(4000)
                //在此插入课程表的布局，加载完成后显示
                //////////////////////////////////////////////////////////////////////////////////
                val it = Intent(MyApplication.context, DatumActivity::class.java)
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                MyApplication.context.startActivity(it)
                //////////////////////////////////////////////////////////////////////////////////
            }
        }
        AnimatedVisibility(visible = loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }


        val prefs = getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
        val cookie = prefs.getString("redirect", "")
        vm.getStudentId(cookie!!)
        val grade = intent.getStringExtra("Grade")

        val job = Job()
        val scope = CoroutineScope(job)
        scope.apply {
            launch {
                delay(1000)
                vm.getLessonIds(cookie,grade!!)
            }
            launch {
                delay(2500)
                vm.getDatum(cookie!!)
            }
        }

            }
    @Composable
    fun SettingsScreen() {
        //待开发
    }
    @Composable
    fun PersonScreen() {
       //待开发
        //个人主页
        //   selfButton.setOnClickListener {
        //       val prefs = getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
        //      val cookie = prefs.getString("redirect", "")
        //     vm.Self(cookie!!)
        //     Toast.makeText(this,"获取成功，但是我还没写界面，憋着",Toast.LENGTH_SHORT).show()
        //  }
    }
    @Composable
    fun SearchScreen() {
       //待开发
    }

}




