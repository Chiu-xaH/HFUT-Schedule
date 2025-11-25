package com.hfut.schedule.ui.screen.grade

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.glance.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.GradeBarItems
import com.hfut.schedule.logic.model.NavigationBarItemData
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.button.BUTTON_PADDING
import com.hfut.schedule.ui.component.button.HazeBottomBar
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.grade.analysis.AnalysisScreen
import com.hfut.schedule.ui.screen.grade.grade.community.GradeItemUI
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.GPAWithScore
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.GradeItemUIJXGLSTU
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.containerBackDrop
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.ui.util.navigation.AppAnimationManager.currentPage
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.transition.util.currentRouteWithoutArgs
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.color.topBarTransplantColor
import com.xah.uicommon.style.padding.InnerPaddingHeight
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

private val items = listOf(
    NavigationBarItemData(
        GradeBarItems.GRADE.name,"教务源", R.drawable.article, R.drawable.article_filled
    ),
    NavigationBarItemData(
        GradeBarItems.COMMUNITY.name,"社区源", R.drawable.article, R.drawable.article_filled
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
) {
    val targetRoute = remember { AppNavRoute.Grade.receiveRoute() }
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val navController = rememberNavController()

    var showBottomSheet by remember { mutableStateOf(false) }

    val currentAnimationIndex by DataStoreManager.animationType.collectAsState(initial = 0)
    val targetPage = when(navController.currentRouteWithoutArgs()) {
        GradeBarItems.GRADE.name ->GradeBarItems.GRADE
        GradeBarItems.COUNT.name -> GradeBarItems.COUNT
        GradeBarItems.COMMUNITY.name -> GradeBarItems.COMMUNITY
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
    var input by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val backDrop = rememberLayerBackdrop()
    CustomTransitionScaffold (
        route = targetRoute,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        navHostController = navTopController,
        topBar = {
            Column(
                modifier = Modifier.topBarBlur(hazeState),
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.Grade.label) },
                    navigationIcon = {
                        TopBarNavigationIcon(navTopController,targetRoute,AppNavRoute.Grade.icon)
                    },
                    actions = {
                        Row(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
                            LiquidButton(
                                onClick = { showBottomSheet = true } ,
                                isCircle = true,
                                backdrop = backDrop
                            ) {
                                Icon(painter = painterResource(id = R.drawable.info),null)
                            }
                            Spacer(Modifier.width(BUTTON_PADDING))
                            LiquidButton(
                                onClick = {
                                    if(true) {
                                        Starter.loginXwx(context)
                                    } else {
                                        Starter.startXwx(context)
                                    }
                                } ,
                                backdrop = backDrop
                            ) {
                               Text("校务行")
                            }
                        }
                    }
                )
                if(targetPage == GradeBarItems.GRADE) {
                    CustomTextField(
                        modifier = Modifier
                            .padding(horizontal = APP_HORIZONTAL_DP)
                            .containerBackDrop(backDrop, MaterialTheme.shapes.medium),
                        input = input,
                        label = { Text("搜索 课程名、代码") },
                        trailingIcon = {
                            IconButton(
                                onClick = {}) {
                                Icon(
                                    painter = painterResource(R.drawable.search),
                                    contentDescription = "description"
                                )
                            }
                        },
                    ) { input = it }
                }
            }
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
            modifier = Modifier
                .backDropSource(backDrop)
                .hazeSource(state = hazeState)
        ) {
            composable(GradeBarItems.GRADE.name) {
                Scaffold(
                ) {
                    GradeItemUIJXGLSTU(innerPadding,vm,input,hazeState,ifSaved)
                }
            }
            composable(GradeBarItems.COUNT.name) {
                Scaffold(
                ) {
                    AnalysisScreen(vm,innerPadding)
                }
            }
            composable(GradeBarItems.COMMUNITY.name) {
                Scaffold(
                ) {
                    GradeItemUI(vm,innerPadding)
                }
            }
        }
    }
//    }

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
}

