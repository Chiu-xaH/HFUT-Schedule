package com.hfut.schedule.ui.activity.nologin

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.beans.NavigationBarItemData
import com.hfut.schedule.logic.enums.BottomBarItems
import com.hfut.schedule.logic.utils.DataStoreManager
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.logic.utils.VersionUtils
import com.hfut.schedule.logic.utils.data.SharePrefs
import com.hfut.schedule.ui.activity.home.cube.items.subitems.MyAPIItem
import com.hfut.schedule.ui.activity.home.cube.items.subitems.update.getUpdates
import com.hfut.schedule.ui.activity.home.cube.main.SettingsScreen
import com.hfut.schedule.ui.activity.home.main.saved.texts
import com.hfut.schedule.ui.activity.home.search.functions.notifications.NotificationItems
import com.hfut.schedule.ui.activity.home.search.functions.notifications.getNotifications
import com.hfut.schedule.ui.activity.home.search.functions.webLab.LabUI
import com.hfut.schedule.ui.activity.home.search.main.SearchFuncs
import com.hfut.schedule.ui.utils.NavigateAnimationManager
import com.hfut.schedule.ui.utils.NavigateAnimationManager.currentPage

import com.hfut.schedule.ui.utils.components.appHorizontalDp
import com.hfut.schedule.ui.utils.components.CustomTabRow
import com.hfut.schedule.ui.utils.components.DividerTextExpandedWith
import com.hfut.schedule.ui.utils.components.HazeBottomSheetTopBar
import com.hfut.schedule.ui.utils.components.ScrollText
import com.hfut.schedule.ui.utils.navigateAndSave
import com.hfut.schedule.ui.utils.style.HazeBottomSheet
import com.hfut.schedule.ui.utils.style.bottomBarBlur
import com.hfut.schedule.ui.utils.style.topBarBlur
import com.hfut.schedule.viewmodel.LoginViewModel
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.viewmodel.UIViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoLoginUI(vm : NetWorkViewModel,vm2 : LoginViewModel,vmUI : UIViewModel) {
    val navController = rememberNavController()
    val isEnabled by remember { mutableStateOf(true) }
    val switch = SharePrefs.prefs.getBoolean("SWITCH",true)
    var showlable by remember { mutableStateOf(switch) }
    val hazeState = remember { HazeState() }

    var showBadge by remember { mutableStateOf(false) }
    if (getUpdates().version != VersionUtils.getVersionName()) showBadge = true
    val switchblur = SharePrefs.prefs.getBoolean("SWITCHBLUR", VersionUtils.canBlur)
    var blur by remember { mutableStateOf(switchblur) }

    val animation by remember { mutableStateOf(SharePrefs.prefs.getInt("ANIMATION", MyApplication.ANIMATION_SPEED)) }

    val first = BottomBarItems.SEARCH

    var bottomBarItems by remember { mutableStateOf(first) }


    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    val ifSaved = false

    CoroutineScope(Job()).launch { NetWorkUpdateNoLogin(vm2) }

    val currentAnimationIndex by DataStoreManager.animationTypeFlow.collectAsState(initial = 0)
//    var targetPage by remember { mutableStateOf(FixBarItems.Fix) }
    // 保存上一页页码 用于决定左右动画
    LaunchedEffect(bottomBarItems) {
        currentPage = bottomBarItems.page
    }



    if (showBottomSheet) {
        SharePrefs.saveString("Notifications", getNotifications().size.toString())
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
//            sheetState = sheetState, modifier = Modifier,
//            shape = bottomSheetRound(sheetState)
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        //.blur(blurRadius, BlurredEdgeTreatment.Unbounded),
        topBar = {
            Column(modifier = Modifier.topBarBlur(hazeState)) {
                TopAppBar(
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color.Transparent
                        ///MaterialTheme.colorScheme.primaryContainer.copy(alpha = if(blur) 0f else 1f)
                        ,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {

                        if(bottomBarItems != BottomBarItems.SEARCH) {
                            ScrollText(texts(bottomBarItems))
                        } else {
                            if(!showSearch) {
                                ScrollText(texts(BottomBarItems.SEARCH))
                            } else {
                                SearchFuncs(ifSaved,blur,searchText) {
                                    searchText = it
                                }
                            }
                        }
                    },
                    actions = {
                        when(bottomBarItems){
                            BottomBarItems.FOCUS -> {
                                Row {
                                    TextButton(onClick = { showBottomSheet = true }) {
                                        BadgedBox(badge = {
                                            if (getNotifications().size.toString() != SharePrefs.prefs.getString("Notifications",""))
                                                Badge()
                                        }) { Icon(painterResource(id = R.drawable.notifications), contentDescription = "") }
                                    }
                                }
                            }
                            BottomBarItems.SEARCH -> {
                                if(!showSearch) {
                                    IconButton(onClick = { showSearch = !showSearch }) {
                                        Icon(painter = painterResource(id =  R.drawable.search), contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    TextButton(onClick = { Starter.refreshLogin() }) {
                                        Icon(painter = painterResource(id =  R.drawable.login), contentDescription = "")
                                    }
                                }
                                Spacer(modifier = Modifier.width(appHorizontalDp()))
                                //null
                            }
                            else -> null
                        }
                    },
                )
//                if(!blur) {
//                    if(bottomBarItems != FOCUS)
//                        Divider()
//                }
                when(bottomBarItems){
                    BottomBarItems.FOCUS -> CustomTabRow(pagerState, titles, blur)
                    else -> null
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
                    //modifier = Modifier.padding(AppHorizontalDp()).shadow(10.dp).clip(RoundedCornerShape(14.dp))
                    val items = listOf(
                        NavigationBarItemData(
                            BottomBarItems.FOCUS.name,"聚焦", painterResource(R.drawable.lightbulb), painterResource(
                                R.drawable.lightbulb_filled)
                        ),
                        NavigationBarItemData(
                            BottomBarItems.SEARCH.name,"查询中心", painterResource(
                                R.drawable.search), painterResource(R.drawable.search_filledx)
                        ),
                        NavigationBarItemData(BottomBarItems.SETTINGS.name,"选项", painterResource(if (getUpdates().version == VersionUtils.getVersionName()) R.drawable.deployed_code else R.drawable.deployed_code_update), painterResource(if (getUpdates().version == VersionUtils.getVersionName()) R.drawable.deployed_code_filled else R.drawable.deployed_code_update_filled ))
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
                                if(item == items[0]) bottomBarItems = BottomBarItems.FOCUS
                                if(item == items[1]) bottomBarItems = BottomBarItems.SEARCH
                                if(item == items[2]) bottomBarItems = BottomBarItems.SETTINGS
                                //     atEnd = !atEnd
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
        val animation = NavigateAnimationManager.getAnimationType(currentAnimationIndex,bottomBarItems.page)

        NavHost(
            navController = navController,
            startDestination = first.name,
            enterTransition = { animation.enter },
            exitTransition = { animation.exit },
            modifier = Modifier
                .haze(
                    state = hazeState,
                    //backgroundColor = MaterialTheme.colorScheme.surface,
                )) {

            composable(BottomBarItems.FOCUS.name) {
                Scaffold {
                    TodayScreenNoLogin(vm,vm2,innerPadding, blur,vmUI,ifSaved,false,pagerState,hazeState)
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
                        blur,
                        blurchanged = { blurch -> blur = blurch},
                        vm2,
                        hazeState
                    )
                }
            }
        }
    }
}


suspend fun NetWorkUpdateNoLogin(vm2 : LoginViewModel){

    CoroutineScope(Job()).apply {
        async { vm2.My() }
    }
}