package com.hfut.schedule.ui.screen.grade

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.GradeBarItems
import com.hfut.schedule.logic.model.NavigationBarItemData
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.container.StyleCardListItem
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.grade.analysis.GradeCountUI
import com.hfut.schedule.ui.screen.grade.grade.community.GradeItemUI
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.GPAWithScore
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.GradeItemUIJXGLSTU
import com.hfut.schedule.ui.screen.home.search.TopBarTopIcon
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.bottomBarBlur
import com.hfut.schedule.ui.style.topBarBlur
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.ui.util.AppAnimationManager.currentPage
import com.hfut.schedule.ui.util.navigateAndSave
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.component.TransitionScaffold
import com.xah.transition.component.containerShare
import com.xah.transition.component.iconElementShare
import com.xah.transition.component.titleElementShare
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun GradeScreen(
    ifSaved : Boolean,
    vm : NetWorkViewModel,
    navTopController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val targetRoute = remember { AppNavRoute.Grade.receiveRoute() }
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val navController = rememberNavController()

    var showSearch by remember { mutableStateOf(false) }

    var showBottomSheet by remember { mutableStateOf(false) }

    val currentAnimationIndex by DataStoreManager.animationTypeFlow.collectAsState(initial = 0)
    var targetPage by remember { mutableStateOf(GradeBarItems.GRADE) }
    // 保存上一页页码 用于决定左右动画
    if(currentAnimationIndex == 2) {
        LaunchedEffect(targetPage) {
            currentPage = targetPage.page
        }
    }


    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet= false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("说明")
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    Infos()
                }
            }
        }
    }
    val items = listOf(
        NavigationBarItemData(
            GradeBarItems.GRADE.name,"学期", painterResource(R.drawable.article), painterResource(
                R.drawable.article_filled)
        ),
        NavigationBarItemData(
            GradeBarItems.COUNT.name,"统计", painterResource(R.drawable.leaderboard),
            painterResource(R.drawable.leaderboard_filled)
        )
    )
    with(sharedTransitionScope) {
        TransitionScaffold (
            route = targetRoute,
            animatedContentScope = animatedContentScope,
            navHostController = navTopController,
            modifier = containerShare(Modifier.fillMaxSize(), animatedContentScope, targetRoute, resize = false),
            topBar = {
                Column {
                    TopAppBar(
                        modifier = Modifier.topBarBlur(hazeState),
                        colors = topAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary
                        ),
                        title = { Text("成绩") },
                        navigationIcon = {
                            TopBarTopIcon(navTopController,animatedContentScope,targetRoute,R.drawable.article)
                        },
                        actions = {
                            Row {
                                if(!ifSaved) {
                                    IconButton(onClick = {
                                        showSearch = !showSearch
                                    }) {
                                        Icon(painter = painterResource(id = R.drawable.search), contentDescription = "")
                                    }
                                }
                                IconButton(onClick = {
                                    showBottomSheet = true
                                }) {
                                    Icon(painter = painterResource(id = R.drawable.info), contentDescription = "")
                                }
                            }
                        }
                    )
                }
            },
            bottomBar = {
                Column {
                    NavigationBar(containerColor = Color.Transparent ,
                        modifier = Modifier.bottomBarBlur(hazeState,)
                    ) {
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
                                    if(currentAnimationIndex == 2) {
                                        when(item) {
                                            items[0] -> targetPage = GradeBarItems.GRADE
                                            items[1] -> targetPage = GradeBarItems.COUNT
                                        }
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
            val animation = AppAnimationManager.getAnimationType(currentAnimationIndex,targetPage.page)

            NavHost(navController = navController,
                startDestination = GradeBarItems.GRADE.name,
                enterTransition = { animation.enter },
                exitTransition = { animation.exit },
                modifier = Modifier.hazeSource(state = hazeState)
            ) {
                composable(GradeBarItems.GRADE.name) {
                    Scaffold(
                    ) {
                        if (ifSaved) GradeItemUI(vm,innerPadding)
                        else GradeItemUIJXGLSTU(innerPadding,vm,showSearch,hazeState)
                    }
                }
                composable(GradeBarItems.COUNT.name) {
                    Scaffold(
                    ) {
                        GradeCountUI(vm,innerPadding)
                    }
                }
            }
        }
    }

}

@Composable
fun Infos() {
    DividerTextExpandedWith("绩点与分数对应关系") {
        GPAWithScore()
    }
    StyleCardListItem(
        headlineContent = { Text(text = "平均绩点的计算") },
        supportingContent = { Text(text = "每门课的(学分*绩点)累加后，除以所有课的总学分")}
    )
    StyleCardListItem(
        headlineContent = { Text(text = "校务行") },
        supportingContent = { Text(text = "微信小程序搜校务行，注意宣区选择 合肥工业大学（宣城校区），学号为账号，身份证后六位为密码（包括最后的X）")}
    )
}