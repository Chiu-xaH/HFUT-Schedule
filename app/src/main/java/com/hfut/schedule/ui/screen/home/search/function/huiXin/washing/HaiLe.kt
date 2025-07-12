package com.hfut.schedule.ui.screen.home.search.function.huiXin.washing

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.HaiLeDeviceDetailRequestBody
import com.hfut.schedule.logic.model.HaiLeNearPositionRequestDTO
import com.hfut.schedule.logic.model.HaiLeType
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.AnimationCardListItem
import com.hfut.schedule.ui.component.CommonNetworkScreen
import com.hfut.schedule.ui.component.LoadingIcon
import com.hfut.schedule.ui.component.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.PagingController
import com.hfut.schedule.ui.component.RotatingIcon
import com.hfut.schedule.ui.component.custom.CustomTabRow
import com.hfut.schedule.ui.component.custom.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Campus
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.CampusDetail
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.getCampus
import com.hfut.schedule.ui.style.ColumnVertical
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

private val d = mapOf<String, String>(
    HaiLeType.SHOES_WASHER.typeCode to HaiLeType.SHOES_WASHER.description,
    HaiLeType.CLOTHES_DRYER.typeCode to HaiLeType.CLOTHES_DRYER.description,
    HaiLeType.WASHING_MACHINE.typeCode to HaiLeType.WASHING_MACHINE.description,
)

@Composable
fun HaiLeScreen(vm : NetWorkViewModel,hazeState : HazeState) {
    val t = remember { CampusDetail.entries }
    val titles = remember { t.map { it.description } }
    val pagerState = rememberPagerState(pageCount = { titles.size }, initialPage = when(getCampus()) {
        Campus.HEFEI -> 1
        Campus.XUANCHENG -> t.lastIndex
    })
    val uiState by vm.haiLeNearPositionResp.state.collectAsState()
    var itemId: Long by remember { mutableLongStateOf(-1) }
    var itemName by remember { mutableStateOf("详情") }

    var showBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    if (showBottomSheet) {
        var type by remember { mutableStateOf<HaiLeType?>(null) }
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
        ) {
            if(itemId < 0) return@HazeBottomSheet
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar(itemName) {
                        FilledTonalIconButton(
                            onClick = {
                                scope.launch {
                                    vm.haiLeDeviceDetailResp.clear()
                                    vm.getHaiLeDeviceDetail(
                                        HaiLeDeviceDetailRequestBody(
                                            positionId = itemId.toString(),
                                            page = 1,
                                            categoryCode = type?.typeCode
                                        )
                                    )
                                }
                            }
                        ) {
                            Icon(painterResource(R.drawable.rotate_right),null)
                        }
                    }
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    HaiLeDetailScreen(vm,itemId) { type = it }
                }
            }
        }
    }
    var page by remember { mutableIntStateOf(1) }

    val refreshNetwork = suspend {
        vm.haiLeNearPositionResp.clear()
        vm.getHaiLeNearPosition(
            HaiLeNearPositionRequestDTO(
                campus = t[pagerState.currentPage],
                page = page
            )
        )
    }
    LaunchedEffect(pagerState.currentPage,page) {
        refreshNetwork()
    }
    CustomTabRow(pagerState,titles)
    HorizontalPager(state = pagerState) { pager ->
        val campus = t[pager]
        CommonNetworkScreen(uiState = uiState, onReload = refreshNetwork) {
            val list = (uiState as UiState.Success).data
                .filter {
                    when(campus) {
                        CampusDetail.XC -> it.address.contains("宣州区薰化路301号")
                        CampusDetail.TXL -> it.address.contains("合肥工业大学") || it.name.contains(CampusDetail.TXL.description)
                        CampusDetail.FCH -> it.address.contains("合肥工业大学") || it.name.contains(CampusDetail.FCH.description)
                    }
                }
                .sortedBy { it.id }
            val listState = rememberLazyListState()

            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(state = listState) {
                    items (list.size, key = { list[it].id } ){ index ->
                        val item = list[index]
                        with(item) {
                            AnimationCardListItem(
                                index = index,
                                headlineContent = { Text(name) },
                                supportingContent = {
                                    Text(categoryCodeList.map { d[it] }.toString().replace("[","").replace("]","")) },
                                overlineContent = { Text(address) },
                                leadingContent = {
                                    Icon(painterResource(R.drawable.near_me),null)
                                },
                                trailingContent = {
                                    Text(if(enableReserve)"可预约 $reserveNum/$idleCount" else "设备数 $idleCount")
                                },
                                modifier = Modifier.clickable {
                                    itemId = id
                                    itemName = name
                                    showBottomSheet = true
                                }
                            )
                        }
                    }
                    item { PaddingForPageControllerButton() }
                }
                PagingController(listState,page, showUp = true,nextPage = { page = it }, previousPage = { page = it })
            }
        }
    }
}

@Composable
fun HaiLeDetailScreen(vm : NetWorkViewModel,itemId : Long,onType : (HaiLeType) -> Unit) {
    val t = remember { HaiLeType.entries }
    val titles = remember { t.map { it.description } }
    val pagerState = rememberPagerState(pageCount = { titles.size })
    val uiState by vm.haiLeDeviceDetailResp.state.collectAsState()
    var page by remember { mutableIntStateOf(1) }

    val refreshNetwork = suspend {
        vm.haiLeDeviceDetailResp.clear()
        vm.getHaiLeDeviceDetail(
            HaiLeDeviceDetailRequestBody(
                positionId = itemId.toString(),
                page = page,
                categoryCode = t[pagerState.currentPage].typeCode
            )
        )
    }
    LaunchedEffect(page,pagerState.currentPage) {
        onType(t[pagerState.currentPage])
        refreshNetwork()
    }
    CustomTabRow(pagerState,titles)
    HorizontalPager(state = pagerState) { pager ->
        val type = t[pager]

        CommonNetworkScreen(uiState = uiState, onReload = refreshNetwork) {
            val list = (uiState as UiState.Success).data.sortedBy { it.name }
            val listState = rememberLazyListState()

            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(state = listState) {
                    items (list.size, key = { list[it].name } ){ index ->
                        val item = list[index]
                        with(item) {
                            AnimationCardListItem(
                                index = index,
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
                }
                PagingController(listState,page, showUp = true,nextPage = { page = it }, previousPage = { page = it })
            }
        }
    }
}