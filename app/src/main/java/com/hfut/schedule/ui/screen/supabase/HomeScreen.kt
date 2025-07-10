package com.hfut.schedule.ui.screen.supabase

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.CardBarItems
import com.hfut.schedule.logic.enumeration.SortType
import com.hfut.schedule.logic.enumeration.SupabaseScreen
import com.hfut.schedule.logic.model.NavigationBarItemData
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.ui.component.custom.CustomTabRow
import com.hfut.schedule.ui.screen.home.focus.funiction.AddEventFloatButton
import com.hfut.schedule.ui.screen.supabase.cube.SupabaseSettingsScreen
import com.hfut.schedule.ui.screen.supabase.focus.SupabaseStorageScreen
import com.hfut.schedule.ui.screen.supabase.home.SupabaseHomeScreen
import com.hfut.schedule.ui.screen.supabase.manage.SupabaseMeScreenRefresh
import com.hfut.schedule.ui.style.bottomBarBlur
import com.hfut.schedule.ui.style.topBarBlur
import com.hfut.schedule.ui.style.transitionBackground
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.ui.util.AppAnimationManager.currentPage
import com.hfut.schedule.ui.util.navigateAndSave
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupabaseHome(vm : NetWorkViewModel,navHostController: NavHostController,vmUI : UIViewModel) {
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    var bottomBarItems by remember { mutableStateOf(SupabaseScreen.HOME) }
    val navController = rememberNavController()
    val titles = listOf("日程","网址导航")

    val pagerState = rememberPagerState(pageCount = { titles.size })

    val isAddUIExpanded by remember { derivedStateOf { vmUI.isAddUIExpandedSupabase } }
    val isAddUIExpandedS by remember { derivedStateOf { vmUI.isAddUIExpanded } }



    val currentAnimationIndex by DataStoreManager.animationTypeFlow.collectAsState(initial = 0)
    // 保存上一页页码 用于决定左右动画
    if(currentAnimationIndex == 2) {
        LaunchedEffect(bottomBarItems) {
            currentPage = bottomBarItems.page
        }
    }

    val context = LocalActivity.current

    var sortType by remember { mutableStateOf(SortType.TIME_LINE) }
    var sortReversed by remember { mutableStateOf(false) }


    Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
        var innerPaddingValues by remember { mutableStateOf<PaddingValues?>(null) }

        Box(modifier = Modifier.align(Alignment.BottomEnd).zIndex(3f)) {
            innerPaddingValues?.let { AddEventFloatButton(isSupabase = bottomBarItems != SupabaseScreen.STORAGE,isVisible = bottomBarItems != SupabaseScreen.SETTINGS,vmUI,it,vm) }
        }

        Scaffold(
            modifier = transitionBackground(if(bottomBarItems == SupabaseScreen.STORAGE) isAddUIExpandedS else isAddUIExpanded ).fillMaxSize(),
            topBar = {
                Column(modifier = Modifier.topBarBlur(hazeState)) {
                    TopAppBar(
                        colors = topAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary
                        ),
                        title = { Text("信息共建") },
                        actions = {
                            if(bottomBarItems == SupabaseScreen.HOME) {
                                IconButton(onClick = {
                                    sortReversed = !sortReversed
                                }) {
                                    Icon(if(sortReversed) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp, contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                                }

                                TextButton(
                                    onClick = {
                                        sortType = when(sortType) {
                                            SortType.TIME_LINE -> SortType.CREATE_TIME
                                            SortType.CREATE_TIME -> SortType.TIME_LINE
                                        }
                                    }
                                ) {
                                    Text(
                                        when(sortType) {
                                            SortType.TIME_LINE -> "按时间线"
                                            SortType.CREATE_TIME -> "按创建时间"
                                        }
                                    )
                                }
                            }

                            IconButton(onClick = {
                                context?.finish()
                            }) {
                                Icon(Icons.Filled.Close, contentDescription = "",tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    )
                    if(bottomBarItems == SupabaseScreen.HOME) {
                        CustomTabRow(pagerState, titles)
                    }
                }
            },
            bottomBar = {
                Column {
                    NavigationBar(containerColor = Color.Transparent ,
                        modifier = Modifier.bottomBarBlur(hazeState)) {

                        val items = listOf(
                            NavigationBarItemData(
                                SupabaseScreen.HOME.name,"源", painterResource(R.drawable.cloud), painterResource(
                                    R.drawable.cloud_filled)
                            ),
                            NavigationBarItemData(
                                SupabaseScreen.ME.name,"已贡献", painterResource(R.drawable.database), painterResource(
                                    R.drawable.database_filled)
                            ),
                            NavigationBarItemData(
                                SupabaseScreen.STORAGE.name,"已下载", painterResource(R.drawable.lightbulb),
                                painterResource(R.drawable.lightbulb_filled)
                            ),
                            NavigationBarItemData(
                                SupabaseScreen.SETTINGS.name,"选项", painterResource(R.drawable.deployed_code),
                                painterResource(R.drawable.deployed_code_filled)
                            )
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
                                modifier = Modifier.scale(scale.value),
                                interactionSource = interactionSource,
                                onClick = {
                                    when(item) {
                                        items[0] -> bottomBarItems = SupabaseScreen.HOME
                                        items[1] -> bottomBarItems = SupabaseScreen.ME
                                        items[2] -> bottomBarItems = SupabaseScreen.STORAGE
                                        items[3] -> bottomBarItems = SupabaseScreen.SETTINGS
                                    }
                                    if (!selected) {
                                        navController.navigateAndSave(route)
                                    }
                                },
                                label = { Text(text = item.label) },
                                icon = {
                                    Icon(if(selected)item.filledIcon else item.icon, contentDescription = item.label)
                                },
                                colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = .9f))

                            )
                        }
                    }
                }

            }
        ) { innerPadding ->
            innerPaddingValues = innerPadding
            val animation = AppAnimationManager.getAnimationType(currentAnimationIndex,bottomBarItems.page)

            NavHost(navController = navController,
                startDestination = CardBarItems.HOME.name,
                enterTransition = { animation.enter },
                exitTransition = { animation.exit },
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
                    Scaffold {
                        SupabaseSettingsScreen(vm,innerPadding,hazeState)
                    }
                }
            }
        }
    }
}