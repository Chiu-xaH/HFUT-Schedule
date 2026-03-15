package com.hfut.schedule.ui.screen.home.search.function.community.failRate

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.community.courseFailRateDTOList
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.screen.pager.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.pager.PageController
import com.hfut.schedule.ui.component.status.EmptyIcon
import com.hfut.schedule.ui.destination.FailRateDetailDestination
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.container.container.sharedContainer
import com.xah.navigation.utils.LocalNavController
import com.xah.common.ui.style.align.CenterScreen
import com.xah.common.ui.style.padding.InnerPaddingHeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FailRateUI(
    vm : NetWorkViewModel,
    page : Int,
    nextPage : (Int) -> Unit,
    previousPage : (Int) -> Unit,
    innerPadding : PaddingValues,
    filterCode : String? = null
) {
    val navController = LocalNavController.current
    val uiState by vm.failRateData.state.collectAsState()
    val listState = rememberLazyListState()
    val list = (uiState as UiState.Success).data.let {
        filterCode?.let { code ->
            it.filter { it.courseMetaId == filterCode }
        } ?: it
    }
    if(list.isEmpty()) {
        CenterScreen {
            EmptyIcon("未搜到课程" + (filterCode?.let { "($it)" } ?: ""))
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            PageController(listState,page,onNextPage=nextPage,onPreviousPage=previousPage,modifier = Modifier.zIndex(2f))
            LazyColumn(state = listState) {
                item { Spacer(Modifier.height(CARD_NORMAL_DP)) }
                item { InnerPaddingHeight(innerPadding,true) }
                items(list.size){ item ->
                    val bean = list[item]
                    val dest = FailRateDetailDestination(bean.courseName,bean.courseMetaId,bean.courseFailRateDTOList)
                    CardListItem(
                        cardModifier = Modifier.sharedContainer(
                            key = dest.key,
                            shape = MaterialTheme.shapes.medium,
                            containerColor = cardNormalColor()
                        ),
                        shape = RoundedCornerShape(0.dp),
                        overlineContent = { Text(list[item].courseMetaId)},
                        headlineContent = {  Text(list[item].courseName) },
                        leadingContent = { Icon(painterResource(AppNavRoute.FailRate.icon), contentDescription = "Localized description",) },
                        modifier = Modifier.clickable {
                            navController.push(dest)
                        },
                    )
                }
                item { PaddingForPageControllerButton() }
                item { InnerPaddingHeight(innerPadding,false) }
            }
        }
    }
}




@Composable
fun FailRateDetailScreen(
    innerPadding : PaddingValues,
    detailList : List<courseFailRateDTOList>
) {
    LazyColumn {
        item { InnerPaddingHeight(innerPadding,true) }
        items(detailList.size,key = { it }){ item ->
            val dataItem = detailList[item]
            val rate = (1 - dataItem.successRate) * 100
            CardListItem(
                headlineContent = {  Text("平均分 ${dataItem.avgScore}") },
                supportingContent = { Text("人数: 挂科 ${dataItem.failCount} | 总 ${dataItem.totalCount}") },
                overlineContent = { Text(text = "${dataItem.xn}年 第${dataItem.xq}学期")},
                leadingContent = { Icon(painterResource(R.drawable.article), contentDescription = "Localized description",) },
                trailingContent = { Text("挂科率 ${String.format("%.2f", rate)} %") },
            )
        }
        item { InnerPaddingHeight(innerPadding,true) }
        item { Spacer(Modifier.height(APP_HORIZONTAL_DP).navigationBarsPadding()) }
    }
}