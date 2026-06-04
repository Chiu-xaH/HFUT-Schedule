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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.model.community.DormitoryWeeklyScores
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.SemesterParser
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.component.chart.LineChart
import com.xah.common.ui.component.chart.PieChart
import com.xah.common.ui.component.chart.PieChartData
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.shared.LogUtil

@Composable
fun LifeReportSection(vm: NetWorkViewModel, semester: Int) {
    val dormitoryInfo by vm.dormitoryFromCommunityResp.state.collectAsState()
    val dormitoryUsers by vm.dormitoryInfoFromCommunityResp.state.collectAsState()

    val termInfo = remember(semester) { SemesterParser.parseSemester(semester) }

    LaunchedEffect(Unit) {
        try {
            val token = prefs.getString("TOKEN", "") ?: ""
            if (token.isNotEmpty()) {
                vm.dormitoryFromCommunityResp.clear()
                vm.getDormitory(token)
                vm.dormitoryInfoFromCommunityResp.clear()
                vm.getDormitoryInfo(token)
            }
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
        }
    }

    val weeklyScores by produceState<DormitoryWeeklyScores?>(initialValue = null, semester) {
        value = null
        try {
            val token = prefs.getString("TOKEN", "") ?: ""
            if (token.isEmpty()) return@produceState
            val semStr = SemesterParser.parseSemesterForDormitory(semester)
            val result = vm.getAllDormitoryScores(token, semStr, semester)
            value = result
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            LogUtil.error(e)
        }
    }

    DividerTextExpandedWith("生活报表") {
        when (dormitoryInfo) {
            is UiState.Success -> {
                val info = (dormitoryInfo as UiState.Success).data
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
            is UiState.Error -> {
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
            is UiState.Success -> {
                val users = (dormitoryUsers as UiState.Success).data
                if (users.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                    CustomCard(color = cardNormalColor()) {
                        TransplantListItem(
                            overlineContent = { Text("室友") },
                            headlineContent = { Text("${users.size}人", style = MaterialTheme.typography.titleMedium) }
                        )
                        users.forEach { user ->
                            TransplantListItem(headlineContent = { Text(user.realname) }, supportingContent = { Text(user.username) })
                        }
                    }
                }
            }
            else -> {}
        }

        if (weeklyScores != null) {
            val data = weeklyScores!!
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
                            Text(formatDecimal(avgTotal, 1), style = MaterialTheme.typography.headlineMedium)
                            Text("学期均分", style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(8.dp))
                            maxWeek?.let {
                                Text("最高周 第${it.week}周 ${formatDecimal(it.total, 1)}", style = MaterialTheme.typography.bodySmall)
                            }
                            minWeek?.let {
                                Text("最低周 第${it.week}周 ${formatDecimal(it.total, 1)}", style = MaterialTheme.typography.bodySmall)
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
                        headlineContent = { Text("共 ${weeks.size} 周数据", style = MaterialTheme.typography.titleMedium) }
                    )
                    LineChart(
                        data = totalTrend,
                        showLabel = true,
                        modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP).height(180.dp),
                        xLabelSpacing = if (weeks.size > 15) 3 else 1
                    )
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
                            headlineContent = { Text("共 ${rankingTrend.size} 周数据", style = MaterialTheme.typography.titleMedium) }
                        )
                        LineChart(
                            data = rankingTrend,
                            showLabel = true,
                            modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP).height(180.dp),
                            xLabelSpacing = if (rankingTrend.size > 15) 3 else 1
                        )
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
            CustomCard(color = cardNormalColor()) {
                TransplantListItem(headlineContent = { Text("暂无卫生评分数据") })
            }
        }
    }
}
