package com.hfut.schedule.ui.screen.guest

import android.annotation.SuppressLint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.NavigationBarItemData
import com.hfut.schedule.logic.enumeration.BottomBarItems
import com.hfut.schedule.logic.enumeration.BottomBarItems.FOCUS
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.component.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.screen.home.cube.sub.MyAPIItem
import com.hfut.schedule.ui.screen.home.cube.sub.update.getUpdates
import com.hfut.schedule.ui.screen.home.cube.SettingsScreen
import com.hfut.schedule.ui.screen.home.focus.funiction.AddEventFloatButton
import com.hfut.schedule.ui.screen.home.texts
import com.hfut.schedule.ui.screen.home.search.function.other.life.ApiFromLife
import com.hfut.schedule.ui.screen.home.search.function.my.notification.NotificationItems
import com.hfut.schedule.ui.screen.home.search.function.my.notification.getNotifications
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.LabUI
import com.hfut.schedule.ui.screen.home.search.SearchFuncs
import com.hfut.schedule.ui.util.MyAnimationManager
import com.hfut.schedule.ui.util.MyAnimationManager.currentPage
import com.hfut.schedule.ui.component.CustomTabRow
import com.hfut.schedule.ui.component.DividerTextExpandedWith
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.ScrollText
 
import com.hfut.schedule.ui.util.navigateAndSave
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.bottomBarBlur
import com.hfut.schedule.ui.style.topBarBlur
import com.hfut.schedule.ui.style.topBarTransplantColor
import com.hfut.schedule.ui.style.transitionBackground
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.UIViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestMainScreen(vm : NetWorkViewModel, vm2 : LoginViewModel, vmUI : UIViewModel, celebrationText : String?) {

    val navController = rememberNavController()
    val isEnabled by remember { mutableStateOf(true) }
    val switch = SharedPrefs.prefs.getBoolean("SWITCH",true)
    var showlable by remember { mutableStateOf(switch) }
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)

    var showBadge by remember { mutableStateOf(false) }
    if (getUpdates().version != AppVersion.getVersionName()) showBadge = true


    val first = BottomBarItems.SEARCH

    var targetPage by remember { mutableStateOf(first) }


    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    val ifSaved = false

    LaunchedEffect(Unit) {
        initGuestNetwork(vm,vm2)
    }

    val currentAnimationIndex by DataStoreManager.animationTypeFlow.collectAsState(initial = 0)
    // 保存上一页页码 用于决定左右动画
    LaunchedEffect(targetPage) {
        currentPage = targetPage.page
    }

    if (showBottomSheet) {
        SharedPrefs.saveString("Notifications", getNotifications().size.toString())
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("收纳")
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ){
                    MyAPIItem()
                    DividerTextExpandedWith("通知") {
                        NotificationItems()
                    }

                    DividerTextExpandedWith("实验室") {
                        LabUI()
                    }

                }
            }
        }
    }


    val pagerState = rememberPagerState(pageCount = { 2 })

    val titles = listOf("重要安排","其他事项")


    var searchText by remember { mutableStateOf("") }

    var showSearch by remember { mutableStateOf(false) }

    val isAddUIExpanded by remember { derivedStateOf { vmUI.isAddUIExpanded } }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var isNavigationIconVisible by remember { mutableStateOf(true) }
    // 监听滚动状态
    LaunchedEffect(scrollBehavior.state) {
        snapshotFlow { scrollBehavior.state.collapsedFraction }
            .collect { collapsedFraction -> isNavigationIconVisible = collapsedFraction < 0.5f }
    }

    val focusActions = @Composable {
        Row {
            ApiFromLife(vm,hazeState)
            TextButton(onClick = { showBottomSheet = true }) {
                BadgedBox(badge = {
                    if (getNotifications().size.toString() != prefs.getString("Notifications",""))
                        Badge()
                }) { Icon(painterResource(id = R.drawable.notifications), contentDescription = "") }
            }
        }
    }


    Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
        var innerPaddingValues by remember { mutableStateOf<PaddingValues?>(null) }

        Box(modifier = Modifier.align(Alignment.BottomEnd).zIndex(3f)) {
            innerPaddingValues?.let { AddEventFloatButton(isSupabase = false,isVisible = isNavigationIconVisible && targetPage == FOCUS,vmUI,it,vm) }
        }
        Scaffold(
            modifier = transitionBackground(isAddUIExpanded).fillMaxSize(),
            topBar = {
                Column(modifier = Modifier.topBarBlur(hazeState)) {
                    if(targetPage == FOCUS && celebrationText != null) {
                        MediumTopAppBar(
                            colors = topBarTransplantColor(),
                            navigationIcon = {
                                if(isNavigationIconVisible) {
                                    Box(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP-3.dp)) {
                                        Text(
                                            text = celebrationText,
                                            fontSize = 30.sp,
                                            color = MaterialTheme.colorScheme.secondaryContainer
                                        )
                                    }
                                }
                            },
                            title = { ScrollText(texts(targetPage)) },
                            actions = { focusActions() },
                            scrollBehavior = scrollBehavior
                        )
                    } else {
                        TopAppBar(
                            colors = topAppBarColors(
                                containerColor = Color.Transparent,
                                titleContentColor = MaterialTheme.colorScheme.primary
                            ),
                            title = {
                                if(targetPage != BottomBarItems.SEARCH) {
                                    ScrollText(texts(targetPage))
                                } else {
                                    if(!showSearch) {
                                        ScrollText(texts(BottomBarItems.SEARCH))
                                    } else {
                                        SearchFuncs(ifSaved,searchText) {
                                            searchText = it
                                        }
                                    }
                                }
                            },
                            actions = {
                                when(targetPage){
                                    FOCUS -> { focusActions() }
                                    BottomBarItems.SEARCH -> {
                                        if(!showSearch) {
                                            IconButton(onClick = { showSearch = !showSearch }) {
                                                Icon(painter = painterResource(id =  R.drawable.search), contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                                            }
                                            TextButton(onClick = { Starter.refreshLogin() }) {
                                                Icon(painter = painterResource(id =  R.drawable.login), contentDescription = "")
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(APP_HORIZONTAL_DP))
                                        //null
                                    }
                                    else -> {}
                                }
                            },
                        )
                    }

                    when(targetPage){
                        FOCUS -> CustomTabRow(pagerState, titles)
                        else -> {}
                    }
                }
            },
            bottomBar = {
                Column {
//                if(!blur)
//                    Divider()
                    NavigationBar(containerColor = Color.Transparent ,
                        modifier = Modifier.bottomBarBlur(hazeState)
                    ) {
                        //悬浮底栏效果
                        //modifier = Modifier.padding(APP_HORIZONTAL_DP).shadow(10.dp).clip(RoundedCornerShape(14.dp))
                        val items = listOf(
                            NavigationBarItemData(
                                FOCUS.name,"聚焦", painterResource(R.drawable.lightbulb), painterResource(
                                    R.drawable.lightbulb_filled)
                            ),
                            NavigationBarItemData(
                                BottomBarItems.SEARCH.name,"查询中心", painterResource(
                                    R.drawable.search), painterResource(R.drawable.search_filledx)
                            ),
                            NavigationBarItemData(BottomBarItems.SETTINGS.name,"选项", painterResource(if (getUpdates().version == AppVersion.getVersionName()) R.drawable.deployed_code else R.drawable.deployed_code_update), painterResource(if (getUpdates().version == AppVersion.getVersionName()) R.drawable.deployed_code_filled else R.drawable.deployed_code_update_filled ))
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
                                enabled = isEnabled,
                                modifier = Modifier.scale(scale.value),
                                interactionSource = interactionSource,
                                onClick = {
                                    if(item == items[0]) targetPage = FOCUS
                                    if(item == items[1]) targetPage = BottomBarItems.SEARCH
                                    if(item == items[2]) targetPage = BottomBarItems.SETTINGS
                                    if (!selected) {
                                        navController.navigateAndSave(route)
                                    }
                                },
                                label = { Text(text = item.label) },
                                icon = {
                                    BadgedBox(badge = {
                                        if (item == items[2]){
                                            if (showBadge)
                                                Badge{ Text(text = "1") }
                                        }
                                    }) { Icon(if(selected)item.filledIcon else item.icon, contentDescription = item.label) }
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            innerPaddingValues = innerPadding

            val animation = MyAnimationManager.getAnimationType(currentAnimationIndex,targetPage.page)

            NavHost(
                navController = navController,
                startDestination = first.name,
                enterTransition = { animation.enter },
                exitTransition = { animation.exit },
                modifier = Modifier
                    .hazeSource(
                        state = hazeState
                        //backgroundColor = MaterialTheme.colorScheme.surface,
                    )
            ) {

                composable(FOCUS.name) {
                    Scaffold {
                        TodayScreenNoLogin(vm,vm2,innerPadding,vmUI,pagerState,hazeState)
                    }
                    //Test()
                }
                composable(BottomBarItems.SEARCH.name) {
                    Scaffold {
                        SearchScreenNoLogin(vm,ifSaved,innerPadding,vmUI,false,searchText,hazeState)
                    }

                }
                composable(BottomBarItems.SETTINGS.name) {
                    Scaffold {
                        SettingsScreen(
                            vm,
                            showlable,
                            showlablechanged = { showlablech -> showlable = showlablech},
                            ifSaved,
                            innerPadding,
//                            blur,
//                            blurchanged = { blurch -> blur = blurch},
                            vm2,
                            hazeState,
                        )
                    }
                }
            }
        }
    }
}


