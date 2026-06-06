package com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.schoolnet.SchoolNetMonthPayRecord
import com.hfut.schedule.logic.model.schoolnet.SchoolNetMonthPayResult
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import kotlinx.coroutines.launch
import java.util.Calendar

private fun formatFlow(mb: Double): String {
    return if (mb >= 1024) {
        "${formatDecimal(mb / 1024.0, 2)} GB"
    } else {
        "${formatDecimal(mb, 2)} MB"
    }
}

private fun formatDuration(minutes: Int): String {
    return if (minutes >= 60) {
        "${minutes / 60} 时 ${minutes % 60} 分"
    } else {
        "$minutes 分钟"
    }
}

@Composable
fun SchoolNetHistoryUsage(vm: NetWorkViewModel) {
    val scope = rememberCoroutineScope()
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    var selectedYear by remember { mutableIntStateOf(currentYear) }
    var isFirstQuery by remember { mutableStateOf(true) }
    val monthPayState by vm.schoolNetMonthPayResp.state.collectAsState()

    LaunchedEffect(monthPayState) {
        if (monthPayState is UiState.Error) {
            val msg = (monthPayState as UiState.Error).exception?.message.orEmpty()
            if (msg.contains("登录态已失效") && !isFirstQuery) {
                vm.loginAndGetSchoolNetMonthPay(selectedYear)
            }
        }
    }

    DividerTextExpandedWith(text = "历史用量查询", defaultIsExpanded = false) {
        CustomCard(color = cardNormalColor()) {
            Column(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("查询年份", style = MaterialTheme.typography.bodyLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(currentYear, currentYear - 1, currentYear - 2).forEach { year ->
                            FilledTonalButton(
                                onClick = {
                                    selectedYear = year
                                    scope.launch {
                                        vm.getSchoolNetMonthPayAfterLogin(year)
                                    }
                                },
                                enabled = selectedYear != year
                            ) {
                                Text("$year")
                            }
                        }
                    }
                }

                Spacer(Modifier.height(APP_HORIZONTAL_DP / 2))

                FilledTonalButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        scope.launch {
                            if (isFirstQuery) {
                                vm.loginAndGetSchoolNetMonthPay(selectedYear)
                                isFirstQuery = false
                            } else {
                                vm.getSchoolNetMonthPayAfterLogin(selectedYear)
                            }
                        }
                    }
                ) {
                    Text("查询 $selectedYear 年历史用量")
                }

                Spacer(Modifier.height(APP_HORIZONTAL_DP / 2))

                CommonNetworkScreen(
                    uiState = monthPayState,
                    isFullScreen = false,
                    onReload = {
                        vm.getSchoolNetMonthPayAfterLogin(selectedYear)
                    },
                    prepareContent = {
                        Text(
                            "点击上方按钮查询校园网历史用量",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = APP_HORIZONTAL_DP)
                        )
                    }
                ) {
                    val data = (monthPayState as UiState.Success).data
                    MonthPayContent(data)
                }
            }
        }
    }
}

@Composable
private fun MonthPayContent(result: SchoolNetMonthPayResult) {
    Column {
        result.year?.let {
            Text(
                "${it} 年度汇总",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        CustomCard(color = cardNormalColor()) {
            TransplantListItem(
                headlineContent = { Text(formatFlow(result.summary.flowMb)) },
                overlineContent = { Text("总流量") },
                leadingContent = {
                    Icon(painterResource(R.drawable.net), contentDescription = null)
                }
            )
            PaddingHorizontalDivider()
            TransplantListItem(
                headlineContent = { Text(formatDuration(result.summary.durationMinutes)) },
                overlineContent = { Text("总时长") },
                leadingContent = {
                    Icon(painterResource(R.drawable.schedule), contentDescription = null)
                }
            )
            PaddingHorizontalDivider()
            TransplantListItem(
                headlineContent = { Text("￥${formatDecimal(result.summary.baseFee, 2)}") },
                overlineContent = { Text("基本月租") },
                leadingContent = {
                    Icon(painterResource(R.drawable.paid), contentDescription = null)
                }
            )
            PaddingHorizontalDivider()
            TransplantListItem(
                headlineContent = { Text("￥${formatDecimal(result.summary.usageFee, 2)}") },
                overlineContent = { Text("时长/流量计费") },
                leadingContent = {
                    Icon(painterResource(R.drawable.paid), contentDescription = null)
                }
            )
        }

        if (result.records.isNotEmpty()) {
            Text(
                "每月明细",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = APP_HORIZONTAL_DP)
            )

            result.records.forEach { record ->
                MonthPayRecordCard(record)
            }
        }

        Spacer(Modifier.height(APP_HORIZONTAL_DP))
    }
}

@Composable
private fun MonthPayRecordCard(record: SchoolNetMonthPayRecord) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = APP_HORIZONTAL_DP, vertical = CARD_NORMAL_DP)
    ) {
        Column(modifier = Modifier.padding(APP_HORIZONTAL_DP)) {
            Text(
                "${record.startDate} 至 ${record.endDate}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "套餐: ${record.packageName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    record.billTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("使用时长", style = MaterialTheme.typography.labelSmall)
                    Text(formatDuration(record.durationMinutes), style = MaterialTheme.typography.bodyMedium)
                }
                Column {
                    Text("使用流量", style = MaterialTheme.typography.labelSmall)
                    Text(formatFlow(record.flowMb), style = MaterialTheme.typography.bodyMedium)
                }
                Column {
                    Text("基本月租", style = MaterialTheme.typography.labelSmall)
                    Text("￥${formatDecimal(record.baseFee, 2)}", style = MaterialTheme.typography.bodyMedium)
                }
                Column {
                    Text("计费", style = MaterialTheme.typography.labelSmall)
                    Text("￥${formatDecimal(record.usageFee, 2)}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
