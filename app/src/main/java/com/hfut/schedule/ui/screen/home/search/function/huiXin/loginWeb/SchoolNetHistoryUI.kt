package com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.xah.common.logic.state.NetworkUiState

import com.hfut.schedule.logic.util.parse.roundOffString
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.input.WheelPicker
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.pager.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.status.PrepareSearchIcon
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.logic.util.LogUtil
import com.xah.common.ui.component.text.BottomTip
import kotlinx.coroutines.launch
import java.util.Calendar

private fun formatFlow(mb: Double): String {
    return if (mb >= 1024) {
        "${(mb / 1024.0).roundOffString(2)} GB"
    } else {
        "${mb.roundOffString(2)} MB"
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
    var selectedYear by remember { mutableStateOf<Int?>(null) }
    var isFirstQuery by remember { mutableStateOf(true) }
    val monthPayState by vm.schoolNetMonthPayResp.state.collectAsState()
    val refreshNetwork = suspend m@ {
        if(selectedYear == null) {
            return@m
        }
        vm.schoolNetMonthPayResp.clear()
        if (isFirstQuery) {
            vm.loginAndGetSchoolNetMonthPay(selectedYear!!)
            isFirstQuery = false
        } else {
            vm.getSchoolNetMonthPayAfterLogin(selectedYear!!)
        }
    }
    val initValue = { offset : Int ->
        selectedYear = DateTimeManager.Date_yyyy.toInt() + offset
    }

    LaunchedEffect(monthPayState) {
        if(selectedYear == null) {
            return@LaunchedEffect
        }
        if (monthPayState is NetworkUiState.Error) {
            val msg = (monthPayState as NetworkUiState.Error).exception?.message.orEmpty()
            if (msg.contains("登录态已失效") && !isFirstQuery ) {
                vm.loginAndGetSchoolNetMonthPay(selectedYear!!)
            }
        }
    }

    LaunchedEffect(selectedYear) {
        if(selectedYear == null) {
            vm.schoolNetMonthPayResp.emitPrepare()
        } else {
            refreshNetwork()
        }
    }

    DividerTextExpandedWith(text = "历史用量") {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP)) {
            FilledTonalButton(
                onClick = {
                    if(selectedYear == null) {
                        initValue(-1)
                    } else {
                        selectedYear = selectedYear!! - 1
                    }
                },
                modifier = Modifier.weight(1/3f)
            ) {
                Icon(painterResource(R.drawable.keyboard_arrow_left),null)
            }
            Spacer(Modifier.width(APP_HORIZONTAL_DP/2))
            FilledTonalButton(
                onClick = {
                    initValue(0)
                },
                modifier = Modifier.weight(1/3f)
            ) {
                Text(
                    selectedYear?.let { "${it}年" } ?: "${DateTimeManager.Date_yyyy}年"
                )
            }
            Spacer(Modifier.width(APP_HORIZONTAL_DP/2))
            FilledTonalButton(
                onClick = {
                    if(selectedYear == null) {
                        initValue(+1)
                    } else {
                        selectedYear = selectedYear!! + 1
                    }
                },
                modifier = Modifier.weight(1/3f)
            ) {
                Icon(painterResource(R.drawable.keyboard_arrow_right),null)
            }
        }
        CommonNetworkScreen(
            uiState = monthPayState,
            isFullScreen = false,
            onReload = refreshNetwork,
            // 特殊处理 为节省空间
            prepareContent = { BottomTip("点按年份开始搜素") }
        ) {
            val data = (monthPayState as NetworkUiState.Success).data
            MonthPayContent(data)
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
                headlineContent = { Text("￥${result.summary.baseFee.roundOffString(2)}") },
                overlineContent = { Text("基本月租") },
                leadingContent = {
                    Icon(painterResource(R.drawable.paid), contentDescription = null)
                }
            )
            PaddingHorizontalDivider()
            TransplantListItem(
                headlineContent = { Text("￥${result.summary.usageFee.roundOffString(2)}") },
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
                    Text("￥${record.baseFee.roundOffString(2)}", style = MaterialTheme.typography.bodyMedium)
                }
                Column {
                    Text("计费", style = MaterialTheme.typography.labelSmall)
                    Text("￥${record.usageFee.roundOffString(2)}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
