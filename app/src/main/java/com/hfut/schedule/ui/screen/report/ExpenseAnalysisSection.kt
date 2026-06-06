package com.hfut.schedule.ui.screen.report

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.SemesterParser
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.xah.shared.LogUtil
import com.xah.common.ui.component.status.LoadingUI
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.component.chart.BarChart
import com.xah.common.ui.component.chart.LineChart
import com.xah.common.ui.component.chart.PieChart
import com.xah.common.ui.component.chart.PieChartData
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

private fun classifyMeal(resume: String, time: String): String {
    val hour = try {
        time.substringAfter(" ").substringBefore(":").toIntOrNull() ?: -1
    } catch (_: Exception) { -1 }
    val name = resume.lowercase()
    return when {
        name.contains("充值") || name.contains("补助") || name.contains("转账") -> "其他"
        hour in 6..10 -> "早餐"
        hour in 11..13 -> "午餐"
        hour in 14..16 -> "下午茶"
        hour in 17..19 -> "晚餐"
        hour in 20..23 -> "夜宵"
        else -> "其他"
    }
}

@Composable
fun ExpenseAnalysisSection(vm: NetWorkViewModel, semester: Int, periodLabel: String = "本学期") {
    val billState by vm.huiXinBillResult.state.collectAsState()
    val predictedState by vm.cardPredictedResponse.state.collectAsState()

    LaunchedEffect(Unit) {
        val auth = prefs.getString("auth", "") ?: ""
        if (auth.isNotEmpty()) {
            if (billState !is UiState.Success) {
                vm.huiXinBillResult.clear()
                vm.getCardBill("bearer $auth", 1, 500)
            }
            if (predictedState !is UiState.Success) {
                vm.cardPredictedResponse.clear()
                vm.getCardPredicted("bearer $auth")
            }
        }
    }

    DividerTextExpandedWith("消费分析") {
        when (billState) {
            is UiState.Success -> {
                val allRecords = (billState as UiState.Success).data.records.filter { it.turnoverType == "消费" }

                val dateRange = remember(semester) { SemesterParser.getSemesterDateRange(semester) }
                val records = remember(allRecords, semester) {
                    if (dateRange == null || semester == 0) allRecords
                    else allRecords.filter {
                        try {
                            val ym = it.effectdateStr.substring(0, 7)
                            ym in dateRange.startYearMonth..dateRange.endYearMonth
                        } catch (e: Exception) {
                            LogUtil.error(e)
                            false
                        }
                    }
                }

                if (records.isEmpty()) {
                    CustomCard(color = cardNormalColor()) {
                        TransplantListItem(headlineContent = { Text("暂无消费记录") })
                    }
                    return@DividerTextExpandedWith
                }

                // 基础统计
                val totalAmount = remember(records) { records.sumOf { (it.tranamt ?: 0) / 100.0 } }
                val avgPerTransaction = remember(records) { if (records.isEmpty()) 0.0 else totalAmount / records.size }
                
                val activeDays = remember(records) { records.map { it.effectdateStr.substringBefore(" ") }.distinct().size }
                val dailyAvg = remember(totalAmount, activeDays) { if (activeDays > 0) totalAmount / activeDays else 0.0 }
                
                val activeMonths = remember(records) { records.map { it.effectdateStr.substringBeforeLast("-") }.distinct().size }
                val monthlyAvg = remember(totalAmount, activeMonths) { if (activeMonths > 0) totalAmount / activeMonths else 0.0 }

                val sorted = remember(records) { records.sortedBy { it.jndatetimeStr } }
                val earliest = sorted.first()
                val latest = sorted.last()
                val days = remember(sorted) {
                    try {
                        val f = LocalDate.parse(earliest.effectdateStr.substringBefore(" "))
                        val l = LocalDate.parse(latest.effectdateStr.substringBefore(" "))
                        ChronoUnit.DAYS.between(f, l).toInt()
                    } catch (_: Exception) { 0 }
                }

                // 单笔最高消费
                val highestRecord = remember(records) { records.maxByOrNull { (it.tranamt ?: 0) } }
                val highestAmount = remember(highestRecord) { ((highestRecord?.tranamt ?: 0) / 100.0) }

                // 周几消费最多
                val weekdayStats = remember(records) {
                    val dayMap = mutableMapOf<Int, Double>()
                    for (record in records) {
                        try {
                            val date = LocalDate.parse(record.effectdateStr.substringBefore(" "))
                            val dayOfWeek = date.dayOfWeek.value // 1=Monday, 7=Sunday
                            dayMap[dayOfWeek] = (dayMap[dayOfWeek] ?: 0.0) + ((record.tranamt ?: 0) / 100.0)
                        } catch (_: Exception) {}
                    }
                    dayMap.toList().sortedByDescending { it.second }
                }
                val busiestWeekday = weekdayStats.firstOrNull()

                // 哪个月消费最多
                val monthStats = remember(records) {
                    val monthMap = mutableMapOf<Int, Double>()
                    for (record in records) {
                        try {
                            val date = LocalDate.parse(record.effectdateStr.substringBefore(" "))
                            val month = date.monthValue
                            monthMap[month] = (monthMap[month] ?: 0.0) + ((record.tranamt ?: 0) / 100.0)
                        } catch (_: Exception) {}
                    }
                    monthMap.toList().sortedByDescending { it.second }
                }
                val busiestMonth = monthStats.firstOrNull()

                // 早午晚餐分类
                val mealGroups = remember(records) {
                    records.groupBy { classifyMeal(it.resume, it.jndatetimeStr) }
                }
                val mealStats = remember(mealGroups) {
                    listOf("早餐", "午餐", "下午茶", "晚餐", "夜宵").mapNotNull { meal ->
                        val items = mealGroups[meal] ?: return@mapNotNull null
                        val total = items.sumOf { (it.tranamt ?: 0) / 100.0 }
                        Triple(meal, items.size, total)
                    }.sortedByDescending { it.third }
                }

                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(
                        overlineContent = { Text(SemesterParser.parseSemester(semester) ?: "全部学期") },
                        headlineContent = { Text("餐饮消费分析", style = MaterialTheme.typography.titleMedium) }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            mealStats.forEach { (meal, count, amount) ->
                                TransplantListItem(
                                    overlineContent = { Text("$meal ${count}次", style = MaterialTheme.typography.bodySmall) },
                                    headlineContent = { Text("￥${formatDecimal(amount, 0)}", style = MaterialTheme.typography.titleSmall) },
                                    usePadding = false,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                        Box(modifier = Modifier.weight(2f)) {
                            PieChart(
                                data = mealStats.map { (meal, _, amount) -> PieChartData(meal, amount.toFloat()) },
                                modifier = Modifier.fillMaxWidth().height(200.dp),
                                title = "比例"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(CARD_NORMAL_DP))

                // 最常消费地点
                val merchants = remember(records) {
                    records.groupBy { it.resume.substringBefore("-").replace("有限公司", "").trim() }
                        .mapValues { (_, v) -> v.size to v.sumOf { (it.tranamt ?: 0) / 100.0 } }
                        .toList().sortedByDescending { it.second.first }.take(5)
                }

                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(
                        overlineContent = { Text("最常消费") },
                        headlineContent = { Text("Top 5 消费地点", style = MaterialTheme.typography.titleMedium) }
                    )
                    merchants.forEach { (name, pair) ->
                        TransplantListItem(
                            overlineContent = { Text("${pair.first}次") },
                            headlineContent = { Text(name) },
                            trailingContent = { Text("￥${formatDecimal(pair.second, 2)}") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(CARD_NORMAL_DP))

                // 消费概览
                val earliestLatestByTime = remember(records) {
                    try {
                        records.mapNotNull { record ->
                            val time = record.jndatetimeStr.substringAfter(" ").substringBeforeLast(":")
                            val date = record.effectdateStr.substringBefore(" ")
                            if (time.isNotEmpty() && date.isNotEmpty()) Triple(record, date, time) else null
                        }.let { triples ->
                            val earliest = triples.minByOrNull { it.third }
                            val latest = triples.maxByOrNull { it.third }
                            Pair(earliest, latest)
                        }
                    } catch (_: Exception) { null to null }
                }

                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(
                        overlineContent = { Text("总消费") },
                        headlineContent = { Text("￥${formatDecimal(totalAmount, 2)}", style = MaterialTheme.typography.headlineMedium) },
                        supportingContent = { Text("共 ${records.size} 笔 | 跨越 $days 天") }
                    )
                    Row(modifier = Modifier.fillMaxWidth()) {
                        TransplantListItem(
                            overlineContent = { Text("日均") },
                            headlineContent = { Text("￥${formatDecimal(dailyAvg, 2)}", style = MaterialTheme.typography.titleMedium) },
                            modifier = Modifier.weight(1f)
                        )
                        TransplantListItem(
                            overlineContent = { Text("月均") },
                            headlineContent = { Text("￥${formatDecimal(monthlyAvg, 2)}", style = MaterialTheme.typography.titleMedium) },
                            modifier = Modifier.weight(1f)
                        )
                        TransplantListItem(
                            overlineContent = { Text("单笔均") },
                            headlineContent = { Text("￥${formatDecimal(avgPerTransaction, 2)}", style = MaterialTheme.typography.titleMedium) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (highestRecord != null) {
                        TransplantListItem(
                            overlineContent = { Text("单笔最高") },
                            headlineContent = { Text("￥${formatDecimal(highestAmount, 2)}", style = MaterialTheme.typography.titleMedium) },
                            supportingContent = { Text("${highestRecord.resume.substringBefore("-").take(15)} | ${highestRecord.jndatetimeStr}") }
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        earliestLatestByTime.first?.let { (_, date, time) ->
                            TransplantListItem(
                                overlineContent = { Text("最早消费") },
                                headlineContent = { Text("$date $time", style = MaterialTheme.typography.bodySmall) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        earliestLatestByTime.second?.let { (_, date, time) ->
                            TransplantListItem(
                                overlineContent = { Text("最晚消费") },
                                headlineContent = { Text("$date $time", style = MaterialTheme.typography.bodySmall) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(CARD_NORMAL_DP))

                if (predictedState is UiState.Success) {
                    val predictData = (predictedState as UiState.Success).data
                    CustomCard(color = cardNormalColor()) {
                        androidx.compose.foundation.layout.Row(modifier = Modifier.fillMaxWidth()) {
                            TransplantListItem(
                                overlineContent = { Text("明日预计") },
                                headlineContent = { Text("￥${formatDecimal(predictData.day.predictData.predict, 2)}", style = MaterialTheme.typography.titleMedium) },
                                modifier = Modifier.weight(1f)
                            )
                            TransplantListItem(
                                overlineContent = { Text("下月预计") },
                                headlineContent = { Text("￥${formatDecimal(predictData.month.predictData.predict, 2)}", style = MaterialTheme.typography.titleMedium) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                }

                // 每学期各周最早/最晚消费时间折线图
                val semesterWeekTimeStats = remember(records) {
                    try {
                        val sortedByDate = records.mapNotNull {
                            val timeStr = it.jndatetimeStr.substringAfter(" ")
                            val dateStr = it.effectdateStr.substringBefore(" ")
                            if (timeStr.isNotEmpty() && dateStr.isNotEmpty()) {
                                val date = LocalDate.parse(dateStr)
                                val hour = timeStr.substringBefore(":").toDoubleOrNull() ?: return@mapNotNull null
                                val minute = timeStr.substringAfter(":").substringBefore(":").toDoubleOrNull() ?: 0.0
                                val timeDecimal = hour + minute / 60.0
                                Triple(it, date, timeDecimal)
                            } else null
                        }.sortedBy { it.second }

                        if (sortedByDate.isEmpty()) Pair(emptyMap(), emptyMap())
                        else {
                            val firstDate = sortedByDate.first().second
                            val earliestMap = mutableMapOf<Int, Double>()
                            val latestMap = mutableMapOf<Int, Double>()
                            
                            for (item in sortedByDate) {
                                val weekNum = (ChronoUnit.DAYS.between(firstDate, item.second) / 7 + 1).toInt()
                                val timeDecimal = item.third
                                
                                val curEarliest = earliestMap[weekNum]
                                if (curEarliest == null || timeDecimal < curEarliest) {
                                    earliestMap[weekNum] = timeDecimal
                                }
                                val curLatest = latestMap[weekNum]
                                if (curLatest == null || timeDecimal > curLatest) {
                                    latestMap[weekNum] = timeDecimal
                                }
                            }
                            
                            val earliestChart = earliestMap.entries.sortedBy { it.key }.associate { it.key.toString() to it.value.toFloat() }
                            val latestChart = latestMap.entries.sortedBy { it.key }.associate { it.key.toString() to it.value.toFloat() }
                            
                            Pair(earliestChart, latestChart)
                        }
                    } catch (_: Exception) { Pair(emptyMap(), emptyMap()) }
                }

                val (earliestTimeData, latestTimeData) = semesterWeekTimeStats
                if (earliestTimeData.isNotEmpty()) {
                    val timeFormatter: (Float) -> String = {
                        val h = it.toInt()
                        val m = ((it - h) * 60).toInt()
                        "${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}"
                    }

                    CustomCard(color = cardNormalColor()) {
                        TransplantListItem(
                            overlineContent = { Text("各周消费时间分布 (横轴:周)") },
                            headlineContent = { Text("最早消费时间", style = MaterialTheme.typography.titleMedium) }
                        )
                        LineChart(
                            data = earliestTimeData,
                            showLabel = true,
                            modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP).height(120.dp),
                            xLabelSpacing = 4,
                            yAxisFormatter = timeFormatter
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TransplantListItem(
                            headlineContent = { Text("最晚消费时间", style = MaterialTheme.typography.titleMedium) }
                        )
                        LineChart(
                            data = latestTimeData,
                            showLabel = true,
                            modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP).height(120.dp),
                            xLabelSpacing = 4,
                            yAxisFormatter = timeFormatter
                        )
                    }

                    Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                }

                // 周几消费最多
                val weekdayChartData = remember(weekdayStats) {
                    (1..7).associate { dayOfWeek ->
                        val dayName = DayOfWeek.of(dayOfWeek).getDisplayName(TextStyle.SHORT, Locale.CHINESE)
                        val amount = weekdayStats.find { it.first == dayOfWeek }?.second ?: 0.0
                        dayName to amount.toFloat()
                    }
                }

                // 哪个月消费最多
                val monthChartData = remember(monthStats) {
                    monthStats.sortedBy { it.first }.associate { (month, amount) ->
                        "${month}月" to amount.toFloat()
                    }
                }

                if (monthStats.isNotEmpty()) {
                    CustomCard(color = cardNormalColor()) {
                        TransplantListItem(
                            overlineContent = { Text("月份分析") },
                            headlineContent = { Text("哪个月最能花", style = MaterialTheme.typography.titleMedium) }
                        )
                        BarChart(
                            data = monthChartData,
                            modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP, vertical = 8.dp)
                        )
                        monthStats.forEach { (month, amount) ->
                            TransplantListItem(
                                headlineContent = { Text("${month}月", style = MaterialTheme.typography.bodyMedium) },
                                trailingContent = { Text("￥${formatDecimal(amount, 2)}", style = MaterialTheme.typography.bodyMedium) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                }


                if (weekdayStats.isNotEmpty()) {
                    CustomCard(color = cardNormalColor()) {
                        TransplantListItem(
                            overlineContent = { Text("星期分析") },
                            headlineContent = { Text("哪天最能花", style = MaterialTheme.typography.titleMedium) }
                        )
                        BarChart(
                            data = weekdayChartData,
                            modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP, vertical = 8.dp)
                        )
                        weekdayStats.forEach { (dayOfWeek, amount) ->
                            val dayName = DayOfWeek.of(dayOfWeek).getDisplayName(TextStyle.SHORT, Locale.CHINESE)
                            TransplantListItem(
                                headlineContent = { Text(dayName, style = MaterialTheme.typography.bodyMedium) },
                                trailingContent = { Text("￥${formatDecimal(amount, 2)}", style = MaterialTheme.typography.bodyMedium) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                }

                Spacer(modifier = Modifier.height(CARD_NORMAL_DP))

                // 小贴士
                val messages = remember(totalAmount, records.size, days, avgPerTransaction, highestAmount, busiestWeekday) {
                    mutableListOf<String>().apply {
                        val avgPerDay = if (days > 0) totalAmount / days else 0.0

                        add("${periodLabel}共消费 ${records.size} 笔，总计 ￥${formatDecimal(totalAmount, 2)}")
                        add("平均每笔消费 ￥${formatDecimal(avgPerTransaction, 2)}")

                        if (records.size > 100) add("你真是个消费达人，${records.size} 笔消费记录！")
                        if (records.size < 10) add("消费记录这么少，你是用现金的吗？")

                        if (totalAmount > 5000) add("${periodLabel}消费超过 5000 元，省着点花哦~")
                        if (totalAmount < 1000) add("${periodLabel}消费不到 1000 元，省钱冠军就是你！")

                        if (days > 100) add("跨越 $days 天的消费记录，你一直在坚持记账！")

                        if (avgPerDay > 30) add("日均消费 ￥${formatDecimal(avgPerDay, 0)}，食堂不香吗？")
                        else if (avgPerDay < 15) add("日均消费 ￥${formatDecimal(avgPerDay, 0)}，你是省钱小能手！")
                        else add("日均消费 ￥${formatDecimal(avgPerDay, 0)}，花得刚刚好~")

                        if (highestAmount > 200) add("单笔最高 ￥${formatDecimal(highestAmount, 0)}，这是吃了什么大餐？")
                        if (highestAmount in 50.0..100.0) add("单笔最高 ￥${formatDecimal(highestAmount, 0)}，是不是买了什么好东西？")

                        busiestWeekday?.let { (day, _) ->
                            val dayName = DayOfWeek.of(day).getDisplayName(TextStyle.SHORT, Locale.CHINESE)
                            add("你最能花的一天是周$dayName，小心钱包~")
                        }

                        busiestMonth?.let { (month, _) ->
                            add("${month}月是你消费最高的一个月，注意生活费规划哦~")
                        }

                        if (avgPerTransaction > 30) add("平均单笔 ￥${formatDecimal(avgPerTransaction, 0)}，偶尔奢侈一下也不错~")
                        if (avgPerTransaction < 10) add("平均单笔不到 ￥10，勤俭持家！")

                        if (isEmpty()) add("${periodLabel}共消费 ${records.size} 笔，总计 ￥${formatDecimal(totalAmount, 2)}")
                    }
                }

                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(
                        overlineContent = { Text("小贴士") },
                        headlineContent = { Text(messages.joinToString("\n"), style = MaterialTheme.typography.bodyMedium) }
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
                    LoadingUI()
                }
            }
        }
    }
}
