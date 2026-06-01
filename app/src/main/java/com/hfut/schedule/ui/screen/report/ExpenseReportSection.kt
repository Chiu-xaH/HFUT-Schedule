package com.hfut.schedule.ui.screen.report

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.model.huixin.BillMonth
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.screen.card.count.drawLineChart
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import androidx.compose.foundation.layout.padding
import com.xah.common.ui.style.APP_HORIZONTAL_DP

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
                val allDayList = data.day.analyzeData.statisticalData.toList()
                val allMonthList = data.month.analyzeData.statisticalData.toList()

                val termInfo = parseSemesterInt(semester)

                val dayList = if (termInfo == null) allDayList else {
                    val start = termInfo.dateRangeStart
                    val end = termInfo.dateRangeEnd
                    allDayList.filter { it.first.substring(0, 7) >= start && it.first.substring(0, 7) < end }
                }
                val monthList = if (termInfo == null) allMonthList else {
                    val start = termInfo.dateRangeStart
                    val end = termInfo.dateRangeEnd
                    allMonthList.filter { it.first >= start && it.first < end }
                }

                val total = dayList.sumOf { it.second }
                val avg = if (dayList.isEmpty()) 0.0 else total / dayList.size
                val mAvg = if (monthList.isEmpty()) 0.0 else monthList.sumOf { it.second } / monthList.size

                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(
                        overlineContent = { Text(termInfo?.displayName ?: "全部学期") },
                        headlineContent = { Text("消费概览", style = MaterialTheme.typography.titleMedium) }
                    )
                    TransplantListItem(overlineContent = { Text("总消费") }, headlineContent = { Text("￥${formatDecimal(total, 2)}", style = MaterialTheme.typography.headlineMedium) })
                    TransplantListItem(overlineContent = { Text("日均") }, headlineContent = { Text("￥${formatDecimal(avg, 2)}", style = MaterialTheme.typography.headlineMedium) })
                    TransplantListItem(overlineContent = { Text("月均") }, headlineContent = { Text("￥${formatDecimal(mAvg, 2)}", style = MaterialTheme.typography.headlineMedium) })
                }

                Spacer(modifier = Modifier.height(CARD_NORMAL_DP))

                if (dayList.isNotEmpty()) {
                    CustomCard(color = cardNormalColor()) {
                        drawLineChart(dayList.take(30).reversed().map { BillMonth(it.first, it.second) }, modifier = Modifier.padding(APP_HORIZONTAL_DP))
                    }
                    Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                }

                if (monthList.isNotEmpty()) {
                    CustomCard(color = cardNormalColor()) {
                        drawLineChart(monthList.reversed().map { BillMonth(it.first, it.second) }, modifier = Modifier.padding(APP_HORIZONTAL_DP))
                    }
                    Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                }

                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(
                        overlineContent = { Text("消费预测") },
                        headlineContent = { Text("明日预计") },
                        trailingContent = { Text("￥${formatDecimal(data.day.predictData.predict, 2)}", style = MaterialTheme.typography.titleMedium) }
                    )
                    TransplantListItem(
                        headlineContent = { Text("下月预计") },
                        trailingContent = { Text("￥${formatDecimal(data.month.predictData.predict, 2)}", style = MaterialTheme.typography.titleMedium) }
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
