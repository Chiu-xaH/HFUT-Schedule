package com.hfut.schedule.ui.screen.home.search.function.huiXin.washing

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.xah.common.logic.model.Campus
import com.hfut.schedule.logic.util.helper.getCampus
import com.hfut.schedule.network.api.model.response.json.haile.HaiLeNearPositionBean
import com.hfut.schedule.logic.model.dto.HaiLeNearPositionRequestDto
import com.hfut.schedule.logic.model.dto.HaiLeType
import com.xah.common.logic.state.NetworkUiState
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.network.api.model.request.haile.HaiLeDeviceDetailRequest
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.icon.LoadingIcon
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.RefreshIndicator
import com.hfut.schedule.ui.component.screen.pager.CustomTabRow
import com.hfut.schedule.ui.component.screen.pager.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.pager.PageController
import com.hfut.schedule.ui.nav.destination.HaiLeWashingDestination
import com.hfut.schedule.ui.nav.destination.HaiLeWashingDetailDestination
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.style.align.ColumnVertical
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.container.component.base.sharedContainer
import com.sharednav.common.helper.NoneRoundShape
import com.xah.navigation.util.LocalNavController
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

private val d = mapOf(
    HaiLeType.SHOES_WASHER.typeCode to HaiLeType.SHOES_WASHER.description,
    HaiLeType.CLOTHES_DRYER.typeCode to HaiLeType.CLOTHES_DRYER.description,
    HaiLeType.WASHING_MACHINE.typeCode to HaiLeType.WASHING_MACHINE.description,
)


@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class,
)
@Composable
fun HaiLeWashingScreen(
    vm : NetWorkViewModel,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val t = remember { listOf(Campus.FCH,Campus.XC) }
    val titles = remember { t.map { it.description } }
    val pagerState = rememberPagerState(pageCount = { titles.size }, initialPage = when(getCampus()) {
        Campus.XC -> 1
        else -> 0
    })

    val uiState by vm.haiLeNearPositionResp.state.collectAsState()
    var page by rememberSaveable { mutableIntStateOf(1) }
    val refreshNetwork : suspend(Boolean) -> Unit =  m@ { skip : Boolean ->
        if(skip && uiState is NetworkUiState.Success) {
            return@m
        }
        vm.haiLeNearPositionResp.clear()
        vm.getHaiLeNearPosition(
            HaiLeNearPositionRequestDto(
                campus = t[pagerState.currentPage],
                page = page
            )
        )
    }

    val refreshing = uiState is NetworkUiState.Loading
    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        scope.launch {
            refreshNetwork(false)
        }
    })
    var savedCurrentPage by rememberSaveable { mutableStateOf<Int?>(null) }

    LaunchedEffect(pagerState.currentPage) {
        val currentPage = pagerState.currentPage
        if(savedCurrentPage == currentPage) {
            return@LaunchedEffect
        }
        savedCurrentPage = currentPage
        refreshNetwork(false)
    }

    val navController = LocalNavController.current

    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column(
                modifier = Modifier.topBarBlur(hazeState),
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(HaiLeWashingDestination.title.asString()) },
                    navigationIcon = {
                        TopBarNavigationIcon()
                    }
                )
                CustomTabRow(pagerState,titles)
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .hazeSource(hazeState)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(pullRefreshState)
            ) {
                HorizontalPager(state = pagerState) { pager ->
                    val campus = t[pager]
                    CommonNetworkScreen(uiState = uiState, onReload = { refreshNetwork(false) }) {
                        val list = (uiState as NetworkUiState.Success).data
                            .filter {
                                when(campus) {
                                    Campus.XC -> it.address.contains("宣州区薰化路301号")
                                    Campus.TXL -> it.address.contains("合肥工业大学") || it.name.contains(Campus.TXL.description)
                                    Campus.FCH -> it.address.contains("合肥工业大学") || it.name.contains(Campus.FCH.description)
                                }
                            }
                            .sortedBy { it.id }
                        val listState = rememberLazyListState()

                        Box(modifier = Modifier.fillMaxSize()) {
                            LazyColumn(state = listState) {
                                item { InnerPaddingHeight(innerPadding,true) }
                                items (list.size, key = { list[it].id } ){ index ->
                                    val item = list[index]
                                    val destination = HaiLeWashingDetailDestination(item)
                                    with(item) {
                                        CardListItem(
                                            cardModifier = Modifier.sharedContainer(
                                                destination.key,
                                                MaterialTheme.shapes.medium,
                                                cardNormalColor()
                                            ),
                                            shape = NoneRoundShape,
                                            headlineContent = { Text(name) },
                                            supportingContent = {
                                                Text(
                                                    categoryCodeList.mapNotNull { d[it] }.joinToString(" ")
                                                ) },
                                            overlineContent = { Text(address) },
                                            leadingContent = {
                                                Icon(painterResource(R.drawable.near_me),null)
                                            },
                                            trailingContent = {
                                                Text(if(enableReserve)"可预约 $reserveNum/$idleCount" else "设备数 $idleCount")
                                            },
                                            modifier = Modifier.clickable {
                                                navController.push(destination)
                                            }
                                        )
                                    }
                                }
                                item { InnerPaddingHeight(innerPadding,false) }
                                item { PaddingForPageControllerButton() }
                            }
                            PageController(
                                listState,
                                page,
                                onNextPage = {
                                    page = it
                                    scope.launch { refreshNetwork(false) }
                                },
                                onPreviousPage = {
                                    page = it
                                    scope.launch { refreshNetwork(false) }
                                }
                            )
                        }
                    }
                }
                RefreshIndicator(
                    refreshing,
                    pullRefreshState,
                    Modifier
                        .align(Alignment.TopCenter)
                        .padding(innerPadding)
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HaiLeDetailScreen(
    vm : NetWorkViewModel,
    item: HaiLeNearPositionBean,
    description : String
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val t = remember { HaiLeType.entries }
    val titles = remember { t.map { it.description } }
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { titles.size })
    val uiState by vm.haiLeDeviceDetailResp.state.collectAsState()

    var page by remember { mutableIntStateOf(1) }
    val typeCode = remember(pagerState.currentPage) { t[pagerState.currentPage].typeCode }

    val refreshNetwork = suspend {
        vm.haiLeDeviceDetailResp.clear()
        vm.getHaiLeDeviceDetail(
            HaiLeDeviceDetailRequest(
                positionId = item.id.toString(),
                page = page,
                categoryCode = typeCode
            )
        )
    }

    val refreshing = uiState is NetworkUiState.Loading
    val pullRefreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        scope.launch {
            refreshNetwork()
        }
    })

    LaunchedEffect(page,pagerState.currentPage) {
        refreshNetwork()
    }

    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column(
                modifier = Modifier.topBarBlur(hazeState),
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(description) },
                    navigationIcon = {
                        TopBarNavigationIcon()
                    },
                )
                CustomTabRow(pagerState,titles)
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .hazeSource(hazeState)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(pullRefreshState)
            ) {
                HorizontalPager(state = pagerState) { pager ->
                    val type = t[pager]

                    CommonNetworkScreen(uiState = uiState, onReload = refreshNetwork) {
                        val list = (uiState as NetworkUiState.Success).data.sortedBy { it.name }
                        val listState = rememberLazyListState()

                        Box(modifier = Modifier.fillMaxSize()) {
                            LazyColumn(state = listState) {
                                item { InnerPaddingHeight(innerPadding,true) }
                                items (list.size, key = { list[it].name } ){ index ->
                                    val item = list[index]
                                    with(item) {
                                        CardListItem(
                                            headlineContent = { Text(name) },
                                            leadingContent = {
                                                if(finishTime  != null) {
                                                    LoadingIcon()
                                                } else {
                                                    Icon(painterResource(
                                                        when(type) {
                                                            HaiLeType.SHOES_WASHER -> R.drawable.steps
                                                            HaiLeType.CLOTHES_DRYER -> R.drawable.cool_to_dry
                                                            HaiLeType.WASHING_MACHINE -> R.drawable.laundry
                                                        }
                                                    ),null)
                                                }
                                            },
                                            trailingContent = {
                                                ColumnVertical {
                                                    Text(when(state) {
                                                        1 -> "空闲"
                                                        2 -> "占用"
                                                        3 -> "故障"
                                                        else -> "未知情况"
                                                    })
                                                    if(enableReserve)
                                                        Text("可预约 $reserveNum")
                                                }
                                            },
                                            supportingContent = if(state == 2){
                                                finishTime?.let{ {
                                                    Text("预计 ${it.substringAfter("-")} 完成")
                                                } }
                                            } else null,
                                            color = if(finishTime != null || state != 1) MaterialTheme.colorScheme.errorContainer else null,
                                            modifier = Modifier.clickable {
                                                showToast("支付请使用 微信小程序-海乐生活")
                                            }
                                        )
                                    }
                                }
                                item { PaddingForPageControllerButton() }
                                item { InnerPaddingHeight(innerPadding,false) }
                            }
                            PageController(listState,page,onNextPage = { page = it }, onPreviousPage = { page = it })
                        }
                    }
                }
                RefreshIndicator(
                    refreshing,
                    pullRefreshState,
                    Modifier
                        .align(Alignment.TopCenter)
                        .padding(innerPadding)
                )
            }
        }
    }
}