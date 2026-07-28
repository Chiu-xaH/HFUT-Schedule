package com.hfut.schedule.ui.screen.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.helper.getCampusRegion
import com.hfut.schedule.network.api.model.response.dto.SchoolNetInfo
import com.hfut.schedule.network.api.model.response.html.SchoolNetSemesterUsageResult
import com.xah.common.logic.model.CampusRegion
import com.xah.common.logic.state.NetworkUiState
import com.hfut.schedule.logic.util.parse.SemesterParser

import com.hfut.schedule.logic.util.parse.roundOffString
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.xah.common.ui.component.status.LoadingUI
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.component.chart.LineChart
import com.xah.common.ui.component.chart.PieChart
import com.xah.common.ui.component.chart.PieChartData
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.logic.util.LogUtil

@Composable
fun LifeReportSection(vm: NetWorkViewModel, semester: Int, allSemesters: List<Int> = emptyList()) {
    val dormitoryInfo by vm.dormitoryFromCommunityResp.state.collectAsState()
    val dormitoryUsers by vm.dormitoryInfoFromCommunityResp.state.collectAsState()
    val weeklyScoresState by vm.allDormitoryScoresResp.state.collectAsState()
    val huiXinSchoolNetState by vm.huiXinSchoolNetInfoResp.state.collectAsState()
    val schoolNetUsageState by vm.schoolNetSemesterUsageResp.state.collectAsState()
    val communityToken by DataStoreManager.communityToken.collectAsState(initial = "")
    val huiXinAuth by DataStoreManager.huiXinAuth.collectAsState(initial = "")

    val termInfo = remember(semester) { SemesterParser.parseSemester(semester) }
    val isXuanCheng = remember { getCampusRegion() == CampusRegion.XUANCHENG }

    val refreshHuiXinSchoolNet: suspend () -> Unit = {
        try {
            if (huiXinAuth.isEmpty()) {
                vm.clearHuiXinSchoolNetInfo()
            } else {
                vm.getHuiXinSchoolNetInfo("bearer $huiXinAuth")
            }
        } catch (e: Exception) {
            LogUtil.error(e)
        }
    }

    LaunchedEffect(communityToken) {
        try {
            if (communityToken.isEmpty()) return@LaunchedEffect

            vm.dormitoryFromCommunityResp.clear()
            vm.getDormitory(communityToken)

            vm.dormitoryInfoFromCommunityResp.clear()
            vm.getDormitoryInfo(communityToken)
        } catch (e: Exception) {
            LogUtil.error(e)
        }
    }

    LaunchedEffect(semester, allSemesters, communityToken) {
        try {
            if (communityToken.isNotEmpty()) {
                val semStr = SemesterParser.parseSemesterForDormitory(semester)
                vm.getAllDormitoryScores(communityToken, semStr, semester)
            }
        } catch (e: Exception) {
            LogUtil.error(e)
        }

        try {
            if (semester > 0) {
                vm.loginAndGetSchoolNetSemesterUsage(semester)
            } else if (allSemesters.isNotEmpty()) {
                vm.loginAndGetSchoolNetAllSemestersUsage(allSemesters)
            }
        } catch (e: Exception) {
            LogUtil.error(e)
        }
    }

    LaunchedEffect(isXuanCheng, huiXinAuth) {
        if (isXuanCheng) refreshHuiXinSchoolNet()
    }

    DividerTextExpandedWith("生活报表") {
        when (dormitoryInfo) {
            is NetworkUiState.Success -> {
                val info = (dormitoryInfo as NetworkUiState.Success).data
                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(
                        overlineContent = { Text("宿舍信息") },
                        headlineContent = { Text("${info.campus} ${info.dormitory}", style = MaterialTheme.typography.titleMedium) }
                    )
                    TransplantListItem(
                        overlineContent = { Text("房间号") },
                        headlineContent = { Text(info.room, style = MaterialTheme.typography.headlineMedium) }
                    )
                }
            }
            is NetworkUiState.Error -> {
                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(headlineContent = { Text("暂无宿舍信息") }, supportingContent = { Text("请先在宿舍页面加载数据") })
                }
            }
            else -> {
                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(headlineContent = { Text("加载宿舍信息中...") })
                }
            }
        }

        when (dormitoryUsers) {
            is NetworkUiState.Success -> {
                val users = (dormitoryUsers as NetworkUiState.Success).data
                if (users.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                    CustomCard(color = cardNormalColor()) {
                        TransplantListItem(
                            overlineContent = { Text("室友") },
                            headlineContent = { Text("${users.size}人", style = MaterialTheme.typography.titleMedium) }
                        )
                        users.forEach { user ->
                            TransplantListItem(headlineContent = { Text(user.name) }, supportingContent = { Text(user.studentId) })
                        }
                    }
                }
            }
            is NetworkUiState.Loading -> {
                Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                CustomCard(color = cardNormalColor()) {
                    LoadingUI()
                }
            }
            else -> {}
        }

        when (weeklyScoresState) {
            is NetworkUiState.Loading -> {
                Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                CustomCard(color = cardNormalColor()) {
                    LoadingUI()
                }
            }
            is NetworkUiState.Success -> {
                val data = (weeklyScoresState as NetworkUiState.Success).data
                val weeks = data.weeks
                val latest = weeks.lastOrNull()

                if (weeks.size >= 2) {
                    val avgTotal = remember(weeks) { weeks.sumOf { it.total } / weeks.size }
                    val maxWeek = remember(weeks) { weeks.maxByOrNull { it.total } }
                    val minWeek = remember(weeks) { weeks.minByOrNull { it.total } }
                    val gradeDistribution = remember(weeks) {
                        val gradeMap = mutableMapOf<String, Int>()
                        for (week in weeks) {
                            val gradeItem = week.scores.find { it.title == "等级" }
                            val grade = gradeItem?.value
                            if (!grade.isNullOrBlank() && grade != "/") {
                                gradeMap[grade] = (gradeMap[grade] ?: 0) + 1
                            }
                        }
                        gradeMap.map { PieChartData(it.key, it.value.toFloat()) }
                    }

                    Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                    CustomCard(color = cardNormalColor()) {
                        Row(modifier = Modifier.padding(APP_HORIZONTAL_DP)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("评分概览", style = MaterialTheme.typography.titleMedium)
                                Text(avgTotal.roundOffString(2), style = MaterialTheme.typography.headlineMedium)
                                Text("学期均分", style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.height(8.dp))
                                maxWeek?.let {
                                    Text("最高周 第${it.week}周 ${it.total.roundOffString(2)}", style = MaterialTheme.typography.bodySmall)
                                }
                                minWeek?.let {
                                    Text("最低周 第${it.week}周 ${it.total.roundOffString(2)}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            if (gradeDistribution.isNotEmpty()) {
                                Column(modifier = Modifier.weight(2f), horizontalAlignment = Alignment.CenterHorizontally) {
                                    PieChart(
                                        data = gradeDistribution,
                                        modifier = Modifier.size(120.dp),
                                        title = ""
                                    )
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                        gradeDistribution.forEach { item ->
                                            Text(
                                                "${item.label} ${item.value.toInt()}次",
                                                style = MaterialTheme.typography.bodySmall,
                                                modifier = Modifier.padding(horizontal = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (latest != null) {
                    Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                    CustomCard(color = cardNormalColor()) {
                        TransplantListItem(
                            overlineContent = {
                                Text("${termInfo ?: ""} · 第${latest.week}周")
                            },
                            headlineContent = { Text("最新卫生评分", style = MaterialTheme.typography.titleMedium) }
                        )
                        for (index in latest.scores.indices step 2) {
                            val item = latest.scores[index]
                            Row {
                                TransplantListItem(
                                    overlineContent = { Text(item.title) },
                                    headlineContent = { Text(item.value, style = MaterialTheme.typography.titleMedium) },
                                    modifier = Modifier.weight(1f)
                                )
                                if (index + 1 < latest.scores.size) {
                                    val item2 = latest.scores[index + 1]
                                    TransplantListItem(
                                        overlineContent = { Text(item2.title) },
                                        headlineContent = { Text(item2.value, style = MaterialTheme.typography.titleMedium) },
                                        modifier = Modifier.weight(1f)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                if (weeks.size >= 2) {
                    val totalTrend = remember(weeks) {
                        weeks.associate { "第${it.week}周" to it.total.toFloat() }
                    }

                    Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                    CustomCard(color = cardNormalColor()) {
                        TransplantListItem(
                            overlineContent = { Text("学期趋势") },
                            headlineContent = {
                                Text("共 ${weeks.size} 周数据", style = MaterialTheme.typography.titleMedium)
                            }
                        )

                        if (totalTrend.canDrawFlowChart()) {
                            LineChart(
                                data = totalTrend,
                                showLabel = true,
                                modifier = Modifier
                                    .padding(horizontal = APP_HORIZONTAL_DP)
                                    .height(180.dp),
                                xLabelSpacing = if (weeks.size > 15) 3 else 1
                            )
                        } else {
                            TransplantListItem(
                                headlineContent = { Text("暂无可绘制的趋势变化") },
                                supportingContent = { Text("当前多周评分相同或数据不足，暂不展示折线图") }
                            )
                        }
                    }

                    val rankingTrend = remember(weeks) {
                        weeks.mapNotNull { week ->
                            val rankingItem = week.scores.find { it.title == "排名" }
                            val ranking = rankingItem?.value?.toDoubleOrNull()
                            if (ranking != null) "第${week.week}周" to ranking.toFloat() else null
                        }.toMap()
                    }

                    if (rankingTrend.size >= 2) {
                        Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                        CustomCard(color = cardNormalColor()) {
                            TransplantListItem(
                                overlineContent = { Text("排名趋势") },
                                headlineContent = {
                                    Text("共 ${rankingTrend.size} 周数据", style = MaterialTheme.typography.titleMedium)
                                }
                            )

                            if (rankingTrend.canDrawFlowChart()) {
                                LineChart(
                                    data = rankingTrend,
                                    showLabel = true,
                                    modifier = Modifier
                                        .padding(horizontal = APP_HORIZONTAL_DP)
                                        .height(180.dp),
                                    xLabelSpacing = if (rankingTrend.size > 15) 3 else 1
                                )
                            } else {
                                TransplantListItem(
                                    headlineContent = { Text("暂无可绘制的排名变化") },
                                    supportingContent = { Text("当前多周排名相同或数据不足，暂不展示折线图") }
                                )
                            }
                        }
                    }
                }
            }
            is NetworkUiState.Error -> {
                Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(headlineContent = { Text("暂无卫生评分数据") })
                }
            }
            else -> {}
        }

        if (isXuanCheng) {
            HuiXinSchoolNetMonthlyCard(
                state = huiXinSchoolNetState,
                onReload = refreshHuiXinSchoolNet
            )
        }
        SchoolNetUsageReportCard(state = schoolNetUsageState)
        ReportDataSourceText(
            buildList {
                if (
                    dormitoryInfo is NetworkUiState.Success ||
                    dormitoryUsers is NetworkUiState.Success ||
                    weeklyScoresState is NetworkUiState.Success
                ) {
                    add("智慧社区")
                }
                if (isXuanCheng && huiXinSchoolNetState is NetworkUiState.Success) add("慧新易校")
                if (schoolNetUsageState is NetworkUiState.Success) add("校园网自服务接口")
            }
        )
    }
}

@Composable
private fun HuiXinSchoolNetMonthlyCard(
    state: NetworkUiState<SchoolNetInfo>,
    onReload: suspend () -> Unit
) {
    if (state == NetworkUiState.Prepare) return

    Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
    CustomCard(color = cardNormalColor()) {
        CommonNetworkScreen(
            uiState = state,
            isFullScreen = false,
            modifier = Modifier.fillMaxWidth(),
            onReload = onReload
        ) {
            val data = (state as NetworkUiState.Success).data
            Column {
                TransplantListItem(
                    overlineContent = { Text("本月校园网使用 · 慧新易校") },
                    headlineContent = {
                        Text(formatHuiXinSchoolNetFlow(data.flow), style = MaterialTheme.typography.headlineMedium)
                    },
                    supportingContent = { Text("本期已使用流量") }
                )
                TransplantListItem(
                    overlineContent = { Text("网费储值余额") },
                    headlineContent = { Text("￥${data.fee}", style = MaterialTheme.typography.titleMedium) }
                )
            }
        }
    }
}

@Composable
private fun SchoolNetUsageReportCard(
    state: NetworkUiState<SchoolNetSemesterUsageResult>
) {
    Spacer(modifier = Modifier.height(CARD_NORMAL_DP))

    when (state) {
        is NetworkUiState.Loading -> {
            CustomCard(color = cardNormalColor()) {
                LoadingUI()
            }
        }

        is NetworkUiState.Success -> {
            val data = state.data
            val records = data.records
            val isAllSemesters = data.semester == 0
            val periodLabel = if (isAllSemesters) "四年" else "本学期"

            if (records.isEmpty()) {
                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(
                        overlineContent = { Text("校园网使用情况") },
                        headlineContent = { Text("暂无${periodLabel}校园网用量数据") },
                        supportingContent = { Text("${data.startYearMonth} 至 ${data.endYearMonth}") }
                    )
                }
                return
            }

            val avgFlow = data.totalFlowMb / records.size
            val maxRecord = records.maxByOrNull { it.flowMb }

            val flowTrend = remember(records) {
                records.associate { record ->
                    val label = record.startDate.substring(5, 7).trimStart('0') + "月"
                    label to record.flowMb.toFloat()
                }
            }

            CustomCard(color = cardNormalColor()) {
                TransplantListItem(
                    overlineContent = {
                        Text("校园网使用情况 · ${data.startYearMonth} 至 ${data.endYearMonth}")
                    },
                    headlineContent = {
                        Text(formatSchoolNetFlow(data.totalFlowMb), style = MaterialTheme.typography.headlineMedium)
                    },
                    supportingContent = { Text("${periodLabel}总流量") }
                )

                Row {
                    TransplantListItem(
                        overlineContent = { Text("总时长") },
                        headlineContent = {
                            Text(formatSchoolNetDuration(data.totalDurationMinutes), style = MaterialTheme.typography.titleMedium)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    TransplantListItem(
                        overlineContent = { Text("月均流量") },
                        headlineContent = {
                            Text(formatSchoolNetFlow(avgFlow), style = MaterialTheme.typography.titleMedium)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                maxRecord?.let {
                    TransplantListItem(
                        overlineContent = { Text("使用最多月份") },
                        headlineContent = {
                            Text("${it.startDate.take(7)} · ${formatSchoolNetFlow(it.flowMb)}", style = MaterialTheme.typography.titleMedium)
                        },
                        supportingContent = { Text("使用时长 ${formatSchoolNetDuration(it.durationMinutes)}") }
                    )
                }

                if (flowTrend.canDrawFlowChart()) {
                    LineChart(
                        data = flowTrend,
                        showLabel = true,
                        modifier = Modifier
                            .padding(horizontal = APP_HORIZONTAL_DP)
                            .height(180.dp),
                        xLabelSpacing = 1
                    )
                } else {
                    TransplantListItem(
                        headlineContent = { Text("暂无可绘制的流量趋势") },
                        supportingContent = { Text("当前月份数据不足或流量变化过小") }
                    )
                }

                records.forEach { record ->
                    TransplantListItem(
                        overlineContent = { Text("${record.startDate} 至 ${record.endDate}") },
                        headlineContent = {
                            Text(formatSchoolNetFlow(record.flowMb), style = MaterialTheme.typography.titleMedium)
                        },
                        supportingContent = {
                            Text("使用时长 ${formatSchoolNetDuration(record.durationMinutes)} · 出账 ${record.billTime}")
                        }
                    )
                }
            }
        }

        is NetworkUiState.Error -> {
            CustomCard(color = cardNormalColor()) {
                TransplantListItem(
                    overlineContent = { Text("校园网使用情况") },
                    headlineContent = { Text("暂无校园网使用数据") },
                    supportingContent = { Text("当前网络无法访问自服务接口,请切换到校园网后重试") }
                )
            }
        }

        else -> {}
    }
}

private fun formatSchoolNetFlow(mb: Double): String = if (mb >= 1024) "${(mb / 1024.0).roundOffString(2)} GB" else "${mb.roundOffString(2)} MB"

private fun formatHuiXinSchoolNetFlow(flow: String): String {
    val mb = flow.toDoubleOrNull() ?: return "$flow MB"
    return formatSchoolNetFlow(mb)
}

private fun formatSchoolNetDuration(minutes: Int): String {
    val hours = minutes / 60
    val leftMinutes = minutes % 60
    return when {
        hours > 0 && leftMinutes > 0 -> "${hours}小时${leftMinutes}分钟"
        hours > 0 -> "${hours}小时"
        else -> "${minutes}分钟"
    }
}

private fun Map<String, Float>.canDrawFlowChart(): Boolean {
    val values = values.filter { it.isFinite() }
    return values.size >= 2 && values.distinct().size >= 2
}
