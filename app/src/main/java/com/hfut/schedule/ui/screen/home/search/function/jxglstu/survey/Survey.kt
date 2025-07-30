package com.hfut.schedule.ui.screen.home.search.function.jxglstu.survey

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.enumeration.PostMode
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.icon.LoadingIcon
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.style.topBarBlur
import com.hfut.schedule.ui.style.topBarTransplantColor
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.component.TopBarNavigateIcon
import com.xah.transition.component.TransitionScaffold
import com.xah.transition.component.iconElementShare
import com.xah.transition.util.navigateAndSaveForTransition
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun Survey(
    ifSaved : Boolean,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
){
    val route = remember { AppNavRoute.Survey.route }

    TransplantListItem(
        headlineContent = { Text(text = AppNavRoute.Survey.title)},
        leadingContent = {
            with(sharedTransitionScope) {
                Icon(painterResource(AppNavRoute.Survey.icon), contentDescription = null,modifier = iconElementShare(animatedContentScope = animatedContentScope, route = route))
            }
        },
        modifier = Modifier.clickable {
            if(ifSaved) refreshLogin() else {
                navController.navigateAndSaveForTransition(route)
            }
        }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SurveyScreen(
    vm: NetWorkViewModel,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.Survey.route }
    var refresh by rememberSaveable { mutableStateOf(false) }

    with(sharedTransitionScope) {
        TransitionScaffold (
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            topBar = {
                TopAppBar(
                    modifier = Modifier.topBarBlur(hazeState,useTry = true),
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.Survey.title) },
                    navigationIcon = {
                        TopBarNavigateIcon(navController,animatedContentScope,route, AppNavRoute.Survey.icon)
                    },
                    actions = {
                        Box(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
                            SurveyAllButton(vm) {
                                refresh = !refresh
                            }
                        }
                    }
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.hazeSource(hazeState).fillMaxSize()
            ) {
                SurveyUI(vm,hazeState,refresh,innerPadding)
            }
        }
    }
}

@Composable
private fun SurveyAllButton(vm: NetWorkViewModel,refresh : suspend () -> Unit) {
    val surveyListData by vm.surveyListData.state.collectAsState()
    val surveyData by vm.surveyData.state.collectAsState()
    val scope = rememberCoroutineScope()
    val refreshOne: suspend (Int) -> Unit = { id : Int ->
        val cookie = getJxglstuCookie(vm)
        cookie?.let {
            vm.surveyData.clear()
            vm.getSurveyToken(it,id.toString())
            vm.getSurvey(it,id.toString())
        }
    }
    var loading by remember { mutableStateOf(false) }
    FilledTonalButton(
        onClick = {
            // 未评教的教师们
            scope.launch(Dispatchers.IO) {
                val list = (surveyListData as UiState.Success).data.flatMap { it.lessonSurveyTasks }.filter { it.submitted == false }
                if(list.isEmpty()) {
                    showToast("无未完成的评教")
                    refresh()
                    return@launch
                }
                loading = true
                for(task in list) {
                    // 获取下一个教师
                    refreshOne(task.id)
                    val bean = (surveyData as? UiState.Success)?.data
                    if(bean == null) {
                        showToast("失败")
                        loading = false
                        break
                    }
                    // 发送教评
                    postSurvey(vm, PostMode.GOOD,bean)
                }
                loading = false
                refresh()
            }
        },
        enabled = surveyListData is UiState.Success && !loading
    ) {
        if(loading) {
            LoadingIcon()
        } else {
            Text("全部评教(100分)")
        }
    }
}

