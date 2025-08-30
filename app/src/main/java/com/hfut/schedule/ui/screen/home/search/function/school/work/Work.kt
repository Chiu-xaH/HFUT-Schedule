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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.util.fastMap
import androidx.navigation.NavHostController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.WorkSearchType
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.sys.Starter
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.AnimationCardListItem
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.mixedCardNormalColor
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.pager.CustomTabRow
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.screen.pager.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.pager.PagingController
   
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.enumeration.getCampusRegion
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.ui.style.special.topBarBlur
import com.xah.uicommon.style.color.topBarTransplantColor
import com.hfut.schedule.ui.util.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.xah.transition.component.iconElementShare
import com.xah.uicommon.component.text.ScrollText
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
        headlineContent = { ScrollText(text = AppNavRoute.Work.label) },
        leadingContent = {
            Icon(painterResource(AppNavRoute.Work.icon), contentDescription = null,modifier = Modifier.iconElementShare(sharedTransitionScope,animatedContentScope = animatedContentScope, route = route))
        },
        modifier = Modifier.clickable {
            navController.navigateForTransition(AppNavRoute.Work,route)
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
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val hazeState = rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code)
    val route = remember { AppNavRoute.Work.route }

    var campus by rememberSaveable { mutableStateOf(getCampusRegion()) }

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
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    with(sharedTransitionScope) {
        CustomTransitionScaffold (
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                Column (
                    modifier = Modifier.topBarBlur(hazeState, ),
                ) {
                    MediumTopAppBar(
                        scrollBehavior = scrollBehavior,
                        colors = topBarTransplantColor(),
                        title = { Text(AppNavRoute.Work.label) },
                        navigationIcon = {
                            TopBarNavigationIcon(navController,animatedContentScope,route, AppNavRoute.Work.icon)
                        },
                        actions = {
                            Row(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
                                val url = when(campus) {
                                    CampusRegion.HEFEI -> MyApplication.WORK_URL
                                    CampusRegion.XUANCHENG -> MyApplication.WORK_XC_URL
                                }
                                val iconRoute =  AppNavRoute.WebView.shareRoute(url)
                                FilledTonalIconButton(
                                    onClick = {
                                        navController.navigateForTransition(
                                            AppNavRoute.WebView,
                                            AppNavRoute.WebView.withArgs(
                                            url = url,
                                            title = "就业网(${campus.description})",
                                            icon = R.drawable.net,
                                        ),transplantBackground = true)
                                    },
                                ) {
                                    Icon(painterResource(R.drawable.net), contentDescription = null,modifier = Modifier.iconElementShare(sharedTransitionScope,animatedContentScope = animatedContentScope, route = iconRoute))
                                }
                                FilledTonalButton(
                                    onClick = {
                                        campus = when(campus) {
                                            CampusRegion.HEFEI -> CampusRegion.XUANCHENG
                                            CampusRegion.XUANCHENG -> CampusRegion.HEFEI
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
                WorkSearchUI(vm,campus,pagerState,innerPadding,navController,sharedTransitionScope,animatedContentScope)
            }
        }
    }
}
// 模范写法
@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun WorkSearchUI(
    vm : NetWorkViewModel,
    campus: CampusRegion,
    pagerState : PagerState,
    innerPadding : PaddingValues,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    var input by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    HorizontalPager(state = pagerState) { page ->
        val uiState by vm.workSearchResult.state.collectAsState()
        var currentPage by rememberSaveable { mutableIntStateOf(1) }
        val refreshNetwork: suspend () -> Unit =  {
            vm.workSearchResult.clear()
            vm.searchWorks(keyword = input.let { if(it.isBlank() || it.isEmpty()) null else it }, page = currentPage, type = page,campus)
        }

        LaunchedEffect(currentPage,campus) {
            // 避免旧数据影响
            refreshNetwork()
        }

        CommonNetworkScreen(uiState, onReload = { refreshNetwork() }) {
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
                            with(sharedTransitionScope) {
                                val url = when(campus) {
                                    CampusRegion.HEFEI -> MyApplication.WORK_URL
                                    CampusRegion.XUANCHENG -> MyApplication.WORK_XC_URL
                                } + "detail/" + enumType.url +  id
                                AnimationCardListItem(
                                    color = mixedCardNormalColor(),
//                                    cardModifier = containerShare(animatedContentScope=animatedContentScope, route = AppNavRoute.WebView.shareRoute(url)),
                                    headlineContent = { Text(title) },
                                    overlineContent = { Text(time + if(page == 0) " " + enumType.description else "") },
                                    index = index,
                                    modifier = Modifier.clickable {
//                                        navController.navigateForTransition(AppNavRoute.WebView,AppNavRoute.WebView.withArgs(url,title,null,AppNavRoute.Work.icon))
                                    Starter.startWebView(url,title, icon = AppNavRoute.Work.icon)
                                    },
                                    leadingContent = { Text((index+1).toString()) }
                                )
                            }
                        }
                    }
                    item { PaddingForPageControllerButton() }
                    item { InnerPaddingHeight(innerPadding,false) }
                }
                PagingController(listState,currentPage,nextPage = { currentPage = it }, previousPage = { currentPage = it })
            }
        }
    }
}