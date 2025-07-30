package com.hfut.schedule.ui.screen.home.search.function.school.admission

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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.AdmissionType
import com.hfut.schedule.logic.model.AdmissionDetailBean
import com.hfut.schedule.logic.model.AdmissionMapBean
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.ui.component.container.AnimationCustomCard
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.LargeCard
import com.hfut.schedule.ui.component.container.SmallCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.container.mixedCardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.CustomTabRow
import com.hfut.schedule.ui.component.screen.RefreshIndicator
import com.hfut.schedule.ui.component.text.DividerText
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.style.topBarBlur
import com.hfut.schedule.ui.style.topBarTransplantColor
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.component.TopBarNavigateIcon
import com.xah.transition.component.TransitionScaffold
import com.xah.transition.component.containerShare
import com.xah.transition.util.navigateAndSaveForTransition
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AdmissionScreen(
    vm : NetWorkViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    navController : NavHostController,
) {
    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.Admission.route }
    with(sharedTransitionScope) {
        TransitionScaffold (
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            topBar = {
                Column {
                    TopAppBar(
                        modifier = Modifier.topBarBlur(hazeState),
                        colors = topBarTransplantColor(),
                        title = { Text("本科招生") },
                        navigationIcon = {
                            TopBarNavigateIcon(navController,animatedContentScope,route,R.drawable.publics)
                        },
                    )
                }
            },

        ) { innerPadding ->
            AdmissionListUI(vm,innerPadding,navController,sharedTransitionScope,animatedContentScope)
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterialApi::class)
@Composable
fun AdmissionListUI(
    vm: NetWorkViewModel,
    innerPadding : PaddingValues,
    navController: NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val pageList = remember { AdmissionType.entries }
    val titles = remember { pageList.map { it.description } }
    val pagerState = rememberPagerState { pageList.size }
    val uiState by vm.admissionListResp.state.collectAsState()
    val refreshNetwork : suspend(Boolean) -> Unit = m@ { skip : Boolean ->
        val type = pageList[pagerState.currentPage]
        if(skip && uiState is UiState.Success && (uiState as UiState.Success).data.first == type) return@m
        vm.admissionListResp.clear()
        vm.getAdmissionList(type)
    }
    LaunchedEffect(pagerState.currentPage) {
        refreshNetwork(true)
    }
    val refreshing = uiState is UiState.Loading
    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        scope.launch {
            refreshNetwork(false)
        }
    })
    Column(modifier = Modifier.padding(innerPadding)) {
//        Spacer(Modifier.height(innerPadding.calculateTopPadding()))
        CustomTabRow(pagerState,titles)
        Box(modifier = Modifier.fillMaxSize().pullRefresh(pullRefreshState)) {
            RefreshIndicator(refreshing, pullRefreshState, Modifier.align(Alignment.TopCenter).zIndex(1f))
            HorizontalPager(pagerState) { page ->
                CommonNetworkScreen(uiState, onReload = { refreshNetwork(false) }) {
                    val list = (uiState as UiState.Success).data.second.entries.toList()
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.padding(horizontal = 10.dp),
                    ) {
                        items(list.size) { index ->
                            val item = list[index]
                            val route = AppNavRoute.AdmissionRegionDetail.withArgs(index, pageList[page].description)
                            with(sharedTransitionScope) {
                                SmallCard (
                                    color = mixedCardNormalColor(),
                                    modifier = containerShare(
                                        Modifier.padding(2.5.dp),
                                        animatedContentScope,
                                        route
                                    )
                                ) {
                                    TransplantListItem(
                                        headlineContent = { Text(item.key) },
                                        modifier = Modifier.clickable {
                                            navController.navigateAndSaveForTransition(route)
                                        }
                                    )
                                }
                            }
                        }
//                        items(3) { Spacer(Modifier.height(innerPadding.calculateBottomPadding())) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AdmissionRegionScreen(
    vm : NetWorkViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    navController : NavHostController,
    type : String,
    index: Int
) {
    if(index < 0) return

    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = false)
    val route = remember { AppNavRoute.AdmissionRegionDetail.withArgs(index,type) }

    val listState by vm.admissionListResp.state.collectAsState()
    val data = (listState as? UiState.Success)?.data?.second?.entries?.toList()[index] ?: return
    val titles = remember { data.value.map { it.toString() } }
    val pagerState = rememberPagerState { titles.size }
    with(sharedTransitionScope) {
        TransitionScaffold (
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            topBar = {
                Column(modifier = Modifier.topBarBlur(hazeState)) {
                    TopAppBar(
                        colors = topAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary
                        ),
                        title = { Text(type + " : "+ data.key) },
                        navigationIcon = {
                            TopBarNavigateIcon(navController)
                        }
                    )
                    CustomTabRow(pagerState,titles)
                }
            },
        ) { innerPadding ->
            HorizontalPager(pagerState) { page ->
                val bean = data.value[page]
                val typeE = AdmissionType.entries.find { it.description == type }!!
                val region = data.key
                AdmissionDetailScreen(innerPadding,bean,typeE,region,vm)
            }
        }
    }
}

@Composable
fun AdmissionDetailScreen(
    innerPadding : PaddingValues,
    bean :  AdmissionMapBean,
    type : AdmissionType,
    region : String,
    vm : NetWorkViewModel
) {
    val uiState by vm.admissionDetailResp.state.collectAsState()
    val refreshNetwork : suspend() -> Unit = {
        vm.admissionDetailResp.clear()
        vm.getAdmissionToken()
        vm.getAdmissionDetail(type,bean,region)
    }
    LaunchedEffect(Unit) {
        refreshNetwork()
    }
    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        val data = (uiState as UiState.Success).data
        val ui : LazyListScope.() -> Unit =   {
            when (data) {
                is AdmissionDetailBean.History -> {
                    val majorList = data.data.majorSituationList
                    val generalList = data.data.generalSituationList
                    items(generalList.size,key = { it }) { index ->
                        val item = generalList[index]
                        AnimationCustomCard (containerColor = MaterialTheme.colorScheme.secondaryContainer, index = index) {
                            Column {
                                Row {
                                    item.minScore?.let {
                                        TransplantListItem(
                                            headlineContent = { Text(it.toString()) },
                                            overlineContent = { Text("最低分") },
                                            leadingContent = { Icon(painterResource(R.drawable.arrow_downward),null)},
                                            modifier = Modifier.weight(.5f)
                                        )
                                    }
                                    item.maxScore?.let {
                                        TransplantListItem(
                                            headlineContent = { Text(it.toString()) },
                                            overlineContent = { Text("最高分") },
                                            leadingContent = { Icon(painterResource(R.drawable.arrow_upward),null)},
                                            modifier = Modifier.weight(.5f)
                                        )
                                    }
                                }
                                Row {
                                    item.avgScore?.let {
                                        TransplantListItem(
                                            headlineContent = { Text(it.toString()) },
                                            overlineContent = { Text("平均分") },
                                            leadingContent = { Icon(painterResource(R.drawable.filter_vintage),null)},
                                            modifier = Modifier.weight(.5f)
                                        )
                                    }
                                    item.fsx?.let {
                                        TransplantListItem(
                                            headlineContent = { Text(it.toString()) },
                                            overlineContent = { Text("控制线") },
                                            leadingContent = { Icon(painterResource(R.drawable.stat_minus_2),null)},
                                            modifier = Modifier.weight(.5f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item { DividerText("专业分数线") }
                    items(majorList.size,key= { it + generalList.size }) { index ->
                        val item = majorList[index]
                        AnimationCustomCard  (containerColor = cardNormalColor(), index = index+generalList.size) {
                            Column {
                                TransplantListItem(
                                    headlineContent = { Text(item.major,fontWeight = FontWeight.Bold) }
                                )
                                PaddingHorizontalDivider()
                                Row {
                                    item.minScore?.let {
                                        TransplantListItem(
                                            headlineContent = { Text(it.toString()) },
                                            overlineContent = { Text("最低分") },
                                            leadingContent = { Icon(painterResource(R.drawable.arrow_downward),null)},
                                            modifier = Modifier.weight(.5f)
                                        )
                                    }
                                    item.maxScore?.let {
                                        TransplantListItem(
                                            headlineContent = { Text(it.toString()) },
                                            overlineContent = { Text("最高分") },
                                            leadingContent = { Icon(painterResource(R.drawable.arrow_upward),null)},
                                            modifier = Modifier.weight(.5f)
                                        )
                                    }
                                }
                                Row {
                                    item.avgScore?.let {
                                        TransplantListItem(
                                            headlineContent = { Text(it.toString()) },
                                            overlineContent = { Text("平均分") },
                                            leadingContent = { Icon(painterResource(R.drawable.filter_vintage),null)},
                                            modifier = Modifier.weight(.5f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                is AdmissionDetailBean.Plan -> {
                    val majorList = data.data.majorSituationList
                    val generalList = data.data.generalSituationList
                    items(generalList.size, key = { it }) { index ->
                        val item = generalList[index]
                        Column {
                            Spacer(Modifier.height(CARD_NORMAL_DP))
                            LargeCard("合计招生 ${item.count} 人") {
                                Column {
                                    item.firstSubjectRequirement?.let {
                                        TransplantListItem(
                                            headlineContent = { Text(it) },
                                            overlineContent = { Text("首选科目要求") }
                                        )
                                    }
                                    TransplantListItem(
                                        headlineContent = { Text(item.subjectRequirement) },
                                        overlineContent = { Text("选科要求") }
                                    )
                                }
                            }
                        }
                    }
                    item { DividerText("招生专业") }
                    items(majorList.size,key = { generalList.size + it }) { index ->
                        val item = majorList[index]
                        AnimationCustomCard (containerColor = cardNormalColor(), index = generalList.size + index) {
                            Column {
                                TransplantListItem(
                                    headlineContent = { Text(item.major, fontWeight = FontWeight.Bold)},
                                    trailingContent = { Text(item.count.toString() + "人")}
                                )
                                PaddingHorizontalDivider()
                                item.firstSubjectRequirement?.let {
                                    TransplantListItem(
                                        headlineContent = { Text(it) },
                                        overlineContent = { Text("首选科目要求") }
                                    )
                                }
                                TransplantListItem(
                                    headlineContent = { Text(item.subjectRequirement) },
                                    overlineContent = { Text("选科要求") }
                                )
                            }
                        }
                    }
                }
            }
        }
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
//            item { Spacer(Modifier.height(innerPadding.calculateTopPadding())) }
            ui()
//            item { Spacer(Modifier.height(innerPadding.calculateBottomPadding())) }
        }
    }
}
