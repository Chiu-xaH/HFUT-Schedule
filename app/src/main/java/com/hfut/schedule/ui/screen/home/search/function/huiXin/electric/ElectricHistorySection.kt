package com.hfut.schedule.ui.screen.home.search.function.huiXin.electric

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.parse.roundOffString
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.screen.pager.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.pager.PageController
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.viewmodel.ui.ElectricChartMode
import com.hfut.schedule.viewmodel.ui.ElectricHistoryRange
import com.hfut.schedule.viewmodel.ui.ElectricHistoryUiState
import com.hfut.schedule.viewmodel.ui.ElectricHistoryViewModel
import com.xah.common.ui.component.chart.LineChart
import com.xah.common.ui.component.text.BottomTip
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ElectricHistorySection(
    meterKey: String?,
    roomName: String,
    viewModel: ElectricHistoryViewModel,
    onOpenRecords: () -> Unit
) {
    LaunchedEffect(meterKey, roomName) {
        viewModel.setMeterKey(meterKey, roomName)
    }

    val uiState by viewModel.uiState.collectAsState()

    DividerTextExpandedWith(text = "消费趋势", defaultIsExpanded = false, openBlurAnimation = false) {
        if (meterKey == null) {
            return@DividerTextExpandedWith
        }

        // 时间范围选择 - 始终显示
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = APP_HORIZONTAL_DP),
            horizontalArrangement = Arrangement.Start
        ) {
            FilterChip(
                selected = uiState.range == ElectricHistoryRange.SEVEN_DAYS,
                onClick = { viewModel.setRange(ElectricHistoryRange.SEVEN_DAYS) },
                label = { Text("7天") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(
                selected = uiState.range == ElectricHistoryRange.THIRTY_DAYS,
                onClick = { viewModel.setRange(ElectricHistoryRange.THIRTY_DAYS) },
                label = { Text("30天") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(
                selected = uiState.range == ElectricHistoryRange.ALL,
                onClick = { viewModel.setRange(ElectricHistoryRange.ALL) },
                label = { Text("全部") }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 根据数据显示不同内容
        when {
            uiState.totalRecordCount == 0 -> {
                // 从未保存过历史
                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(
                        headlineContent = { Text("暂无历史数据") },
                        supportingContent = {
                            Text("App 会在冷启动、刷新首页或手动查询电费成功后记录余额。消费趋势需要积累至少两次有效查询结果。")
                        },
                        leadingContent = { Icon(painterResource(R.drawable.info), null) }
                    )
                }
            }
            uiState.filteredRecords.size < 2 -> {
                // 当前范围数据不足，但有历史数据
                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(
                        headlineContent = { Text("当前时间范围内暂无足够数据") },
                        supportingContent = {
                            Text("请尝试切换到\"30天\"或\"全部\"查看。共 ${uiState.totalRecordCount} 条历史记录。")
                        },
                        leadingContent = { Icon(painterResource(R.drawable.info), null) }
                    )
                }
            }
            else -> {
                // 数据充足，显示图表和摘要

                // 图表模式选择
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = APP_HORIZONTAL_DP),
                    horizontalArrangement = Arrangement.Start
                ) {
                    FilterChip(
                        selected = uiState.chartMode == ElectricChartMode.BALANCE,
                        onClick = { viewModel.setChartMode(ElectricChartMode.BALANCE) },
                        label = { Text("余额趋势") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = uiState.chartMode == ElectricChartMode.CONSUMPTION_RATE,
                        onClick = { viewModel.setChartMode(ElectricChartMode.CONSUMPTION_RATE) },
                        label = { Text("每日消费") }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 图表
                when (uiState.chartMode) {
                    ElectricChartMode.BALANCE -> BalanceChart(uiState)
                    ElectricChartMode.CONSUMPTION_RATE -> ConsumptionRateChart(uiState)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 摘要信息
                SummaryCard(uiState, roomName)
            }
        }

        if (uiState.totalRecordCount > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            BalanceRecordEntry(uiState, onOpenRecords)
        }

        // 清除历史 - 只要当前电表有记录就显示
        if (uiState.totalRecordCount > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            var showClearDialog by remember { mutableStateOf(false) }
            CustomCard(color = cardNormalColor()) {
                TransplantListItem(
                    headlineContent = { Text("清除当前电表历史") },
                    supportingContent = { Text("仅删除本机保存的余额记录") },
                    leadingContent = { Icon(painterResource(R.drawable.delete), null) }
                )
                FilledTonalButton(
                    onClick = { showClearDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = APP_HORIZONTAL_DP, vertical = 8.dp)
                ) {
                    Text("清除历史")
                }
            }

            if (showClearDialog) {
                AlertDialog(
                    onDismissRequest = { showClearDialog = false },
                    title = { Text("清除电费历史") },
                    text = {
                        Text("将删除当前寝室电表保存在本机的所有余额记录。该操作不会影响学校账户余额，也不会清除寝室配置。")
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.clearHistory()
                            showClearDialog = false
                        }) {
                            Text("确认清除")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showClearDialog = false }) {
                            Text("取消")
                        }
                    }
                )
            }
        }

        BottomTip("余额数据仅保存在本机，根据两次查询之间的余额变化估算，不代表每天的实际精确用电")
    }
}

@Composable
private fun BalanceRecordEntry(
    uiState: ElectricHistoryUiState,
    onOpenRecords: () -> Unit
) {
    CustomCard(color = cardNormalColor()) {
        TransplantListItem(
            headlineContent = { Text("余额记录") },
            supportingContent = { Text("共 ${uiState.totalRecordCount} 条，点击分页查看") },
            leadingContent = { Icon(painterResource(R.drawable.receipt_long), null) }
        )
        FilledTonalButton(
            onClick = onOpenRecords,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = APP_HORIZONTAL_DP, vertical = 8.dp)
        ) {
            Text("查看记录")
        }
    }
}

@Composable
fun ElectricBalanceRecordPage(
    viewModel: ElectricHistoryViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    val records = remember(uiState.allRecords) {
        uiState.allRecords.sortedByDescending { it.sampledAt }
    }
    val pageSize = 8
    val totalPage = ((records.size + pageSize - 1) / pageSize).coerceAtLeast(1)
    var page by remember(records.size) { mutableIntStateOf(1) }
    val listState = rememberLazyListState()
    val pageRecords = remember(records, page) {
        records.drop((page - 1) * pageSize).take(pageSize)
    }

    LaunchedEffect(page, totalPage) {
        if (page > totalPage) page = totalPage
    }

    Box {
        LazyColumn(state = listState) {
            item {
                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(
                        headlineContent = { Text("${records.size} 条余额记录") },
                        supportingContent = { Text("每页 $pageSize 条，仅显示本机去重后的记录") },
                        leadingContent = { Icon(painterResource(R.drawable.receipt_long), null) }
                    )
                }
            }
            if (pageRecords.isEmpty()) {
                item {
                    CustomCard(color = cardNormalColor()) {
                        TransplantListItem(
                            headlineContent = { Text("暂无余额记录") },
                            supportingContent = { Text("查询电费成功后会自动记录余额") },
                            leadingContent = { Icon(painterResource(R.drawable.info), null) }
                        )
                    }
                }
            } else {
                items(pageRecords.size, key = { pageRecords[it].id }) { index ->
                    val record = pageRecords[index]
                    CustomCard(color = cardNormalColor()) {
                        TransplantListItem(
                            overlineContent = { Text(dateFormat.format(Date(record.sampledAt))) },
                            headlineContent = {
                                Text(
                                    "￥${record.remainingBalance.roundOffString(2)}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            },
                            supportingContent = { Text(record.roomName) },
                            leadingContent = { Icon(painterResource(R.drawable.flash_on), null) }
                        )
                    }
                }
            }
            item { PaddingForPageControllerButton() }
        }
        PageController(
            listState = listState,
            currentPage = page,
            onNextPage = { page = it },
            onPreviousPage = { page = it },
            range = 1 to totalPage,
            text = "第${page}/${totalPage}页"
        )
    }
}

@Composable
private fun BalanceChart(uiState: ElectricHistoryUiState) {
    val dateFormat = remember { SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()) }

    val chartData = remember(uiState.filteredRecords, dateFormat) {
        if (uiState.filteredRecords.isEmpty()) return@remember emptyMap()

        val records = uiState.filteredRecords.sortedBy { it.sampledAt }
        val result = linkedMapOf<String, Float>()

        for (record in records) {
            result[dateFormat.format(Date(record.sampledAt))] = record.remainingBalance.toFloat()
        }
        result
    }

    if (chartData.isNotEmpty()) {
        CustomCard(color = cardNormalColor()) {
            LineChart(
                data = chartData,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = APP_HORIZONTAL_DP),
                showLabel = true,
                title = "余额趋势 (元)",
                yAxisFormatter = { "￥${it.roundOffString(2)}" },
                xLabelSpacing = if (chartData.size > 3) chartData.size / 3 else 1
            )
        }
    }
}

@Composable
private fun ConsumptionRateChart(uiState: ElectricHistoryUiState) {
    val summary = uiState.summary ?: return
    val dateFormat = remember { SimpleDateFormat("MM/dd", Locale.getDefault()) }

    val chartData = remember(summary.dailyConsumptions, dateFormat) {
        if (summary.dailyConsumptions.isEmpty()) return@remember emptyMap()

        val result = linkedMapOf<String, Float>()
        for (item in summary.dailyConsumptions) {
            if (item.consumed > 0) {
                result[dateFormat.format(Date(item.dayStartAt))] = item.consumed.toFloat()
            }
        }
        result
    }

    if (chartData.isNotEmpty()) {
        CustomCard(color = cardNormalColor()) {
            LineChart(
                data = chartData,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .padding(horizontal = APP_HORIZONTAL_DP),
                showLabel = true,
                title = "每日估算消费 (元)",
                yAxisFormatter = { "￥${it.roundOffString(2)}" },
                xLabelSpacing = if (chartData.size > 3) chartData.size / 3 else 1
            )
        }
        BottomTip("按自然日汇总两次查询之间的余额变化，跨天区间会按时间比例分摊")
    } else {
        CustomCard(color = cardNormalColor()) {
            TransplantListItem(
                headlineContent = { Text("暂无有效每日消费") },
                supportingContent = { Text("需要至少两次余额下降的记录才能估算每日消费") },
                leadingContent = { Icon(painterResource(R.drawable.info), null) }
            )
        }
    }
}

@Composable
private fun SummaryCard(uiState: ElectricHistoryUiState, roomName: String) {
    val summary = uiState.summary ?: return
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    CustomCard(color = cardNormalColor()) {
        TransplantListItem(
            overlineContent = { Text("当前余额") },
            headlineContent = {
                val balance = summary.latestBalance ?: 0.0
                val balanceText = if (balance < 0) {
                    "￥${balance.roundOffString(2)} (余额不足)"
                } else {
                    "￥${balance.roundOffString(2)}"
                }
                Text(
                    balanceText,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        )

        AnimatedVisibility(visible = summary.firstSampleAt != null) {
            TransplantListItem(
                overlineContent = { Text("首次记录") },
                headlineContent = {
                    Text(summary.firstSampleAt?.let { dateFormat.format(Date(it)) } ?: "--")
                }
            )
        }

        AnimatedVisibility(visible = summary.latestSampleAt != null) {
            TransplantListItem(
                overlineContent = { Text("最近记录") },
                headlineContent = {
                    Text(summary.latestSampleAt?.let { dateFormat.format(Date(it)) } ?: "--")
                }
            )
        }

        if (summary.firstSampleAt != null && summary.latestSampleAt != null) {
            val days = (summary.latestSampleAt - summary.firstSampleAt) / (24 * 60 * 60 * 1000.0)
            TransplantListItem(
                overlineContent = { Text("已记录天数") },
                headlineContent = { Text("${days.roundOffString(1)} 天") }
            )
        }

        TransplantListItem(
            overlineContent = { Text("有效消费区间") },
            headlineContent = { Text("${summary.validIntervalCount} 个") }
        )

        if (uiState.recent7DaysConsumption > 0) {
            TransplantListItem(
                overlineContent = { Text("最近7天估算消费") },
                headlineContent = { Text("￥${uiState.recent7DaysConsumption.roundOffString(2)}") }
            )
        }

        AnimatedVisibility(visible = summary.averageDailyConsumption != null) {
            TransplantListItem(
                overlineContent = { Text("平均每天估算消费") },
                headlineContent = {
                    Text("￥${(summary.averageDailyConsumption ?: 0.0).roundOffString(2)}")
                }
            )
        }

        AnimatedVisibility(visible = summary.estimatedRemainingDays != null) {
            TransplantListItem(
                overlineContent = { Text("预计可使用天数") },
                headlineContent = {
                    Text("${(summary.estimatedRemainingDays ?: 0.0).roundOffString(1)} 天")
                }
            )
        }

        if (summary.increases.isNotEmpty()) {
            val lastIncrease = summary.increases.last()
            TransplantListItem(
                overlineContent = { Text("最近一次余额增加") },
                headlineContent = {
                    Text(
                        "￥${lastIncrease.amount.roundOffString(2)} (${
                            dateFormat.format(Date(lastIncrease.sampledAt))
                        })"
                    )
                }
            )
        }
    }
}
