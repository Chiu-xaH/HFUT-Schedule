package com.hfut.schedule.ui.screen.grade

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.GradeBarItems
import com.hfut.schedule.logic.model.NavigationBarItemData
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.button.BUTTON_PADDING
import com.hfut.schedule.ui.component.button.HazeBottomBar
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.screen.pager.CustomTabRow
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.grade.analysis.AnalysisScreen
import com.hfut.schedule.ui.screen.grade.grade.community.GradeItemUI
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.GPAWithScore
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.GradeItemJxglstuUI
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.GradeItemUIUniApp
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.grade.goToXwx
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.containerBackDrop
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.ui.util.navigation.AppAnimationManager.currentPage
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.network.XwxViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.transition.util.currentRouteWithoutArgs
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.color.topBarTransplantColor
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

private val items = listOf(
    NavigationBarItemData(
        GradeBarItems.GRADE.name,"成绩", R.drawable.article, R.drawable.article_filled
    ),
//    NavigationBarItemData(
//        GradeBarItems.COMMUNITY.name,"社区源", R.drawable.article, R.drawable.article_filled
//    ),
//    NavigationBarItemData(
//        GradeBarItems.UNI_APP.name,"合工大教务源", R.drawable.article, R.drawable.article_filled
//    ),
    NavigationBarItemData(
        GradeBarItems.COUNT.name,"计算", R.drawable.leaderboard,R.drawable.leaderboard_filled
    )
)

enum class GradeDataOrigin(val title : String) {
    UNI_APP("合工大教务"),
    JXGLSTU("教务系统"),
    COMMUNITY("智慧社区"),
}

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
//        GradeBarItems.COMMUNITY.name -> GradeBarItems.COMMUNITY
//        GradeBarItems.UNI_APP.name -> GradeBarItems.UNI_APP
        else -> GradeBarItems.GRADE
    }
    // 保存上一页页码 用于决定左右动画
    if(currentAnimationIndex == 2) {
        LaunchedEffect(targetPage) {
            currentPage = targetPage.page
        }
    }
    val gradeOriginList = remember { GradeDataOrigin.entries }
    val pageState = rememberPagerState(initialPage = if(ifSaved) 0 else 1 ) { gradeOriginList.size }

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
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }
    val viewModel = viewModel { XwxViewModel() }
    var input by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val backDrop = rememberLayerBackdrop()
    var displayCompactly by rememberSaveable { mutableStateOf(false) }
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
                            if(targetPage != GradeBarItems.COUNT && gradeOriginList[pageState.currentPage] != GradeDataOrigin.COMMUNITY) {
                                Spacer(Modifier.width(BUTTON_PADDING))
                                LiquidButton(
                                    onClick = {
                                        displayCompactly = !displayCompactly
                                    } ,
                                    isCircle = true,
                                    backdrop = backDrop
                                ) {
                                    Icon(
                                        painterResource(
                                            if(!displayCompactly) R.drawable.horizontal_split
                                            else R.drawable.reorder
                                        ),
                                        null
                                    )
                                }
                            }
                            Spacer(Modifier.width(BUTTON_PADDING))
                            LiquidButton(
                                onClick = {
                                    scope.launch {
                                        loading = true
                                        goToXwx(viewModel,context)
                                        loading = false
                                    }
                                } ,
                                backdrop = backDrop
                            ) {
                               Text("校务行")
                            }
                        }
                    }
                )
                if(targetPage != GradeBarItems.COUNT) {
                    CustomTabRow(
                        pageState,
                        gradeOriginList.map { it.title }
                    )
                    if(gradeOriginList[pageState.currentPage] != GradeDataOrigin.COMMUNITY) {
                        CustomTextField(
                            modifier = Modifier
                                .padding(top = CARD_NORMAL_DP)
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
                Scaffold() {
                    HorizontalPager(
                        pageState
                    ) { page ->
                        val currentPage = gradeOriginList[page]
                        when(currentPage) {
                            GradeDataOrigin.JXGLSTU -> GradeItemJxglstuUI(navTopController,innerPadding,vm,input,hazeState,ifSaved,displayCompactly)
                            GradeDataOrigin.UNI_APP -> GradeItemUIUniApp(navTopController,innerPadding,vm,input,displayCompactly)
                            GradeDataOrigin.COMMUNITY -> GradeItemUI(vm,innerPadding)
                        }
                    }
                }
            }
            composable(GradeBarItems.COUNT.name) {
                Scaffold(
                ) {
                    AnalysisScreen(vm,innerPadding)
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
}

