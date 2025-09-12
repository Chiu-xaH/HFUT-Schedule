package com.hfut.schedule.ui.screen.grade

import android.annotation.SuppressLint
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.grade.analysis.AnalysisScreen
import com.hfut.schedule.ui.screen.grade.grade.community.GradeItemUI
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.GPAWithScore
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.GradeItemUIJXGLSTU
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.ui.component.button.HazeBottomBar
import com.xah.uicommon.style.padding.NavigationBarSpacer
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.style.special.bottomBarBlur
import com.hfut.schedule.ui.style.special.topBarBlur
import com.xah.uicommon.style.color.topBarTransplantColor
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.ui.util.AppAnimationManager.currentPage
import com.hfut.schedule.ui.util.navigateForBottomBar
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.xah.transition.util.currentRouteWithoutArgs
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
private val items = listOf(
    NavigationBarItemData(
        GradeBarItems.GRADE.name,"学期", R.drawable.article, R.drawable.article_filled
    ),
    NavigationBarItemData(
        GradeBarItems.COUNT.name,"计算", R.drawable.leaderboard,R.drawable.leaderboard_filled
    )
)
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
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val hazeState = rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code)
    val navController = rememberNavController()

    var showSearch by remember { mutableStateOf(false) }

    var showBottomSheet by remember { mutableStateOf(false) }

    val currentAnimationIndex by DataStoreManager.animationType.collectAsState(initial = 0)
    val targetPage = when(navController.currentRouteWithoutArgs()) {
        GradeBarItems.GRADE.name ->GradeBarItems.GRADE
        GradeBarItems.COUNT.name -> GradeBarItems.COUNT
        else -> GradeBarItems.GRADE
    }
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

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    with(sharedTransitionScope) {
        CustomTransitionScaffold (
            route = targetRoute,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            animatedContentScope = animatedContentScope,
            navHostController = navTopController,
            topBar = {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.topBarBlur(hazeState),
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.Grade.label) },
                    navigationIcon = {
                        TopBarNavigationIcon(navTopController,animatedContentScope,targetRoute,AppNavRoute.Grade.icon)
                    },
                    actions = {
                        Row {
                            if(!ifSaved) {
                                IconButton(onClick = {
                                    showSearch = !showSearch
                                }) {
                                    Icon(painter = painterResource(id = R.drawable.search), contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                            IconButton(onClick = {
                                showBottomSheet = true
                            }) {
                                Icon(painter = painterResource(id = R.drawable.info), contentDescription = "",tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                )
            },
            bottomBar = {
                HazeBottomBar(hazeState,items,navController)
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
                        AnalysisScreen(vm,innerPadding)
//                        GradeCountUI(vm,innerPadding)
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
    CardListItem(
        headlineContent = { Text(text = "平均绩点的计算") },
        supportingContent = { Text(text = "每门课的(学分*绩点)累加后，除以所有课的总学分")}
    )
    CardListItem(
        headlineContent = { Text(text = "校务行") },
        supportingContent = { Text(text = "微信小程序搜校务行，注意宣区选择 合肥工业大学（宣城校区），学号为账号，身份证后六位为密码（包括最后的X）")}
    )
}