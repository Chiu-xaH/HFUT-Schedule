package com.hfut.schedule.ui.screen.home.search.function.school.work

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.util.fastMap
import androidx.navigation.NavHostController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.WorkSearchType
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.AnimationCardListItem
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.CustomTabRow
import com.hfut.schedule.ui.component.screen.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.PagingController
import com.hfut.schedule.ui.component.webview.WebDialog
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Campus
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.getCampus
import com.hfut.schedule.ui.style.InnerPaddingHeight
import com.hfut.schedule.ui.style.topBarBlur
import com.hfut.schedule.ui.style.topBarTransplantColor
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.component.TopBarNavigateIcon
import com.xah.transition.component.TransitionScaffold
import com.xah.transition.component.containerShare
import com.xah.transition.component.iconElementShare
import com.xah.transition.util.navigateAndSaveForTransition
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Work(
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val route = remember { AppNavRoute.Work.route }

    TransplantListItem(
        headlineContent = { Text(text = AppNavRoute.Work.title) },
        leadingContent = {
            with(sharedTransitionScope) {
                Icon(painterResource(AppNavRoute.Work.icon), contentDescription = null,modifier = iconElementShare(animatedContentScope = animatedContentScope, route = route))
            }
        },
        modifier = Modifier.clickable {
            navController.navigateAndSaveForTransition(route)
        }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WorkScreen(
    vm: NetWorkViewModel,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.Work.route }

    var campus by rememberSaveable { mutableStateOf(getCampus()) }

    val types = remember { listOf(
        WorkSearchType.ALL,
        WorkSearchType.JOB_FAIR,
        WorkSearchType.JOB_FAIR_COMPANY,
        WorkSearchType.PRESENTATION,
        WorkSearchType.ONLINE_RECRUITMENT,
        WorkSearchType.POSITION,
        WorkSearchType.ANNOUNCEMENT
    ) }
    val pagerState = rememberPagerState(pageCount = { types.size })

    with(sharedTransitionScope) {
        TransitionScaffold (
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            topBar = {
                Column (
                    modifier = Modifier.topBarBlur(hazeState,useTry = true),
                ) {
                    TopAppBar(
                        colors = topBarTransplantColor(),
                        title = { Text(AppNavRoute.Work.title) },
                        navigationIcon = {
                            TopBarNavigateIcon(navController,animatedContentScope,route, AppNavRoute.Work.icon)
                        },
                        actions = {
                            Row(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
                                val url = when(campus) {
                                    Campus.HEFEI -> MyApplication.WORK_URL
                                    Campus.XUANCHENG -> MyApplication.WORK_XC_URL
                                }
                                val iconRoute =  AppNavRoute.WebView.shareRoute(url)
                                with(sharedTransitionScope) {
                                    FilledTonalIconButton(
                                        onClick = {
                                            navController.navigateAndSaveForTransition(AppNavRoute.WebView.withArgs(
                                                url = url,
                                                title = "就业网(${campus.description})",
                                                icon = R.drawable.net,
                                            ),transplantBackground = true)
                                        },
//                                        modifier = containerShare(animatedContentScope = animatedContentScope, route = iconRoute)
                                    ) {
                                        Icon(painterResource(R.drawable.net), contentDescription = null,modifier = iconElementShare(animatedContentScope = animatedContentScope, route = iconRoute))
                                    }
                                }

                                FilledTonalButton(
                                    onClick = {
                                        campus = when(campus) {
                                            Campus.HEFEI -> Campus.XUANCHENG
                                            Campus.XUANCHENG -> Campus.HEFEI
                                        }
                                    }
                                ) {
                                    Text(campus.description)
                                }
                            }
                        }
                    )
                    CustomTabRow(pagerState,types.fastMap { it.description })
                }
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.hazeSource(hazeState).fillMaxSize()
            ) {
                WorkSearchUI(vm,campus,pagerState,innerPadding)
            }
        }
    }
}
// 模范写法
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun WorkSearchUI(vm : NetWorkViewModel,campus: Campus,pagerState : PagerState,innerPadding : PaddingValues) {
    var showDialog by remember { mutableStateOf(false) }
    var url by remember { mutableStateOf("") }
    var webTitle by remember { mutableStateOf("详情") }

    var input by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    WebDialog(showDialog,{ showDialog = false }, url,webTitle, showTop = false)

    HorizontalPager(state = pagerState) { page ->
        val uiState by vm.workSearchResult.state.collectAsState()
        var currentPage by remember { mutableIntStateOf(1) }
        val refreshNetwork: suspend () -> Unit = {
            vm.workSearchResult.clear()
            vm.searchWorks(keyword = input.let { if(it.isBlank() || it.isEmpty()) null else it }, page = currentPage, type = page,campus)
        }


        LaunchedEffect(currentPage,campus) {
            // 避免旧数据影响
            refreshNetwork()
        }

        CommonNetworkScreen(uiState, onReload = refreshNetwork) {
            val response = (uiState as UiState.Success).data
            val repos = response.data
            val listState = rememberLazyListState()
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(state = listState) {
                    item { InnerPaddingHeight(innerPadding,true) }
                    item {
                        CustomTextField(
                            input = input,
                            label = { Text("搜索") },
                            trailingIcon = { IconButton(
                                onClick = { scope.launch { refreshNetwork() } }
                            ) { Icon(painterResource(R.drawable.search),null) } },
                            leadingIcon = if(!(input.isEmpty() || input.isBlank())) {
                                {
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                input = ""
                                                refreshNetwork()
                                            }
                                        }
                                    ) { Icon(painterResource(R.drawable.close),null) }
                                }
                            } else null
                        ) { input = it }
                        Spacer(Modifier.height(CARD_NORMAL_DP))
                    }
                    items(repos.size, key = { it }) { index ->
                        with(repos[index]) {
                            val enumType = when(type) {
                                WorkSearchType.JOB_FAIR.code.toString() -> WorkSearchType.JOB_FAIR
                                WorkSearchType.JOB_FAIR_COMPANY.code.toString() -> WorkSearchType.JOB_FAIR_COMPANY
                                WorkSearchType.PRESENTATION.code.toString() -> WorkSearchType.PRESENTATION
                                WorkSearchType.ONLINE_RECRUITMENT.code.toString() -> WorkSearchType.ONLINE_RECRUITMENT
                                WorkSearchType.POSITION.code.toString() -> WorkSearchType.POSITION
                                WorkSearchType.ANNOUNCEMENT.code.toString() -> WorkSearchType.ANNOUNCEMENT
                                else -> WorkSearchType.ALL
                            }
                            AnimationCardListItem(
                                headlineContent = { Text(title) },
                                overlineContent = { Text(time + if(page == 0) " " + enumType.description else "") },
                                index = index,
                                modifier = Modifier.clickable {
                                    url = when(campus) {
                                        Campus.HEFEI -> MyApplication.WORK_URL
                                        Campus.XUANCHENG -> MyApplication.WORK_XC_URL
                                    } + "detail/" + enumType.url +  id
                                    webTitle = title
                                    showDialog = true
                                },
                                leadingContent = { Text((index+1).toString()) }
                            )
                        }
                    }
                    item { PaddingForPageControllerButton() }
                    item { InnerPaddingHeight(innerPadding,false) }
                }
                PagingController(listState,currentPage,showUp = true, nextPage = { currentPage = it }, previousPage = { currentPage = it })
            }
        }
    }
}