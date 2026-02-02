package com.hfut.schedule.ui.screen.home.search.function.jxglstu.survey

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.enumeration.PostMode
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.logic.util.sys.showToast
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.icon.LoadingIcon
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.ui.component.button.BUTTON_PADDING
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.style.special.topBarBlur
import com.xah.uicommon.style.color.topBarTransplantColor
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.screen.pager.CustomTabRow
import com.hfut.schedule.ui.component.status.DevelopingIcon
import com.hfut.schedule.ui.style.special.backDropSource
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.transition.component.iconElementShare
import com.xah.transition.state.LocalAnimatedContentScope
import com.xah.transition.state.LocalSharedTransitionScope
import com.xah.uicommon.component.text.ScrollText
import com.xah.uicommon.style.align.CenterScreen
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun Survey(
    ifSaved : Boolean,
    navController : NavHostController,
){
    val route = remember { AppNavRoute.Survey.route }
    val context = LocalContext.current

    TransplantListItem(
        headlineContent = { ScrollText(text = stringResource(AppNavRoute.Survey.label))},
        leadingContent = {
            Icon(painterResource(AppNavRoute.Survey.icon), contentDescription = null,modifier = Modifier.iconElementShare(route = route))
        },
        modifier = Modifier.clickable {
            if(ifSaved) refreshLogin(context) else {
                navController.navigateForTransition(AppNavRoute.Survey, AppNavRoute.Survey.withArgs(ifSaved))
            }
        }
    )
}

private const val PAGE_UNI_APP = 0
private const val PAGE_JXGLSTU = 1

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SurveyScreen(
    ifSaved: Boolean,
    vm: NetWorkViewModel,
    navController : NavHostController,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.Survey.route }
    var refresh by rememberSaveable { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val backDrop = rememberLayerBackdrop()
    val pagerState = rememberPagerState(initialPage = if(ifSaved) PAGE_UNI_APP else PAGE_JXGLSTU) { 2 }

    CustomTransitionScaffold (
        route = route,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        navHostController = navController,
        topBar = {
            Column(
                modifier = Modifier.topBarBlur(hazeState),
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(stringResource(AppNavRoute.Survey.label)) },
                    navigationIcon = {
                        TopBarNavigationIcon(route, AppNavRoute.Survey.icon)
                    },
                    actions = {
                        Box(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
                            SurveyAllButton(vm,backDrop) {
                                refresh = !refresh
                            }
                        }
                    }
                )
//                CustomTabRow(pagerState,listOf("合工大教务","教务系统"))
            }
        },
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .backDropSource(backDrop)
                .hazeSource(hazeState)
                .fillMaxSize()
        ) {
//            HorizontalPager(pagerState) { page ->
//                when(page) {
//                    PAGE_JXGLSTU -> {
                        SurveyUI(vm,hazeState,refresh,innerPadding)
//                    }
//                    PAGE_UNI_APP -> {
//                        CenterScreen {
//                            DevelopingIcon()
//                        }
//                    }
//                }
//            }
        }
    }
}

@Composable
private fun SurveyAllButton(
    vm: NetWorkViewModel,
    backDrop : Backdrop,
    refresh : suspend () -> Unit
) {
    val surveyListData by vm.surveyListData.state.collectAsState()
    val scope = rememberCoroutineScope()
    val refreshOne: suspend (Int) -> Unit = { id : Int ->
        val cookie = getJxglstuCookie()
        cookie?.let {
            vm.surveyData.clear()
            vm.getSurveyToken(it,id.toString())
            vm.getSurvey(it,id.toString())
        }
    }
    var successfulNum by remember { mutableIntStateOf(0) }
    var failedNum by remember { mutableIntStateOf(0) }
    var currentTeacherTask by remember { mutableStateOf("") }
    var currentNumTask by remember { mutableIntStateOf(0) }
    var totalNum by remember { mutableIntStateOf(0) }
    var loading by remember { mutableStateOf(false) }
    Row {
        if(loading) {
            LiquidButton (
                onClick = {},
                backdrop = backDrop,
                enabled = false
            ) {
                Text("$currentTeacherTask $currentNumTask/$totalNum")
            }
            Spacer(Modifier.width(BUTTON_PADDING))
        }
        LiquidButton (
            onClick = {
                // 未评教的教师们
                scope.launch(Dispatchers.IO) {
                    val list = (surveyListData as UiState.Success).data.flatMap { it.lessonSurveyTasks }.filter { it.submitted == false }
                    if(list.isEmpty()) {
                        showToast("无未完成的评教")
                        refresh()
                        return@launch
                    }
                    totalNum = list.size
                    loading = true
                    for(task in list) {
                        val teacherName = task.teacher.person?.nameZh
                        currentTeacherTask = teacherName ?: ""
                        // 跳过评教过的
                        if(task.submitted) {
                            currentNumTask++
                            continue
                        }
                        // 获取下一个教师
                        refreshOne(task.id)
                        val bean = vm.surveyData.state.first()
                        if(bean !is UiState.Success) {
                            showToast("逻辑出错(可能是教务系统反爬机制)")
                            failedNum++
                            continue
                        }
                        // 发送教评
                        val result = postSurvey(vm, PostMode.HIGH,bean.data,"好",task.id,teacherName)
                        if(result) {
                            successfulNum++
                        } else {
                            failedNum++
                        }
                        currentNumTask++
                    }
                    loading = false
                    showToast("评教完成: 成功${successfulNum} 失败${failedNum}")
                    successfulNum = 0
                    failedNum = 0
                    currentTeacherTask = ""
                    currentNumTask = 0
                    totalNum = 0
                    refresh()
                }
            },
            isCircle = loading,
            backdrop = backDrop,
            enabled = surveyListData is UiState.Success && !loading
        ) {
            if(loading) {
                LoadingIcon()
            } else {
                Text("全部评教(100分)")
            }
        }
    }
}

