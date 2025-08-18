package com.hfut.schedule.ui.screen.home.search.function.community.failRate

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.ui.component.container.AnimationCardListItem
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.text.BottomSheetTopBar
import com.hfut.schedule.ui.component.screen.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.PagingController
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.InnerPaddingHeight
import com.hfut.schedule.ui.style.bottomSheetRound
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FailRateUI(vm : NetWorkViewModel,page : Int,nextPage : (Int) -> Unit, previousPage : (Int) -> Unit,innerPadding : PaddingValues,hazeState: HazeState) {
    val uiState by vm.failRateData.state.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var num by remember { mutableIntStateOf(0) }
    val listState = rememberLazyListState()
    val list = (uiState as UiState.Success).data
    Box(modifier = Modifier.fillMaxSize()) {
        PagingController(listState,page,nextPage=nextPage,previousPage=previousPage,modifier = Modifier.zIndex(2f))
        LazyColumn(state = listState) {
            item { Spacer(Modifier.height(CARD_NORMAL_DP)) }
            item { InnerPaddingHeight(innerPadding,true) }
            items(list.size){ item ->
                AnimationCardListItem(
                    headlineContent = {  Text(list[item].courseName) },
                    leadingContent = { Icon(painterResource(R.drawable.monitoring), contentDescription = "Localized description",) },
                    trailingContent = { Icon(Icons.Filled.ArrowForward, contentDescription = "")},
                    modifier = Modifier.clickable {
                        showBottomSheet = true
                        num = item
                    },
                    index = item
                )
            }
            item { PaddingForPageControllerButton() }
            item { InnerPaddingHeight(innerPadding,false) }
        }
    }



    if (showBottomSheet) {

        HazeBottomSheet(
            showBottomSheet = showBottomSheet,
            onDismissRequest = { showBottomSheet = false },
            hazeState = hazeState
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar(list[num].courseName)
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    val detailList = list[num].courseFailRateDTOList
                    LazyColumn{
                        items(detailList.size){ item ->
                            val dataItem = detailList[item]
                            val rate = (1 - dataItem.successRate) * 100
                            AnimationCardListItem(
                                headlineContent = {  Text("平均分 ${dataItem.avgScore}") },
                                supportingContent = { Text("人数: 挂科 ${dataItem.failCount} | 总 ${dataItem.totalCount}") },
                                overlineContent = { Text(text = "${dataItem.xn}年 第${dataItem.xq}学期")},
                                leadingContent = { Icon(painterResource(R.drawable.article), contentDescription = "Localized description",) },
                                trailingContent = { Text("挂科率 ${String.format("%.2f", rate)} %") },
                                index = item
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}
