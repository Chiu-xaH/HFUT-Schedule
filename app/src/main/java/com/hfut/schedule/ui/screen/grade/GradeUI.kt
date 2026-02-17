package com.hfut.schedule.ui.screen.grade

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.button.BUTTON_PADDING
import com.hfut.schedule.ui.component.button.LargeButton
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.button.containerBackDrop
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.screen.pager.CustomTabRow
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.grade.grade.community.GradeItemUI
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.GPAWithScore
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.GradeItemJxglstuUI
import com.hfut.schedule.ui.screen.grade.grade.jxglstu.GradeItemUIUniApp
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.grade.goToXwx
import com.hfut.schedule.ui.style.color.textFiledAllTransplant
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.bottomBarBlur
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.network.XwxViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.transition.component.iconElementShare
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.color.topBarTransplantColor
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

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
    var showBottomSheet by remember { mutableStateOf(false) }
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

    var isNavigationIconVisible by rememberSaveable { mutableStateOf(true) }
    // 监听滚动状态
    LaunchedEffect(scrollBehavior.state) {
        snapshotFlow { scrollBehavior.state.collapsedFraction }
            .collect { collapsedFraction -> isNavigationIconVisible = collapsedFraction < 0.5f }
    }
    var buttonText by rememberSaveable { mutableStateOf<String>(context.getString(AppNavRoute.AverageGrade.label)) }

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
                    title = { Text(stringResource(AppNavRoute.Grade.label)) },
                    navigationIcon = {
                        TopBarNavigationIcon(targetRoute,AppNavRoute.Grade.icon)
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
                            if(gradeOriginList[pageState.currentPage] != GradeDataOrigin.COMMUNITY) {
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
                CustomTabRow(
                    pageState,
                    gradeOriginList.map { it.title }
                )
                if(gradeOriginList[pageState.currentPage] != GradeDataOrigin.COMMUNITY) {
                    CustomTextField(
                        colors = textFiledAllTransplant(),
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
        },
        bottomBar = {
            val display = when(gradeOriginList[pageState.currentPage]) {
                GradeDataOrigin.JXGLSTU -> {
                    true
                }
                GradeDataOrigin.COMMUNITY -> {
                    false
                }
                GradeDataOrigin.UNI_APP -> {
                    // 等待加载完毕
                    val uiState by vm.uniAppGradesResp.state.collectAsState()
                    uiState is UiState.Success<*>
                }
            }
            val display2 = if(displayCompactly) {
                true
            } else {
                isNavigationIconVisible
            }
            AnimatedVisibility(
                visible = display2 && display,
                exit = AppAnimationManager.toBottomAnimation.exit,
                enter = AppAnimationManager.toBottomAnimation.enter
            ) {
                val route = AppNavRoute.AverageGrade.route
                Column (modifier = Modifier.bottomBarBlur(hazeState).navigationBarsPadding()) {
                    LargeButton(
                        iconModifier = Modifier.iconElementShare(route),
                        onClick = {
                            navTopController.navigateForTransition(AppNavRoute.AverageGrade,AppNavRoute.AverageGrade.withArgs(
                                when(gradeOriginList[pageState.currentPage]) {
                                    GradeDataOrigin.JXGLSTU -> {
                                        false
                                    }
                                    GradeDataOrigin.COMMUNITY -> {
                                        false
                                    }
                                    GradeDataOrigin.UNI_APP -> {
                                        true
                                    }
                                }
                            ))
                        },
                        icon = AppNavRoute.AverageGrade.icon,
                        text = buttonText,
                        shape = MaterialTheme.shapes.large,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(APP_HORIZONTAL_DP)
                                .containerBackDrop(backDrop,MaterialTheme.shapes.large),
//                                .containerShare(
//                                    AppNavRoute.AverageGrade.receiveRoute(),
//                                    roundShape = MaterialTheme.shapes.large,
//                                ),
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    ) { innerPadding ->
        Scaffold(
            modifier = Modifier
                .backDropSource(backDrop)
                .hazeSource(state = hazeState)
        ) {
            HorizontalPager(
                pageState
            ) { page ->
                val currentPage = gradeOriginList[page]
                when(currentPage) {
                    GradeDataOrigin.JXGLSTU -> GradeItemJxglstuUI(navTopController,innerPadding,vm,input,hazeState,ifSaved,displayCompactly) { buttonText = it }
                    GradeDataOrigin.UNI_APP -> GradeItemUIUniApp(navTopController,innerPadding,vm,input,displayCompactly) { buttonText = it }
                    GradeDataOrigin.COMMUNITY -> GradeItemUI(vm,innerPadding)
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
        headlineContent = { Text(text = "平均成绩的计算") },
        supportingContent = { Text(text = "平均绩点：每门课的学分*绩点累加，再除以所有课的总学分\n平均分数：每门课的学分*分数累加，再除以所有课的总学分")}
    )
}

