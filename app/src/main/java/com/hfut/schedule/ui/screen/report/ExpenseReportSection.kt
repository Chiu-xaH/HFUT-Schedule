package com.hfut.schedule.ui.screen.report

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.xah.common.logic.state.NetworkUiState

import com.hfut.schedule.logic.util.parse.roundOffString
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.viewmodel.network.NetWorkViewModel

@Composable
fun ExpenseReportSection(vm: NetWorkViewModel, semester: Int) {
    val uiState by vm.cardPredictedResponse.state.collectAsState()
    val huiXinAuth by DataStoreManager.huiXinAuth.collectAsState(initial = "")

    val refreshNetwork: suspend () -> Unit = rN@ {
        if (uiState is NetworkUiState.Success) return@rN
        if (huiXinAuth.isEmpty()) return@rN
        vm.cardPredictedResponse.clear()
        vm.getCardPredicted("bearer $huiXinAuth")
    }

    LaunchedEffect(huiXinAuth) { refreshNetwork() }

    DividerTextExpandedWith("消费报表", false) {
        when (uiState) {
            is NetworkUiState.Success -> {
                val data = (uiState as NetworkUiState.Success).data


                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(
                        overlineContent = { Text("消费预测") },
                        headlineContent = { Text("明日预计") },
                        trailingContent = { Text("￥${data.day.predictData.predict.roundOffString(2)}", style = MaterialTheme.typography.titleMedium) }
                    )
                    TransplantListItem(
                        headlineContent = { Text("下月预计") },
                        trailingContent = { Text("￥${data.month.predictData.predict.roundOffString(2)}", style = MaterialTheme.typography.titleMedium) }
                    )
                }
            }
            is NetworkUiState.Error -> {
                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(headlineContent = { Text("暂无消费数据") }, supportingContent = { Text("请先在一卡通页面加载数据") })
                }
            }
            else -> {
                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(headlineContent = { Text("加载中...") })
                }
            }
        }
    }
}
