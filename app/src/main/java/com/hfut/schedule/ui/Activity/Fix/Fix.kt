package com.hfut.schedule.ui.Activity.Fix

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.ViewModel.LoginViewModel
import com.hfut.schedule.logic.Enums.FixBarItems
import com.hfut.schedule.logic.datamodel.NavigationBarItemData
import com.hfut.schedule.logic.utils.AndroidVersion
import com.hfut.schedule.logic.utils.SharePrefs
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Fix(vm : LoginViewModel,vm2 : LoginSuccessViewModel) {
    val switchblur = SharePrefs.prefs.getBoolean("SWITCHBLUR", AndroidVersion.sdkInt >= 32)
    var blur by remember { mutableStateOf(switchblur) }
    val hazeState = remember { HazeState() }
    val navController = rememberNavController()
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
                    title = { Text("修复与检测") },
                    actions = {
                        IconButton(onClick = {

                        }) {
                            Icon(Icons.Filled.Close, contentDescription = "")
                        }
                    }
                )
                Divider()
            }
        },
        bottomBar = {
            Divider()
            NavigationBar(containerColor = if(blur) MaterialTheme.colorScheme.primaryContainer.copy(.25f) else ListItemDefaults.containerColor ,
                modifier = Modifier
                    .hazeChild(state = hazeState, blurRadius = MyApplication.Blur, tint = Color.Transparent, noiseFactor = 0f)) {

                val items = listOf(
                    NavigationBarItemData(
                        FixBarItems.Fix.name,"修复", painterResource(R.drawable.build), painterResource(
                            R.drawable.build_filled)
                    ),
                    NavigationBarItemData(
                        FixBarItems.About.name,"关于", painterResource(R.drawable.info), painterResource(
                            R.drawable.info_filled)
                    )
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
                        icon = {
                            BadgedBox(badge = {}) { Icon(if(selected)item.filledIcon else item.icon, contentDescription = item.label) }
                        }
                    )
                }
            }
        }
    ) {innerPadding ->
        NavHost(navController = navController,
            startDestination = FixBarItems.Fix.name,
            enterTransition = {
                scaleIn(animationSpec = tween(durationMillis = 250)) + expandVertically(expandFrom = Alignment.CenterVertically)
            },
            exitTransition = {
                scaleOut(animationSpec = tween(durationMillis = 250)) + shrinkVertically(shrinkTowards = Alignment.CenterVertically)
            },
            modifier = Modifier
            .haze(
                state = hazeState,
                backgroundColor = MaterialTheme.colorScheme.surface,
            )) {
            composable(FixBarItems.Fix.name) {
                Scaffold {
                    FixUI(innerPadding = innerPadding,vm, vm2)
                }

            }
            composable(FixBarItems.About.name) {
                Scaffold {
                    AboutUI(innerPadding = innerPadding, vm = vm)
                }
            }
        }
    }
}