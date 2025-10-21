package com.hfut.schedule.ui.screen.home.search.function.one.emptyRoom

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.logic.model.one.BuildingBean
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.SmallCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.mixedCardNormalColor
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.screen.RefreshIndicator
import com.hfut.schedule.ui.component.screen.pager.CustomTabRow
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.logic.enumeration.Campus
import com.hfut.schedule.logic.enumeration.getCampus
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.component.TopBarNavigateIcon
import com.xah.transition.component.containerShare
import com.xah.transition.state.LocalAnimatedContentScope
import com.xah.transition.state.LocalSharedTransitionScope
import com.xah.uicommon.component.text.BottomTip
import com.xah.uicommon.component.text.ScrollText
import com.xah.uicommon.style.color.topBarTransplantColor
import com.xah.uicommon.style.padding.InnerPaddingHeight
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch


private val campusList = Campus.entries

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)
@Composable
fun ClassroomScreen(
    vm : NetWorkViewModel,
    navController : NavHostController,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val route = remember { AppNavRoute.Classroom.route }
    val pagerState = rememberPagerState(initialPage =
        when(getCampus()) {
            Campus.XC -> 2
            Campus.TXL -> 1
            else -> 0
        }
    ) { campusList.size }
    val uiState by vm.buildingsResponse.state.collectAsState()

    val refreshNetwork : suspend(Boolean) -> Unit = m@ { skip : Boolean ->
        val campus = campusList[pagerState.currentPage]
        if(skip && uiState is UiState.Success && (uiState as UiState.Success).data.first == campus) return@m
        val token = prefs.getString("bearer","")
        token?.let {
            vm.admissionListResp.clear()
            vm.getBuildings(campus,it)
        }
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
        CustomTransitionScaffold (
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            route = route,
            
            navHostController = navController,
            topBar = {
                Column(
                    modifier = Modifier.topBarBlur(hazeState),
                ) {
                    MediumTopAppBar(
                        scrollBehavior = scrollBehavior,
                        colors = topBarTransplantColor(),
                        title = { Text(AppNavRoute.Classroom.label) },
                        navigationIcon = {
                            TopBarNavigationIcon(navController,route, AppNavRoute.Classroom.icon)
                        }
                    )
                    CustomTabRow(pagerState,campusList.map { it.description })
                }
            },
        ) { innerPadding ->
            Box(modifier = Modifier.hazeSource(hazeState).fillMaxSize().pullRefresh(pullRefreshState)) {
                RefreshIndicator(refreshing, pullRefreshState, Modifier.align(Alignment.TopCenter).padding(innerPadding).zIndex(1f))
                HorizontalPager(pagerState) { pager ->
                    CommonNetworkScreen(uiState, onReload = { refreshNetwork(false) }) {
                        val list = (uiState as UiState.Success).data.second
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.padding(horizontal = 10.dp),
                        ) {
                            items(2) {  InnerPaddingHeight(innerPadding,true) }
                            items(list.size) { index ->
                                val item = list[index]
                                val pRoute = AppNavRoute.ClassroomDetail.withArgs(item)
                                SmallCard (
                                    color = mixedCardNormalColor(),
                                    modifier = Modifier
                                        .padding(2.5.dp)
                                        .containerShare(pRoute,)
                                ) {
                                    TransplantListItem(
                                        headlineContent = { ScrollText(item.nameZh) },
                                        modifier = Modifier.clickable {
                                                navController.navigateForTransition(AppNavRoute.ClassroomDetail,pRoute)
                                        }
                                    )
                                }
                            }
                            items(2) {  InnerPaddingHeight(innerPadding,false) }
                        }
                    }
                }
            }
        }
//    }
}


@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ClassroomDetailScreen(
    vm : NetWorkViewModel,
    navController : NavHostController,
    code : String,
    name: String
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.ClassroomDetail.withArgs(BuildingBean(name,code)) }

    val uiState by vm.classroomResponse.state.collectAsState()
    val refreshNetwork : suspend () -> Unit = {
        val token = prefs.getString("bearer","")
        token?.let {
            vm.classroomResponse.clear()
            vm.getClassroomInfo(code,it)
        }
    }
    LaunchedEffect(Unit) {
        refreshNetwork()
    }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        CustomTransitionScaffold (
            route = route,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            
            navHostController = navController,
            topBar = {
                Column(modifier = Modifier.topBarBlur(hazeState)) {
                    MediumTopAppBar(
                        scrollBehavior = scrollBehavior,
                        colors = topBarTransplantColor(),
                        title = { Text(name) },
                        navigationIcon = {
                            TopBarNavigateIcon(navController)
                        }
                    )
                }
            },
        ) { innerPadding ->
            Column(modifier = Modifier.hazeSource(hazeState).fillMaxSize()) {
                CommonNetworkScreen(uiState, onReload = refreshNetwork) {
                    val list = (uiState as UiState.Success).data.sortedBy { it.floor.toString() + it.nameZh }
                    LazyColumn() {
                        item { InnerPaddingHeight(innerPadding,true) }
                        items(list.size) { index ->
                            val item = list[index]
                            val enabled = item.enabled == 1
                            CardListItem(
                                headlineContent = { Text(item.nameZh) },
                                leadingContent = { Text(item.floor.toString() + "F")},
                                color = if(enabled) null else MaterialTheme.colorScheme.errorContainer,
                                trailingContent = {
//                                    Text(if(enabled) "可用" else "不可用" )
                                },
                                overlineContent = { Text("容量 " + item.seatsForLesson.toString() + "人"
//                                        + " | 类型 ${item.roomTypeId}"
                                ) }
                            )
                        }
                        item { InnerPaddingHeight(innerPadding,false) }
                    }
                }
            }
        }
//    }
}