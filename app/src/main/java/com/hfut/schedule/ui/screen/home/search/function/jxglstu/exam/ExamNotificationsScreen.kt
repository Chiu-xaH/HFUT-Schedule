package com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.enumeration.CampusRegion.HEFEI
import com.hfut.schedule.logic.enumeration.CampusRegion.XUANCHENG
import com.hfut.schedule.logic.enumeration.getCampusRegion
import com.hfut.schedule.logic.model.AcademicXCType
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.screen.pager.CustomTabRow
import com.hfut.schedule.ui.component.screen.pager.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.pager.PageController
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.isValidWebUrl
import com.hfut.schedule.ui.screen.home.search.function.school.webvpn.autoWebVpnForNews
import com.hfut.schedule.ui.screen.home.search.function.school.webvpn.getWebVpnCookie
import com.hfut.schedule.ui.screen.news.home.TotalNewsScreen
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.uicommon.style.color.topBarTransplantColor
import com.xah.uicommon.style.padding.InnerPaddingHeight
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamNotificationsScreen(
    navController : NavHostController,
    vm : NetWorkViewModel,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.ExamNews.route }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val backdrop = rememberLayerBackdrop()
    val campusList = remember { CampusRegion.entries }
    val titles = remember { campusList.map { it.description } }
    val pagerState = rememberPagerState(
        initialPage = when(getCampusRegion()) {
            HEFEI -> 0
            XUANCHENG -> 1
        }
    ) { campusList.size }
    val cookies by produceState<String?>(initialValue = null) {
        value = getWebVpnCookie(vm)
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    CustomTransitionScaffold (
        route = route,
        roundShape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        navHostController = navController,
        topBar = {
            Column(
                modifier = Modifier.topBarBlur(hazeState),
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(stringResource(AppNavRoute.ExamNews.label)) },
                    navigationIcon = {
                        TopBarNavigationIcon(route, AppNavRoute.ExamNews.icon)
                    }
                )
                CustomTabRow(pagerState,titles)
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .backDropSource(backdrop)
                .hazeSource(hazeState)
                .fillMaxSize()
        ) {
            // 待精简
            HorizontalPager(pagerState) { page ->
                val campus = campusList[page]
                when(campus) {
                    HEFEI -> {
                        TotalNewsScreen(vm, AppNavRoute.NewsApi.Keyword.EXAM_SCHEDULE_HEFEI.keyword,innerPadding)
                    }
                    XUANCHENG -> {
                        var page by remember { mutableIntStateOf(1) }
                        val uiState by vm.academicXCResp.state.collectAsState()
                        val refreshNetwork: suspend () -> Unit = {
                            vm.academicXCResp.clear()
                            vm.getAcademicXCNews(AcademicXCType.EXAM,page)
                        }

                        LaunchedEffect(page) {
                            refreshNetwork()
                        }

                        CommonNetworkScreen(uiState, onReload = refreshNetwork) {
                            val list = (uiState as UiState.Success).data
                            val listState = rememberLazyListState()
                            Box(modifier = Modifier.fillMaxSize()) {
                                LazyColumn(state = listState) {
                                    item { InnerPaddingHeight(innerPadding,true) }
                                    items(list.size, key = { it }) { index ->
                                        val item = list[index]
                                        CardListItem(
                                            headlineContent = { Text(item.title) },
                                            overlineContent = { Text(item.date) },
                                            leadingContent = { Text((index+1).toString()) },
                                            modifier = Modifier.clickable {
                                                scope.launch {
                                                    val link = if (isValidWebUrl(item.link)) {
                                                        item.link
                                                    } else {
                                                        MyApplication.XC_ACADEMIC_URL + item.link
                                                    }
                                                    autoWebVpnForNews(
                                                        context,
                                                        link,
                                                        item.title,
                                                        cookie = cookies
                                                    )
                                                }
                                            },
                                        )
                                    }
                                    item { InnerPaddingHeight(innerPadding,false) }
                                    item { PaddingForPageControllerButton() }
                                }
                                PageController(
                                    listState,
                                    page,
                                    onNextPage = { page = it },
                                    onPreviousPage = { page = it },
                                    paddingSafely = false,
                                    modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
