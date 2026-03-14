package com.hfut.schedule.ui.screen.supabase

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.CardBarItems
import com.hfut.schedule.logic.enumeration.SortType
import com.hfut.schedule.logic.enumeration.SupabaseScreen
import com.hfut.schedule.logic.model.NavigationBarItemData
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.button.HazeBottomBar
import com.hfut.schedule.ui.component.screen.pager.CustomTabRow
import com.hfut.schedule.ui.screen.home.focus.funiction.AddEventFloatButton
import com.hfut.schedule.ui.screen.supabase.cube.SupabaseSettingsScreen
import com.hfut.schedule.ui.screen.supabase.focus.SupabaseStorageScreen
import com.hfut.schedule.ui.screen.supabase.home.SupabaseHomeScreen
import com.hfut.schedule.ui.screen.supabase.manage.SupabaseMeScreenRefresh
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.ui.util.navigation.AppAnimationManager.currentPage
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.hfut.schedule.ui.util.navigation.currentRouteWithoutArgs
import com.xah.common.style.color.topBarTransplantColor
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

private val items = listOf(
    NavigationBarItemData(
        SupabaseScreen.HOME.name,"源", R.drawable.cloud,  R.drawable.cloud_filled
    ),
    NavigationBarItemData(
        SupabaseScreen.ME.name,"已贡献", R.drawable.database,R.drawable.database_filled
    ),
    NavigationBarItemData(
        SupabaseScreen.STORAGE.name,"已下载", R.drawable.lightbulb,R.drawable.lightbulb_filled
    ),
    NavigationBarItemData(
        SupabaseScreen.SETTINGS.name,"选项", R.drawable.deployed_code,R.drawable.deployed_code_filled
    )
)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupabaseHome(vm : NetWorkViewModel,navHostController: NavHostController,vmUI : UIViewModel) {
    val navController = rememberNavController()
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val bottomBarItems = when(navController.currentRouteWithoutArgs()) {
        SupabaseScreen.HOME.name -> SupabaseScreen.HOME
        SupabaseScreen.SETTINGS.name -> SupabaseScreen.SETTINGS
        SupabaseScreen.STORAGE.name -> SupabaseScreen.STORAGE
        SupabaseScreen.ME.name -> SupabaseScreen.ME
        else -> SupabaseScreen.HOME
    }
    val titles = listOf("日程","网址导航")
    val pagerState = rememberPagerState(pageCount = { titles.size })
    val context = LocalActivity.current
    var sortType by remember { mutableStateOf(SortType.TIME_LINE) }
    var sortReversed by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
        var innerPaddingValues by remember { mutableStateOf<PaddingValues?>(null) }

        Box(modifier = Modifier.align(Alignment.BottomEnd).zIndex(3f)) {
            innerPaddingValues?.let { AddEventFloatButton(isSupabase = bottomBarItems != SupabaseScreen.STORAGE,isVisible = bottomBarItems != SupabaseScreen.SETTINGS,vmUI,it,vm) }
        }

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                Column(modifier = Modifier.topBarBlur(hazeState)) {
                    MediumTopAppBar(
                        scrollBehavior = scrollBehavior,
                        colors = topBarTransplantColor(),
                        title = { Text("信息共建") },
                        navigationIcon = {
                            IconButton(onClick = {
                                context?.finish()
                            }) {
                                Icon(painterResource(R.drawable.arrow_back), contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                            }
                        },
                        actions = {
                            if(bottomBarItems == SupabaseScreen.HOME) {
                                IconButton(onClick = {
                                    sortReversed = !sortReversed
                                }) {
                                    Icon(painterResource(
                                        R.drawable.keyboard_arrow_down
                                    ), contentDescription = "", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.rotate(
                                        if(sortReversed) {
                                            0f
                                        } else {
                                            180f
                                        }
                                    ))
                                }

                                TextButton(
                                    onClick = {
                                        sortType = when(sortType) {
                                            SortType.TIME_LINE -> SortType.CREATE_TIME
                                            SortType.CREATE_TIME -> SortType.TIME_LINE
                                        }
                                    },
                                    modifier = Modifier.padding(end = 5.dp)
                                ) {
                                    Text(
                                        when(sortType) {
                                            SortType.TIME_LINE -> "按时间线"
                                            SortType.CREATE_TIME -> "按创建时间"
                                        }
                                    )
                                }
                            }
                        }
                    )
                    if(bottomBarItems == SupabaseScreen.HOME) {
                        CustomTabRow(pagerState, titles)
                    }
                }
            },
            bottomBar = {
                HazeBottomBar(hazeState,items,navController)
            }
        ) { innerPadding ->
            innerPaddingValues = innerPadding
            NavHost(navController = navController,
                startDestination = CardBarItems.HOME.name,
                enterTransition = {
                    AppAnimationManager.centerAnimation.enter
                },
                exitTransition = {
                    AppAnimationManager.centerAnimation.exit
                },
                modifier = Modifier.hazeSource(state = hazeState)
            ) {
                composable(SupabaseScreen.HOME.name) {
                    Scaffold {
                        SupabaseHomeScreen(vm,sortType,sortReversed,innerPadding,pagerState)
                    }
                }
                composable(SupabaseScreen.ME.name) {
                    Scaffold {
                        SupabaseMeScreenRefresh(vm,innerPadding)
                    }
                }
                composable(SupabaseScreen.STORAGE.name) {
                    Scaffold {
                        SupabaseStorageScreen(innerPadding,hazeState)
                    }
                }
                composable(SupabaseScreen.SETTINGS.name) {
                    Scaffold(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                        SupabaseSettingsScreen(vm,innerPadding,hazeState)
                    }
                }
            }
        }
    }
}