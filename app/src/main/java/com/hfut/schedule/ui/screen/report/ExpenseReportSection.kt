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
import com.hfut.schedule.logic.util.network.state.UiState

import com.hfut.schedule.logic.util.parse.roundOffString
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.viewmodel.network.NetWorkViewModel

@Composable
fun ExpenseReportSection(vm: NetWorkViewModel, semester: Int) {
    val uiState by vm.cardPredictedResponse.state.collectAsState()

    val refreshNetwork: suspend () -> Unit = rN@ {
        if (uiState is UiState.Success) return@rN
        val auth = prefs.getString("auth", "")
        if (auth.isNullOrEmpty()) return@rN
        vm.cardPredictedResponse.clear()
        vm.getCardPredicted("bearer $auth")
    }

    LaunchedEffect(Unit) { refreshNetwork() }

    DividerTextExpandedWith("消费报表", false) {
        when (uiState) {
            is UiState.Success -> {
                val data = (uiState as UiState.Success).data


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
            is UiState.Error -> {
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
